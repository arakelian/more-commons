# more-commons

Small utility classes useful in a variety of projects. Philosophically, this library is intended to 
augment what is available in Guava or Apache Commons, which we assume is linked into most projects. 
We make every attempt to avoid reinventing the wheel.

## Installation

The library is available on Maven Central

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
    <version>1.6.6</version>
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
  compile 'com.arakelian:jackson-utils:1.6.6'
}
```

## Licence

Apache Version 2.0
