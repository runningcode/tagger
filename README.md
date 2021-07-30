# Tagger

This project makes it easier for plugin authors and build authors to tag builds without needing to check if the Gradle Enterprise plugin is applied.

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
// TODO add maven coordinates and example for both