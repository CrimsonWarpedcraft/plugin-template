import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.spotbugs.snom.SpotBugsTask
import org.gradle.api.plugins.jvm.JvmTestSuite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

plugins {
    checkstyle
    alias(libs.plugins.spotbugs)
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
    java
}

group = "com.crimsonwarpedcraft.exampleplugin"

fun getTime(): String {
    val sdf = SimpleDateFormat("yyMMdd-HHmm")
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date())
}

version = (if (!hasProperty("ver")) {
    "${getTime()}-SNAPSHOT"
} else {
    val ver = property("ver") as String
    val base = if (ver.startsWith("v")) ver.drop(1) else ver.replace('/', '-')
    if (ver.startsWith("v") && !ver.lowercase().contains("-rc-")) base else "$base-SNAPSHOT"
}).uppercase()

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

// Drives both the local development server and plugin.yml's declared minimum API.
val supportedMinecraftVersion = libs.versions.minecraft.get()

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
        content {
            includeModule("io.papermc.paper", "paper-api")
            includeModule("net.md-5", "bungeecord-chat")
            includeGroup("io.papermc.adventure")
        }
    }

    maven {
        name = "minecraft"
        url = uri("https://libraries.minecraft.net")
        content {
            includeModule("com.mojang", "brigadier")
        }
    }

    mavenCentral()

    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
        content {
            includeGroup("com.github.CrimsonWarpedcraft")
        }
    }
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    compileOnly(libs.paper.api)

    // Code quality and unit testing. Not required for code functionality.
    compileOnly(libs.spotbugs.annotations)
    spotbugsPlugins(libs.findsecbugs)
    testCompileOnly(libs.spotbugs.annotations)
    testImplementation(libs.paper.api)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)

    // Example dependencies. Paper plugins do not require these libraries.
    implementation(libs.cw.commons)
    // PluginConfig imports annotations from Jackson and Hibernate Validator directly.
    implementation(libs.jackson.yaml)
    implementation(libs.commandapi) {
        // Upstream metadata references unpublished modules already bundled in this JAR.
        // https://github.com/CommandAPI/CommandAPI/issues/704
        isTransitive = false
    }
    implementation(libs.hibernate.validator)

    testImplementation(libs.mockito.core)
    mockitoAgent(libs.mockito.core) { isTransitive = false }
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}

tasks.runServer {
    minecraftVersion(supportedMinecraftVersion)
}

// An explicitly supplied candidate must be tested as-is, without building a replacement.
val candidatePath = providers.gradleProperty("artifactUnderTest")
val artifactUnderTest = if (candidatePath.isPresent) {
    files(candidatePath.map { file(it) })
} else {
    files(tasks.named<ShadowJar>("shadowJar").flatMap { it.archiveFile })
}

testing {
    suites {
        register<JvmTestSuite>("integrationTest") {
            useJUnitJupiter(libs.versions.junit.get())

            dependencies {
                implementation(sourceSets.main.get().output)
                implementation(libs.paper.api)
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(tasks.test)
                        jvmArgs("--enable-native-access=ALL-UNNAMED")
                    }
                }
            }
        }

        register<JvmTestSuite>("artifactTest") {
            useJUnitJupiter(libs.versions.junit.get())
            // No main output, project dependency or inheritance from implementation/test.
            dependencies {
                implementation(artifactUnderTest)
                implementation(libs.paper.api)
            }
            targets.all {
                testTask.configure {
                    jvmArgs("--enable-native-access=ALL-UNNAMED")
                    systemProperty("artifact.jar", artifactUnderTest.singleFile.absolutePath)
                    systemProperty("artifact.name", rootProject.name)
                    systemProperty("artifact.package", project.group.toString())
                    systemProperty("artifact.version", project.version.toString())
                    systemProperty("artifact.api", supportedMinecraftVersion)
                }
            }
        }
    }
}

configurations.named("integrationTestImplementation") {
    extendsFrom(configurations.implementation.get())
}

tasks.processResources {
    val pluginProperties = mapOf(
        "NAME" to rootProject.name,
        "VERSION" to project.version.toString(),
        "PACKAGE" to project.group.toString(),
        "MINECRAFT_VERSION" to supportedMinecraftVersion,
    )
    inputs.properties(pluginProperties)
    filesMatching("**/plugin.yml") {
        expand(pluginProperties)
    }
}

checkstyle {
    toolVersion = libs.versions.checkstyle.get()
    maxWarnings = 0
}

configurations.named("checkstyle") {
    resolutionStrategy.capabilitiesResolution
        .withCapability("com.google.collections:google-collections") {
            select("com.google.guava:guava:23.0")
        }
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required.set(false)
        html.required.set(true)
    }
}

tasks.withType<SpotBugsTask>().configureEach {
    reports.create("html") {
        required.set(true)
    }
    reports.create("xml") {
        required.set(false)
    }
}

val shadowJar = tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")
    if (!project.version.toString().endsWith("-SNAPSHOT")) {
        archiveVersion.set("") // Produce the stable release filename directly; verify this file.
    }
    filesMatching("META-INF/services/**") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
    mergeServiceFiles()
    relocate("dev.jorel.commandapi", "${project.group}.commandapi")
    relocate("com.fasterxml", "${project.group}.fasterxml")
    relocate("org.yaml.snakeyaml", "${project.group}.snakeyaml")
    relocate("org.hibernate.validator", "${project.group}.hibernatevalidator")
    relocate("jakarta.validation", "${project.group}.jakartavalidation")
    relocate("org.jboss.logging", "${project.group}.jbosslogging")
    // Shadow otherwise scans every source set, making the JAR depend on its own tests and
    // allowing test-only references to keep classes alive during minimization.
    sourceSetsClassesDirs.setFrom(sourceSets.main.get().output.classesDirs)
    // These libs load classes via reflection or SPI and must not be minimized
    minimize {
        exclude(dependency("dev.jorel:commandapi-paper-shade:.*"))
        exclude(dependency("com.fasterxml.jackson.core:.*:.*"))
        exclude(dependency("com.fasterxml.jackson.dataformat:.*:.*"))
        exclude(dependency("com.fasterxml:classmate:.*"))
        exclude(dependency("org.hibernate.validator:.*:.*"))
        exclude(dependency("jakarta.validation:.*:.*"))
        exclude(dependency("org.yaml:snakeyaml:.*"))
        exclude(dependency("org.jboss.logging:.*:.*"))
        // cw-commons bundles the SQLite JDBC driver (loaded via SPI) inside its own jar;
        // it never appears as a separate resolvable dependency, so it must be excluded by name.
        exclude(dependency("com.github.CrimsonWarpedcraft:cw-commons:.*"))
    }
}

tasks.jar {
    enabled = false
}

tasks.assemble {
    dependsOn(shadowJar)
}

tasks.register("printProjectName") {
    doLast {
        println(rootProject.name)
    }
}

tasks.register("release") {
    dependsOn("build", "integrationTest", "artifactTest")
    doFirst {
        check(!candidatePath.isPresent) { "Use artifactTest to check an external candidate" }
    }
}
