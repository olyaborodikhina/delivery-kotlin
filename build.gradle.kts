plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.14"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

val grpcVersion = "1.57.2"
val protobufVersion = "3.24.3"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.kafka:spring-kafka")

	implementation("org.postgresql:postgresql")

	implementation("io.grpc:grpc-netty-shaded:${grpcVersion}")
	implementation("io.grpc:grpc-protobuf:${grpcVersion}")
	implementation("io.grpc:grpc-stub:${grpcVersion}")
	implementation("com.google.protobuf:protobuf-java:4.28.2")
	implementation("com.google.protobuf:protobuf-java-util:${protobufVersion}")
	implementation("javax.annotation:javax.annotation-api:1.3.2")

	implementation("com.fasterxml.jackson.core:jackson-databind")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")
	implementation("io.swagger.core.v3:swagger-annotations:2.2.14")
	implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")

	implementation("org.slf4j:slf4j-api:2.0.13")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
	testImplementation("org.assertj:assertj-core:3.24.2")
	testImplementation("org.testcontainers:postgresql:1.21.3")
	testImplementation("org.testcontainers:junit-jupiter:1.21.3")
	testImplementation("org.reflections:reflections:0.10.2")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
