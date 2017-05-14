# Java uml-generator
A Parser which generates UML Class Diagram from Java code
***
### Instructions for execution

#### Requirements:
- Java JDK version 1.8
- Maven

The program expects following arguments:

1. Keyword:
  - One word string.
  - “class” for generating class diagram and keyword “seq” for generating a sequence
diagram.


2. Path:
  - String, may contain spaces, must be enclosed in double quotes.
  - Full path of the folder which contains all the .java source files. The program picks only
the .java files and ignores other files.
  - Ex - "C:\Users\uml-generator-java\Test Classes\class-diagram-test-1"

3. Name of output file
  - One word string
  - File name of the output png file. The file will be created at the same folder as Path given
in second argument.
  - Do not include extension along with the file name, the program will generate a PNG file.
  - Ex – diagram

Example:-
class diagram
```command
java -jar umlparser.jar class "C:\Users\uml-generator-java\Test Classes\class-diagram-test-1" diagram
```


For sequence diagram, additional 2 arguments are required before the name of output file argument

4. Name of class
  * Class Name in which the method resides for which the sequence diagram is needed
  * Ex: Customer

5. Name of function
  - Method Name for which the sequence diagram is needed
  - Do not include parenthesis, brackets after function name
  - Ex - depositMoney


sequence diagram
```command
java -jar umlparser.jar seq "C:\Users\uml-generator-java\Test Classes\sequence-diagram-test-1" Customer depositMoney diagram
```


***
### Libraries and tools 


There are 2 parts of this UML parser program:

- Parser – The parser takes the java source code from the input path, and creates
a grammar language that is interpretable by the UML generator

- UML Generator – This part just generates a diagram as per the input provided

(For parsing) Parser:
For parsing the JAVA code into a usable grammar, I have used the javaparser library:

https://github.com/javaparser/javaparser

The library provides various methods and classes that read the source code and provide access to
each sub-unit of the code via various methods or classes.

(For generating diagram) UML Generator:

The program sends an HTTP request to the URL : http://yuml.me/diagram/simple/class/<Grammar>
and gets the diagram.

For generating the sequence diagram, plantUML is required: http://plantuml.com/
