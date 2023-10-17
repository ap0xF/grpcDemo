plugins {
    id("java")
    id("application")
    id("com.google.protobuf") version "0.9.4"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        url = uri("https://maven-central.storage-download.googleapis.com/maven2/")
    }
    mavenCentral()
    mavenLocal()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


val grpcVersion = "1.58.0" // CURRENT_GRPC_VERSION
val protobufVersion = "3.24.0"
val protocVersion = protobufVersion

dependencies {
    implementation("io.grpc:grpc-netty-shaded:1.54.0")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-services:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2") // added for performacne testing of json vs proto

    compileOnly("org.apache.tomcat:annotations-api:6.0.53")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

// this guy is responsible for reading the proto file and generating the java class required according to the protofile.
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${protocVersion}"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${grpcVersion}"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
            }
        }
    }
}

sourceSets {
    main{
        proto{
            srcDir("${projectDir}/src/main/proto")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}


tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}