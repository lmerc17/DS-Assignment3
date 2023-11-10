import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MemberNode {

    public static void main(String[] args){

        String hostName = "localhost"; // defining hostname of centralised communication controller
        int portNumber = 4567; // defining port number of centralised communication controller
        boolean isProposer = false; // defining boolean to determine if this node is a proposer or not
        int memberID; // initialising the integer that will store the ID of this node
        int responseTime; // initialising the integer that will determine the response time of the node

        // checking correct number of arguments have been given
        if(args.length != 3){
            System.err.println("Usage: java MemberNode <memberID> <proposer status> <response time>");
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
        // try and catch to get responseTime
        try {
            responseTime = Integer.parseInt(args[2]);
        }
        catch(NumberFormatException e){
            System.err.println("Please enter valid response time");
            return;
        }

        // Label at top of terminal to know which member's output is being shown.
        System.out.println("Member " + memberID);

        // try and catch statement used to connect to and communicate with the centralised communication controller
        try (Socket serverSocket = new Socket(hostName, portNumber);
             PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader((new InputStreamReader(serverSocket.getInputStream())))
        )
        {
            // initialisation of String request which stores the request message to be sent
            String request; // requests can be in different formats depending on the message being sent
            int proposalNumber = 1; // defining variable to keep track of proposal numbers
            int currentProposalNumber = 0;
            int currentProposalMember = 0; // used to check who the current proposal's proposer is
            int number_of_nodes = 9;

            if(isProposer){ // if this node is the proposer, send a proposal request to the central communication controller
                // proposal request is in the format of <type of message>:<sender>:<recipient>:<current proposal number>

                request = "proposal:N" + memberID + ":broadcast:" + proposalNumber; // create the proposal request
                out.println(request); // send the proposal request
                System.out.println("Message Sent: " + request);
                currentProposalMember = memberID;
                currentProposalNumber = proposalNumber;

            }

            // initialisation of variables used when reading or sending messages
            String response;
            String messageType;
            int recipient;
            int previousProposalNumber = 0;
            int previousProposalValue = 0;
            int newProposalNumber; // defining variable to keep track of incoming new proposal numbers
            int value = 0; // defining the value to be proposed by the node provided this is a proposer node
            int promiseCount = 1; // defining count for promise messages
            int acceptOKCount = 1; // defining count for acceptOK messages
            int acceptRejectCount = 0;


            while((response = in.readLine()) != null){ // wait for messages and when one comes in
                System.out.println("Message Received: " + response);
                messageType = response.split(":")[0]; // determine the type of message

                if(responseTime >= 20){ // if the response time is greater than or equal to 20, wait indefinitely
                    try {
                        TimeUnit.SECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        System.err.println("Could not sleep for " + responseTime + " seconds");
                    }
                }

                switch(messageType){

                    // when a prepare message has been received (only received by acceptors)
                    case "proposal":

                        // find sender of prepare message to send message back to and new proposal number from incoming message
                        recipient = Integer.parseInt(response.split(":")[1].substring(1));
                        newProposalNumber = Integer.parseInt(response.split(":")[3]);

                        // if there has been no proposal proposed by someone already and new proposal number is greater than the old proposal number,
                        // send back a promise message and update proposal number
                        if(currentProposalMember == 0 && newProposalNumber > currentProposalNumber){
                            // promise is of format <type of message>:<sender>:<recipient>:<previous proposal number>:<previous proposal value>
                            request = "promise:N" + memberID + ":N" + recipient + ":" + previousProposalNumber + ":" + previousProposalValue;
                            currentProposalNumber = newProposalNumber; // update proposal number
                            currentProposalMember = recipient; // update proposal member
                        }
                        else{
                            request = "promiseNack:N" + memberID + ":N" + recipient; // form promiseNack if not promising
                        }

                        try{ // respond to the proposal message only after the response time has occurred
                            TimeUnit.SECONDS.sleep(responseTime);
                        } catch (InterruptedException e) {
                            System.err.println("Could not sleep for " + responseTime + " seconds");
                        }

                        out.println(request); // send the promise message
                        System.out.println("Message Sent: " + request);

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

                            try{ // respond to the promise messages only after the response time has occurred
                                TimeUnit.SECONDS.sleep(responseTime);
                            } catch (InterruptedException e) {
                                System.err.println("Could not sleep for " + responseTime + " seconds");
                            }

                            out.println(request); // send the accept message
                            System.out.println("Message Sent: " + request);
                            promiseCount = 0; // set promise count to 0 to make sure this if statement is not entered again
                        }

                        break;

                    // when a promiseNack message has been received, ignore it (only received by proposers)
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

                        try{ // respond to the accept message only after the response time has occurred
                            TimeUnit.SECONDS.sleep(responseTime);
                        } catch (InterruptedException e) {
                            System.err.println("Could not sleep for " + responseTime + " seconds");
                        }

                        System.out.println("Message Sent: " + request);
                        out.println(request);

                        break;

                    // when an acceptOK message has been received (only received by proposers)
                    case "acceptOK":
                        // set promise count back to 1 as we have passed that phase of the algorithm
                        promiseCount = 1;
                        acceptOKCount++;

                        // if a majority of acceptOKs have been received
                        if(acceptOKCount > number_of_nodes/2) {
                            // create a decide message with format <type of message>:<sender>:<recipient>:<proposal number>:<proposal value>
                            request = "decide:N" + memberID + ":broadcast:" + proposalNumber + ":" + value;

                            try{ // respond to the acceptOK messages only after the response time has occurred
                                TimeUnit.SECONDS.sleep(responseTime);
                            } catch (InterruptedException e) {
                                System.err.println("Could not sleep for " + responseTime + " seconds");
                            }

                            out.println(request); // send the accept message
                            System.out.println("Message Sent: " + request);
                            acceptOKCount = 0; // set acceptOK count to 0 to make sure this if statement is not entered again
                        }

                        // the previous proposal value is set here as the proposer will not receive a
                        // decide message and will not get a chance later to set it
                        previousProposalValue = value;

                        break;

                    // when an acceptReject or promiseNack message has been received (only received by proposers)
                    case "acceptReject":
                        acceptRejectCount++;

                        // if a majority of acceptReject or promiseNack messages have been received, try sending a proposal again
                        if(acceptRejectCount > number_of_nodes/2){
                            proposalNumber++;
                            request = "proposal:N" + memberID + ":broadcast:" + proposalNumber; // create the proposal request

                            try{ // respond to the acceptReject messages only after the response time has occurred
                                TimeUnit.SECONDS.sleep(responseTime);
                            } catch (InterruptedException e) {
                                System.err.println("Could not sleep for " + responseTime + " seconds");
                            }

                            out.println(request); // send the proposal request
                            System.out.println("Message Sent: " + request);
                            acceptRejectCount = 0; // set to 0 to ensure it is not entered again
                            currentProposalMember = memberID;
                            currentProposalNumber = proposalNumber;
                        }

                        break;

                    // when a decide message has been received (only received by acceptors)
                    case "decide":
                        // a value has been decided
                        previousProposalValue = Integer.parseInt(response.split(":")[4]);
                        currentProposalMember = 0; // set to 0 so a new proposer can propose (but the value will not change)

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
        catch(NumberFormatException e){ // Exception catching for bad number format
            System.err.println("A non-number string has been attempted to be converted into a number");
        }
        catch(ArrayIndexOutOfBoundsException e){ // Exception catching for out of bounds array accessing
            System.err.println("Access at and out of bounds index for an array has been attempted");
        }

        // Due to each client being opened in a new terminal window, it would close when the process ends
        // The next two lines are included so the terminal window stays open until the user presses enter
        Scanner scan = new Scanner(System.in);
        scan.nextLine();

    }

}
