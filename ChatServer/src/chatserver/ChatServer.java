// The struncture and the idea comes from 
//http://pirate.shu.edu/~wachsmut/Teaching/CSAS2214/Virtual/Lectures/chat-client-server.html
package chatserver;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class ChatServer implements Runnable {

    private ArrayList<ChatServerThread> users = new ArrayList<ChatServerThread>();
    private ServerSocket server = null;
    private Thread serverThread = null;
    private ChatServerClient serverClient;
    private boolean openOutput = true;

    public ChatServer(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Connection establieshed on port:" + port);
            start();
        } catch (IOException e) {
            System.out.println("Error in establishing a connection");
        }

    }

    public void start() {
        serverThread = new Thread(this);
        serverThread.start();
        serverClient = new ChatServerClient(this);
        serverClient.start();

    }

    @Override
    public void run() {
        while (serverThread != null) {
            try {

                addUser(server.accept());
                System.out.println("User connected!");
            } catch (IOException e) {
                System.out.println("Problem with connection");
                serverThread = null;
            }
        }
    }

    public void addUser(Socket userToAdd) {
        users.add(new ChatServerThread(this, userToAdd));
        try {
            users.get(users.size() - 1).setIO();
            users.get(users.size() - 1).start();
        } catch (IOException e) {
            System.out.println();
        }
    }

    public static void main(String args[]) {
        ChatServer chatServer = new ChatServer(1500);
    }

    public int findID(int ID) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).userID == ID) {
                return i;
            }
        }
        return -1;
    }

    public synchronized void transmitInput(int ID, String input) {
        
        int index = findID(ID);
        System.out.println(users.get(index).name + ": " + input);
        if (input.charAt(0) == '/') {
            if (input.equals("/Exit")) {
                users.get(index).sendMessege("/Exit");
                remove(ID);
                for (int i = 0; i < users.size(); i++) {

                    users.get(i).sendMessege("Server: User " + ID + " has disconnected");
                }
            } else if (input.length() > 8 && input.substring(0, 7).equals("/Wisper")) {
                int spaceIndex = input.indexOf(' ', 8);
                String wisperName = input.substring(8, spaceIndex);
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).name.equals(wisperName)) {
                        users.get(i).sendMessege("Wispered from " + users.get(index).name + ": " + input.substring(spaceIndex + 1));
                    }
                }
            } else if (input.equals("/Users")) {
                for (int i = 0; i < users.size(); i++) {
                    users.get(index).sendMessege(users.get(i).name);
                }
            } else {
                users.get(index).sendMessege("Server: No command found");
            }
        } else if (openOutput) {
            for (int i = 0; i < users.size(); i++) {
                users.get(i).sendMessege(users.get(index).name + ": " + input);
            }
        }

    }

    public synchronized void serverInput(String input) {
        if (input.substring(0, 5).equals("/Kick")) {
            String name = input.substring(6);
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).name.equals(name)) {
                    users.get(i).sendMessege("You were disconected!");
                    users.get(i).sendMessege("/Exit");
                    remove(users.get(i).userID);
                }
            }
        } else if (input.substring(0, 5).equals("/Mute")) {
            if (openOutput == true) {
                for (int i = 0; i < users.size(); i++) {
                    users.get(i).sendMessege("Server: Chat Muted");
                }
            }
            openOutput = false;
        } else if (input.substring(0, 7).equals("/UnMute")) {
            if (openOutput == false) {
                for (int i = 0; i < users.size(); i++) {
                    users.get(i).sendMessege("Server: Chat Unmuted");
                }
            }
            openOutput = true;

        } else {
            for (int i = 0; i < users.size(); i++) {
                users.get(i).sendMessege("Server: " + input);
            }
        }
    }

    public synchronized void remove(int ID) {
        int index = findID(ID);
        if (index >= 0) {
            try {
                users.get(index).close();
            } catch (IOException ex) {
                System.out.println("Error in closing a thread");
                users.get(index).stop();
            }
            users.remove(index);
        }
    }

}
