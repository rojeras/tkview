import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    val kotlinVersion: String by System.getProperties()
    id("kotlinx-serialization") version kotlinVersion
    kotlin("js") version kotlinVersion
}

version = "1.0.0-SNAPSHOT"
group = "se.skoview"

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
    maven { url = uri("https://dl.bintray.com/kotlin/kotlin-js-wrappers") }
    maven { url = uri("https://dl.bintray.com/rjaros/kotlin") }
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
                    // contentBase = mutableListOf("$buildDir/processedResources/js/main")
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
        // implementation("io.kvision:kvision-i18n:$kvisionVersion")
        implementation("io.kvision:kvision-tabulator:$kvisionVersion")
        implementation("io.kvision:kvision-datacontainer:$kvisionVersion")
        implementation("io.kvision:kvision-redux-kotlin:$kvisionVersion")
        implementation("io.kvision:kvision-pace:$kvisionVersion")
        implementation("io.kvision:kvision-routing-navigo:$kvisionVersion")
    }
    sourceSets["test"].dependencies {
        implementation(kotlin("test-js"))
        implementation("io.kvision:kvision-testutils:$kvisionVersion:tests")
    }
    sourceSets["main"].resources.srcDir(webDir)
}

fun getNodeJsBinaryExecutable(): String {
    val nodeDir = NodeJsRootPlugin.apply(project).nodeJsSetupTaskProvider.get().destination
    val isWindows = System.getProperty("os.name").toLowerCase().contains("windows")
    val nodeBinDir = if (isWindows) nodeDir else nodeDir.resolve("bin")
    val command = NodeJsRootPlugin.apply(project).nodeCommand
    val finalCommand = if (isWindows && command == "node") "node.exe" else command
    return nodeBinDir.resolve(finalCommand).absolutePath
}

tasks {
    create("generatePotFile", Exec::class) {
        dependsOn("compileKotlinJs")
        executable = getNodeJsBinaryExecutable()
        args("$buildDir/js/node_modules/gettext-extract/bin/gettext-extract")
        inputs.files(kotlin.sourceSets["main"].kotlin.files)
        outputs.file("$projectDir/src/main/resources/i18n/messages.pot")
    }
}
afterEvaluate {
    tasks {
        getByName("processResources", Copy::class) {
            dependsOn("compileKotlinJs")
            exclude("**/*.pot")
            doLast("Convert PO to JSON") {
                destinationDir.walkTopDown().filter {
                    it.isFile && it.extension == "po"
                }.forEach {
                    exec {
                        executable = getNodeJsBinaryExecutable()
                        args(
                            "$buildDir/js/node_modules/gettext.js/bin/po2json",
                            it.absolutePath,
                            "${it.parent}/${it.nameWithoutExtension}.json"
                        )
                        println("Converted ${it.name} to ${it.nameWithoutExtension}.json")
                    }
                    it.delete()
                }
            }
        }
        create("zip", Zip::class) {
            dependsOn("browserProductionWebpack")
            group = "package"
            destinationDirectory.set(file("$buildDir/libs"))
            val distribution =
                project.tasks.getByName("browserProductionWebpack", KotlinWebpack::class).destinationDirectory!!
            from(distribution) {
                include("*.*")
            }
            from(webDir)
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            inputs.files(distribution, webDir)
            outputs.file(archiveFile)
        }
    }
}
