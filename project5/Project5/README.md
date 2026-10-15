# ConcurrentMiniJava
Implement a Parser for our dialect of the MiniJava programming language, as detailed below.

- Read Chapter 3 and 4 of Appel.
- Use JavaCC to implement the scanner and parser rules for our dialect of MiniJava.
- Run the reference implementation of the parser on Morbius with the command: ~brylow/cosc4400/Projects/parser < program.java
- Create your parser in a package called "Parse" with a main program in class "Main". My grading protocol will assume that your project can be compiled and run with the following command line: cd Project3; make; java Parse.Main < inputfile.java. TA-Bot will assume your JavaCC grammar will be named MiniJava.jj.
- Build a decent set of MiniJava testcases. Several exist in the book, and on the web. Consider sharing testcases with the other teams. Having a good set of test inputs will be critical to your success in later phases of the project. The majority of project points will be assigned by running diff to compare your output against the expected answer.

