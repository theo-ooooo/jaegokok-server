plugins {
    id("org.springframework.boot")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
}
tasks.named<Jar>("jar") {
    enabled = false
}

dependencies {
    implementation(project(":jaegokok-common"))
    implementation(project(":jaegokok-core"))
    implementation(project(":jaegokok-domain"))
    implementation(project(":jaegokok-infra"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // springdoc-openapi
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")

    runtimeOnly("com.mysql:mysql-connector-j")
}
