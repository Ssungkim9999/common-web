<img src="https://capsule-render.vercel.app/api?type=slice&color=auto&height=150&section=header&text=Common-Web&fontSize=70" />

# Common-Web
### Web Service Helper for Java
Common-Web is a library that provides a collection of frequently used methods when creating web services. It aims to simplify the process of building and maintaining web services by providing a convenient set of tools that developers can use.

# Getting Started
### Using Maven
To use Web Service Helper for Java with Maven, you can add the following dependency to your **pom.xml** file:
```xml
<dependency>
  <groupId>io.github.Ssungkim9999</groupId>
  <artifactId>common-web</artifactId>
  <version>${common-web-version}</version>
</dependency>
```
### Using Gradle
To use Common-Web with Gradle, you can add the following dependency to your **build.gradle** file:
```groovy
Gradle
dependencies {
    implementation group: 'io.github.Ssungkim9999', name: 'common-web', version: '1.0.3'
}
Gradle (short)
dependencies {
    implementation 'io.github.Ssungkim9999:common-web:1.0.3'
}
Gradle (Kotlin)
dependencies {
    implementation("io.github.Ssungkim9999:common-web:1.0.3")
}
```

# Features
Common-Web provides the following features:

* getJson: This method gets data from a file in JsonObject format, given as a parameter, and returns it.
* parsingFromFile: This method reads all the data in a file, regardless of its format, and returns it as a list of exact values of all cell data.
* returnPage: This method moves to a view page, given as a parameter, including all parameter parameters.

And Common-Web have more other methods in addition to above methods.

# Usage
To use Common-Web, simply import the library into your Java project and call the methods you need. For example:
```java
import com.example.webservicehelper.*;

JsonObject json = JsonService.getJson("data.json");
List<String> data = FileService.parsingFromFile("data", "xlsx");
ViewService.returnPage("layoutPage.jsp", "viewPage.jsp", parameterMap, caller.class);
```

# License
Common-Web is licensed under the Apache License. Please see the LICENSE file for more information.
