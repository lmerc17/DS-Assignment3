import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class CentralCommunicationController {

    public static boolean memberNodeInitialisation(Socket[] memberNodes, PrintWriter[] memberNodesOut, BufferedReader[] memberNodesIn, ServerSocket serverSocket){

        try {
            for (int i=0; i<9; i++) {
                memberNodes[i] = serverSocket.accept();
                memberNodesOut[i] = new PrintWriter(memberNodes[i].getOutputStream(), true);
                memberNodesIn[i] = new BufferedReader(new InputStreamReader(memberNodes[i].getInputStream()));
            }
        }
        catch(IOException e){
            System.err.println("Exception caught when trying to listen on port 4567 or listening for a connection");
            return false;
        }

        return true;

    }

    public static void main(String[] args){

        int port = 4567;
        ServerSocket serverSocket = new ServerSocket(port);
        Socket[] memberNodes;

        if(!memberNodeInitialisation(memberNodes, memberNodesOut, memberNodesIn, serverSocket)){
            return;
        }

        while(true) {
            try (ServerSocket serverSocket = new ServerSocket(port);
                 Socket clientSocket = serverSocket.accept();
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ) {
                System.out.println(in.readLine());
            } catch (IOException e) {
                System.err.println("Exception caught when trying to listen on port 4567 or listening for a connection");
                return;
            }
        }

    }

}
