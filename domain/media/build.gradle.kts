plugins {
    id("org.jetbrains.kotlin.plugin.noarg")
}

noArg {
    annotation("jakarta.persistence.Entity")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}