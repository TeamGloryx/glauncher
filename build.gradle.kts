import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.openjfx.javafxplugin") version "0.0.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jetbrains.compose")
    kotlin("plugin.serialization")
    idea
}

idea {
    module {
        isDownloadSources = true
    }
}

group = "net.gloryx"
version = "1.0.0"

val composeVersion = extra["compose.version"] as String
val kotlinVersion = extra["kotlin.version"] as String

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://dev.gloryx.net/main")
    maven("https://dev.gloryx.net/snap")
    maven("https://repo.u-team.info")
    maven("https://maven.fabricmc.net")
}
val cat = "0.2.0-SNAPSHOT"
dependencies {
    implementation("net.gloryx.cat:ui:$cat")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib", kotlinVersion))
            }
        }
        val jvmMain by getting {
            val ktor = "2.1.1"
            dependencies {
                implementation(kotlin("stdlib-jdk8", kotlinVersion))
                implementation(compose.desktop.currentOs)
                implementation("net.hycrafthd:minecraft_authenticator:+")
                implementation("org.spongepowered:configurate-core:4.1.2")
                implementation("commons-codec:commons-codec:1.15")
                implementation("org.spongepowered:configurate-hocon:4.1.2")
                implementation("net.gloryx:cat:$cat")
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
                implementation("net.gloryx.cat:ui-jvm:$cat")
                implementation("org.python:jython:+")
                implementation("org.apache.logging.log4j:log4j-core:2.19.0")
                implementation("org.apache.logging.log4j:log4j-api:2.19.0")
                implementation("net.lingala.zip4j:zip4j:2.11.2")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4")
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

            linux {
                shortcut = true
                appCategory = "Games"
            }
            windows {
                perUserInstall = true
                dirChooser = true
                menu = true
                menuGroup = "Gloryx"
            }
        }
    }
}

javafx {
    version = "16"
    modules = listOf("javafx.controls", "javafx.swing", "javafx.web", "javafx.graphics")
}