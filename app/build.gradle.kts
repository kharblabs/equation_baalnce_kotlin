plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.kharblabs.balancer.equationbalancer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kharblabs.balancer.equationbalancer"
        minSdk = 30
        targetSdk = 35
        versionCode = 75
        versionName = "3.4"

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
        debug {

         //   applicationIdSuffix = ".dev"
            buildConfigField( "String", "BUILD_VARIANT", "\"dev\"")

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
        buildConfig = true
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
    val lifecycle_version = "2.5.1"

    implementation("androidx.lifecycle:lifecycle-common:2.4.1")

    implementation("com.google.android.gms:play-services-oss-licenses:17.0.1")
    val billing_version = "6.2.1"

    implementation("com.android.billingclient:billing-ktx:$billing_version")
    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
    testImplementation(libs.junit)
    testImplementation(libs.junit.junit)
    val room_version = "2.7.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation( "com.leinardi.android:speed-dial:3.3.0")
    implementation("com.facebook.android:audience-network-sdk:6.+")
    androidTestImplementation(libs.androidx.junit)
    ksp("androidx.room:room-compiler:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    androidTestImplementation(libs.androidx.espresso.core)
}