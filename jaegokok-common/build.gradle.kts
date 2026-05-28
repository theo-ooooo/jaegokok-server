tasks.findByName("bootJar")?.enabled = false
tasks.findByName("jar")?.enabled = true

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("jakarta.validation:jakarta.validation-api")
}
