import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.1.10"
    id("org.jetbrains.compose") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10"
    id("jacoco")
    kotlin("plugin.serialization") version "1.9.10"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://raw.github.com/gephi/gephi/mvn-thirdparty-repo/")
    maven("https://jitpack.io")
}

dependencies {
    testImplementation(libs.junit.jupiter)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation(compose.desktop.currentOs)
    testImplementation(compose.desktop.uiTestJUnit4)
    implementation(compose.materialIconsExtended)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(libs.guava)
    implementation(libs.ktlint)
    implementation("com.github.JetBrains-Research:louvain:main-SNAPSHOT")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "AppKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "graphs-graphs-team-5"
            packageVersion = "1.0.0"
        }
    }
}
