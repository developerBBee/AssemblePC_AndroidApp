import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary {
        namespace = "jp.developer.bbee.assemblepc.shared"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }

        androidResources {
            enable = true
        }

        withHostTest {
            isIncludeAndroidResources = true
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
            // Required when using NativeSQLiteDriver
            linkerOpts.add("-lsqlite3")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)

            implementation(libs.navigation3.ui)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.coil3)
            implementation(libs.coil3.compose)
            implementation(libs.coil3.compose.core)
            implementation(libs.coil3.network.ktor)

            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.logging)

            implementation(libs.datastore.preferences)

            implementation(libs.napier)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }

        androidMain.dependencies {
            implementation(libs.ktor.android.client)
        }

        iosMain.dependencies {
            implementation(libs.ktor.darwin.client)
        }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    // Add any other platform target you use in your project, for example kspDesktop

    androidRuntimeClasspath(libs.compose.uiTooling)
}

room {
    schemaDirectory("$projectDir/schemas")
}
