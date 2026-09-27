plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'com.google.dagger.hilt.android'
    id 'org.jetbrains.kotlin.plugin.serialization'
    id 'kotlin-android'
    id 'kotlin-kapt'
}

android {
    namespace = 'com.mindtrainer'
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mindtrainer"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile('proguard-android-optimize.txt'),
                'proguard-rules.pro'
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = '17'
        freeCompilerArgs += [
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi",
            "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        ]
    }

    composeOptions {
        kotlinCompilerExtensionVersion = '1.5.11'
    }

    packagingOptions {
        resources {
            excludes += '/META-INF/{AL2.0,LGPL2.1,LICENSE,NOTICE}/*'
        }
    }

    buildFeatures {
        compose = true
        viewBinding = false
        dataBinding = false
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
}

dependencies {
    // Android Core
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
    implementation 'androidx.activity:activity-compose:1.8.2'

    // Compose BOM
    val composeBom = platform('androidx.compose:compose-bom:2024.05.00')
    implementation(composeBom)
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.ui:ui-graphics'
    implementation 'androidx.compose.ui:ui-tooling-preview'
    implementation 'androidx.compose.material3:material3'
    implementation 'androidx.compose.material:material-icons-extended'
    implementation 'androidx.compose.runtime:runtime-livedata'
    implementation 'androidx.compose.runtime:runtime-rxjava2'

    // Navigation
    implementation 'androidx.navigation:navigation-compose:2.7.6'
    implementation 'androidx.navigation:navigation-fragment-ktx:2.7.6'

    // Hilt
    implementation 'com.google.dagger:hilt-android:2.48.1'
    kapt 'com.google.dagger:hilt-compiler:2.48.1'

    // Room
    val roomVersion = '2.6.1'
    implementation "androidx.room:room-runtime:$roomVersion"
    implementation "androidx.room:room-ktx:$roomVersion"
    kapt "androidx.room:room-compiler:$roomVersion"

    // DataStore
    implementation 'androidx.datastore:datastore-preferences:1.0.0'
    implementation 'androidx.datastore:datastore-preferences-rxjava2:1.0.0'

    // Retrofit & OkHttp
    implementation 'com.squareup.retrofit2:retrofit:2.11.0'
    implementation 'com.squareup.retrofit2:converter-moshi:2.11.0'
    implementation 'com.squareup.retrofit2:converter-scalars:2.11.0'
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'

    // Moshi
    implementation 'com.squareup.moshi:moshi-kotlin:1.15.1'
    kapt 'com.squareup.moshi:moshi-kotlin-codegen:1.15.1'

    // Coroutines & Flow
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.0'
    implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3'

    // WorkManager
    implementation 'androidx.work:work-runtime-ktx:2.9.0'

    // Coil (Image loading)
    implementation 'io.coil-kt:coil-compose:2.5.0'

    // MPAndroidChart (for charts)
    implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'

    // Accompanist (for permissions, etc)
    implementation 'com.google.accompanist:accompanist-permissions:0.32.0'
    implementation 'com.google.accompanist:accompanist-systemuicontroller:0.32.0'

    // Preferences DataStore
    implementation 'androidx.preference:preference-ktx:1.2.1'

    // Biometric
    implementation 'androidx.biometric:biometric:1.1.0'

    // Testing
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'org.jetbrains.kotlin:kotlin-test-junit:1.9.23'
    testImplementation 'org.mockito:mockito-core:5.12.0'
    testImplementation 'org.mockito:mockito-kotlin:5.12.0'
    testImplementation 'com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0'
    testImplementation 'org.robolectric:robolectric:4.11.1'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
    androidTestImplementation 'androidx.compose.ui:ui-test-junit4:1.6.1'
    androidTestImplementation 'androidx.compose.ui:ui-test-manifest:1.6.1'
    debugImplementation 'androidx.compose.ui:ui-tooling:1.6.1'
    debugImplementation 'androidx.compose.ui:ui-tooling-preview:1.6.1'
}

kapt {
    correctErrorTypes = true
    javacOptions += [
        "-Xlint:unchecked",
        "-Xlint:deprecation"
    ]
}

android {
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            returnDefaultValues = true
        }
    }
}