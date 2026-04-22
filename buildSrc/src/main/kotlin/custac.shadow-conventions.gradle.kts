import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import versioning.BuildConfig

plugins {
    id("com.gradleup.shadow")
}

tasks.named<ShadowJar>("shadowJar") {
    minimize()
    archiveFileName = "${rootProject.name}-${project.name}-${rootProject.version}.jar"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    if (BuildConfig.relocate) {
        if (BuildConfig.shadePE) {
            relocate("io.github.retrooper.packetevents", "ac.cust.custac.shaded.io.github.retrooper.packetevents")
            relocate("com.github.retrooper.packetevents", "ac.cust.custac.shaded.com.github.retrooper.packetevents")
            relocate("net.kyori", "ac.cust.custac.shaded.kyori") // use PE's built-in adventure instead when not shading PE
        }
        relocate("club.minnced", "ac.cust.custac.shaded.discord-webhooks")
        relocate("org.slf4j", "ac.cust.custac.shaded.slf4j") // Required by discord-webhooks
        relocate("github.scarsz.configuralize", "ac.cust.custac.shaded.configuralize")
        relocate("com.github.puregero", "ac.cust.custac.shaded.com.github.puregero")
        relocate("com.google.code.gson", "ac.cust.custac.shaded.gson")
        relocate("alexh", "ac.cust.custac.shaded.maps")
        relocate("it.unimi.dsi.fastutil", "ac.cust.custac.shaded.fastutil")
        relocate("okhttp3", "ac.cust.custac.shaded.okhttp3")
        relocate("okio", "ac.cust.custac.shaded.okio")
        relocate("org.yaml.snakeyaml", "ac.cust.custac.shaded.snakeyaml")
        relocate("org.json", "ac.cust.custac.shaded.json")
        relocate("org.intellij", "ac.cust.custac.shaded.intellij")
        relocate("org.jetbrains", "ac.cust.custac.shaded.jetbrains")
        relocate("org.incendo", "ac.cust.custac.shaded.incendo")
        relocate("io.leangen.geantyref", "ac.cust.custac.shaded.geantyref") // Required by cloud
        relocate("com.zaxxer", "ac.cust.custac.shaded.zaxxer") // Database history
    }
    mergeServiceFiles()
}

tasks.named("assemble") {
    dependsOn(tasks.named("shadowJar"))
}
