import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
    id("fabric-loom") version "1.8-SNAPSHOT"
    id("com.modrinth.minotaur") version "2.+"
}

group = property("maven_group")!!
version = property("mod_version")!!

repositories {
    // YamlConfiguration
    maven { url = uri("https://oss.sonatype.org/content/repositories/releases") }
    // Fabric Permissions API
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
    modImplementation("me.lucko:fabric-permissions-api:${property("fabric_permissions_api_version")}")

    include(implementation("org.bspfsystems:yamlconfiguration:${property("yaml_config_version")}")!!)
    include(implementation("net.dv8tion:JDA:${property("jda_version")}") {
        exclude(module="opus-java")
    })

    // YamlConfiguration
    include("org.yaml:snakeyaml:${property("snakeyaml_version")}")

    // JDA
    include("com.neovisionaries:nv-websocket-client:${property("nv_websocket_client_version")}")
    include("com.squareup.okhttp3:okhttp:${property("okhttp_version")}")
    include("com.squareup.okio:okio:${property("okio_version")}")
    include("net.sf.trove4j:core:${property(("trove4j_version"))}")
    include("org.apache.commons:commons-collections4:${property(("commons_collection_version"))}")
    include("com.fasterxml.jackson.core:jackson-core:${property("jackson_version")}")
    include("com.fasterxml.jackson.core:jackson-databind:${property("jackson_version")}")
    include("com.fasterxml.jackson.core:jackson-annotations:${property("jackson_version")}")
}

base {
    archivesName.set(property("archives_base_name")!! as String)
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(mutableMapOf(
            "version" to project.version,
            "minecraft_version" to project.extra["minecraft_version"]
        ))
    }

    filesMatching("config.yml") {
        filter<ReplaceTokens>("beginToken" to "\${", "endToken" to "}", "tokens" to mapOf(
            "version" to project.version
        ))
    }
}

tasks.withType<JavaCompile> {
    // Minecraft 1.18 (1.18-pre2) upwards uses Java 17.
    options.release.set(17)
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${base.archivesName.get()}" }
    }
}

modrinth {
    token = System.getenv("MODRINTH_TOKEN")
    projectId = property("modrinth_id")!! as String
    versionNumber = "[${property("minecraft_version")}] ${property("mod_name")} ${project.version}"
    versionType = property("release_type")!! as String
    uploadFile.set(tasks.jar)
    gameVersions.add(property("minecraft_version")!! as String)
    loaders.add("fabric")
    changelog = file(".github/changelog.md").readText()
    syncBodyFrom = file("README.md").readText()
    dependencies {
        required.version("fabric-api", property("fabric_version")!! as String)
    }
}

tasks.modrinth.configure {
    group = "publishing"
    onlyIf { System.getenv("MODRINTH_TOKEN").isNullOrBlank() }
    dependsOn(tasks.build, tasks.modrinthSyncBody)
}