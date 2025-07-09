dependencies {
    implementation ("ch.qos.logback:logback-classic")
    implementation ("org.flywaydb:flyway-database-postgresql:10.20.1")
    implementation ("org.postgresql:postgresql")

    implementation ("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-test")

    implementation ("org.projectlombok:lombok")
    annotationProcessor ("org.projectlombok:lombok")
}

tasks.test {
    useJUnitPlatform()
}