# intellij-plugin-java-builder-pattern

<b>Java Builder Pattern</b> plugin for Intellij IDEA with two types of builder :
- classic builder
- fluent builder with interfaces

Available on Jetbrains repository: https://plugins.jetbrains.com/plugin/13749-java-builder-pattern

## Setup

Open File > Settings > Plugins. The easy way to find it is by searching `java fluent builder pattern` and to install <b>Java Builder pattern</b>:

![alt text](https://github.com/sbouclier/intellij-plugin-java-builder-pattern/blob/master/assets/images/install.png?raw=true "Install")

## Quickstart

Create a class with members, in this example :

```java
public class Employee {
    private String socialSecurityNumber;    // required
    private String firstName;               // required
    private String lastName;                // required
    private LocalDate birthday;             // optional
    private int salary;                     // optional

}
```

Set your cursor inside the class where you want to generate the builder code, then right click and open 'Generate' menu :

![alt text](https://github.com/sbouclier/intellij-plugin-java-builder-pattern/blob/master/assets/images/generate.png?raw=true "Generate")

Click on Builder menu, this will open a dialog box. You have choice to generate two types of builder :

### Classic builder

![alt text](https://github.com/sbouclier/intellij-plugin-java-builder-pattern/blob/master/assets/images/classic_builder.png?raw=true "Classic builder")

First, select which class members you want to include in constructor builder. You can also override parameter methods by using prefix. Then select members you want to include and click on OK button, this will generate the builder code.
You can now build your class like this :

```java
Employee johnDoe = Employee.builder("123")
    .withFirstName("John")
    .withLastName("Doe")
    .withBirthday(LocalDate.of(1970, 1, 1))
    .withSalary(1000)
    .build();
```

### Fluent builder with interfaces

![alt text](https://github.com/sbouclier/intellij-plugin-java-builder-pattern/blob/master/assets/images/builder_with_interfaces.png?raw=true "Builder with interfaces")

Same steps as classic builder except you can now select mandatory/optional parameters. You can only select constructor parameters for mandatory parameters.
You can build your class like this (as classic builder) :

```java
Employee johnDoe = Employee.builder("123")
    .withFirstName("John")
    .withLastName("Doe")
    .withBirthday(LocalDate.of(1970, 1, 1))
    .withSalary(1000)
    .build();
```

But using auto completion, Intellij will provide you next valid parameter.

Next mandatory parameter:

![alt text](https://github.com/sbouclier/intellij-plugin-java-builder-pattern/blob/master/assets/images/mandatory_parameter_completion.png?raw=true "Auto completion of next mandatory parameter")

Next optional parameter(s):

![alt text](https://github.com/sbouclier/intellij-plugin-java-builder-pattern/blob/master/assets/images/optional_parameter_completion.png?raw=true "Auto completion of next optional parameter(s)")
