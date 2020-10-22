
package serwer;

import java.io.*;
import java.util.*;
import java.net.*;

public class Serwer {

    static Vector<ClientHandler> MembersList = new Vector<>();
    static int i = 0;

    public static void main(String[] args) throws IOException {

        ServerSocket serversocket = new ServerSocket(1234);
        Socket socket;
        System.out.println("Server is working");
        while (true) {
            socket = serversocket.accept();
            System.out.println("New client request received : " + socket);
            DataInputStream inputData = new DataInputStream(socket.getInputStream());
            DataOutputStream outputData = new DataOutputStream(socket.getOutputStream());
            ClientHandler newMember = new ClientHandler(socket, "members " + i, inputData, outputData);
            Thread t = new Thread(newMember);
            MembersList.add(newMember);
            t.start();
            i++;
        }
    }
}

class ClientHandler implements Runnable {
    Scanner scn = new Scanner(System.in);
    private String memberName;
    final DataInputStream inputData;
    final DataOutputStream outputData;
    Socket socket;
    boolean isloggedin; 
    static int socketClose = 0;

    public ClientHandler(Socket socket, String memberName,
            DataInputStream inputData, DataOutputStream outputData) {
        this.inputData = inputData;
        this.outputData = outputData;
        this.memberName = memberName;
        this.socket = socket;
        this.isloggedin=true; 
    }
    @Override
    public void run() {
        String received;
        
        while (true) {
            try {
                received = inputData.readUTF();
                System.out.println("["+this.memberName+"] "+received);
                String Message= received;
                if (received.equals("logout")) {
                    this.isloggedin=false; 
                    break;
                }else{
                 for (ClientHandler mc : Serwer.MembersList) {
                    if(mc.memberName.equals(this.memberName) == false && mc.isloggedin == true) {
                    mc.outputData.writeUTF("["+this.memberName + "]" +": " + Message);
                    }
                }}}
            catch (IOException e) {
                e.printStackTrace();
            }}
            try{
                socketClose=0;
                 for (ClientHandler mc: Serwer.MembersList){
                    if(mc.isloggedin== true){
                        this.inputData.close();
                        this.outputData.close();
                        break;
                    }else{
                        socketClose++;
                    }
                }
                if(socketClose == Serwer.MembersList.size()){
                        this.inputData.close();
                        this.outputData.close();
                        socket.close();
                        System.exit(0);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

    }
}
