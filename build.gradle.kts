import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    val kotlinVersion: String by System.getProperties()
    val kvisionVersion: String by System.getProperties()
    val dokkaVersion: String by System.getProperties()
    kotlin("plugin.serialization") version kotlinVersion
    kotlin("js") version kotlinVersion
    id("io.kvision") version kvisionVersion
    id("org.jetbrains.dokka") version dokkaVersion
}

version = "1.0.0-SNAPSHOT"
group = "se.skoview"

repositories {
    mavenCentral()
    mavenLocal()
}

// Versions
val kotlinVersion: String by System.getProperties()
val kvisionVersion: String by System.getProperties()

val webDir = file("src/main/web")

kotlin {
    js {
        browser {
            runTask {
                outputFileName = "main.bundle.js"
                sourceMaps = false
                devServer = KotlinWebpackConfig.DevServer(
                    open = false,
                    port = 4000,
                    proxy = mutableMapOf(
                        "/kv/*" to "http://localhost:8080",
                        "/kvws/*" to mapOf("target" to "ws://localhost:8080", "ws" to true)
                    ),
                    static = mutableListOf("$buildDir/processedResources/js/main")
                )
            }
            webpackTask {
                outputFileName = "main.bundle.js"
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
    }
    sourceSets["main"].dependencies {
        implementation("io.kvision:kvision:$kvisionVersion")
        implementation("io.kvision:kvision-bootstrap:$kvisionVersion")
        implementation("io.kvision:kvision-bootstrap-css:$kvisionVersion")
        implementation("io.kvision:kvision-bootstrap-datetime:$kvisionVersion")
        implementation("io.kvision:kvision-bootstrap-select:$kvisionVersion")
        implementation("io.kvision:kvision-bootstrap-spinner:$kvisionVersion")
        implementation("io.kvision:kvision-bootstrap-upload:$kvisionVersion")
        implementation("io.kvision:kvision-bootstrap-dialog:$kvisionVersion")
        implementation("io.kvision:kvision-bootstrap-typeahead:$kvisionVersion")
        implementation("io.kvision:kvision-tabulator:$kvisionVersion")
        implementation("io.kvision:kvision-datacontainer:$kvisionVersion")
        implementation("io.kvision:kvision-redux-kotlin:$kvisionVersion")
        implementation("io.kvision:kvision-pace:$kvisionVersion")
        implementation("io.kvision:kvision-routing-navigo:$kvisionVersion")
        implementation("io.kvision:kvision-fontawesome:$kvisionVersion")
        implementation("io.kvision:kvision-richtext:$kvisionVersion")
    }
    sourceSets["test"].dependencies {
        implementation(kotlin("test-js"))
        implementation("io.kvision:kvision-testutils:$kvisionVersion:tests")
    }
    sourceSets["main"].resources.srcDir(webDir)
}

tasks.dokkaHtml.configure {
    val USER_HOME = System.getenv("HOME")
    // Set module name displayed in the final output
    moduleName.set("tkview")
    outputDirectory.set(buildDir.resolve("dokka/html"))
    cacheRoot.set(file("$USER_HOME/.cache/dokka"))
    dokkaSourceSets.configureEach {
        includes.from("src/main/kotlin/se/skoview/MODULE.md")
        includes.from("src/main/kotlin/se/skoview/controller/CONTROLLER.PACKAGE.md")
        includes.from("src/main/kotlin/se/skoview/model/MODEL.PACKAGE.md")
        includes.from("src/main/kotlin/se/skoview/view/VIEW.PACKAGE.md")
    }
}

tasks.dokkaGfm.configure {
    val USER_HOME = System.getenv("HOME")
    // Set module name displayed in the final output
    moduleName.set("tkview")
    outputDirectory.set(buildDir.resolve("dokka/gfm"))
    cacheRoot.set(file("$USER_HOME/.cache/dokka"))
    dokkaSourceSets.configureEach {
        includes.from("src/main/kotlin/se/skoview/MODULE.md")
        includes.from("src/main/kotlin/se/skoview/controller/CONTROLLER.PACKAGE.md")
        includes.from("src/main/kotlin/se/skoview/model/MODEL.PACKAGE.md")
        includes.from("src/main/kotlin/se/skoview/view/VIEW.PACKAGE.md")
    }
}
