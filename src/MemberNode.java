import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MemberNode {

    public static void main(String[] args){

        String hostName = "localhost"; // defining hostname of centralised communication controller
        int portNumber = 4567; // defining port number of centralised communication controller
        boolean isProposer = false; // defining boolean to determine if this node is a proposer or not
        int memberID; // initialising the integer that will store the ID of this node

        // checking correct number of arguments have been given
        if(args.length != 2){
            System.err.println("Usage: java MemberNode <memberID> <proposer status>");
            return;
        }
        // if statement to check whether this node is the proposer or not
        if(args[1].equals("true")){
            isProposer = true;
        }
        else if(!(args[1].equals("false"))) {
            System.err.println("Please enter a valid proposer status");
            return;
        }
        // try and catch to get memberID
        try {
            memberID = Integer.parseInt(args[0]);
        }
        catch(NumberFormatException e){
            System.err.println("Please enter valid memberID");
            return;
        }


        // try and catch statement used to connect to and communicate with the centralised communication controller
        try (Socket serverSocket = new Socket(hostName, portNumber);
             PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader((new InputStreamReader(serverSocket.getInputStream())))
        )
        {
            // initialisation of String request which stores the request message to be sent
            String request; // requests can be in different formats depending on the message being sent
            int proposalNumber = 1; // defining variable to keep track of proposal numbers

            if(isProposer){ // if this node is the proposer, send a proposal request to the central communication controller
                // proposal request is in the format of <type of message>:<sender>:<recipient>:<current proposal number>

                request = "proposal:N" + memberID + ":broadcast:" + proposalNumber; // create the proposal request
                out.println(request); // send the proposal request
                System.out.println("Message Sent: " + request);
                //out.flush(); (here just in case I need it) -----------------------------------------------------------------------------------------------------------------------------
            }

            // initialisation of variables used when reading or sending messages
            String response;
            String messageType;
            int number_of_nodes = 3;
            int recipient;
            int previousProposalNumber = 0;
            int previousProposalValue = 0;
            int newProposalNumber; // defining variable to keep track of incoming new proposal numbers
            int currentProposalNumber = 0;
            int currentProposalMember = 0; // used to check who the current proposal's proposer is
            int value; // defining the value to be proposed by the node provided this is a proposer node
            int promiseCount = 1; // defining count for promise messages (starts as one as proposer always promises to itself)


            while((response = in.readLine()) != null){ // wait for messages and when one comes in
                System.out.println("Message Received: " + response);
                messageType = response.split(":")[0]; // determine the type of message

                switch(messageType){

                    // when a prepare message has been received (only received by acceptors)
                    case "proposal":

                        // find sender of prepare message to send message back to and new proposal number from incoming message
                        recipient = Integer.parseInt(response.split(":")[1].substring(1));
                        newProposalNumber = Integer.parseInt(response.split(":")[3]);

                        // if the new proposal number is greater than the old proposal number,
                        // send back a promise message and update proposal number
                        if(newProposalNumber > currentProposalNumber){
                            // promise is of format <type of message>:<sender>:<recipient>:<previous proposal number>:<previous proposal value>
                            request = "promise:N" + memberID + ":N" + recipient + ":" + previousProposalNumber + ":" + previousProposalValue;
                            currentProposalNumber = newProposalNumber; // update proposal number
                        }
                        else{
                            request = "promiseNack:N" + memberID + ":N" + recipient; // form promiseNack if not promising
                        }
                        out.println(request); // send the promise message
                        System.out.println("Message Sent: " + request);

                        // if this is a proposer, add one to promise count (this simulates the proposer receiving its own message as an acceptor)
                        if(isProposer){
                            promiseCount++;
                        }

                        break;

                    // when a promise message has been received (only received by proposers)
                    case "promise":
                        promiseCount++;


                        // if the received promise message has a previous proposal value
                        if (Integer.parseInt(response.split(":")[4]) != 0) {
                            value = Integer.parseInt(response.split(":")[4]); // set value to that value
                        } else {
                            value = memberID; // otherwise set it to the memberID
                        }

                        // if a majority of promises have been received
                        if(promiseCount > number_of_nodes/2) {
                            // create an accept message with format <type of message>:<sender>:<recipient>:<proposal number>:<proposal value>
                            request = "accept:N" + memberID + ":broadcast:" + proposalNumber + ":" + value;

                            out.println(request); // send the accept message
                            System.out.println("Message Sent: " + request);
                            promiseCount = 0; // set promise count to 0 to make sure this if statement is not entered again
                        }

                        break;

                    // when a promiseNack message has been received (only received by proposers)
                    case "promiseNack":

                        break;

                    // when an accept message has been received (only received by acceptors)
                    case "accept":

                        // find sender of prepare message to send message back to and new proposal number from incoming message
                        recipient = Integer.parseInt(response.split(":")[1].substring(1));
                        newProposalNumber = Integer.parseInt(response.split(":")[3]);

                        // if the proposal number in the message is equal to or greater than the current proposal number,
                        // send back an accept-ok message
                        if(newProposalNumber >= currentProposalNumber){
                            // accept-ok is of format <type of message>:<sender>:<recipient>
                            request = "acceptOK:N" + memberID + ":N" + recipient;
                        }
                        else{
                            request = "acceptReject:N" + memberID + ":N" + recipient; // form acceptReject if not promising
                        }

                        System.out.println("Message Sent: " + request);
                        out.println(request);

                        break;

                    case "acceptOK":

                        break;

                    case "acceptReject":

                        break;

                    case "decide":

                        break;
                }
            }

        }
        catch(UnknownHostException e){ //Exception catching for unknown host
            System.err.println("Unknown host: " + hostName);
        }
        catch(IOException e){ //Exception catching for IOException
            System.err.println("Couldn't get or lost connection to " + hostName);
        }

    }

}
