plugins {
    id("com.android.application")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.doan_music"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.doan_music"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/NOTICE")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/NOTICE.txt")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildToolsVersion = "34.0.0"
    buildFeatures {
        viewBinding = true
    }

}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.media3:media3-session:1.3.0")
    implementation("com.google.firebase:firebase-firestore:24.10.3")
    implementation ("com.google.firebase:firebase-storage:20.2.1")
    implementation(fileTree(mapOf(
        "dir" to "E:\\ZaloPayLib",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))
    implementation("com.squareup.okhttp3:okhttp:4.6.0")
    implementation("commons-codec:commons-codec:1.14")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("de.hdodenhof:circleimageview:2.1.0")
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("io.github.chaosleung:pinview:1.4.4")
    implementation("com.sendgrid:sendgrid-java:4.7.0")
    implementation("androidx.media:media:1.7.0")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    //sqlite
    implementation("com.readystatesoftware.sqliteasset:sqliteassethelper:2.0.1")

    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")

    implementation("com.sun.mail:android-mail:1.6.0")
    implementation("com.sun.mail:android-activation:1.6.0")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation("net.sourceforge.jtds:jtds:1.3.1")
    implementation ("com.jjoe64:graphview:4.2.2")
    implementation("com.google.android.gms:play-services-ads:23.3.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
}