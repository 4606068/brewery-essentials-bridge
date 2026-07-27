plugins {
    java
}

group = "org.example"
version = "1.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.jsinco.dev/releases")
    maven("https://repo.essentialsx.net/releases/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("com.dre.brewery:BreweryX:3.7.0")
    compileOnly("net.essentialsx:EssentialsX:2.21.0")
}

configurations.all {
    resolutionStrategy.capabilitiesResolution.withCapability("org.spigotmc:spigot-api") {
        selectHighestVersion()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

