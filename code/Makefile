SRC=src/
LIB=libs/
TARGET=bin/
INSTANCE=1

# directory which contains cplex library
CPLEX=/opt/ibm/ILOG/CPLEX_Studio1271/cplex/bin/x86-64_linux/

default: compile

compile:
	mkdir -p $(TARGET)
	javac -cp $(SRC):$(LIB)* $(SRC)edu/aa12/MainMethods.java -d $(TARGET)

run:
	java -cp $(TARGET):$(LIB)* -Djava.library.path=$(CPLEX)	edu.aa12.MainMethods $(INSTANCE)

clean:
	rm -rf $(TARGET)

.PHONY: clean compile run
