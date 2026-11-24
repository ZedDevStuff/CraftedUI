import earth.terrarium.cloche.api.target.FabricTarget
import earth.terrarium.cloche.api.target.NeoforgeTarget
import org.gradle.kotlin.dsl.withType
import java.util.Properties

plugins {
    //id("earth.terrarium.cloche") version "0.18.11-dust.20"
    id("earth.terrarium.cloche") version "0.19.13"
    id("maven-publish")
    id("me.modmuss50.mod-publish-plugin") version "2.2.1"
}

group = "dev.zeddevstuff"
version = "0.25.0"

repositories {
    mavenCentral()
    cloche {
        librariesMinecraft()
        main()
        mavenNeoforgedMeta()
        mavenNeoforged()
        mavenFabric()
        mavenForge()
    }
}

cloche {
    metadata {
        modId = "crafted_ui"
        name = rootProject.name
        description = "A simple declarative UI framework for Minecraft"
        license = "MIT"
        author("ZedDevStuff")
        url = "https://modrinth.com/mod/crafted_ui"
        issues = "https://github.com/ZedDevStuff/CraftedUI/issues"
        icon = "icon.png"
    }
    common {}

// region 1.21.11
    val common12111 = common("common:1.21.11") {
        accessWideners.from("src/common/1.21.11/main/crafted_ui.accesswidener")
    }
    neoforge("neoforge:1.21.11") {
        loaderVersion = "21.11.48"

        mixins.from("crafted_ui.mixins.json", "crafted_ui.neoforge.mixins.json")

        dependsOn(common12111)
    }
    fabric("fabric:1.21.11") {
        includedClient()

        dependencies {
            fabricApi("0.141.6")
        }

        mixins.from("crafted_ui.mixins.json", "crafted_ui.fabric.mixins.json")

        metadata {
            dependency("fabric-api", "0.141.6")
        }

        dependsOn(common12111)
    }
// endregion
// region 1.21.1
    val common1211 = common("common:1.21.1") {
        accessWideners.from("src/common/1.21.1/main/crafted_ui.accesswidener")
    }
    neoforge("neoforge:1.21.1") {
        loaderVersion = "21.1.219"

        mixins.from("crafted_ui.mixins.json", "crafted_ui.neoforge.mixins.json")

        dependsOn(common1211)
    }
    fabric("fabric:1.21.1") {
        includedClient()

        dependencies {
            fabricApi("0.116.8")
        }

        mixins.from("crafted_ui.mixins.json", "crafted_ui.fabric.mixins.json")

        metadata {
            dependency("fabric-api", "0.116.8")
        }

        dependsOn(common1211)
    }
// endregion
    targets.withType<FabricTarget>().configureEach {
        loaderVersion = "0.18.4"

        metadata {
            entrypoint("client", "dev.zeddevstuff.crafted.CraftedFabric")
        }
    }

    targets.configureEach {
        minecraftVersion = name.split(":").last()

        mappings {
            official()
        }

        dependencies {
            implementation("com.twelvemonkeys.imageio:imageio-core:3.15.3")
            implementation("com.twelvemonkeys.imageio:imageio-bmp:3.15.3")
            implementation("com.twelvemonkeys.imageio:imageio-jpeg:3.15.3")
            implementation("com.twelvemonkeys.imageio:imageio-tiff:3.15.3")
            implementation("com.twelvemonkeys.imageio:imageio-webp:3.15.3")
            include("com.twelvemonkeys.imageio:imageio-core:3.15.3")
            include("com.twelvemonkeys.imageio:imageio-bmp:3.15.3")
            include("com.twelvemonkeys.imageio:imageio-jpeg:3.15.3")
            include("com.twelvemonkeys.imageio:imageio-tiff:3.15.3")
            include("com.twelvemonkeys.imageio:imageio-webp:3.15.3")
        }

        metadata {
        }

        runs {
            client {
                properties.put("crafted.devMode", "true")
                properties.put("crafted.debug", "true")
            }
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                groupId = project.group.toString()
                artifactId = rootProject.name
                version = project.version.toString()
            }
        }
    }
}

