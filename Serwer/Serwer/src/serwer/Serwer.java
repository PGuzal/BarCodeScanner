
package serwer;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import com.sun.net.httpserver.*;

public class Serwer {

    public static void main(String[] args) throws IOException {
    	HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8000), 0);
    	ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
    	server.createContext("/get", new  HttpHandler() {
    		@Override
    	    public void handle(HttpExchange exchange) throws IOException {
    			try {
    				List data = new ArrayList();
    				JSONObject json = receiveJSON(exchange);
    				data.add(json.get("code").toString());
    				String response = connectDB(true, data);
	    	    	sendJSON(exchange, response);
    	        } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }	    	    	
    		}
    	});
    	server.createContext("/save", new  HttpHandler() {
    		@Override
    	    public void handle(HttpExchange exchange) throws IOException {
    			try {
    				List data = new ArrayList();
    				JSONObject json = receiveJSON(exchange);
    				data.add(json.get("code").toString());
    				data.add(json.get("name").toString());
    				data.add(json.get("calorie").toString());
    				data.add(json.get("fat").toString());
    				data.add(json.get("saturated").toString());
    				data.add(json.get("carb").toString());
    				data.add(json.get("sugar").toString());
    				data.add(json.get("protein").toString());
    				data.add(json.get("sodium").toString());
    				String response = connectDB(false, data);
	    	    	sendJSON(exchange, response);
    	        } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
    		}
    	});
    	server.setExecutor(threadPoolExecutor);
    	server.start();
    	System.out.println("Server started on port 8000");
    }

    public static JSONObject receiveJSON(HttpExchange exchange) throws IOException {
    	try {
    		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8"));
    		JSONTokener tokener = new JSONTokener(bufferedReader);
    	    JSONObject json = new JSONObject(tokener);
    		return json;
    	}catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }    
    
    public static void sendJSON(HttpExchange exchange, String response) throws IOException {
    	try {
    		byte[] bytes = response.getBytes("UTF-8");
    		exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
    		exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
    	} catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    public static String connectDB(Boolean isGet, List data) {
    	List results = new ArrayList();
    	Boolean inDB = false;
    	String jsonString = "";
    	try{
    		Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/barcode_data?useUnicode=true&characterEncoding=utf-8","root", "");
            Statement statement = connection.createStatement();
            System.out.println("Database is connected!");
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
            if(isGet) {
            	if(inDB) {
	            	String sql_select = "SELECT * FROM barcode WHERE code = "+data.get(0)+";";
	            	ResultSet result = statement.executeQuery(sql_select);
	            	if (result.next()) {
	            		jsonString = "{\"code\": "+result.getString("code")+", \"name\": "+result.getString("name")+", \"calorie\": "+result.getString("calorie")+", \"fat\": "+result.getString("fat")+", \"saturated\": "+result.getString("saturated")+", \"carb\": "+result.getString("carb")+", \"sugar\": "+result.getString("sugar")+", \"protein\": "+result.getString("protein")+", \"sodium\": "+result.getString("sodium")+"}";
	            	}
            	}
            	else {
            		jsonString = "{\"code\": \"brak\"}";
            	}
            }	
            else {
            	if(inDB) {
            		jsonString = "{\"message\": \"Pobrany kod jest ju¿ obecny jest w bazie.\"}";
            	}
            	else {
	            	String sql_insert = "INSERT INTO barcode(code, name, calorie, fat, saturated, carb, sugar, protein, sodium) VALUES ("+data.get(0)+",\""+data.get(1)+"\","+data.get(2)+","+data.get(3)+","+data.get(4)+","+data.get(5)+","+data.get(6)+","+data.get(7)+","+data.get(8)+");";
	            	statement.executeUpdate(sql_insert);
	            	jsonString = "{\"message\": \"Dodano dane do bazy.\"}";
            	}
            }
            statement.close();
            connection.close();
         }
         catch(Exception e) {
            System.out.println("Do not connect to DB - Error:"+e);
         }
		return jsonString;
    }
}

