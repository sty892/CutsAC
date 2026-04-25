import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission
import versioning.BuildConfig

plugins {
    `maven-publish`
    custac.`base-conventions`
    custac.`shadow-conventions`
    id("de.eldoria.plugin-yml.bukkit") version "0.8.0"
    id("xyz.jpenilla.run-paper") version "3.0.0-beta.1"
}

repositories {
    // 1. Fallback for non-exclusive deps (e.g. Maven Central deps)
    if (BuildConfig.mavenLocalOverride) mavenLocal()

    // 2. Exclusive Repositories (One HTTP request per dep)
    exclusive("https://repo.papermc.io/repository/maven-public/", { name = "papermc" }) {
        includeGroup("io.papermc.paper")
        includeGroup("net.md-5")
    }

    exclusive("https://libraries.minecraft.net", { mavenContent { releasesOnly() } }) {
        includeModule("com.mojang", "brigadier")
    }

    exclusive("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        includeGroup("me.clip")
    }

    exclusive("https://repo.grim.ac/snapshots") {
        includeGroup("ac.grim.grimac")
        includeGroup("com.github.retrooper")
    }

    exclusive("https://nexus.scarsz.me/content/repositories/releases", { mavenContent { releasesOnly() } }) {
        includeGroup("github.scarsz")
    }

    mavenCentral()
}


dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.placeholderapi)

    if (BuildConfig.shadePE) {
        implementation(libs.packetevents.spigot)
    } else {
        compileOnly(libs.packetevents.spigot)
    }
    implementation(libs.cloud.paper)
    implementation(libs.adventure.platform.bukkit)
    implementation(libs.grim.bukkit.internal)

    implementation(project(":common"))
    shadow(project(":common"))
}

bukkit {
    name = "CustAC"
    author = "CustAC"
    main = "ac.cust.custac.platform.bukkit.CustACBukkitLoaderPlugin"
    website = "https://custac.ac/"
    apiVersion = "1.13"
    foliaSupported = true

    if (!BuildConfig.shadePE) {
        depend = listOf("packetevents")
    }

    softDepend = listOf(
        "ProtocolLib",
        "ProtocolSupport",
        "Essentials",
        "ViaVersion",
        "ViaBackwards",
        "ViaRewind",
        "Geyser-Spigot",
        "floodgate",
        "FastLogin",
        "PlaceholderAPI",
    )

    permissions {
        register("custac.alerts") {
            description = "Receive alerts for violations"
            default = Permission.Default.OP
        }

        register("custac.alerts.enable-on-join") {
            description = "Enable alerts on join"
            default = Permission.Default.OP
        }

        register("custac.performance") {
            description = "Check performance metrics"
            default = Permission.Default.OP
        }

        register("custac.profile") {
            description = "Check user profile"
            default = Permission.Default.OP
        }

        register("custac.brand") {
            description = "Show client brands on join"
            default = Permission.Default.OP
        }

        register("custac.brand.enable-on-join") {
            description = "Enable showing client brands on join"
            default = Permission.Default.OP
        }

        register("custac.sendalert") {
            description = "Send cheater alert"
            default = Permission.Default.OP
        }

        register("custac.nosetback") {
            description = "Disable setback"
            default = Permission.Default.FALSE
        }

        register("custac.nomodifypacket") {
            description = "Disable modifying packets"
            default = Permission.Default.FALSE
        }

        register("custac.exempt") {
            description = "Exempt from all checks"
            default = Permission.Default.FALSE
        }

        register("custac.verbose") {
            description = "Receive verbose alerts for violations"
            default = Permission.Default.OP
        }

        register("custac.verbose.enable-on-join") {
            description =
                "Enable verbose alerts on join"
            default = Permission.Default.FALSE
        }

        register("custac.list") {
            description =
                "Shows lists of specific data"
            default = Permission.Default.FALSE
        }

        register("custac.admin") {
            description =
                "Use CustAC moderation commands"
            default = Permission.Default.OP
        }

    }
}

publishing.publications.create<MavenPublication>("maven") {
    artifact(tasks["shadowJar"])
}

tasks {
    runServer {
        val javaToolchains = project.extensions.getByType<JavaToolchainService>()
        javaLauncher = javaToolchains.launcherFor {
            vendor = JvmVendorSpec.JETBRAINS
            languageVersion = JavaLanguageVersion.of(25)
        }
        minecraftVersion("26.1.2")
    }

    shadowJar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
    }
}