cloche.targets.forEach {
    tasks.register<Jar>("${it.loaderName}${it.minecraftVersion.get().replace(":", "").replace(".", "")}JavadocJar") {
        group = "build"
        description = "Assembles a Javadoc JAR for the $name target."
        archiveBaseName.set("${rootProject.name}-$version-${it.loaderName}-${it.minecraftVersion.get()}")
        archiveVersion.set("")
        archiveClassifier.set("javadoc")
        from(tasks.named<Javadoc>("${it.loaderName}${it.minecraftVersion.get().replace(":", "").replace(".", "")}Javadoc").get().destinationDir)
    }
}

val envFile = rootProject.file(".env")
val envProps = Properties()
if (envFile.exists()) {
    envFile.inputStream().use { envProps.load(it) }
}

val javadocJarsTask = tasks.register("javadocJars") {
    group = "build"
    description = "Assembles Javadoc JARs for all targets."
    dependsOn(cloche.targets.map { tasks.named<Jar>("${it.loaderName}${it.minecraftVersion.get().replace(":", "").replace(".", "")}JavadocJar") })
}

publishMods {
    //dryRun.set(true)
    // TODO: Read changelog from file. Also support overrides
    changelog = ""
    type = BETA

    github("github") {
        accessToken = envProps.getProperty("GITHUB_TOKEN")
        repository = "ZedDevStuff/CraftedUI"
        commitish = "main"
        allowEmptyFiles = true

        val jars = cloche.targets.map {
            it.finalJar.get().archiveFile.get().asFile
        }.toMutableList()
        jars += cloche.targets.map {
            tasks.named<Jar>("${it.loaderName}${it.minecraftVersion.get().replace(":", "").replace(".", "")}JavadocJar").get().archiveFile.get().asFile
        }
        additionalFiles.from(jars)
    }

    cloche.targets.forEach {
        curseforge("curseforge-${it.loaderName}-${it.minecraftVersion.get()}") {
            projectId = "1823906"
            accessToken = envProps.getProperty("CURSEFORGE_TOKEN")
            changelogType = "markdown"
            minecraftVersions.add(it.minecraftVersion.get())
            file.set(it.finalJar.get().archiveFile)
//            val javadocJar = tasks.named<Jar>("${it.loaderName}${it.minecraftVersion.get().replace(".", "")}JavadocJar")
//            if(javadocJar.isPresent) {
//                additionalFiles.from(javadocJar.get().archiveFile)
//            }
            client = true
            server = false
            if(it is FabricTarget) {
                modLoaders.add("fabric")
                requires {
                    slug = "fabric-api"
                }
            } else if(it is NeoforgeTarget) {
                modLoaders.add("neoforge")
            }
        }
        modrinth("modrinth-${it.loaderName}-${it.minecraftVersion.get()}") {
            projectId = "uxXQaVR9"
            accessToken = envProps.getProperty("MODRINTH_TOKEN")
            minecraftVersions.add(it.minecraftVersion.get())
            file.set(it.finalJar.get().archiveFile)
//            val javadocJar = tasks.named<Jar>("${it.loaderName}${it.minecraftVersion.get().replace(".", "")}JavadocJar")
//            if(javadocJar.isPresent) {
//                additionalFiles.from(javadocJar.get().archiveFile)
//            }
            environment.set(CLIENT_ONLY)
            if(it is FabricTarget) {
                modLoaders.add("fabric")
                requires {
                    slug = "fabric-api"
                }
            } else if(it is NeoforgeTarget) {
                modLoaders.add("neoforge")
            }
        }
    }
}

tasks.named("publishGithub").configure {
    cloche.targets.forEach {
        dependsOn(tasks.named("${it.loaderName}${it.minecraftVersion.get().replace(":", "").replace(".", "")}IncludeJar"))
    }
    dependsOn(tasks.named("javadocJars"))
}
tasks.named("publishMods").configure {
    dependsOn(tasks.named("javadocJars"))
}