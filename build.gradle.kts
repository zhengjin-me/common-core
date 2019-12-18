import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val versionCode: String by extra
val fastJsonVersion: String by extra
val swaggerVersion: String by extra
val groupName: String by extra

plugins {
    val kotlinVersion: String by System.getProperties()
    val springBootVersion: String by System.getProperties()

    kotlin("plugin.jpa") version kotlinVersion
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("com.jfrog.bintray") version "1.8.4"
}

apply {
    from("maven.gradle.kts")
    from("travis.gradle.kts")
    from("bintray.gradle")
}

group = groupName
version = versionCode
java.sourceCompatibility = JavaVersion.VERSION_1_8

tasks {
    bootJar {
        enabled = false
    }
    jar {
        enabled = true
    }
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }
}

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public/")
    mavenCentral()
}

dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-data-jpa")
    compileOnly("org.springframework.boot:spring-boot-starter-amqp")
    //fastjson
    api("com.alibaba:fastjson:$fastJsonVersion")
    //Swagger2
    compileOnly("io.springfox:springfox-swagger2:$swaggerVersion")
    compileOnly("io.springfox:springfox-swagger-ui:$swaggerVersion")
    compileOnly("io.springfox:springfox-bean-validators:$swaggerVersion")
    api(kotlin("reflect"))
    api(kotlin("stdlib-jdk8"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}