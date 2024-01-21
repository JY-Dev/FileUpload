import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.0" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
    id("org.jetbrains.kotlin.plugin.noarg") version "1.3.72" apply false
    kotlin("plugin.spring") version "1.9.20" apply false
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.jpa") version "1.9.20"
}

noArg {
    // JPA 엔티티에 대해 no-arg 생성자를 자동 생성합니다.
    annotation("javax.persistence.Entity")
}

java.sourceCompatibility = JavaVersion.VERSION_17

allprojects {
    group = "com.jydev"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.springframework.boot")


    dependencies {
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

}


