import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;

    public Client(Socket socket, String username){
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.name = username;
        } catch (IOException exception) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
        try {
           bufferedWriter.write(name);
           bufferedWriter.newLine();
           bufferedWriter.flush();

           Scanner scan = new Scanner(System.in);
           if(socket.isConnected()) {
               String myMessage = scan.nextLine();
               bufferedWriter.write(name + ": " +myMessage);
               bufferedWriter.newLine();
               bufferedWriter.flush();
           }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenOthersMessages() {
         Thread thread = new Thread(new Runnable() {
             @Override
             public void run() {
                 String othersMessages;
                 while (socket.isConnected()){
                     try {
                         othersMessages = bufferedReader.readLine();
                         System.out.println(othersMessages);
                     } catch (IOException e) {
                         closeEverything(socket, bufferedReader, bufferedWriter);
                     }
                 }
             }
         });
         thread.start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        Socket socket1 = new Socket("localhost", 9654); // can create Enum to hold const values or proxy to connect
        Client client = new Client(socket1, username);
        client.listenOthersMessages();
        client.sendMessage();
    }
}
