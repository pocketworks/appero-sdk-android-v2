import com.vanniktech.maven.publish.SonatypeHost
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish)
}

// Load local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

// Map local.properties to project properties so the signing plugin can find them
localProperties.forEach { (key, value) ->
    project.extensions.extraProperties.set(key.toString(), value.toString())
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

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
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
    implementation(libs.play.review)
    
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
    testImplementation(kotlin("test"))
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
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

    // Configure publishing to Maven Central
    publishToMavenCentral(host = SonatypeHost.CENTRAL_PORTAL, automaticRelease = false)

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
