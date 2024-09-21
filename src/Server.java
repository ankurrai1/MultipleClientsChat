import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import java.util.InvalidPropertiesFormatException;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try {
            while (!serverSocket.isClosed()){
                //blocking method
                Socket socket = serverSocket.accept();
                System.out.println("A NEW CLIENT HAS CONNECTED !!");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException exception) {

        }
    }

    public void closeSocket() {
        try {
            if(serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
