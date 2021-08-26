plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.14.0"
    signing
}

group = "com.osacky.tagger"
version = "0.2"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        create("tagger") {
            id = "com.osacky.tagger"
            displayName = "Tagger plugin for Gradle Enterprise"
            implementationClass = "com.osacky.tagger.ScanApiPlugin"
        }
    }
}

val isReleaseBuild : Boolean = !version.toString().endsWith("SNAPSHOT")

val sonatypeUsername : String? by project
val sonatypePassword : String? by project

pluginBundle {
    tags = listOf("buildscan", "tags")

    website = "https://github.com/runningcode/tagger"
    vcsUrl = "https://github.com/runningcode/tagger"

    description = "An easy way to tag build scans for Gradle plugin authors."

    mavenCoordinates {
        artifactId = "tagger-plugin"
        groupId = project.group.toString()
    }
}

publishing {
    repositories {
        maven {
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (isReleaseBuild) releasesRepoUrl else snapshotsRepoUrl
            credentials {
                username = sonatypeUsername
                password = sonatypePassword
            }
        }
    }

    publications {
        afterEvaluate {
            named<MavenPublication>("pluginMaven") {
                signing.sign(this)
                pom.configurePom("tagger")
            }
            named<MavenPublication>("taggerPluginMarkerMaven") {
                signing.sign(this)
                pom.configurePom("tagger")
            }
        }
    }
}

dependencies {
    compileOnly("com.gradle:gradle-enterprise-gradle-plugin:3.6.4")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.apache.commons:commons-io:1.3.2")
    testImplementation("com.google.truth:truth:1.0.1")
}

tasks.withType<ValidatePlugins>().configureEach {
    failOnWarning.set(true)
    enableStricterValidation.set(true)
}

java {
    withJavadocJar()
    withSourcesJar()
}

signing {
    isRequired = isReleaseBuild
}

tasks.withType<JavaCompile>().configureEach {
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(8))
    })
}

val java8Int = tasks.register<Test>("java8IntegrationTest") {
    group = "verification"
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(8))
    })
}
val java11Int = tasks.register<Test>("java11IntegrationTest") {
    group = "verification"
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(11))
    })
}

tasks.named("check").configure {
    dependsOn(java8Int, java11Int)
}

fun MavenPom.configurePom(pluginName: String) {
    name.set(pluginName)
    description.set("An easy way to tag build scans for Gradle plugin authors.")
    url.set("https://github.com/runningcode/tagger")
    licenses {
        license {
            name.set("The Apache License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
        }
    }
    developers {
        developer {
            id.set("runningcode")
            name.set("Nelson Osacky")
        }
    }
    scm {
        connection.set("scm:git:git://github.com/runningcode/tagger.git")
        developerConnection.set("scm:git:ssh://github.com/runningcode/tagger.git")
        url.set("https://github.com/runningcode/tagger")
    }
}