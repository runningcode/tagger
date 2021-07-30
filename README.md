# Tagger

This project makes it easier to tag builds from Gradle plugins without needing to check if the Gradle Enterprise plugin is applied.

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


You can use it as a plugin or a standard library dependency.