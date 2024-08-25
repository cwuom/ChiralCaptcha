import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.cwuom.chiralcaptcha"
    compileSdk = 34

    val appVerCode: Int by lazy {
        val versionCode = SimpleDateFormat("yyMMddHH", Locale.ENGLISH).format(Date())
        println("versionCode: $versionCode")
        versionCode.toInt()
    }

    val appVerName: String by lazy {
        val versionName = "${getShortGitRevision()}.${getCurrentDate()}"
        println("versionName: $versionName")
        versionName
    }



    defaultConfig {
        applicationId = "com.cwuom.chiralcaptcha"
        minSdk = 29
        targetSdk = 34
        versionCode = appVerCode
        versionName = appVerName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }


    buildTypes {
        release {
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


// -------------------- functions --------------------

fun getShortGitRevision(): String {  // Auto Generate a VersionName
    val command = "git rev-parse --short HEAD"
    val processBuilder = ProcessBuilder("cmd", "/c", command) // Only Run On Windows!
    val process = processBuilder.start()

    val output = process.inputStream.bufferedReader().use { it.readText() }
    val exitCode = process.waitFor()

    if (exitCode == 0) {
        return output.trim()
    } else {
        // 你需要配置好Git环境并且至少做一次提交
        throw RuntimeException("Failed to get the commit count. " +
                "Make sure you have Git installed and your working directory is a Git repository." +
                "If this is a new repository, you need to make at least one commit.")
    }
}


fun getCurrentDate(): String { // Auto Generate a VersionCode
    val sdf = SimpleDateFormat("MMddHHmm", Locale.getDefault())
    return sdf.format(Date())
}

configurations.configureEach {
    exclude(group = "androidx.appcompat", module = "appcompat")
}


dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.hutool.all) // A set of tools that keep Java sweet.

    // room database
    implementation(libs.room.runtime)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    annotationProcessor(libs.room.compiler)

    // DialogX
    implementation(libs.dialogx)
    implementation(libs.dialogxiosstyle)

    // never write another getter or equals method again
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Material Preference
    implementation(libs.material.preference)
    implementation(libs.dev.appcompat)
    implementation(libs.recyclerview)

    // Preference
    implementation(libs.preference)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}