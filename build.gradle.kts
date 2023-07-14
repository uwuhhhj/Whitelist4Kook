plugins {
    id 'java'
}

group = 'com.github.yufiriamazenta'
version = '1.0.0'

repositories {
    mavenCentral()
    maven {
        url = "https://jitpack.io"
    }
    maven {
        name = "spigotmc-repo"
        url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/"
    }
    maven {
        name = "sonatype"
        url = "https://oss.sonatype.org/content/groups/public/"
    }
}

dependencies {
    compileOnly "org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT"
    compileOnly "com.github.YufiriaMazenta:KookMC:2815a8017a"
    compileOnly "com.github.YufiriaMazenta:ParettiaLib:c146db9a75"
    compileOnly "mysql:mysql-connector-java:8.0.29"
    implementation "com.zaxxer:HikariCP:5.0.0"
}

def targetJavaVersion = 17
java {
    def javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

tasks.withType(JavaCompile).configureEach {
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible()) {
        options.release = targetJavaVersion
    }
}

processResources {
    def props = [version: version]
    inputs.properties props
    filteringCharset 'UTF-8'
    filesMatching('plugin.yml') {
        expand props
    }
}
