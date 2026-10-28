import earth.terrarium.cloche.api.target.FabricTarget
import earth.terrarium.cloche.api.target.NeoforgeTarget
import org.gradle.kotlin.dsl.withType

plugins {
    //id("earth.terrarium.cloche") version "0.18.11-dust.20"
    id("earth.terrarium.cloche") version "0.19.13"
    //id("com.gradleup.shadow") version "9.3.0"
}

group = "dev.zeddevstuff"
version = "1.0-SNAPSHOT"

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
    maven("https://maven.twelveiterations.com/repository/maven-public/") {
        name = "Twelve Iterations"
        content {
            includeGroup("net.blay09.mods")
        }
    }
}

cloche {
    metadata {
        modId = "crafted_ui"
        name = rootProject.name
        description = "Simpler UI for Minecraft"
        license = "MIT"
        author("ZedDevStuff")
        url = "https://modrinth.com/mod/crafted"
        issues = "https://github.com/ZedDevStuff/CraftedUI/issues"
        icon = "icon.png"
    }


    common {

    }

    val common1211 = common("common:1.21.1") {
        //dependsOn(baseCommon)
    }
    neoforge("neoforge:1.21.1") {
        loaderVersion = "21.1.219"

        dependsOn(common1211)
    }
    fabric("fabric:1.21.1") {
        includedClient()

        dependencies {
            fabricApi("0.116.8")
        }

        dependsOn(common1211)
    }

    targets.withType<FabricTarget>().configureEach {
        loaderVersion = "0.18.4"

        metadata {
            entrypoint("client", "dev.zeddevstuff.crafted.CraftedFabric")
        }
    }

    commonTargets.configureEach {
        dependencies {
        }
    }

    targets.configureEach {
        minecraftVersion = name.split(":").last()

        mappings {
            official()
        }

        metadata {
            mixins = files("crafted.mixins.json")
            components
        }

        runs {
            client {
                properties.put("crafted.devMode", "true")
                properties.put("crafted.debug", "true")
            }
        }
    }
}