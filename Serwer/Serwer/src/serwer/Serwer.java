
package serwer;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.net.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import com.sun.net.httpserver.*;

public class Serwer {

    public static void main(String[] args) throws IOException {
    	HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8000), 0);
    	ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
    	server.createContext("/receive", new  HttpHandler() {
    		@Override
    	    public void handle(HttpExchange exchange) throws IOException {
    			try {
    				List data = new ArrayList();
    				JSONObject json = requestJSON(exchange);
    				data.add(json.get("code").toString());
    				String response = connectDB(true, data, exchange);
    				if (response!="NO") {
    					respondJSON(exchange, response);
    				}
    				else {
    					respondJSON(exchange, "{\"code\": \"brakdb\"}");
    				}
    	        } catch (IOException e) {    	        	
    	        	e.printStackTrace();
                    throw e;
                }	    	    	
    		}
    	});
    	//obs³uga zapisywania nowych danych do bazy
    	server.createContext("/save", new  HttpHandler() {
    		@Override
    	    public void handle(HttpExchange exchange) throws IOException {
    			try {
    				List data = new ArrayList();
    				JSONObject json = requestJSON(exchange);
    				data.add(json.get("code").toString());
    				data.add(json.get("name").toString());
    				data.add(json.get("calorie").toString());
    				data.add(json.get("fat").toString());
    				data.add(json.get("saturated").toString());
    				data.add(json.get("carb").toString());
    				data.add(json.get("sugar").toString());
    				data.add(json.get("protein").toString());
    				data.add(json.get("sodium").toString());
    				String response = connectDB(false, data, exchange);
    				if (response!="NO") {
    					respondJSON(exchange, response);
    				}
    				else {
    	    	        	respondJSON(exchange, "{\"message\": \"Nie uda³o siê nawi¹zaæ po³¹czenia z baz¹.\"}");
    				}
    				
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

    //odbieranie danych w formacie JSON
    public static JSONObject requestJSON(HttpExchange exchange) throws IOException {
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
    
    //wysy³anie danych w formacie JSON
    public static void respondJSON(HttpExchange exchange, String response) throws IOException {
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
    
    //wymiana danych miêdzy serwerem a baz¹
    public static String connectDB(Boolean isGet, List data, HttpExchange exchange) {
    	List results = new ArrayList();
    	Boolean inDB = false;
    	String jsonString = "";
    	try{
    		Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/barcode_data?useUnicode=true&characterEncoding=utf-8","root", "");
            Statement statement = connection.createStatement();
            System.out.println("Database is connected!");
            //sprawdzanie czy przes³any kod znajduje siê ju¿ w bazie
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
            //dla pobierania danych na podstawie kodu kreskowego 
            if(isGet) {
            	//dla obecnoœci kodu w bazie - pobranie danych
            	if(inDB) {
	            	String sql_select = "SELECT * FROM barcode WHERE code = "+data.get(0)+";";
	            	ResultSet result = statement.executeQuery(sql_select);
	            	if (result.next()) {
	            		jsonString = "{\"code\": "+result.getString("code")+", \"name\": \""+result.getString("name")+"\", \"calorie\": "+result.getString("calorie")+", \"fat\": "+result.getString("fat")+", \"saturated\": "+result.getString("saturated")+", \"carb\": "+result.getString("carb")+", \"sugar\": "+result.getString("sugar")+", \"protein\": "+result.getString("protein")+", \"sodium\": "+result.getString("sodium")+"}";
	            	}
            	}
            	//dla braku kodu w bazie - ustawienie danych na informacje o braku kodu w bazie
            	else {
            		jsonString = "{\"code\": \"brak\"}";
            	}
            }	
            //dla zapisywania nowych danych do bazy
            else {
            	//dla obecnosci kodu w bazie - stworzenie wiadomoœci informuj¹cej o niemo¿liwoœci wykonania procesu
            	if(inDB) {
            		jsonString = "{\"message\": \"Pobrany kod jest ju¿ obecny jest w bazie.\"}";
            	}
            	//dla braku kodu w bazie - stworzenie wiadomoœci informuj¹cej o wykonanym procesie
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
            jsonString = "NO";
         }
		return jsonString;
    }
}

