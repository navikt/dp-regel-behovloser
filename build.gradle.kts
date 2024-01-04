plugins {
    id("common")
    application
}
repositories {
    mavenCentral()
    maven("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
}
dependencies {
    implementation(libs.kotlin.logging)
    implementation(libs.rapids.and.rivers)
    implementation(libs.konfig)
}
application {
    mainClass.set("no.nav.dagpenger.regel.behovloser.AppKt")
}