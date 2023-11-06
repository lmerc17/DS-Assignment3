import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class CentralCommunicationController {

    public static void main(String[] args){

        int port = 4567;
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
