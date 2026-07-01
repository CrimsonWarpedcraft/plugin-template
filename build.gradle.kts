import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.spotbugs.snom.SpotBugsTask
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

plugins {
    checkstyle
    id("com.github.spotbugs") version "6.5.8"
    id("com.gradleup.shadow") version "9.4.3"
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
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.72-stable")

    // Code quality and unit testing. Not required for code functionality.
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.10.2")
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.14.0")
    testCompileOnly("com.github.spotbugs:spotbugs-annotations:4.10.2")
    testImplementation("io.papermc.paper:paper-api:26.1.2.build.72-stable")
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.1.1")

    // Example dependencies. Paper plugins do not require these libraries.
    implementation("com.github.CrimsonWarpedcraft:cw-commons:v0.1.1")
    // PluginConfig imports annotations from Jackson and Hibernate Validator directly.
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.22.0")
    implementation("dev.jorel:commandapi-paper-shade:11.2.0")
    implementation("org.hibernate.validator:hibernate-validator:9.1.1.Final")

    testImplementation("org.mockito:mockito-core:5.23.0")
    mockitoAgent("org.mockito:mockito-core:5.23.0") { isTransitive = false }
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}

tasks.processResources {
    filesMatching("**/plugin.yml") {
        expand(mapOf("NAME" to rootProject.name, "VERSION" to version, "PACKAGE" to project.group))
    }
}

checkstyle {
    toolVersion = "13.6.0"
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
    mergeServiceFiles()
    relocate("dev.jorel.commandapi", "${project.group}.commandapi")
    relocate("com.fasterxml", "${project.group}.fasterxml")
    relocate("org.yaml.snakeyaml", "${project.group}.snakeyaml")
    relocate("org.hibernate.validator", "${project.group}.hibernatevalidator")
    relocate("jakarta.validation", "${project.group}.jakartavalidation")
    relocate("org.jboss.logging", "${project.group}.jbosslogging")
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
    dependsOn("build")

    doLast {
        if (!version.toString().endsWith("-SNAPSHOT")) {
            val releaseJar = layout.buildDirectory.file("libs/${rootProject.name}.jar").get().asFile
            shadowJar.get().archiveFile.get().asFile.renameTo(releaseJar)
        }
    }
}
