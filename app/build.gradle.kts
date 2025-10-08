import de.undercouch.gradle.tasks.download.Download

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("de.undercouch.download")
}

android {
    namespace = "com.example.kianarag"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.kianarag"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
//    externalNativeBuild {
//        cmake {
//            path = file("src/main/cpp/CMakeLists.txt")
//        }
//    }
}

//extra["ASSET_DIR"] = "$projectDir/src/main/assets"
//extra["TEST_ASSETS_DIR"] = "$projectDir/src/androidTest/assets"
//
//
//tasks.register<Download>("downloadMobileBertModel") {
//    src("https://storage.googleapis.com/mediapipe-models/text_embedder/bert_embedder/float32/1/bert_embedder.tflite")
//    dest("$projectDir/src/main/assets/mobile_bert.tflite")
//    overwrite(false)
//}
//
//tasks.register<Download>("downloadAverageWordModel") {
//    src("https://storage.googleapis.com/mediapipe-models/text_embedder/average_word_embedder/float32/1/average_word_embedder.tflite")
//    dest("$projectDir/src/main/assets/average_word.tflite")
//    overwrite(false)
//}


//tasks.named("preBuild") {
//    dependsOn("downloadMobileBertModel", "downloadAverageWordModel")
//}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.commons.math3)
    implementation(libs.gson)
    implementation(libs.tasks.text)
    implementation(libs.itextg)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.localagents.rag)
    implementation(libs.tasks.genai)
    implementation(libs.koin.android)
    implementation(libs.kotlinx.coroutines.guava)
//    implementation(project.dependencies.platform("io.insert-koin:koin-bom:4.1.2-Beta1"))
//    implementation(libs.koin.core)
}