import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MemberNode {

    public static void main(String[] args){
        // defining hostname and port number of centralised communication controller
        String hostName = "localhost";
        int portNumber = 4567;

        // defining variable to keep track of proposal numbers
        int proposalNumber = 1;

        // defining variables to determine functionality for specific members
        boolean isProposer = false;
        int memberID = 0;

        if(args.length != 2){
            System.err.println("Usage: java MemberNode <memberID> <proposer status>");
            return;
        }

        try {
            memberID = Integer.parseInt(args[0]);

            if(args[1].equals("true")) {
                isProposer = true;
            }
            else if(!(args[1].equals("false"))) {
                System.err.println("Please enter a valid proposer status");
                return;
            }
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
            // requests can be in different formats depending on the message being sent
            String request;
            String response;
            int recipient;
            int previous_proposal_number = 0;
            int previous_proposal_value = 0;

            if(isProposer){
                // proposal request is in the format of <type of message>:<sender>:<recipient>:<current proposal number>
                request = "proposal:N" + memberID + ":broadcast:" + proposalNumber;
                proposalNumber++;
                out.println(request);
            }
            else{
                System.out.println("waiting for message...");
                response = in.readLine(); // wait for response
                System.out.println(response);
                recipient = Integer.parseInt(response.split(":")[1]);
                if(proposalNumber < Integer.parseInt(response.split(":")[3])){
                    // if proposal number received is greater than most recently accepted proposal, send back a promise message
                    // promise is of format <type of message>:<sender>:<recipient>:<previous proposal number>:<previous proposal value>
                    request = "promise:N" + memberID + ":" + recipient + ":" + previous_proposal_number + ":" + previous_proposal_value;
                }
                else{
                    request = "promiseNack";
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
