import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class CentralCommunicationController {
    /** Method to initialise sockets upon Central Communication Controller startup
     * @param serverSocket the server socket that will listen for the member nodes.
     * @param memberSockets an array of sockets consisting of the member nodes.
     * @param memberOuts an array of PrintWriters allowing data to be sent to the member nodes.
     * @param memberIns an array of BufferedReaders allowing data to be read from the member nodes.
     * @param number_of_nodes an integer storing the total number of nodes
     * @return an error code: 1 if the function fails, 0 otherwise.
     */
    public static int initialiseSockets(ServerSocket serverSocket, Socket[] memberSockets, PrintWriter[] memberOuts, BufferedReader[] memberIns, int number_of_nodes){
        try{
            for(int i=0; i<number_of_nodes; i++){
                memberSockets[i] = serverSocket.accept();
                memberOuts[i] = new PrintWriter(memberSockets[i].getOutputStream(), true);
                memberIns[i] = new BufferedReader(new InputStreamReader(memberSockets[i].getInputStream()));
            }
        } catch (IOException e) {
            System.err.println("Exception caught when trying to listen on port 4567 or listening for a connection");
            return 1;
        }
        return 0;
    }

    /** Method to close sockets once finished using them
     * @param serverSocket the server socket that will listen for the member nodes.
     * @param memberSockets an array of sockets consisting of the member nodes.
     * @param memberOuts an array of PrintWriters allowing data to be sent to the member nodes.
     * @param memberIns an array of BufferedReaders allowing data to be read from the member nodes.
     * @param number_of_nodes an integer storing the total number of nodes
     * @return an error code: 1 if the function fails, 0 otherwise.
     */
    public static int closeSockets(ServerSocket serverSocket, Socket[] memberSockets, PrintWriter[] memberOuts, BufferedReader[] memberIns, int number_of_nodes){
        try {
            serverSocket.close();
            for (int i=0; i<number_of_nodes; i++) {
                memberSockets[i].close();
                memberOuts[i].close();
                memberIns[i].close();
            }
        }
        catch(IOException e){
            System.err.println("Could not close sockets");
            return 1;
        }
        return 0;
    }

    public static void main(String[] args){

        // initialise port number for server and number of nodes to connect
        int port = 4567;
        int number_of_nodes = 9;

        // try and catch statement to ensure sockets act accordingly
        try{
            // initialisation of all required sockets, inputs and output streams
            ServerSocket serverSocket = new ServerSocket(port);
            Socket[] memberSockets = new Socket[number_of_nodes];
            PrintWriter[] memberOuts = new PrintWriter[number_of_nodes];
            BufferedReader[] memberIns = new BufferedReader[number_of_nodes];

            // function to initialise all the sockets
            if(initialiseSockets(serverSocket, memberSockets, memberOuts, memberIns, number_of_nodes) == 1){
                return;
            }

            // initialisation of used variables for sending messages
            String line;
            String messageRecipient;
            String messageType;
            int currentNode = 0;
            int recipient;

            // infinite while loop, it will cycle through the nodes checking if a message has been received from them
            while(true){
                if((line = memberIns[currentNode].readLine()) != null) { // if a message has been received for the current node

                    messageRecipient = line.split(":")[2]; // save the ID for the message recipient
                    if (messageRecipient.equals("broadcast")) { // if the ID is broadcast
                        for (int i = 0; i < number_of_nodes; i++){ // for all the nodes except the current one
                            if(i!=currentNode){
                                memberOuts[i].println(line); // send data
                                // memberOuts[i].flush(); // ensure data is sent immediately
                            }
                        }
                    }
                    else if(messageRecipient.contains("N")){ // if the ID is specific to a node, determine which node
                        recipient = Integer.parseInt(messageRecipient.substring(1)) - 1;
                        memberOuts[recipient].println(line); // send the message to the node
                        // memberOuts[recipient].flush(); // ensure data is sent immediately
                    }
                    else{ // if none of the conditions are satisfied, an invalid message has been sent
                        System.err.println("Invalid message recipient received, message could not be passed on");
                        return;
                    }

                    messageType = line.split(":")[0]; // once all messages have been sent, determine the type of message sent off
                    if(messageType.equals("decide")){ // if a decide message has been broadcast out, the vote has been settled, therefore no further communication required
                        break;
                    }

                }
                currentNode = (currentNode + 1) % number_of_nodes; // to ensure currentNode does not surpass number of connected nodes
            }

            // function to close all sockets once finished using them
            if(closeSockets(serverSocket, memberSockets, memberOuts, memberIns, number_of_nodes) == 1){
                return;
            }

        }
        catch(IOException e){
            System.err.println("Exception caught when trying to create or communicate to sockets");
        }
        catch(NumberFormatException e){
            System.err.println("A non-number string has been attempted to be converted into a number");
        }
        catch(ArrayIndexOutOfBoundsException e){
            System.err.println("Access at and out of bounds index for an array has been attempted");
        }


    }

}
