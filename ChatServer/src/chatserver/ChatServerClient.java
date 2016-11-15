package chatserver;

import java.io.*;

public class ChatServerClient extends Thread {

    private BufferedReader in;
    private ChatServer server;

    public ChatServerClient(ChatServer server) {
        in = new BufferedReader(new InputStreamReader(System.in));
        this.server = server;
        //start();
    }

    @Override
    public void run() {
        while (true) {
            try {

                server.serverInput(in.readLine());
            } catch (IOException e) {
                System.out.println("Error in reading the input");

            }
        }
    }

    public void terminate() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            System.out.println("Error in closing the input");
        }
    }
}
