plugins {
    java
}

group = "com.github.yufiriamazenta"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.github.YufiriaMazenta:KookMC:2815a8017a")
    compileOnly("com.github.YufiriaMazenta:ParettiaLib:c146db9a75")
    compileOnly("mysql:mysql-connector-java:8.0.29")
    implementation("com.zaxxer:HikariCP:5.0.0")
}

group = "com.github.yufiriamazenta"
version = "1.0.0-dev1"
java.sourceCompatibility = JavaVersion.VERSION_17

tasks.withType(Jar::class.java) {
    project.configurations.getByName("implementation").isCanBeResolved = true
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(configurations.implementation.get().map { if (it.isDirectory) it else zipTree(it) })
}

tasks.withType(JavaCompile::class.java) {
    options.encoding = "UTF-8"
}


tasks {
    val props = HashMap<String, String>()
    props["version"] = version.toString()
    "processResources"(ProcessResources::class) {
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}