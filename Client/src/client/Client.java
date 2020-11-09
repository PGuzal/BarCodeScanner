/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    final static int ServerPort = 1234;

    public static void main(String args[]) throws UnknownHostException, IOException {

        Scanner scn = new Scanner(System.in);
        InetAddress ip = InetAddress.getByName("localhost");
        Socket socket = new Socket(ip, ServerPort);
        DataInputStream dataInput = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Are you connected");
                try{
                while (true) {
                    try{
                    String message = scn.nextLine();
                    //wysylanie
                        dataOutput.writeUTF(message);
                        if(message.equals("logout")){break;}
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    }
                socket.close();
                System.exit(0);
                }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        });
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String messageMember = dataInput.readUTF();
                        System.out.println(messageMember);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();

        readMessage.start();

    }
}
