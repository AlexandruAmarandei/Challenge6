package chatuser;
// The struncture and the idea comes from 
//http://pirate.shu.edu/~wachsmut/Teaching/CSAS2214/Virtual/Lectures/chat-client-server.html

import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.*;

public class ChatUser implements Runnable {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Thread userThread = null;
    private BufferedReader br;
    private String userName;
    private ChatUserThread user;
    private JPanel contentPane;
    private boolean verification = false;
    private List list = new List();

    public ChatUser(String name, int port) {

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setSize(500, 500);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(3, 3, 3, 3));
        frame.setContentPane(contentPane);
        contentPane.setLayout(null);
        JTextField input = new JTextField(100);
        JTextField nameField = new JTextField(20);
        nameField.setBounds(390, 340, 80, 40);
        contentPane.add(nameField);
        input.setBounds(4, 400, 400, 60);
        input.setVisible(true);
        contentPane.add(input);
        JButton sendButton = new JButton("Send");
        JButton setName = new JButton("Set");
        setName.setBounds(390, 300, 80, 40);
        contentPane.add(setName);

        setName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (verification == false) {
                    userName = nameField.getText().toString();
                    input.setText("");
                    try {
                        out.writeUTF(userName);
                        out.flush();
                    } catch (IOException ex) {
                        System.out.println("Error writing username");
                    }
                }
                verification = true;
            }

        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (verification) {
                    try {
                        String aux = input.getText().toString();
                        input.setText("");
                        if (!input.equals("")) {
                            out.writeUTF(aux);
                            out.flush();
                        }

                        //list.add(aux+ "\n");
                    } catch (IOException eio) {
                        System.out.println("Error in writing output!");
                        stop();
                        userThread = null;
                    }
                }
            }

        });

        sendButton.setBounds(405, 400, 80, 60);

        sendButton.setVisible(true);
        list.setBounds(1, 1, 380, 380);
        list.setVisible(true);
        contentPane.add(sendButton);
        contentPane.add(list);
        contentPane.setVisible(true);

        br = new BufferedReader(new InputStreamReader(System.in));
        try {
            socket = new Socket(name, port);
            System.out.println("Succesufully connected to:" + socket);
            start();
        } catch (IOException e) {
            System.out.println("Error in trying to reach a port");
        }
    }

    @Override
    public void run() {

        try {
            userName = br.readLine();
        } catch (IOException ex) {
            System.out.println("Error while reading name");
        }
        try {
            out.writeUTF(userName);
            out.flush();
        } catch (IOException ex) {
            System.out.println("Error writing username");
        }
        while (userThread != null) {
            try {
                String aux = br.readLine();
                out.writeUTF(aux);
                out.flush();
            } catch (IOException e) {
                System.out.println("Error in writing output!");
                stop();
                userThread = null;
            }
        }
    }

    public void interpret(String message) {
        if (message.equals("/Exit")) {
            System.out.println("Good bye!");
            stop();
        } else {
            list.add(message + "\n");
            System.out.println(message);
        }
    }

    public void start() throws IOException {
        in = new DataInputStream(System.in);
        out = new DataOutputStream(socket.getOutputStream());

        if (userThread == null) {

            user = new ChatUserThread(this, socket);
            userThread = new Thread(this);
            userThread.start();
        }
    }

    public static void main(String args[]) {

        ChatUser chatUser = new ChatUser("localhost", 1500);
    }

    public void stop() {
        if (userThread != null) {
            userThread.stop();
            userThread = null;
        }
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Problem in closing IO");
        }
        user.terminate();
        user.stop();
    }
}
