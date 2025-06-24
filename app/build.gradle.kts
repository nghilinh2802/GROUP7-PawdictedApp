plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.group7.pawdicted"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.group7.pawdicted"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.glide)
    implementation(libs.firebase.firestore)
    annotationProcessor(libs.glide.compiler)

    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // ➕ Facebook Login SDK
    implementation("com.facebook.android:facebook-login:18.0.3") // dùng version mới nhất ổn định :contentReference[oaicite:1]{index=1}

    // ➕ Google Sign-In support (cần để dùng GoogleSignInClient, GoogleSignIn...)
    implementation("com.google.android.gms:play-services-auth:21.3.0")  // phiên bản mới nhất :contentReference[oaicite:1]{index=1}

    implementation ("com.google.firebase:firebase-bom:33.15.0")
    implementation ("com.google.firebase:firebase-firestore:25.1.4")

    implementation ("com.google.code.gson:gson:2.8.9")
    implementation ("androidx.appcompat:appcompat:1.6.1")

    implementation("com.cloudinary:cloudinary-android:3.0.2")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
}