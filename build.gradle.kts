import earth.terrarium.cloche.api.target.FabricTarget
import org.gradle.kotlin.dsl.withType

plugins {
    //id("earth.terrarium.cloche") version "0.18.11-dust.20"
    id("earth.terrarium.cloche") version "0.19.13"
    id("maven-publish")
}

group = "dev.zeddevstuff"
version = "0.10.4"

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

    common {
    }

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