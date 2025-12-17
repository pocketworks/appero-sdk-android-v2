plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish)
}

// Read version from gradle.properties
val versionName = property("VERSION_NAME").toString()
val libraryGroup = property("GROUP").toString()

group = libraryGroup
version = versionName

android {
    namespace = "uk.co.pocketworks.appero.sdk.main"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "SDK_VERSION", "\"${versionName}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        buildConfig = true
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files("$rootDir/detekt.yml"))
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    
    // Serialization
    implementation(libs.kotlinx.serialization.json)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    
    // Ktor Client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    
    // Lifecycle
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    detektPlugins(libs.detekt.formatting)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(kotlin("test"))
}

// Maven Publishing Configuration
mavenPublishing {
    // Read properties from gradle.properties
    val pomArtifactId = property("POM_ARTIFACT_ID").toString()
    val pomName = property("POM_NAME").toString()
    val pomDescription = property("POM_DESCRIPTION").toString()
    val pomInceptionYear = property("POM_INCEPTION_YEAR").toString()
    val pomUrl = property("POM_URL").toString()

    val pomLicenceName = property("POM_LICENCE_NAME").toString()
    val pomLicenceUrl = property("POM_LICENCE_URL").toString()
    val pomLicenceDist = property("POM_LICENCE_DIST").toString()

    val pomDeveloperId = property("POM_DEVELOPER_ID").toString()
    val pomDeveloperName = property("POM_DEVELOPER_NAME").toString()
    val pomDeveloperUrl = property("POM_DEVELOPER_URL").toString()
    val pomDeveloperEmail = property("POM_DEVELOPER_EMAIL").toString()

    val pomScmUrl = property("POM_SCM_URL").toString()
    val pomScmConnection = property("POM_SCM_CONNECTION").toString()
    val pomScmDevConnection = property("POM_SCM_DEV_CONNECTION").toString()

    coordinates(
        groupId = group.toString(),
        artifactId = pomArtifactId,
        version = version.toString()
    )

    pom {
        name.set(pomName)
        description.set(pomDescription)
        inceptionYear.set(pomInceptionYear)
        url.set(pomUrl)

        licenses {
            license {
                name.set(pomLicenceName)
                url.set(pomLicenceUrl)
                distribution.set(pomLicenceDist)
            }
        }

        developers {
            developer {
                id.set(pomDeveloperId)
                name.set(pomDeveloperName)
                url.set(pomDeveloperUrl)
                email.set(pomDeveloperEmail)
            }
        }

        scm {
            url.set(pomScmUrl)
            connection.set(pomScmConnection)
            developerConnection.set(pomScmDevConnection)
        }
    }

    // Configure publishing to Maven Central (OSSRH s01)
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.S01, automaticRelease = true)

    // Enable GPG signing for all publications
    signAllPublications()
}

dokka {
    moduleName.set(project.name)
    dokkaPublications.html {
        suppressInheritedMembers.set(true)
        failOnWarning.set(true)
    }
    dokkaSourceSets.main {
        includes.from("../README.md")
        sourceLink {
            localDirectory.set(file("src/main/java"))
            remoteUrl(property("POM_URL").toString())
            remoteLineSuffix.set("#L")
        }
    }
    pluginsConfiguration.html {
        footerMessage.set("Copyright (c) 2025 Pocketworks Mobile")
    }
}
