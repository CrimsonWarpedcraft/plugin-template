import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.spotbugs.snom.SpotBugsTask
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

plugins {
    checkstyle
    id("com.github.spotbugs") version "6.5.6"
    id("com.gradleup.shadow") version "9.4.2"
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

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
        content {
            includeModule("io.papermc.paper", "paper-api")
            includeModule("io.papermc", "paperlib")
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

val mockitoAgent by configurations.creating

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.69-stable")
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.10.2")
    implementation("com.github.CrimsonWarpedcraft:cw-commons:v0.1.0")
    implementation("io.papermc:paperlib:1.0.8")
    // Example command implementation via CommandAPI — remove if not needed
    // https://commandapi.jorel.dev
    implementation("dev.jorel:commandapi-paper-shade:11.2.0")
    // Jackson + Hibernate Validator: also exposed transitively via cw-commons' `api` deps,
    // but declared directly anyway since PluginConfig imports their annotations — don't
    // rely on a transitive exposure decision made by another project for code we compile against.
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.22.0")
    implementation("org.hibernate.validator:hibernate-validator:9.1.0.Final")
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.14.0")
    testCompileOnly("com.github.spotbugs:spotbugs-annotations:4.10.2")
    testImplementation("io.papermc.paper:paper-api:26.1.2.build.69-stable")
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.0")
    testImplementation("org.mockito:mockito-core:5.23.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.1.0")
    mockitoAgent("org.mockito:mockito-core:5.23.0") { isTransitive = false }
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}

tasks.processResources {
    filesMatching("**/plugin.yml") {
        expand(mapOf("NAME" to rootProject.name, "VERSION" to version, "PACKAGE" to rootProject.group.toString()))
    }
}

checkstyle {
    toolVersion = "13.5.0"
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

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")
    mergeServiceFiles()
    relocate("io.papermc.lib", "shadow.io.papermc.paperlib")
    // Update the destination package to match your group when renaming the plugin
    relocate("dev.jorel.commandapi", "com.crimsonwarpedcraft.exampleplugin.commandapi")
    relocate("com.fasterxml", "com.crimsonwarpedcraft.exampleplugin.fasterxml")
    relocate("org.yaml.snakeyaml", "com.crimsonwarpedcraft.exampleplugin.snakeyaml")
    relocate("org.hibernate.validator", "com.crimsonwarpedcraft.exampleplugin.hibernatevalidator")
    relocate("jakarta.validation", "com.crimsonwarpedcraft.exampleplugin.jakartavalidation")
    relocate("org.jboss.logging", "com.crimsonwarpedcraft.exampleplugin.jbosslogging")
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
    }
}

tasks.jar {
    enabled = false
}

tasks.assemble {
    dependsOn(tasks.named("shadowJar"))
}

tasks.register("printProjectName") {
    doLast {
        println(rootProject.name)
    }
}

tasks.register("release") {
    dependsOn(tasks.named("build"))

    doLast {
        if (!version.toString().endsWith("-SNAPSHOT")) {
            // Rename final JAR to trim off version information
            tasks.named<ShadowJar>("shadowJar").get().archiveFile.get().asFile
                .renameTo(layout.buildDirectory.get().asFile.resolve("libs/${rootProject.name}.jar"))
        }
    }
}
