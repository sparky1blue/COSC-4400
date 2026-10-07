PARSER   = LP1Parser

JAVAC    = javac
JAVA     = java
JAVACC   = javacc
JJDOC    = jjdoc
DIFF     = diff

JAVACC_FLAGS = -NOSTATIC

INTERPRETER = Interpreter
TESTIN = tests/test8.txt
EXPECTED = tests/test8-expected.txt
TESTOUT = output.txt

JAVACCGEN = SimpleCharStream.java ParseException.java \
        $(PARSER).java $(PARSER)Constants.java \
        $(PARSER)TokenManager.java Token.java \
        TokenMgrError.java

ABSYN    = Absyn/Goal.java Absyn/Exp.java \
	Absyn/AddExp.java Absyn/SubExp.java \
	Absyn/MulExp.java Absyn/DivExp.java \
	Absyn/NumExp.java Absyn/IdExp.java

SRCS     = $(ABSYN) $(PARSER).java Main.java $(INTERPRETER).java

all:  $(SRCS:.java=.class)

%.class : %.java
	$(JAVAC) $<



$(PARSER).java: $(PARSER).jj
	$(JAVACC) $(JAVACC_FLAGS) $<

parser: $(PARSER).class $(PARSER)Constants.class \
	$(PARSER)TokenManager.class ParseException.class

main: $(PARSER).class Main.class

test: all
	@ ${JAVA} ${INTERPRETER} ${TESTIN} > ${TESTOUT}
	${DIFF} ${DIFFOPT} ${EXPECTED} ${TESTOUT}

clean:
	rm -f $(SRCS:.java=.class) ${JAVACCGEN} $(JAVACCGEN:.java=.class)
