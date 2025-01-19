plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

group = "org.pom"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // SLF4J (required for Lombok's logging annotations)
    implementation("org.slf4j:slf4j-api:2.0.0") // Adjust version if necessary
    runtimeOnly("org.slf4j:slf4j-simple:2.0.0") // or any other binding you need

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Lombok dependency
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // SLF4J (required for Lombok's logging annotations)
    implementation("org.slf4j:slf4j-api:2.0.0") // Adjust version if necessary
    runtimeOnly("org.slf4j:slf4j-simple:2.0.0") // or any other binding you need

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.0.1")

    implementation("com.opencsv:opencsv:3.7")
    implementation("org.yaml:snakeyaml:2.0")

    // For tests, if Lombok is used in test code
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

    // Mockito dependencies
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
}

tasks.test {
    useJUnitPlatform()
}