# Distributed Systems Assignment 3
This is the README file for Distributed Systems Assignment3. 

## Compiling the Centralised Communication Controller and Member Nodes
The CCC and MemberNodes can all be compiled using a Makefile. When in the same directory as the Makefile, run the command "Make" to compile the CCC and MemberNode classes. To delete the class files, "make clean" can be run.

## Running the Centralised Communication Controller and Member Nodes
When in the src directory, the CCC and MemberNodes can be run manually with the following commands:
- CentralCommunicationController: "java CentralCommunicationController".
- MemberNode: "java MemberNode [member ID] [proposer_status]".

The definitions of the arguments are as follows:
- member ID: The ID number of the memberNode.
- proposer status: "true" or "false" should be entered here, this tells the code whether this node is a proposer or not.

## How Each Components Acts
### MemberNode
The MemberNode class consists of one main function. This function begins by checking and reading the arguments given. Once done it connects to the server and if it is a proposer, it will send its first proposal message. Afterward, the MemberNode will go into a listening state, waiting for messages from the server. The following is what occurs when a certain message is received:

- Proposal: determine who sent the proposal and the proposal number, check if the promise can be made. If it can send a promise message, otherwise send a promiseNack message.
- Promise: Once a majority of these have been received, an accept request is sent including either a new value or the previously proposed value.
- accept: determine who sent the accept message and the proposal number, check if acceptOK or acceptReject needs to be sent and send it. 
- acceptOK: Once a majority of these have been received, a decide message is sent out and the previousProposalValue is set to the value sent out.
- acceptReject or promiseNack: Once a majority of either of these have been received, the proposer attempts to send out a proposal again, this time with a higher proposal number.
- decide: When this message is received, the previous proposal value is set to the value received and the current proposal member is set to 0 (as no one is proposing anymore currently)

Lastly, the MemberNode asks the user to enter some text. This is so that, when opening new terminals to run the MemberNodes, they will not close straight away once the processes are done. Therefore, the user is able to read the contents of the terminal and close it when they like.

### CentralCommunicationController
This class consists of a main method, an initialise sockets method and a method to close sockets. 

The initialise sockets method takes in a ServerSocket, an array of Sockets, an array of PrintWriters, an array of BufferedReaders and an integer relating to the number of nodes. This method goes into a for loop for the number of nodes and accepts an incoming socket for each element in the Socket array. The SoTimeout is also set to 250 for these sockets. The output stream and input stream of these sockets are stored in the corresponding index in the PrintWriter and BufferedReader arrays. The method will return 1 if it fails and 0 if it executes without error.

The close sockets method takes in the same arguments as the initialise sockets method and runs through a for loop iterating through each of the sockets, PrintWriters and BufferedReaders and closes each of them. If it runs into issues it returns 1, otherwise it returns 0.

The main method for the CCC initialises all the required sockets, PrintWriters and BufferedReaders. It then enters an infinite while loop where it loops through each node checking if a message has been received on it. The SoTimeout for each socket was set to 250 so that if reading that node, after 0.25 seconds it will move onto the next node. This was done so readLine() was not a blocking reader anymore. When a line is received, the CCC checks who the recipient of the message is and sends it out accordingly. If it's "broadcast" it sends the message to all nodes but the one who sent it. If it is "Nn" where n is some node number, it sends the message to that node specifically. If the CCC has cycled through the nodes and not received a message for 5 seconds, it will assume convergence, break the while loop, close the sockets and end.

It's also important to mention that the CCC will only work provided each memberNode connects to it in the right order. For example, node 1 must connect first, node 2 must connect second etc.

## Error Handling
### MemberNode
The MemberNode class checks for errors in its arguments and throughout its code. Firstly, it checks the argument length to make sure the correct amount of arguments has been entered. It also checks to ensure the second argument is "true" or "false" and the first argument is a valid number to be saved as the memberID. If any of these errors occur, the code will output an error message before stopping.

There are also four main errors being checked for consistently in the code, these are Unknown Host Exceptions, IO Exceptions, Number Format Exceptions and Array Index Out Of Bounds Exceptions. If any of these occur an error message is outputted before the code stops.


### CentralCommunicationController
The CCC is consistently checking for errors. In the initialise sockets and close sockets methods, if an IOException occurs, the methods will return 1 and the CCC will shut down.

In the main method, there are 3 main errors being accounted for. IO Exceptions, Number Format Exceptions and Array Index Out Of Bounds Exceptions. In the event of each of these, the code will output an error message before stopping.

## Testing
### Requirements of Implementation

- [x] Implementation of Paxos works when two councillors send voting proposals at the same time
  - Done in test 2
- [x] Implementation of Paxos works in case where all members have immediate responses
  - Done in tests 1 and 2
- [ ] Implementation works when members respond based on their individual profiles
  - M1 sends/receives messages instantly all the time
  - M2 sends/receives messages after a long delay, or they propose and then go offline
  - M3 sends/receives messages after a short delay, unless camping, in which case he does not send/receive them at all
  - M4-M9: sends/receives messages immediately, after a short delay, or after a long delay.

### Smaller Unit Tests for functionality and error checking

