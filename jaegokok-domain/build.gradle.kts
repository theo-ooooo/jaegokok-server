tasks.findByName("bootJar")?.enabled = false
tasks.findByName("jar")?.enabled = true

dependencies {
    implementation(project(":jaegokok-common"))
    implementation(project(":jaegokok-core"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.mockito:mockito-core")
}
