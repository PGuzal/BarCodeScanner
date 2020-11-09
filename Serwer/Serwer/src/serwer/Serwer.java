
package serwer;

import java.io.*;
import java.util.*;
import java.net.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
        List data = new ArrayList<>();
        data.add("20625071");
        data.add(1);
        data.add(2);
        data.add(3);
        data.add(4);
        data.add(5);
        data.add(6);
        data.add(7.4);
        while (true) {
            try {
                //odbior wiadomoœci
            	connectDB(true, data);
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
            
    public void connectDB(Boolean isGet, List data) {
    	List results = new ArrayList();
    	Boolean inDB = false;
    	try{
    		Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/barcode_data","root", "");
            Statement statement = connection.createStatement();
            System.out.print("Database is connected !");
            String sql_check = "SELECT * FROM barcode WHERE code = "+data.get(0)+";";
            ResultSet check = statement.executeQuery(sql_check);
            if(check.next()) {
            	if (check.getString("code").length()==0) {
            		inDB = false;
            	}
            	else {
            		inDB = true;
            	}
            }
            System.out.print(inDB);;
            if(isGet) {
            	if(inDB) {
	            	String sql_select = "SELECT * FROM barcode WHERE code = "+data.get(0)+";";
	            	ResultSet result = statement.executeQuery(sql_select);
	            	if (result.next()) {
		            	results.add(result.getString("code"));
		            	results.add(result.getString("calorie"));
		            	results.add(result.getString("fat"));
		            	results.add(result.getString("saturated"));
		            	results.add(result.getString("carb"));
		            	results.add(result.getString("sugar"));
		            	results.add(result.getString("protein"));
		            	results.add(result.getString("sodium"));
		            	System.out.print(results);
	            	}
            	}
            	else {
            		System.out.println("No data");
            	}
            }	
            else {
            	if(inDB) {
            		System.out.println("Already exists");
            	}
            	else {
	            	String sql_insert = "INSERT INTO barcode(code, calorie, fat, saturated, carb, sugar, protein, sodium) VALUES ("+data.get(0)+","+data.get(1)+","+data.get(2)+","+data.get(3)+","+data.get(4)+","+data.get(5)+","+data.get(6)+","+data.get(7)+");";
	            	statement.executeUpdate(sql_insert);
            	}
            }
            statement.close();
            connection.close();
         }
         catch(Exception e) {
            System.out.print("Do not connect to DB - Error:"+e);
         }
    }
    
}
