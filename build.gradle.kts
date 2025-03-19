plugins {
    kotlin("jvm") version "2.1.10"
    antlr
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    antlr("org.antlr:antlr4:4.5") // use ANTLR version 4
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

//antlr {
//    // 指定语法文件目录
//    sourceSets["main"].java.srcDirs("src/main/antlr")
//    outputDirectory.set(file("build/generated-src/antlr/main"))
//}

tasks.generateGrammarSource {
    dependsOn(tasks.compileKotlin)
}