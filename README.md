# more-commons
[![build](https://github.com/arakelian/more-commons/actions/workflows/build.yml/badge.svg)](https://github.com/arakelian/more-commons/actions/workflows/build.yml)
[![version](https://img.shields.io/maven-metadata/v.svg?label=maven-central&metadataUrl=https://repo1.maven.org/maven2/com/arakelian/more-commons/maven-metadata.xml)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.arakelian%22%20AND%20a%3A%22more-commons%22)

Small utility classes useful in a variety of projects. Philosophically, this library is intended to 
augment what is available in Guava or Apache Commons, which we assume is linked into most projects. 
We make every attempt to avoid reinventing the wheel.

## Requirements

* Compatible with Java 8+

## Installation

The library is available on [Maven Central](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.arakelian%22%20AND%20a%3A%22more-commons%22).

### Maven

Add the following to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>central</id>
        <name>Central Repository</name>
        <url>http://repo.maven.apache.org/maven2</url>
        <releases>
            <enabled>true</enabled>
        </releases>
    </repository>
</repositories>

...

<dependency>
    <groupId>com.arakelian</groupId>
    <artifactId>more-commons</artifactId>
    <version>3.0.0</version>
    <scope>compile</scope>
</dependency>
```

### Gradle

Add the following to your `build.gradle`:

```groovy
repositories {
  mavenCentral()
}

dependencies {
  compile 'com.arakelian:more-commons:3.0.0'
}
```

## Licence

Apache Version 2.0
