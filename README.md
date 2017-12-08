# SimpleJDBC

[![](https://jitpack.io/v/SMontiel/SimpleJDBC.svg)](https://jitpack.io/#SMontiel/SimpleJDBC)

Java library for simple SQL querying (through JDBC) :slightly_smiling_face:

## Download

You can download a jar from GitHub's [releases page](https://github.com/SMontiel/SimpleJDBC/releases).

Or use Gradle:

#### **Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

#### **Step 2.** Add the dependency

```
dependencies {
  compile 'com.github.SMontiel:SimpleJDBC:0.0.1'
}
```