import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.openjfx.javafxplugin") version "0.0.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.7.10"
}

group = "net.gloryx"
version = "1.0.0"

val composeVersion = extra["compose.version"] as String

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://dev.gloryx.net/main")
    maven("https://dev.gloryx.net/snap")
    mavenLocal()
    maven("https://repo.u-team.info")
    maven("https://maven.fabricmc.net")
}
dependencies {
    implementation("net.gloryx.cat:ui:+")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            val ktor = "2.1.1"
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("net.hycrafthd:minecraft_authenticator:+")
                implementation("org.spongepowered:configurate-core:4.1.2")
                implementation("commons-codec:commons-codec:1.15")
                implementation("org.spongepowered:configurate-hocon:4.1.2")
                implementation("net.gloryx:cat:0.1.52-SNAPSHOT")
                implementation("net.gloryx:oknamer:0.1.02-SNAPSHOT")
                implementation("com.electronwill.night-config:core:3.6.6")
                implementation("com.electronwill.night-config:hocon:3.6.6")
                implementation("io.ktor:ktor-client-core-jvm:$ktor")
                implementation("io.ktor:ktor-client-okhttp:$ktor")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.4.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
                implementation("net.fabricmc:fabric-installer:0.11.1")
                implementation("org.jetbrains.exposed:exposed-core:0.39.2")
                implementation("org.jetbrains.exposed:exposed-jdbc:0.39.2")
                implementation("mysql:mysql-connector-java:8.0.30")
                implementation("at.favre.lib:bcrypt:0.9.0")
                implementation("me.nullicorn:ms-to-mca:0.0.1")
                implementation("com.microsoft.azure:msal4j:1.10.1")
                implementation("org.jetbrains.compose.ui:ui-graphics-desktop:$composeVersion")
                implementation("org.jetbrains.compose.ui:ui-geometry-desktop:$composeVersion")
                implementation("org.jetbrains.compose.foundation:foundation-desktop:$composeVersion")
                implementation("net.gloryx.cat:ui-jvm:+")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "LaunchKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "glauncher"
            packageVersion = rootProject.version.toString()
        }
    }
}

javafx {
    version = "16"
    modules = listOf("javafx.controls", "javafx.swing", "javafx.web", "javafx.graphics")
}