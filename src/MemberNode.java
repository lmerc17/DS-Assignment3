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
        int proposalNumber = 1; // defining variable to keep track of proposal numbers
        int newProposalNumber = 0; // defining variable to keep track of incoming new proposal numbers
        boolean isProposer = false; // defining boolean to determine if this node is a proposer or not
        int memberID = 0; // defining the integer that will store the ID of this node
        int value = 0; // defining the value to be proposed by the node provided this is a proposer node

        if(args.length != 2){ // checking correct number of arguments have been given
            System.err.println("Usage: java MemberNode <memberID> <proposer status>");
            return;
        }

        if(args[1].equals("true")){ // if statement to check whether this node is the proposer or not
            isProposer = true;
        }
        else if(!(args[1].equals("false"))) {
            System.err.println("Please enter a valid proposer status");
            return;
        }

        try { // try and catch to get memberID
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
            // initialisation of variables used to receive and send messages
            String request; // requests can be in different formats depending on the message being sent
            String response;
            int recipient;
            int previous_proposal_number = 0;
            int previous_proposal_value = 0;

            if(isProposer){ // if this node is the proposer, send a proposal request to the central communication controller
                // proposal request is in the format of <type of message>:<sender>:<recipient>:<current proposal number>

                request = "proposal:N" + memberID + ":broadcast:" + proposalNumber; // create the proposal request
                proposalNumber++; // increment the proposalNumber
                out.println(request); // send the proposal request
                //out.flush(); (here just in case I need it) -----------------------------------------------------------------------------------------------------------------------------

                value = memberID; // value to be proposed is the ID of the member applying for presidency

                /*
                I have a feeling that I'm going to change this node so that it is in a listening state by default
                Then when a message is received, it will be handled as necessary. That way I can send it to the
                proposer or acceptor as needed. I will probably make use of functions to help make this look clean too.
                */



            }
            else{
                proposalNumber--; // decrement proposal so it is 0 and less than proposers initial proposal number of 1
                response = in.readLine(); // wait for response
                recipient = Integer.parseInt(response.split(":")[1]);
                newProposalNumber = Integer.parseInt(response.split(":")[3]);
                if(newProposalNumber > proposalNumber){
                    // if the new proposal number is greater than the old proposal number, send back a promise message and update proposal number
                    // promise is of format <type of message>:<sender>:<recipient>:<previous proposal number>:<previous proposal value>
                    request = "promise:N" + memberID + ":" + recipient + ":" + previous_proposal_number + ":" + previous_proposal_value;
                    proposalNumber = newProposalNumber;
                }
                else{
                    request = "promiseNack:";
                }
                out.println(request);
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
