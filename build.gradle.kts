plugins {
    java
    id("com.github.johnrengelman.shadow").version("7.1.2")
}

group = "pers.yufiria"
version = "1.0.1"

repositories {
    mavenCentral()
    maven("https://repo.crypticlib.com:8081/repository/maven-public/") {
        isAllowInsecureProtocol = true
    }
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("mysql:mysql-connector-java:8.0.29")
    compileOnly("pers.yufiria:KookMC:1.0.0")
    implementation("com.crypticlib:common:0.18.10")
    implementation("com.zaxxer:HikariCP:5.1.0")
}

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

tasks {
    compileJava {
        dependsOn(clean)
        options.encoding = "UTF-8"
    }
    val props = HashMap<String, String>()
    props["version"] = version.toString()
    processResources {
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
    shadowJar {
        relocate("crypticlib", "pers.yufiria.whitelist4kook.libs.crypticlib")
        relocate("com.zaxxer.hikari", "pers.yufiria.whitelist4kook.libs.hikari")
        dependencies {
            exclude(dependency("org.slf4j:slf4j-api"))
        }
        archiveFileName.set("${rootProject.name}-${version}.jar")
    }
    build {
        dependsOn(shadowJar)
    }
}
