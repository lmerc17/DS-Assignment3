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
            int proposalNumber = 0; // defining variable to keep track of proposal numbers
            int value = 0; // defining the value to be proposed by the node provided this is a proposer node


            if(isProposer){ // if this node is the proposer, send a proposal request to the central communication controller
                // proposal request is in the format of <type of message>:<sender>:<recipient>:<current proposal number>

                request = "proposal:N" + memberID + ":broadcast:" + proposalNumber; // create the proposal request
                proposalNumber++; // increment the proposalNumber
                out.println(request); // send the proposal request
                //out.flush(); (here just in case I need it) -----------------------------------------------------------------------------------------------------------------------------
            }

            // initialisation of variables used when reading or sending messages
            String response;
            String messageType;
            int recipient;
            int previous_proposal_number = 0;
            int previous_proposal_value = 0;
            int newProposalNumber; // defining variable to keep track of incoming new proposal numbers
            int currentProposalNumber = 0;


            while((response = in.readLine()) != null){ // wait for messages and when one comes in
                messageType = response.split(":")[0]; // determine the type of message

                switch(messageType){

                    case "prepare":

                        recipient = Integer.parseInt(response.split(":")[1]);
                        newProposalNumber = Integer.parseInt(response.split(":")[3]);

                        // if the new proposal number is greater than the old proposal number,
                        // send back a promise message and update proposal number
                        if(newProposalNumber > currentProposalNumber){
                            // promise is of format <type of message>:<sender>:<recipient>:<previous proposal number>:<previous proposal value>
                            request = "promise:N" + memberID + ":" + recipient + ":" + previous_proposal_number + ":" + previous_proposal_value;
                            currentProposalNumber = newProposalNumber;
                        }
                        else{
                            request = "promiseNack:";
                        }
                        out.println(request);

                        break;

                    case "promise":

                        break;

                    case "promiseNack":

                        break;

                    case "accept":

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
