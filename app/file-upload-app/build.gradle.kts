dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    api(project(":domain:media"))
    runtimeOnly("com.h2database:h2")
}