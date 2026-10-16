import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

val keystoreProperties = Properties().apply {
    val propertiesFile = rootProject.file("keystore.properties")
    if (propertiesFile.isFile) {
        propertiesFile.inputStream().use { load(it) }
    }
}

fun releaseCredential(name: String): String =
    System.getenv(name)?.takeIf { it.isNotBlank() }
        ?: keystoreProperties.getProperty(name).orEmpty()

val releaseKeystoreFile = releaseCredential("KEYSTORE_FILE")
val releaseKeystorePassword = releaseCredential("KEYSTORE_PASSWORD")
val releaseKeyAlias = releaseCredential("KEY_ALIAS")
val releaseKeyPassword = releaseCredential("KEY_PASSWORD")
val hasReleaseSigning = listOf(
    releaseKeystoreFile,
    releaseKeystorePassword,
    releaseKeyAlias,
    releaseKeyPassword,
).all { it.isNotBlank() }

val hrviewVersionName = providers.gradleProperty("hrview.versionName").orNull
    ?: error("Set hrview.versionName in gradle.properties, for example 1.0.0")
val hrviewVersionCode = providers.gradleProperty("hrview.versionCode").orNull?.toIntOrNull()
    ?: error("Set hrview.versionCode in gradle.properties to a positive integer")

require(hrviewVersionName.matches(Regex("""\d+\.\d+\.\d+"""))) {
    "hrview.versionName must be major.minor.patch, for example 1.0.1"
}
require(hrviewVersionCode > 0) {
    "hrview.versionCode must be greater than 0"
}

android {
    namespace = "com.hrview"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.hrview"
        minSdk = 23
        targetSdk = 34
        versionCode = hrviewVersionCode
        versionName = hrviewVersionName
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = rootProject.file(releaseKeystoreFile)
                storePassword = releaseKeystorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.karoo.ext)
    implementation(libs.kotlinx.coroutines.android)
}
