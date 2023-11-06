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
     * @return an error code: 1 if the function fails, 0 otherwise.
     */
    public static int initialiseSockets(ServerSocket serverSocket, Socket[] memberSockets, PrintWriter[] memberOuts, BufferedReader[] memberIns){
        try{
            for(int i=0; i<9; i++){
                memberSockets[i] = serverSocket.accept();
                memberOuts[i] = new PrintWriter(memberSockets[i].getOutputStream());
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
     * @return an error code: 1 if the function fails, 0 otherwise.
     */
    public static int closeSockets(ServerSocket serverSocket, Socket[] memberSockets, PrintWriter[] memberOuts, BufferedReader[] memberIns){
        try {
            serverSocket.close();
            for (int i = 0; i < 9; i++) {
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

        int port = 4567;

        while(true) {
            try{
                // initialisation of all required sockets, inputs and output streams
                ServerSocket serverSocket = new ServerSocket(port);
                Socket[] memberSockets = new Socket[9];
                PrintWriter[] memberOuts = new PrintWriter[9];
                BufferedReader[] memberIns = new BufferedReader[9];

                // function to initialise all the sockets
                if(initialiseSockets(serverSocket, memberSockets, memberOuts, memberIns) == 1){
                    return;
                }

                for(int i=0; i<9; i++){
                    System.out.println("i: " + i + " " + memberIns[i].readLine());
                }

                // function to close all sockets once finished using them
                if(closeSockets(serverSocket, memberSockets, memberOuts, memberIns) == 1){
                    return;
                }


            } catch (IOException e) {
                System.err.println("Exception caught when trying to create sockets");
                return;
            }
        }

    }

}
