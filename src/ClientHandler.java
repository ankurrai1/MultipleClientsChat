import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements  Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername =  bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: "+ clientUsername + " has entered the chat!");

        } catch (IOException exception) {
            closeClient(socket, bufferedReader, bufferedWriter);
            exception.printStackTrace();
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            }
            catch (IOException e) {
                closeClient(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String message){
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if(!clientHandler.clientUsername.equals(this.clientUsername)) {
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine(); // to maintain enter "I AM DONE WRITING" like enter
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeClient(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: "+ clientUsername + " has left the chat!");
    }

    public void closeClient (Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                // closing the socket will close input and output stream as well so we don't have to take care of it
                socket.close();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }
}
