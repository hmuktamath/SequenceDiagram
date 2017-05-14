Java Parser for Sequence diagram

Name: Harsha Muktamath		
SJSU ID: 011825138
My YouTube Link – https://youtu.be/DBk8hZ2t97c
Google Drive link for zip files – https://drive.google.com/drive/folders/0B4wVdfCPs4VhUTNvR1FEQ05lTTg?usp=sharing 
Github Link - https://github.com/muktamath81/SequenceDiagram
For Extra Credit (Partnering with CMPE 281 students)
1)	Tuan Ung (SJSU ID – 007671040)
YouTube video  https://www.youtube.com/watch?v=J1p6r9cU8-8
2)	Tanmay Bhatt (SJSU ID – 011499072)
YouTube video https://www.youtube.com/watch?v=LcwwtrVyi1k&feature=youtu.be
Initial setup -
• Install Java and use any available IDE for development. In my project, I have used Eclipse and to manage dependencies Maven. To execute my code, Eclipse is a must. Please install.
• GraphViz must be installed on the system. It does not have plugins, hence it has to be installed (Link- http://www.graphviz.org/Download..php)
Instructions to execute the program
1)	Download the zip file
2)	Import the package/project in Eclipse
3)	Provide Arguments in configuration as below
The first argument is where your tests are located.
The second argument is just main 
The third argument is the name of the sequence diagram file
External Libraries and Tools used
1.	Javaparser 
Javaparser is lightweight and easy to use parser library which parses java code and provides AST (Abstract Syntax Tree). Javaparser uses javacc (Java Compiler Compiler) for generating AST from Java code. One can analyze code structure, Javadoc or comments using AST created by Javaparser library. GraphViz is open source graph visualization software. GraphViz supports dot(.) notation input for drawing directed graphs. The GraphViz software takes input as simple text file and converts it to diagram. GraphViz provides support for generating output diagram in PDF, Images or SVG format. It has many useful features such as custom coloring, shapes and custom messages.
2.	GraphViz 
Limitation: GraphViz application is stand-alone application and cannot be integrated in java project without third party library.
3.	PlantUML 
PlantUML provides a Java based API for using GraphViz software through your Java application. This library is required because GraphViz does not have their Java API exposed which developers can use to integrate GraphViz directly in their application. As PlantUML uses GraphViz internally, most of the functions that are offered by GraphViz are supported by PlantUML.

