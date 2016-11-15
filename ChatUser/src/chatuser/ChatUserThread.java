// The struncture and the idea comes from 
//http://pirate.shu.edu/~wachsmut/Teaching/CSAS2214/Virtual/Lectures/chat-client-server.html
package chatuser;

import java.io.*;
import java.net.*;

public class ChatUserThread extends Thread {

    private ChatUser user;
    private Socket socket;
    private DataInputStream in;

    public ChatUserThread(ChatUser user, Socket socket) {
        this.user = user;
        this.socket = socket;

        try {
            in = new DataInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Problem in reading input");
        }

        start();
    }

    public void run() {

        while (true) {
            try {

                user.interpret(in.readUTF());
            } catch (IOException e) {
                System.out.println("Error in reading the output");
                user.stop();
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
