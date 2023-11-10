# Distributed Systems Assignment 3
This is the README file for Distributed Systems Assignment3. 

## Compiling the Centralised Communication Controller and Member Nodes
The Centralised Communication Controller (CCC) and MemberNodes can all be compiled using a Makefile. When in the same directory as the Makefile, run the command "Make" to compile the CCC and MemberNode classes. To delete the class files, "make clean" can be run.

## Running the Centralised Communication Controller and Member Nodes
When in the src directory, the CCC and MemberNodes can be run manually with the following commands:
- CentralCommunicationController: "java CentralCommunicationController".
- MemberNode: "java MemberNode [member ID] [proposer_status] [response time]".

The definitions of the arguments are as follows:
- member ID: The ID number of the memberNode.
- proposer status: "true" or "false" should be entered here, this tells the code whether this node is a proposer or not.
- response time: the amount of time it takes the node to respond to a message, this is mainly for testing purposes. 0 is entered for immediate responses and 20 or above is entered for no response at all.

## How Each Components Acts
### MemberNode
The MemberNode class consists of one main function. This function begins by checking and reading the arguments given. Once done it connects to the server and if it is a proposer, it will send its first proposal message. Afterward, the MemberNode will go into a listening state, waiting for messages from the server. The following is what occurs when a certain message is received:

- Proposal: determine who sent the proposal and the proposal number, check if the promise can be made. If it can send a promise message, otherwise send a promiseNack message.
- Promise: Once a majority of these have been received, an accept request is sent including either a new value or the previously proposed value, this value is a memberID number.
- accept: determine who sent the accept message and the proposal number, check if acceptOK or acceptReject needs to be sent and send it. 
- acceptOK: Once a majority of these have been received, a decide message is sent out and the previousProposalValue is set to the value sent out.
- acceptReject or promiseNack: Once a majority of either of these have been received, the proposer attempts to send out a proposal again, this time with a higher proposal number.
- decide: When this message is received, the previous proposal value is set to the value received and the current proposal member is set to 0 (as no one is proposing anymore currently)

Lastly, the MemberNode asks the user to enter some text. This is so that, when opening new terminals to run the MemberNodes, they will not close straight away once the processes are done. Therefore, the user is able to read the contents of the terminal and close it when they like.

### CentralCommunicationController
This CCC class acts a server for all communication among member nodes. It consists of a main method, an initialise sockets method and a method to close sockets. 

The initialise sockets method takes in a ServerSocket, an array of Sockets, an array of PrintWriters, an array of BufferedReaders and an integer relating to the number of nodes. This method goes into a for loop for the number of nodes and accepts an incoming socket for each element in the Socket array. The SoTimeout is also set to 250 for these sockets. The output stream and input stream of these sockets are stored in the corresponding index in the PrintWriter and BufferedReader arrays. The method will return 1 if it fails and 0 if it executes without error.

The close sockets method takes in the same arguments as the initialise sockets method and runs through a for loop iterating through each of the sockets, PrintWriters and BufferedReaders and closes each of them. If it runs into issues it returns 1, otherwise it returns 0.

The main method for the CCC initialises all the required sockets, PrintWriters and BufferedReaders. It then enters an infinite while loop where it loops through each node checking if a message has been received on it. The SoTimeout for each socket was set to 250 so that if reading that node, after 0.25 seconds it will move onto the next node. This was done so readLine() was not a blocking reader anymore. When a line is received, the CCC checks who the recipient of the message is and sends it out accordingly. If it's "broadcast" it sends the message to all nodes but the one who sent it. If it is "Nn" where n is some node number, it sends the message to that node specifically. If the CCC has cycled through the nodes and not received a message for 20 seconds, it will assume convergence, break the while loop, close the sockets and end.

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

The tests for the requirements of implementation are done using bash scripts to run the memberNodes and the CCC. Each file begins with a number corresponding to the test it belongs to. Any outputs produced by the bash scripts also contain a corresponding test number. To see the commands needed to run each test, please see the TestCommands.txt file. 

The bash files make use of the windows command "start-process" and use the default windows terminal powershell. Therefore, these tests may not be completely compatible on other devices. The devices must also support bash / running the bash command.

