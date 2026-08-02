plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "2.25.0"
    id("dev.hydraulic.conveyor") version "2.0"
}

group = "fr.eshome"
version = "1.2.3"

repositories {
    mavenCentral()
}

val junitVersion = "5.12.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of("21")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("fr.eshome.watersort")
    mainClass.set("fr.eshome.watersort.GameApplication")
}

javafx {
    version = "21.0.12"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("com.google.code.gson:gson:2.14.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {
    imageZip.set(layout.buildDirectory.file("/distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--no-header-files", "--no-man-pages", "--add-modules", "jdk.random"))
    launcher {
        name = "watersort"
    }
    jpackage {
        var currentOs = org.gradle.internal.os.OperatingSystem.current()
        var imgType = if (currentOs.isWindows) "ico" else "png"
        installerType = "deb"
        installerOptions = listOf(
            "--linux-deb-maintainer", "eshome.fr@gmail.com",
            "--linux-package-name", "watersort",
            "--linux-app-category", "Game;LogicGame;",
            "--vendor", "ESHome33",
            "--description", "A water sort puzzle game",
            "--copyright", "Copyright © 2026 ESHome",
            "--linux-shortcut",                                // adds a .desktop entry / menu shortcut
            "--icon", "src/main/resources/fr/eshome/watersort/watersort.$imgType",
            "--verbose",
        )
        resourceDir = file("packaging/linux")

    }
}

tasks.register("injectMetainfo") {
    dependsOn("jpackage")
    doLast {
        val debDir = layout.buildDirectory.dir("jpackage").get().asFile
        val debFile = debDir.listFiles { f -> f.name.endsWith(".deb") }
            ?.singleOrNull()
            ?: throw GradleException("Expected exactly one .deb file in $debDir")

        val extractDir = layout.buildDirectory.dir("deb-extract").get().asFile
        delete(extractDir)
        mkdir(extractDir)

        exec { commandLine("dpkg-deb", "-R", debFile.absolutePath, extractDir.absolutePath) }

        // metainfo
        val metainfoDestDir = extractDir.resolve("usr/share/metainfo")
        metainfoDestDir.mkdirs()
        copy {
            from("packaging/metainfo/fr.eshome.watersort.metainfo.xml")
            into(metainfoDestDir)
        }

        // themed icon — adjust size to match your actual source PNG dimensions
        val iconDestDir = extractDir.resolve("usr/share/icons/hicolor/256x256/apps")
        iconDestDir.mkdirs()
        copy {
            from("src/main/resources/fr/eshome/watersort/watersort.png")
            into(iconDestDir)
            rename { "watersort.png" }
        }

        exec {
            commandLine(
                "chmod", "755",
                extractDir.resolve("DEBIAN/postinst").absolutePath,
                extractDir.resolve("DEBIAN/preinst").absolutePath
            )
        }

        // fix Icon= in the desktop file to use the themed name instead of absolute path
        val desktopFile = extractDir.resolve("opt/watersort/lib/watersort-watersort.desktop")
        val content = desktopFile.readText().replace(
            Regex("^Icon=.*$", RegexOption.MULTILINE),
            "Icon=watersort"
        )
        desktopFile.writeText(content)

        delete(debFile)
        exec { commandLine("dpkg-deb", "-b", extractDir.absolutePath, debFile.absolutePath) }
    }
}

tasks.named("jpackage") {
    finalizedBy("injectMetainfo")
}
