import com.epages.restdocs.apispec.gradle.OpenApi3Extension
import com.epages.restdocs.apispec.gradle.OpenApi3Task
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"

    id("com.epages.restdocs-api-spec") version "0.19.4"
    id("org.asciidoctor.jvm.convert") version "4.0.2"
}

group = "com.chpark"
version = "0.0.1-SNAPSHOT"
description = "restaurant"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.postgresql:r2dbc-postgresql")
    // implementation("org.postgresql:r2dbc-postgresql:1.0.7.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-logging")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.postgresql:postgresql:42.7.7")

    implementation("io.r2dbc:r2dbc-h2")
    runtimeOnly("com.h2database:h2")

    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.8.10")

    implementation("io.jsonwebtoken:jjwt-api:0.12.7")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.7")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.7")

    implementation("org.springframework.boot:spring-boot-starter-security")

    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.projectreactor:reactor-test")

    testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")
    testImplementation("org.springframework.restdocs:spring-restdocs-core")
    testImplementation("com.epages:restdocs-api-spec:0.19.4")
    testImplementation("com.epages:restdocs-api-spec-webtestclient:0.19.4")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    outputs.dir("build/generated-snippets")

    /*doFirst {
        val dir = layout.buildDirectory.dir("resources/main/static/docs").get().asFile

        if (!dir.exists()) {
            dir.mkdirs()
        }
    }*/
}

tasks.withType<AsciidoctorTask> {
    dependsOn(tasks.test)

    inputs.dir("build/generated-snippets")

    attributes(
        mapOf(
            "snippets" to layout.buildDirectory.dir("generated-snippets").get().asFile.absolutePath
        )
    )

    setOutputDir(layout.buildDirectory.dir("docs/asciidoc").get().asFile)
}

tasks.register<Copy>("copyRestDocs") {
    dependsOn(tasks.asciidoctor)

    from(layout.buildDirectory.dir("docs/asciidoc"))
    into(layout.buildDirectory.dir("resources/main/static/docs"))
}

configure<OpenApi3Extension> {
    setServer("http://localhost:8081")
    title = "Restaurant API"
    description = "실시간 좌석/타임 슬롯 예약 + 웨이팅 시스템 API"
    version = "0.1.0"
    format = "json"

    // outputDirectory = layout.buildDirectory.dir("resources/main/static/docs").get().asFile.path

    outputDirectory = layout.buildDirectory
        .dir("openapi")
        .get()
        .asFile
        .path
}

@Suppress("UNCHECKED_CAST")
tasks.withType<OpenApi3Task> {

    dependsOn(tasks.test)

    doLast {
        val docsDir = layout.buildDirectory.dir("openapi").get().asFile

        val openApiFile = docsDir.resolve("openapi3.json")
        if (!openApiFile.exists()) {
            return@doLast
        }

        val slurper = JsonSlurper()
        val root = slurper.parse(openApiFile) as MutableMap<String, Any?>

        val components = (root["components"] as? MutableMap<String, Any?>)
            ?: mutableMapOf<String, Any?>().also {
                root["components"] = it
            }

        val securitySchemes = (components["securitySchemes"] as? MutableMap<String, Any?>)
            ?: mutableMapOf<String, Any?>().also {
                components["securitySchemes"] = it
            }

        if (!securitySchemes.containsKey("bearerAuth")) {
            securitySchemes["bearerAuth"] = mapOf(
                "type" to "http",
                "scheme" to "bearer",
                "bearerFormat" to "JWT"
            )
        }

        val security = (root["security"] as? MutableList<Map<String, Any?>>)
            ?: mutableListOf<Map<String, Any?>>().also {
                root["security"] = it
            }

        if (security.none { it.containsKey("bearerAuth") }) {
            security.add(mapOf("bearerAuth" to emptyList<Any>()))
        }

        val json = JsonOutput.prettyPrint(JsonOutput.toJson(root))

        openApiFile.writeText(json)
    }
}

tasks.named<BootJar>("bootJar") {
    dependsOn("test", "openapi3", "asciidoctor")

    from(layout.buildDirectory.dir("docs/asciidoc")) {
        into("BOOT-INF/classes/static/docs")
    }

    from(layout.buildDirectory.dir("openapi")) {
        into("BOOT-INF/classes/static/docs")
    }
}