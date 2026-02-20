plugins {
    id("fabric-loom") version "1.8-SNAPSHOT"
    id("maven-publish")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

repositories {
    mavenCentral()
    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/releases/")
    }
}

dependencies {
    // Fabric
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
    
    // Mod Menu integration (optional)
    modCompileOnly("com.terraformersmc:modmenu:13.0.3")
    modRuntimeOnly("com.terraformersmc:modmenu:13.0.3")
}

tasks.processResources {
    inputs.property("version", project.version)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

val targetJavaVersion = 21
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    withSourcesJar()
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.property("archives_base_name")}" }
    }
}
