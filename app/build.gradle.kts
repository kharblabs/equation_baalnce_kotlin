plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.kharblabs.equationbalancer2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kharblabs.equationbalancer2"
        minSdk = 28
        targetSdk = 35
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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.keatex)
    implementation(libs.autotext)
    implementation(libs.mockito)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.preference)
    implementation(libs.piechart)
    testImplementation(libs.junit)
    testImplementation(libs.junit.junit)
    val room_version = "2.7.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation( "com.leinardi.android:speed-dial:3.3.0")
    androidTestImplementation(libs.androidx.junit)
    ksp("androidx.room:room-compiler:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    androidTestImplementation(libs.androidx.espresso.core)
}