 package com.StanfordTimeCard.StanfordTimeCard;
import java.io.File;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.sql.Connection;
 import java.sql.DriverManager;
import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.concurrent.Callable;
 import java.util.concurrent.ExecutionException;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;
 import java.util.concurrent.Future;

 import javax.swing.JOptionPane;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
 
 import com.opencsv.CSVWriter;

 
 
 public class MySQLConnect
 {
	   private final String CLASSNAME = "MySQLConnect";
	   private String jdbcUrl = null;
	   private String userid = null;
	   private String password = null;
	   private final String PAYROLL_HOURS_TABLE = "dailyhours";
	 
	   
	   public List<DailyInfoModel> list = new ArrayList<>();
	   public DailyInfoModel similarDate;
	   private Double databaseTotalHours = Double.valueOf(0.0D);
	   private Double totalRegHours = Double.valueOf(0.0D);
	   private Double totalOTHours = Double.valueOf(0.0D);
	   
	   public MySQLConnect(String jdbcUrl, String userid, String password) {
		     this.jdbcUrl = jdbcUrl;
		     this.userid = userid;
		     this.password = password;
	   }
	 
	 
	 
	   public String getURL() {

		return jdbcUrl;
	}

	public String getID() {

		return userid;
	}

	public  String getPW() {

		return password;
	}


	   public DailyInfoModel getSimilarDate() {
		     return this.similarDate;
	   }
	   public List<DailyInfoModel> getList() {
		     return this.list;
	   }
	   
	   public Double getDatabaseTotalHours() {
		     return this.databaseTotalHours;
	   }
	   
	   public void clearList() {
		     this.list.clear();
	   }
	 
	 
	   
	   public Boolean isConnected() {
		     Boolean isDBaseConnected = null;
		 
		     
		     Connection conn = null;
		     
		     try {
			       conn = DriverManager.getConnection(this.jdbcUrl, this.userid, this.password);
			       conn.close();
			       isDBaseConnected = Boolean.valueOf(true);
		     } catch (Exception e) {
			       
			       isDBaseConnected = Boolean.valueOf(false);
			       JOptionPane.showMessageDialog(null, "MySQLConnect : ERROR. Unable to access database information.\nCheck database access password");
		     } 
		     return isDBaseConnected;
	   }
	 
	   
	   public Boolean placeItemInDatabase(DailyInfoModel item) {
		     Connection conn = null;
		     Boolean isDataAccepted = Boolean.valueOf(false);
		     
		     try {
			       if (isConnected().booleanValue()) {
				         String query = "INSERT INTO dailyhours (DATE, DAY, EVENTNUMBER,  EVENTNAME, TIME, REGHOURS, OTHOURS ) VALUES('" + 
						           item.getDate() + "','" + item.getDay() + "'," + item.getEventNumber() + ",'" + 
						           item.getEventName() + "','" + item.getTime() + "'," + item.getRhours() + "," + item.getOhours() + ")";
				 
				 
				         
				         conn = DriverManager.getConnection(this.jdbcUrl, this.userid, this.password);
				         Statement myStatement = conn.createStatement();
				         myStatement = conn.createStatement();
				         myStatement.executeUpdate(query);
				         isDataAccepted = Boolean.valueOf(true);
			       } 
		     } catch (SQLException e) {
			       
			       e.printStackTrace();
			       JOptionPane.showMessageDialog(null, e.toString());
		     } finally {
			       
			       try {
				         conn.close();
			       } catch (SQLException e) {
				         
				         e.printStackTrace();
				         JOptionPane.showMessageDialog(null, "MySQLConnect ERROR: Unable to close connection to database. Try Again");
			       } 
		     } 
		     return isDataAccepted;
	   }
	 
	   
	   public void removeEntryFromDatabase1(String query) {
		     Connection conn = null;
		     try {
			       if (isConnected().booleanValue())
			       {
				         conn = DriverManager.getConnection(this.jdbcUrl, this.userid, this.password);
				         Statement myStatement = conn.createStatement();
				         myStatement = conn.createStatement();
				         myStatement.executeUpdate(query);
			       }
			     
		     } catch (SQLException e) {
			 
			       
			       JOptionPane.showMessageDialog(null, e.toString());
		     } finally {
			       try {
				         conn.close();
			       } catch (SQLException e) {
				 
				         
				         JOptionPane.showMessageDialog(null, e.toString());
			       } 
		     } 
	   }
	 
	 
	   
	   public void updateDatabase(String result) {
		     System.out.println(result);
		     Connection conn = null;
		     try {
			       if (isConnected().booleanValue()) {
				         String query = result;
				         conn = DriverManager.getConnection(this.jdbcUrl, this.userid, this.password);
				         Statement myStatement = conn.createStatement();
				         myStatement = conn.createStatement();
				         myStatement.executeUpdate(query);
			       }
			     
		     } catch (SQLException e) {
			       
			       e.printStackTrace();
		     } finally {
			       try {
				         conn.close();
			       } catch (SQLException e) {
				         
				         e.printStackTrace();
			       } 
		     } 
	   }
	 
	 
	   
	   public Boolean refreshDatabase(List<DailyInfoModel> item) {
		     Boolean isSuccessful = Boolean.valueOf(false);
		     try {
			       if (isConnected().booleanValue()) {
				         
				         Connection conn = DriverManager.getConnection(this.jdbcUrl, this.userid, this.password);
				         Statement myStatement = conn.createStatement();
				         String deleteQuery = "DELETE FROM dailyhours";
				         myStatement.executeUpdate(deleteQuery);
				         
				         for (int i = 0; i < item.size(); i++) {
					           
					           String query = "INSERT INTO dailyhours (DATE, DAY, EVENTNUMBER,  EVENTNAME, TIME, REGHOURS, OTHOURS ) VALUES('" + (
							             (DailyInfoModel)item.get(i)).getDate() + "','" + ((DailyInfoModel)item.get(i)).getDay() + "'," + ((DailyInfoModel)item.get(i)).getEventNumber() + ",'" + (
									             (DailyInfoModel)item.get(i)).getEventName() + "','" + ((DailyInfoModel)item.get(i)).getTime() + "'," + ((DailyInfoModel)item.get(i)).getRhours() + "," + ((DailyInfoModel)item.get(i)).getOhours() + ")";
					           myStatement.execute(query);
				         } 
			       } 
			       isSuccessful = Boolean.valueOf(true);
		     } catch (SQLException e) {
			       
			       e.printStackTrace();
		     } 
		     
		     return isSuccessful;
	   }
	 
	 
	   
	   public void readTableRows() {
		     String COMMA = ", ";
		     
		     ExecutorService executor = Executors.newCachedThreadPool();
		     Future<List<DailyInfoModel>> myFuture = executor.submit(new Callable<List<DailyInfoModel>>()
				         {
			           
			           public List<DailyInfoModel> call() throws Exception
			           {
				             MySQLConnect.this.list.clear();
				             List<DailyInfoModel> dataArray = new ArrayList<>();
				             
				             if (MySQLConnect.this.isConnected().booleanValue()) {
					               
					               Connection conn = DriverManager.getConnection(MySQLConnect.this.jdbcUrl, MySQLConnect.this.userid, MySQLConnect.this.password);
					               Statement stmt = conn.createStatement();
					               String query = "SELECT * FROM dailyhours";
					               ResultSet rset = stmt.executeQuery(query);
					               while (rset.next()) {
						                 String stringOutput = "";
						 
						                 
						                 stringOutput = 
								                   String.valueOf(rset.getString("DATE")) + ", " + 
										                   rset.getString("DAY") + ", " + 
										                   Integer.toString(rset.getInt("EVENTNUMBER")) + ", " + 
										                   rset.getString("EVENTNAME") + ", " + 
										                   rset.getString("TIME") + ", " + 
										                   Double.toString(rset.getDouble("REGHOURS")) + ", " + 
										                   Double.toString(rset.getDouble("OTHOURS"));
						                 
						                 dataArray.add(new DailyInfoModel(stringOutput));
					               } 
					               
					               rset.close();
					               conn.close();
				             } 
				             return dataArray;
			           }
		         });
		     
		     executor.shutdown();
		     try {
			       this.list = myFuture.get();
		     } catch (InterruptedException e) {
			       
			       e.printStackTrace();
		     } catch (ExecutionException e) {
			       
			       e.printStackTrace();
		     } 
	   }
	 
	 
	 
	 
	 
	 
	   
	   public void getDateRangeResults(final String query) {
		     String COMMA = ", ";
		     ExecutorService executor = Executors.newCachedThreadPool();
		     Future<List<DailyInfoModel>> myFuture = executor.submit(new Callable<List<DailyInfoModel>>()
				         {
			           
			           public List<DailyInfoModel> call() throws Exception
			           {
				             MySQLConnect.this.list.clear();
				             List<DailyInfoModel> dataArray = new ArrayList<>();
				             if (MySQLConnect.this.isConnected().booleanValue()) {
					               
					               Connection conn = DriverManager.getConnection(MySQLConnect.this.jdbcUrl, MySQLConnect.this.userid, MySQLConnect.this.password);
					               Statement stmt = conn.createStatement();
					 
					               
					               ResultSet rset = stmt.executeQuery(query);
					               while (rset.next()) {
						                 String stringOutput = "";
						                 
						                 stringOutput = 
								                   String.valueOf(rset.getString("DATE")) + ", " + 
										                   rset.getString("DAY") + ", " + 
										                   Integer.toString(rset.getInt("EVENTNUMBER")) + ", " + 
										                   rset.getString("EVENTNAME") + ", " + 
										                   rset.getString("TIME") + ", " + 
										                   Double.toString(rset.getDouble("REGHOURS")) + ", " + 
										                   Double.toString(rset.getDouble("OTHOURS"));
						                 
						                 dataArray.add(new DailyInfoModel(stringOutput));
					               } 
					               
					               rset.close();
					               conn.close();
				             } 
				             
				             return dataArray; }
		         });
		     executor.shutdown();
		     try {
			       this.list = myFuture.get();
		     } catch (InterruptedException e) {
			       
			       e.printStackTrace();
		     } catch (ExecutionException e) {
			       
			       e.printStackTrace();
		     } 
	   }
	 
	 
	 
	   
	   public void getTotalHours() {
		     ExecutorService executor = Executors.newCachedThreadPool();
		     Future<Double> myFuture = executor.submit(new Callable<Double>() {
			           Double totalDatabaseHours = Double.valueOf(0.0D);
			 
			 
			 
			           
			           public Double call() throws Exception {
				             if (MySQLConnect.this.isConnected().booleanValue()) {
					               Connection conn = DriverManager.getConnection(MySQLConnect.this.jdbcUrl, MySQLConnect.this.userid, MySQLConnect.this.password);
					               Statement stmt = conn.createStatement();
					               String query = "SELECT * FROM dailyhours";
					               
					               ResultSet rset = stmt.executeQuery(query);
					               while (rset.next()) {
						                 this.totalDatabaseHours = Double.valueOf(this.totalDatabaseHours.doubleValue() + rset.getDouble(7) + rset.getDouble(8));
					               }
				             } 
				             return this.totalDatabaseHours;
			           }
		         });
		     executor.shutdown();
		     try {
			       this.databaseTotalHours = myFuture.get();
		     } catch (InterruptedException e) {
			       
			       e.printStackTrace();
		     } catch (ExecutionException e) {
			       
			       e.printStackTrace();
		     } 
	   }
	 
	 
	   
	   public void getTotalRegHours() {
		     String COMMA = ",";
		     ExecutorService executor = Executors.newCachedThreadPool();
		     Future<Double> myFuture = executor.submit(new Callable<Double>() {
			           Double totalRHours = Double.valueOf(0.0D);
			 
			 
			 
			           
			           public Double call() throws Exception {
				             if (MySQLConnect.this.isConnected().booleanValue()) {
					               Connection conn = DriverManager.getConnection(MySQLConnect.this.jdbcUrl, MySQLConnect.this.userid, MySQLConnect.this.password);
					               Statement stmt = conn.createStatement();
					               String query = "SELECT * FROM dailyhours";
					               
					               ResultSet rset = stmt.executeQuery(query);
					               while (rset.next())
					               {
						                 
						                 this.totalRHours = Double.valueOf(this.totalRHours.doubleValue() + rset.getDouble(7));
					               }
				             } 
				             return this.totalRHours;
			           }
		         });
		     executor.shutdown();
		     try {
			       this.totalRegHours = myFuture.get();
		     } catch (InterruptedException e) {
			       
			       e.printStackTrace();
		     } catch (ExecutionException e) {
			       
			       e.printStackTrace();
		     } 
	   }
	 
	 
	 
	   
	   public void getTotalOtHours() {
		     ExecutorService executor = Executors.newCachedThreadPool();
		     Future<Double> myFuture = executor.submit(new Callable<Double>() {
			           Double totalOHours = Double.valueOf(0.0D);
			 
			 
			 
			           
			           public Double call() throws Exception {
				             if (MySQLConnect.this.isConnected().booleanValue()) {
					               Connection conn = DriverManager.getConnection(MySQLConnect.this.jdbcUrl, MySQLConnect.this.userid, MySQLConnect.this.password);
					               Statement stmt = conn.createStatement();
					               String query = "SELECT * FROM dailyhours";
					               
					               ResultSet rset = stmt.executeQuery(query);
					               while (rset.next()) {
						                 this.totalOHours = Double.valueOf(this.totalOHours.doubleValue() + rset.getDouble(8));
					               }
				             } 
				             return this.totalOHours;
			           }
		         });
		     executor.shutdown();
		     try {
			       this.totalOTHours = myFuture.get();
		     } catch (InterruptedException e) {
			       
			       e.printStackTrace();
		     } catch (ExecutionException e) {
			       
			       e.printStackTrace();
		     } 
	   }
	 
	 
	 
	   
	   public Boolean isDuplicateDateFound(String date) {
		     System.out.println(date);
		     final String query = "SELECT * FROM dailyhours WHERE DATE = '" + date + "'";
		     String COMMA = ", ";
		     Boolean isDateFound = Boolean.valueOf(false);
		 
		     
		     ExecutorService executor = Executors.newCachedThreadPool();
		     Future<DailyInfoModel> myFuture = executor.submit(new Callable<DailyInfoModel>()
				         {
			 
			           
			           public DailyInfoModel call() throws Exception
			           {
				             DailyInfoModel data = null;
				             
				             if (MySQLConnect.this.isConnected().booleanValue()) {
					               Connection conn = DriverManager.getConnection(MySQLConnect.this.jdbcUrl, MySQLConnect.this.userid, MySQLConnect.this.password);
					               Statement stmt = conn.createStatement();
					               ResultSet rset = stmt.executeQuery(query);
					               while (rset.next()) {
						                 String stringOutput = "";
						                 
						                 stringOutput = 
								                   String.valueOf(rset.getString("DATE")) + ", " + 
										                   rset.getString("DAY") + ", " + 
										                   Integer.toString(rset.getInt("EVENTNUMBER")) + ", " + 
										                   rset.getString("EVENTNAME") + ", " + 
										                   rset.getString("TIME") + ", " + 
										                   Double.toString(rset.getDouble("REGHOURS")) + ", " + 
										                   Double.toString(rset.getDouble("OTHOURS"));
						                 data = new DailyInfoModel(stringOutput);
					               } 
					               rset.close();
					               conn.close();
				             } 
				             
				             return data;
			           }
		         });
		     
		     executor.shutdown();
		     try {
			       this.similarDate = myFuture.get();
		     } catch (InterruptedException e) {
			       
			       e.printStackTrace();
		     } catch (ExecutionException e) {
			       
			       e.printStackTrace();
		     } 
		     
		     if (this.similarDate == null) {
			       isDateFound = Boolean.valueOf(false);
		     } else {
			       
			       isDateFound = Boolean.valueOf(true);
		     } 
		     
		     return isDateFound;
	   }
	 
	 
	   
	   public void getQuery() {
		     String query = "SELECT * FROM dailyhours ORDER BY DATE DESC";
		     String csvFilePath = "dailyhours.csv";
		     CSVWriter writer = null;
		     if (isConnected().booleanValue()) {
			       
			       try {
				         Connection conn = DriverManager.getConnection(this.jdbcUrl, this.userid, this.password);
				         Statement stmt = conn.createStatement();
				         ResultSet rset = stmt.executeQuery(query);
				         writer = new CSVWriter(new FileWriter(csvFilePath));
				         
				         ResultSetMetaData metaData = rset.getMetaData();
				         int columnCount = metaData.getColumnCount();
				         String[] header = new String[columnCount];
				         for (int i = 2; i <= columnCount; i++) {
					           header[i - 2] = metaData.getColumnName(i);
				         }
				         writer.writeNext(header);
				 
				         
				         while (rset.next()) {
					           String[] row = new String[columnCount];
					           for (int j = 2; j <= columnCount; j++) {
						             row[j - 2] = rset.getString(j);
					           }
					           writer.writeNext(row);
				         } 
			       } catch (SQLException|IOException e) {
				         
				         e.printStackTrace();
			       } finally {
				         try {
					           writer.close();
				         } catch (IOException e) {
					           
					           e.printStackTrace();
				         } 
			       } 
		     }
	   }
	 
	 
	   
	   public void getQuery(final String query) {
		     String COMMA = ", ";
		     
		     ExecutorService executor = Executors.newCachedThreadPool();
		     Future<List<DailyInfoModel>> myFuture = executor.submit(new Callable<List<DailyInfoModel>>()
				         {
			           
			           public List<DailyInfoModel> call() throws Exception
			           {
				             MySQLConnect.this.list.clear();
				             List<DailyInfoModel> dataArray = new ArrayList<>();
				             
				             if (MySQLConnect.this.isConnected().booleanValue()) {
					               Connection conn = DriverManager.getConnection(MySQLConnect.this.jdbcUrl, MySQLConnect.this.userid, MySQLConnect.this.password);
					               Statement stmt = conn.createStatement();
					               ResultSet rset = stmt.executeQuery(query);
					               while (rset.next()) {
						                 String stringOutput = "";
						                 stringOutput = 
								                   String.valueOf(rset.getString("DATE")) + ", " + 
										                   rset.getString("DAY") + ", " + 
										                   Integer.toString(rset.getInt("EVENTNUMBER")) + ", " + 
										                   rset.getString("EVENTNAME") + ", " + 
										                   rset.getString("TIME") + ", " + 
										                   Double.toString(rset.getDouble("REGHOURS")) + ", " + 
										                   Double.toString(rset.getDouble("OTHOURS"));
						                 
						                 dataArray.add(new DailyInfoModel(stringOutput));
					               } 
					               rset.close();
					               conn.close();
				             } 
				             return dataArray;
			           }
		         });
		     executor.shutdown();
		     try {
			       this.list = myFuture.get();
		     } catch (InterruptedException e) {
			       
			       e.printStackTrace();
		     } catch (ExecutionException e) {
			       
			       e.printStackTrace();
		     } 
	   }





	public boolean updateJSONtoDatabase() {


		Boolean isSuccessful = false;
		String jsonFile = "homeData.json";
		//String sql = "INSERT INTO users (DATE, DAY, EVENTNAME, EVENTNAME, TIME, REGHOURS, OTHOURS) VALUES (?, ?, ?, ?, ?, ?, ?)";
		String sql = "INSERT INTO " + PAYROLL_HOURS_TABLE + " DATE, DAY, EVENTNAME, EVENTNAME, TIME, REGHOURS, OTHOURS) VALUES (?, ?, ?, ?, ?, ?, ?)";


		try (Connection conn = DriverManager.getConnection(this.jdbcUrl, this.userid, this.password);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// Turn off auto-commit for efficient batch processing
			conn.setAutoCommit(false); 

			// Initialize Jackson ObjectMapper to parse the file
			ObjectMapper mapper = new ObjectMapper();
			
			
			
			
	
			JsonNode rootNode = mapper.readTree(new File("homeData.json"));

			// Check if the root elements are an array
			if (rootNode.isArray()) {
				for (JsonNode node : rootNode) {
					// Extract fields safely
					String date = node.has("date") ? node.get("date").asText() : null;
					String day = node.has("day") ? node.get("day").asText() : null;
					int eventnumber = node.has("eventnumber") ? node.get("eventnumber").asInt() : 0;
					String eventname = node.has("eventname") ? node.get("eventname").asText() : null;
					String time = node.has("time") ? node.get("time").asText() : null;
					double rhours = node.has("rhours") ? node.get("rhours").asDouble() : 0.;
					double ohours = node.has("ohours") ? node.get("ohours").asDouble() : 0.;

					// Bind variables to Statement
					pstmt.setString(1, date);
					pstmt.setString(2, day);
					pstmt.setInt(3, eventnumber);
					pstmt.setString(4, eventname);
					pstmt.setString(5, time);
					pstmt.setDouble(6, rhours);
					pstmt.setDouble(7, ohours);

					pstmt.addBatch(); // Add to batch for bulk insert performance
				}
			}

			// Execute the batch insertion
			int[] totalRows = pstmt.executeBatch();
			conn.commit(); // Commit transaction

			//System.out.println("Successfully parsed and imported " + totalRows.length + " JSON records!");
			isSuccessful = true;
		} catch (Exception e) {
			
			
			e.printStackTrace();
		}
		
			
		return isSuccessful;

	}
}


 