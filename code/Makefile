SRC=src/
LIB=libs/
TARGET=bin/

default: compile

compile:
	mkdir -p $(TARGET)
	javac -cp $(SRC):$(LIB)* $(SRC)edu/aa12/MainMethods.java -d $(TARGET)

run:
	java -cp $(TARGET):$(LIB)* edu.aa12.MainMethods

clean:
	rm -rf $(TARGET)

.PHONY: clean compile run
