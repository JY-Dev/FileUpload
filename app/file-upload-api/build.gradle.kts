dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation(project(":domain:media"))
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
}