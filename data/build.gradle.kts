plugins {
    id("com.android.library")
    kotlin("android")
}
android {
    namespace = "com.a6w.memo"
    compileSdk = 36
    defaultConfig {
        minSdk = 28
    }
    compileOptions  {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(project(":domain"))
}