The following points are shown in the tests:
- Implementation of Paxos works when two councillors send voting proposals at the same time:
  - This is shown in test 2.
- Implementation of Paxos works in case where all members have immediate responses:
  - This is shown in tests 1 and 2.
- Implementation works when members respond based on their individual profiles:
  - M1 sends/receives messages instantly all the time:
    - This occurs in all the tests.
  - M2 sends/receives messages after a long delay, or they propose and then go offline:
    - This is performed in test 5.
  - M3 sends/receives messages after a short delay, unless camping, in which case he does not send/receive them at 
    - This functionality is equivalent to M2 in test 5.
  - M4-M9: sends/receives messages immediately, after a short delay, or after a long delay.
    - This is done in tests 3-5.

### Outcomes of each of the tests and explanation
Each of the tests produce 3 or more output files. They show all incoming and outgoing messages for their corresponding MemberNode or CCC. For the CCC, the output is organised so each set of corresponding incoming and outgoing messages is separated by a paragraph.

#### Test 1
Test 1 is a basic test with only 1 proposer. All messages are sent/received instantly. In this test, the CCC shows messages happening in the following order: proposal, promise, accept, acceptOK and decide. The proposer begins by sending a proposal, and once it receives 4 promises it knows it has a majority (as it includes its own promise), therefore it sends a broadcast before receiving the rest of the promises. It then receives the rest of the promises and then 4 acceptOKs. It once again has majority, therefore it sends off the decide message before receiving the rest of the acceptOKs. Lastly, the acceptor (which is similar to the others) receives a proposal, responds with a promise, receives an accept, responses with an acceptOK before receiving a decide message. 

#### Test 2
Test 2 is similar to test 1 except there are now 2 proposers. Messaging is still instant. The acceptor output is very similar to test 1 except there is an extra message received and sent: when the second proposal arrives, the acceptor sends a promiseNack back to the second proposer. The output for proposer 1 is also very similar to that in test 1. There are only 3 extra messages sent and received: a proposal from proposer 2, a promiseNack response back to proposer 2 and a promiseNack from proposer 2. Proposer 2s output is similar to a regular acceptor output except it sends a promiseNack to proposer 1, and it receives all the promiseNacks from the other acceptors. Lastly, the CCC output is the same as before except it shows two sets of broadcasts for proposals and all the promiseNacks being sent around.

#### Test 3
Test 3 is the first test with varying response times from members. There is one proposer with instant response time. Then there are 4 acceptors with instant response times, 2 with short delay response times and 2 with long delay response times. The CCC output is the same as in test 1 except some messages are sent later. For example, there are promise messages occurring after the decide message is broadcasting. All acceptors have the same output as test 1, even the delayed response acceptors. Ths is because they receive messages after they send their delayed one. Therefore, everything is occurring in the same order, it just takes longer. Lastly, the proposer output is similar to test 1, messages are just out of order. But since the proposer knows it can progress when it gets a majority of messages, there is no real affect to short delay or long delay nodes. 


#### Test 4
Test 4 is the second test with the same varying response times from members as explained in test 3. The acceptor outputs are once again the same as test 2 for the same reason explained in test 3. Proposer 1 is also very similar with test 2, there are some messages out of order due to the delay of the acceptors. Once again, receiving a majority of messages allows the proposer to send a decide message before receiving all promise messages. Proposer 2 is also similar to test 2 except it receives promiseNacks out of order. As the code will ignore promiseNacks, this has very little effect on the outcome of the code. Lastly, the CCC output is similar to test 2 but shows messages out of order due to the delay.

#### Test 5
Test 5 is similar to test 4 except the second proposer goes offline after making their proposal. Once again the acceptor outputs are the same. The proposer 1 output is the same except it does not receive the promiseNack or the acceptOK from proposer 2. Proposer 1 is still able to progress due to having the majority of votes from other acceptors. Proposer 2s output only shows its own proposal being sent and the proposal from proposer 1 being received, it then goes offline so no other output is present. Lastly, the CCC output is the same as test 4.

#### A word on all the tests
Each test has the same outcome as MemberNode 1 is always the first to make a proposal. The code works in such a way that the first proposer, whoever they are, will get priority. However, despite all the response times and whether there are one or more proposers, the outcome of each of the tests was still consistent. 1 was the decided value. 
