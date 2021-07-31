plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.14.0"
    signing
}

group = "com.osacky.tagger"
version = "0.1-SNAPSHOT"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        create("tagger") {
            id = "com.osacky.tagger"
            implementationClass = "com.osacky.scan.tag.ScanApiPlugin"
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
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
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
//                artifact(tasks["sourcesJar"])
//                artifact(tasks["javadocJar"])
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

java {
    withJavadocJar()
    withSourcesJar()
}

signing {
    isRequired = isReleaseBuild
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