plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.14.0"
}

group = "com.osacky.tagger"
version = "0.0.1"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        create("tagger") {
            id = "com.osacky.tagger"
            displayName = "Buildscan Tagging API"
            description = "An easy way to tag Buildscans from Gradle Plugins"
            implementationClass = "com.osacky.scan.tag.ScanApiPlugin"
        }
    }
}

pluginBundle {
    tags = listOf("buildscan", "tags")

    mavenCoordinates {
        artifactId = "tagger-plugin"
        groupId = project.group.toString()
    }
}

tasks.withType(Test::class.java).configureEach {
    testLogging {
        events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
        )
    }
    javaLauncher.set(javaToolchains.launcherFor {
        //Use 15 for multi-line strings
        languageVersion.set(JavaLanguageVersion.of(15))
    })
}

tasks.named("compileTestJava", JavaCompile::class.java).configure {
    javaCompiler.set(javaToolchains.compilerFor {
        //Use 15 for multi-line strings
        languageVersion.set(JavaLanguageVersion.of(15))
    })
}

dependencies {
    compileOnly("com.gradle:gradle-enterprise-gradle-plugin:3.6.3")
    compileOnly(gradleApi())

    testImplementation("junit:junit:4.13.2")
    testImplementation(gradleTestKit())
    testImplementation("org.apache.commons:commons-io:1.3.2")
    testImplementation("com.google.truth:truth:1.0.1")
    testImplementation("org.codehaus.groovy:groovy-all:2.4.15")
}

tasks.withType<ValidatePlugins>().configureEach {
    failOnWarning.set(true)
    enableStricterValidation.set(true)
}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

tasks.register<Jar>("javadocJar") {
    from(tasks.javadoc)
    archiveClassifier.set("javadoc")
}