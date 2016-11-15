// The struncture and the idea comes from 
//http://pirate.shu.edu/~wachsmut/Teaching/CSAS2214/Virtual/Lectures/chat-client-server.html
package chatserver;

import java.io.*;
import java.net.*;

public class ChatServerThread extends Thread {

    private Socket socket;
    private ChatServer server;
    public int userID;
    public String name = "";
    public DataInputStream in;
    public DataOutputStream out;

    public ChatServerThread(ChatServer server, Socket socket) {
        super();
        this.server = server;
        this.socket = socket;

        userID = socket.getPort();
    }

    //open the connection between client and server
    public void setIO() throws IOException {
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            name = in.readUTF();

        } catch (IOException e) {
            System.out.println("Error reading input from id:" + userID);
            server.remove(userID);
            stop();
        }
        while (1 == 1) {
            try {
                server.transmitInput(userID, in.readUTF());

            } catch (IOException e) {
                System.out.println("Error reading input from id:" + userID);
                server.remove(userID);
                stop();
            }
        }
    }

    public void close() throws IOException {
        socket.close();
        in.close();
        out.close();
    }

    public void sendMessege(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error in printing the msg");
            server.remove(userID);
        }
    }
}
