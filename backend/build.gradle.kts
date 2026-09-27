plugins {
    id 'org.springframework.boot' version '3.2.5'
    id 'io.spring.dependency-management' version '1.1.4'
    id 'org.jetbrains.kotlin.jvm' version '1.9.23'
    id 'org.jetbrains.kotlin.plugin.spring' version '1.9.23'
    id 'org.jetbrains.kotlin.plugin.jpa' version '1.9.23'
    id 'org.flywaydb.flyway' version '10.13.0'
}

group = 'com.mindtrainer'
version = '1.0.0-SNAPSHOT'

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom "org.testcontainers:testcontainers-bom:1.19.8"
    }
}

dependencies {
    // Spring Boot
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
    implementation 'org.springframework.boot:spring-boot-starter-oauth2-resource-server'
    implementation 'org.springframework.boot:spring-boot-starter-cache'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-mail'

    // Kotlin
    implementation 'org.jetbrains.kotlin:kotlin-reflect'
    implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk8'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.8.0'
    implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3'

    // Database
    runtimeOnly 'org.postgresql:postgresql'
    implementation 'org.flywaydb:flyway-core'
    implementation 'com.zaxxer:HikariCP:5.1.0'

    // Redis
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'
    implementation 'io.lettuce:lettuce-core:6.3.2.RELEASE'

    // JWT
    implementation 'io.jsonwebtoken:jjwt-api:0.12.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.5'

    // OpenAPI / Swagger
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0'

    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
    testImplementation 'org.testcontainers:junit-jupiter:1.19.8'
    testImplementation 'org.testcontainers:postgresql:1.19.8'
    testImplementation 'org.testcontainers:redis:1.19.8'
    testImplementation 'io.rest-assured:rest-assured:5.5.0'
    testImplementation 'org.mockito:mockito-kotlin:5.12.0'
    testImplementation 'org.jetbrains.kotlin:kotlin-test-junit5:1.9.23'
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).configureEach {
    kotlinOptions {
        freeCompilerArgs = ['-Xjsr305=strict', '-Xopt-in=kotlin.RequiresOptIn']
        jvmTarget = '21'
    }
}

tasks.named('test') {
    useJUnitPlatform()
    testLogging {
        events 'passed', 'skipped', 'failed'
    }
}