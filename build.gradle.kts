import java.io.ByteArrayOutputStream

plugins {
    // global version
    val kotlinVersion: String by System.getProperties()
    val dokkaVersion: String by System.getProperties()
    val ktlintVersion: String by System.getProperties()
    val springBootVersion: String by System.getProperties()
    val springDependencyManagementVersion: String by System.getProperties()

    idea
    `maven-publish`
    signing
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version springDependencyManagementVersion
    id("org.jetbrains.dokka") version dokkaVersion
    id("org.jlleitschuh.gradle.ktlint") version ktlintVersion
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
}

val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")

val mavenUsername = (findProperty("MAVEN_CENTER_USERNAME") ?: System.getenv("MAVEN_CENTER_USERNAME")) as String?
val mavenPassword = (findProperty("MAVEN_CENTER_PASSWORD") ?: System.getenv("MAVEN_CENTER_PASSWORD")) as String?

val latestTagVersionNumber = ByteArrayOutputStream().use {
    try {
        exec {
            commandLine("git", "rev-list", "--tags", "--max-count=1")
            standardOutput = it
        }
    } catch (e: Exception) {
        logger.error("Failed to get latest tag version number: [${e.message}]")
        return@use "unknown"
    }
    return@use it.toString().trim()
}

val latestTagVersion = ByteArrayOutputStream().use {
    try {
        exec {
            commandLine("git", "describe", "--tags", latestTagVersionNumber)
            standardOutput = it
        }
    } catch (e: Exception) {
        logger.error("Failed to get latest tag version: [${e.message}]")
        return@use "unknown"
    }
    return@use it.toString().trim()
}

val hutoolVersion: String by project
val querydslVersion: String by project

group = "me.zhengjin"
// 使用最新的tag名称作为版本号
version = latestTagVersion

val isReleaseVersion = !version.toString().endsWith("SNAPSHOT")
println("当前构建产物: [$group:${project.name}:$version]")

/**
 * 源码JDK版本
 */
java.sourceCompatibility = JavaVersion.VERSION_1_8
/**
 * 编译后字节码可运行环境的版本
 */
java.targetCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenLocal()
    mavenCentral()
//    maven {
//        url = releasesRepoUrl
//        credentials {
//            username = mavenUsername
//            password = mavenPassword
//        }
//    }
//    maven {
//        url = snapshotsRepoUrl
//        credentials {
//            username = mavenUsername
//            password = mavenPassword
//        }
//    }
}

dependencies {
    kapt("com.querydsl:querydsl-apt:$querydslVersion:jpa")
    api("com.querydsl:querydsl-jpa:$querydslVersion")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    // Hutool
    api("cn.hutool:hutool-core:$hutoolVersion")
    api(kotlin("reflect"))
    api(kotlin("stdlib-jdk8"))
    testCompileOnly("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set(project.name)
                description.set(project.name)
                url.set("https://github.com/zhengjin-me/common-core")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://zhengjin.me/licenses/MIT-License.txt")
                    }
                }
                developers {
                    developer {
                        id.set("fangzhengjin")
                        name.set("fangzhengjin")
                        email.set("fangzhengjin@gmail.com")
                    }
                }
                scm {
                    url.set("https://github.com/zhengjin-me/common-core")
                }
                versionMapping {
                    usage("java-api") {
                        fromResolutionOf("runtimeClasspath")
                    }
                    usage("java-runtime") {
                        fromResolutionResult()
                    }
                }
            }
        }
    }
    repositories {
        maven {
            url = if (isReleaseVersion) releasesRepoUrl else snapshotsRepoUrl
            credentials {
                username = mavenUsername
                password = mavenPassword
            }
        }
    }
}

signing {
    setRequired({ isReleaseVersion && gradle.taskGraph.hasTask("publish") })
    println("产物签名已启用")
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["mavenJava"])
}

tasks {
    bootJar {
        enabled = false
    }

    jar {
        enabled = true
        classifier = ""
    }

    /**
     * 定义那些注解修饰的类自动开放
     */
    allOpen {
        annotations(
            "javax.persistence.Entity",
            "javax.persistence.MappedSuperclass",
            "javax.persistence.Embeddable"
        )
    }

    test {
        useJUnitPlatform()
    }

    /**
     * kotlin编译
     */
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }
}
