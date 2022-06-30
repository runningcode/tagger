# Tagger

This project makes it easier for plugin authors and buildscript authors to tag builds without needing to check if the Gradle Enterprise plugin is applied.

## Buildscript authors

To use as a buildscript author, add the plugin to your build.gradle plugins block:
```
plugins {
  id("com.osacky.tagger") version "0.3-SNAPSHOT"
}
```

Then call the corresponding method on the extension:

To tag a build:
```groovy
taggerApi {
    tag("foo")
}
```

To add a value to a build:
```groovy
taggerApi {
    value("key", "value")
}
```

To add a link to a build:
```groovy
taggerApi {
    link("Gradle", "https://gradle.com")
}
```

## Plugin authors

First add tagger as a compile dependency:
```
dependencies {
  implementation("com.osacky.tagger:tagger-lib:0.3-SNAPSHOT")
}
```

To tag a build:
```groovy
new ScanApi(project).tag("foo")
```

To add a value to a build:
```groovy
new ScanApi(project).value("key", "value")
```

To add a link to a build:
```groovy
new ScanApi(project).link("Gradle", "https://gradle.com")
```
