plugins {
    kotlin("jvm") version "1.6.10"
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "1.3.5"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
}

group = "com.existencesmp"
version = "1.0.0"
description = "Mini tablist plugin for Existence SMP"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://jitpack.io")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    paperDevBundle("1.19-R0.1-SNAPSHOT")
}

val shade = configurations.create("shade")
shade.extendsFrom(configurations.implementation.get())

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

tasks {

    assemble {
        dependsOn(reobfJar)
    }

    javadoc {
        options.encoding = "UTF-8"
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
    
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
        }
    }
    
    create<Jar>("sourceJar") {
        archiveClassifier.set("source")
        from(sourceSets["main"].allSource)
    }

    jar {
        from (shade.map { if (it.isDirectory) it else zipTree(it) })
    }

}

bukkit {
    name = "Tablist"
    description = description
    main = "com.existencesmp.tablist.Tablist"
    version = version
    apiVersion = "1.19"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "existence-smp-tablist"
            from(components["java"])
        }
    }
}