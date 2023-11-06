all: compile

compile: src/MemberNode.java src/CentralCommunicationController.java
	javac src/MemberNode.java
	javac src/CentralCommunicationController.java

clean:
	rm -f src/MemberNode.class
	rm -f src/CentralCommunicationController.class