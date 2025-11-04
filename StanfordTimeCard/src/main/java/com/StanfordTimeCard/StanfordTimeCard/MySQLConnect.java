/*     */ package com.StanfordTimeCard.StanfordTimeCard;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Future;

/*     */ import javax.swing.JOptionPane;

/*     */ 
/*     */ import com.opencsv.CSVWriter;

/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MySQLConnect
/*     */ {
/*  32 */   private final String CLASSNAME = "MySQLConnect";
/*  33 */   private String jdbcUrl = null;
/*  34 */   private String userid = null;
/*  35 */   private String password = null;
/*  36 */   private final String PAYROLL_HOURS_TABLE = "dailyhours";
/*     */ 
/*     */   
/*  39 */   public List<DailyInfoModel> list = new ArrayList<>();
/*     */   public DailyInfoModel similarDate;
/*  41 */   private Double databaseTotalHours = Double.valueOf(0.0D);
/*  42 */   private Double totalRegHours = Double.valueOf(0.0D);
/*  43 */   private Double totalOTHours = Double.valueOf(0.0D);
/*     */   
/*     */   public MySQLConnect(String jdbcUrl, String userid, String password) {
/*  46 */     this.jdbcUrl = jdbcUrl;
/*  47 */     this.userid = userid;
/*  48 */     this.password = password;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DailyInfoModel getSimilarDate() {
/*  55 */     return this.similarDate;
/*     */   }
/*     */   public List<DailyInfoModel> getList() {
/*  58 */     return this.list;
/*     */   }
/*     */   
/*     */   public Double getDatabaseTotalHours() {
/*  62 */     return this.databaseTotalHours;
/*     */   }
/*     */   
/*     */   public void clearList() {
/*  66 */     this.list.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean isConnected() {
/*  72 */     Boolean isDBaseConnected = null;
/*     */ 
/*     */     
/*  75 */     Connection conn = null;
/*     */     
/*     */     try {
/*  78 */       conn = DriverManager.getConnection(this.jdbcUrl, this.userid, this.password);
/*  79 */       conn.close();
/*  80 */       isDBaseConnected = Boolean.valueOf(true);
/*  81 */     } catch (Exception e) {
/*     */       
/*  83 */       isDBaseConnected = Boolean.valueOf(false);
/*  84 */       JOptionPane.showMessageDialog(null, "MySQLConnect : ERROR. Unable to access database information.\nCheck database access password");
/*     */     } 
/*  86 */     return isDBaseConnected;
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean placeItemInDatabase(DailyInfoModel item) {
/*  91 */     Connection conn = null;
/*  92 */     Boolean isDataAccepted = Boolean.valueOf(false);
/*     */     
/*     */     try {
/*  95 */       if (isConnected().booleanValue()) {
/*  96 */         String query = "INSERT INTO dailyhours (DATE, DAY, EVENTNUMBER,  EVENTNAME, TIME, REGHOURS, OTHOURS ) VALUES('" + 
/*  97 */           item.getDate() + "','" + item.getDay() + "'," + item.getEventNumber() + ",'" + 
/*  98 */           item.getEventName() + "','" + item.getTime() + "'," + item.getRhours() + "," + item.getOhours() + ")";
/*     */ 
/*     */ 
/*     */         
/* 102 */         conn = DriverManager.getConnection(this.jdbcUrl, this.userid, this.password);
/* 103 */         Statement myStatement = conn.createStatement();
/* 104 */         myStatement = conn.createStatement();
/* 105 */         myStatement.executeUpdate(query);
/* 106 */         isDataAccepted = Boolean.valueOf(true);
/*     */       } 
/* 108 */     } catch (SQLException e) {
/*     */       
/* 110 */       e.printStackTrace();
/* 111 */       JOptionPane.showMessageDialog(null, e.toString());
/*     */     } finally {
/*     */       
/*     */       try {
/* 115 */         conn.close();
/* 116 */       } catch (SQLException e) {
/*     */         
/* 118 */         e.printStackTrace();
/* 119 */         JOptionPane.showMessageDialog(null, "MySQLConnect ERROR: Unable to close connection to database. Try Again");
/*     */       } 
/*     */     } 
/* 122 */     return isDataAccepted;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeEntryFromDatabase1(String query) {
/* 127 */     Connection conn = null;
/*     */     try {
/* 129 */       if (isConnected().booleanValue())
/*     */       {
/* 131 */         conn = DriverManager.getConnection(this.jdbcUrl, this.userid, this.password);
/* 132 */         Statement myStatement = conn.createStatement();
/* 133 */         myStatement = conn.createStatement();
/* 134 */         myStatement.executeUpdate(query);
/*     */       }
/*     */     
/* 137 */     } catch (SQLException e) {
/*     */ 
/*     */       
/* 140 */       JOptionPane.showMessageDialog(null, e.toString());
/*     */     } finally {
/*     */       try {
/* 143 */         conn.close();
/* 144 */       } catch (SQLException e) {
/*     */ 
/*     */         
/* 147 */         JOptionPane.showMessageDialog(null, e.toString());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateDatabase(String result) {
/* 155 */     System.out.println(result);
/* 156 */     Connection conn = null;
/*     */     try {
/* 158 */       if (isConnected().booleanValue()) {
/* 159 */         String query = result;
/* 160 */         conn = DriverManager.getConnection(this.jdbcUrl, this.userid, this.password);
/* 161 */         Statement myStatement = conn.createStatement();
/* 162 */         myStatement = conn.createStatement();
/* 163 */         myStatement.executeUpdate(query);
/*     */       }
/*     */     
/* 166 */     } catch (SQLException e) {
/*     */       
/* 168 */       e.printStackTrace();
/*     */     } finally {
/*     */       try {
/* 171 */         conn.close();
/* 172 */       } catch (SQLException e) {
/*     */         
/* 174 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean refreshDatabase(List<DailyInfoModel> item) {
/* 182 */     Boolean isSuccessful = Boolean.valueOf(false);
/*     */     try {
/* 184 */       if (isConnected().booleanValue()) {
/*     */         
/* 186 */         Connection conn = DriverManager.getConnection(this.jdbcUrl, this.userid, this.password);
/* 187 */         Statement myStatement = conn.createStatement();
/* 188 */         String deleteQuery = "DELETE FROM dailyhours";
/* 189 */         myStatement.executeUpdate(deleteQuery);
/*     */         
/* 191 */         for (int i = 0; i < item.size(); i++) {
/*     */           
/* 193 */           String query = "INSERT INTO dailyhours (DATE, DAY, EVENTNUMBER,  EVENTNAME, TIME, REGHOURS, OTHOURS ) VALUES('" + (
/* 194 */             (DailyInfoModel)item.get(i)).getDate() + "','" + ((DailyInfoModel)item.get(i)).getDay() + "'," + ((DailyInfoModel)item.get(i)).getEventNumber() + ",'" + (
/* 195 */             (DailyInfoModel)item.get(i)).getEventName() + "','" + ((DailyInfoModel)item.get(i)).getTime() + "'," + ((DailyInfoModel)item.get(i)).getRhours() + "," + ((DailyInfoModel)item.get(i)).getOhours() + ")";
/* 196 */           myStatement.execute(query);
/*     */         } 
/*     */       } 
/* 199 */       isSuccessful = Boolean.valueOf(true);
/* 200 */     } catch (SQLException e) {
/*     */       
/* 202 */       e.printStackTrace();
/*     */     } 
/*     */     
/* 205 */     return isSuccessful;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void readTableRows() {
/* 211 */     String COMMA = ", ";
/*     */     
/* 213 */     ExecutorService executor = Executors.newCachedThreadPool();
/* 214 */     Future<List<DailyInfoModel>> myFuture = executor.submit(new Callable<List<DailyInfoModel>>()
/*     */         {
/*     */           
/*     */           public List<DailyInfoModel> call() throws Exception
/*     */           {
/* 219 */             MySQLConnect.this.list.clear();
/* 220 */             List<DailyInfoModel> dataArray = new ArrayList<>();
/*     */             
/* 222 */             if (MySQLConnect.this.isConnected().booleanValue()) {
/*     */               
/* 224 */               Connection conn = DriverManager.getConnection(MySQLConnect.this.jdbcUrl, MySQLConnect.this.userid, MySQLConnect.this.password);
/* 225 */               Statement stmt = conn.createStatement();
/* 226 */               String query = "SELECT * FROM dailyhours";
/* 227 */               ResultSet rset = stmt.executeQuery(query);
/* 228 */               while (rset.next()) {
/* 229 */                 String stringOutput = "";
/*     */ 
/*     */                 
/* 232 */                 stringOutput = 
/* 233 */                   String.valueOf(rset.getString("DATE")) + ", " + 
/* 234 */                   rset.getString("DAY") + ", " + 
/* 235 */                   Integer.toString(rset.getInt("EVENTNUMBER")) + ", " + 
/* 236 */                   rset.getString("EVENTNAME") + ", " + 
/* 237 */                   rset.getString("TIME") + ", " + 
/* 238 */                   Double.toString(rset.getDouble("REGHOURS")) + ", " + 
/* 239 */                   Double.toString(rset.getDouble("OTHOURS"));
/*     */                 
/* 241 */                 dataArray.add(new DailyInfoModel(stringOutput));
/*     */               } 
/*     */               
/* 244 */               rset.close();
/* 245 */               conn.close();
/*     */             } 
/* 247 */             return dataArray;
/*     */           }
/*     */         });
/*     */     
/* 251 */     executor.shutdown();
/*     */     try {
/* 253 */       this.list = myFuture.get();
/* 254 */     } catch (InterruptedException e) {
/*     */       
/* 256 */       e.printStackTrace();
/* 257 */     } catch (ExecutionException e) {
/*     */       
/* 259 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void getDateRangeResults(final String query) {
/* 270 */     String COMMA = ", ";
/* 271 */     ExecutorService executor = Executors.newCachedThreadPool();
/* 272 */     Future<List<DailyInfoModel>> myFuture = executor.submit(new Callable<List<DailyInfoModel>>()
/*     */         {
/*     */           
/*     */           public List<DailyInfoModel> call() throws Exception
/*     */           {
/* 277 */             MySQLConnect.this.list.clear();
/* 278 */             List<DailyInfoModel> dataArray = new ArrayList<>();
/* 279 */             if (MySQLConnect.this.isConnected().booleanValue()) {
/*     */               
/* 281 */               Connection conn = DriverManager.getConnection(MySQLConnect.this.jdbcUrl, MySQLConnect.this.userid, MySQLConnect.this.password);
/* 282 */               Statement stmt = conn.createStatement();
/*     */ 
/*     */               
/* 285 */               ResultSet rset = stmt.executeQuery(query);
/* 286 */               while (rset.next()) {
/* 287 */                 String stringOutput = "";
/*     */                 
/* 289 */                 stringOutput = 
/* 290 */                   String.valueOf(rset.getString("DATE")) + ", " + 
/* 291 */                   rset.getString("DAY") + ", " + 
/* 292 */                   Integer.toString(rset.getInt("EVENTNUMBER")) + ", " + 
/* 293 */                   rset.getString("EVENTNAME") + ", " + 
/* 294 */                   rset.getString("TIME") + ", " + 
/* 295 */                   Double.toString(rset.getDouble("REGHOURS")) + ", " + 
/* 296 */                   Double.toString(rset.getDouble("OTHOURS"));
/*     */                 
/* 298 */                 dataArray.add(new DailyInfoModel(stringOutput));
/*     */               } 
/*     */               
/* 301 */               rset.close();
/* 302 */               conn.close();
/*     */             } 
/*     */             
/* 305 */             return dataArray; }
/*     */         });
/* 307 */     executor.shutdown();
/*     */     try {
/* 309 */       this.list = myFuture.get();
/* 310 */     } catch (InterruptedException e) {
/*     */       
/* 312 */       e.printStackTrace();
/* 313 */     } catch (ExecutionException e) {
/*     */       
/* 315 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void getTotalHours() {
/* 323 */     ExecutorService executor = Executors.newCachedThreadPool();
/* 324 */     Future<Double> myFuture = executor.submit(new Callable<Double>() {
/* 325 */           Double totalDatabaseHours = Double.valueOf(0.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           public Double call() throws Exception {
/* 331 */             if (MySQLConnect.this.isConnected().booleanValue()) {
/* 332 */               Connection conn = DriverManager.getConnection(MySQLConnect.this.jdbcUrl, MySQLConnect.this.userid, MySQLConnect.this.password);
/* 333 */               Statement stmt = conn.createStatement();
/* 334 */               String query = "SELECT * FROM dailyhours";
/*     */               
/* 336 */               ResultSet rset = stmt.executeQuery(query);
/* 337 */               while (rset.next()) {
/* 338 */                 this.totalDatabaseHours = Double.valueOf(this.totalDatabaseHours.doubleValue() + rset.getDouble(7) + rset.getDouble(8));
/*     */               }
/*     */             } 
/* 341 */             return this.totalDatabaseHours;
/*     */           }
/*     */         });
/* 344 */     executor.shutdown();
/*     */     try {
/* 346 */       this.databaseTotalHours = myFuture.get();
/* 347 */     } catch (InterruptedException e) {
/*     */       
/* 349 */       e.printStackTrace();
/* 350 */     } catch (ExecutionException e) {
/*     */       
/* 352 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void getTotalRegHours() {
/* 359 */     String COMMA = ",";
/* 360 */     ExecutorService executor = Executors.newCachedThreadPool();
/* 361 */     Future<Double> myFuture = executor.submit(new Callable<Double>() {
/* 362 */           Double totalRHours = Double.valueOf(0.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           public Double call() throws Exception {
/* 368 */             if (MySQLConnect.this.isConnected().booleanValue()) {
/* 369 */               Connection conn = DriverManager.getConnection(MySQLConnect.this.jdbcUrl, MySQLConnect.this.userid, MySQLConnect.this.password);
/* 370 */               Statement stmt = conn.createStatement();
/* 371 */               String query = "SELECT * FROM dailyhours";
/*     */               
/* 373 */               ResultSet rset = stmt.executeQuery(query);
/* 374 */               while (rset.next())
/*     */               {
/*     */                 
/* 377 */                 this.totalRHours = Double.valueOf(this.totalRHours.doubleValue() + rset.getDouble(7));
/*     */               }
/*     */             } 
/* 380 */             return this.totalRHours;
/*     */           }
/*     */         });
/* 383 */     executor.shutdown();
/*     */     try {
/* 385 */       this.totalRegHours = myFuture.get();
/* 386 */     } catch (InterruptedException e) {
/*     */       
/* 388 */       e.printStackTrace();
/* 389 */     } catch (ExecutionException e) {
/*     */       
/* 391 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void getTotalOtHours() {
/* 399 */     ExecutorService executor = Executors.newCachedThreadPool();
/* 400 */     Future<Double> myFuture = executor.submit(new Callable<Double>() {
/* 401 */           Double totalOHours = Double.valueOf(0.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           public Double call() throws Exception {
/* 407 */             if (MySQLConnect.this.isConnected().booleanValue()) {
/* 408 */               Connection conn = DriverManager.getConnection(MySQLConnect.this.jdbcUrl, MySQLConnect.this.userid, MySQLConnect.this.password);
/* 409 */               Statement stmt = conn.createStatement();
/* 410 */               String query = "SELECT * FROM dailyhours";
/*     */               
/* 412 */               ResultSet rset = stmt.executeQuery(query);
/* 413 */               while (rset.next()) {
/* 414 */                 this.totalOHours = Double.valueOf(this.totalOHours.doubleValue() + rset.getDouble(8));
/*     */               }
/*     */             } 
/* 417 */             return this.totalOHours;
/*     */           }
/*     */         });
/* 420 */     executor.shutdown();
/*     */     try {
/* 422 */       this.totalOTHours = myFuture.get();
/* 423 */     } catch (InterruptedException e) {
/*     */       
/* 425 */       e.printStackTrace();
/* 426 */     } catch (ExecutionException e) {
/*     */       
/* 428 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean isDuplicateDateFound(String date) {
/* 436 */     System.out.println(date);
/* 437 */     final String query = "SELECT * FROM dailyhours WHERE DATE = '" + date + "'";
/* 438 */     String COMMA = ", ";
/* 439 */     Boolean isDateFound = Boolean.valueOf(false);
/*     */ 
/*     */     
/* 442 */     ExecutorService executor = Executors.newCachedThreadPool();
/* 443 */     Future<DailyInfoModel> myFuture = executor.submit(new Callable<DailyInfoModel>()
/*     */         {
/*     */ 
/*     */           
/*     */           public DailyInfoModel call() throws Exception
/*     */           {
/* 449 */             DailyInfoModel data = null;
/*     */             
/* 451 */             if (MySQLConnect.this.isConnected().booleanValue()) {
/* 452 */               Connection conn = DriverManager.getConnection(MySQLConnect.this.jdbcUrl, MySQLConnect.this.userid, MySQLConnect.this.password);
/* 453 */               Statement stmt = conn.createStatement();
/* 454 */               ResultSet rset = stmt.executeQuery(query);
/* 455 */               while (rset.next()) {
/* 456 */                 String stringOutput = "";
/*     */                 
/* 458 */                 stringOutput = 
/* 459 */                   String.valueOf(rset.getString("DATE")) + ", " + 
/* 460 */                   rset.getString("DAY") + ", " + 
/* 461 */                   Integer.toString(rset.getInt("EVENTNUMBER")) + ", " + 
/* 462 */                   rset.getString("EVENTNAME") + ", " + 
/* 463 */                   rset.getString("TIME") + ", " + 
/* 464 */                   Double.toString(rset.getDouble("REGHOURS")) + ", " + 
/* 465 */                   Double.toString(rset.getDouble("OTHOURS"));
/* 466 */                 data = new DailyInfoModel(stringOutput);
/*     */               } 
/* 468 */               rset.close();
/* 469 */               conn.close();
/*     */             } 
/*     */             
/* 472 */             return data;
/*     */           }
/*     */         });
/*     */     
/* 476 */     executor.shutdown();
/*     */     try {
/* 478 */       this.similarDate = myFuture.get();
/* 479 */     } catch (InterruptedException e) {
/*     */       
/* 481 */       e.printStackTrace();
/* 482 */     } catch (ExecutionException e) {
/*     */       
/* 484 */       e.printStackTrace();
/*     */     } 
/*     */     
/* 487 */     if (this.similarDate == null) {
/* 488 */       isDateFound = Boolean.valueOf(false);
/*     */     } else {
/*     */       
/* 491 */       isDateFound = Boolean.valueOf(true);
/*     */     } 
/*     */     
/* 494 */     return isDateFound;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void getQuery() {
/* 500 */     String query = "SELECT * FROM dailyhours ORDER BY DATE DESC";
/* 501 */     String csvFilePath = "dailyhours.csv";
/* 502 */     CSVWriter writer = null;
/* 503 */     if (isConnected().booleanValue()) {
/*     */       
/*     */       try {
/* 506 */         Connection conn = DriverManager.getConnection(this.jdbcUrl, this.userid, this.password);
/* 507 */         Statement stmt = conn.createStatement();
/* 508 */         ResultSet rset = stmt.executeQuery(query);
/* 509 */         writer = new CSVWriter(new FileWriter(csvFilePath));
/*     */         
/* 511 */         ResultSetMetaData metaData = rset.getMetaData();
/* 512 */         int columnCount = metaData.getColumnCount();
/* 513 */         String[] header = new String[columnCount];
/* 514 */         for (int i = 2; i <= columnCount; i++) {
/* 515 */           header[i - 2] = metaData.getColumnName(i);
/*     */         }
/* 517 */         writer.writeNext(header);
/*     */ 
/*     */         
/* 520 */         while (rset.next()) {
/* 521 */           String[] row = new String[columnCount];
/* 522 */           for (int j = 2; j <= columnCount; j++) {
/* 523 */             row[j - 2] = rset.getString(j);
/*     */           }
/* 525 */           writer.writeNext(row);
/*     */         } 
/* 527 */       } catch (SQLException|IOException e) {
/*     */         
/* 529 */         e.printStackTrace();
/*     */       } finally {
/*     */         try {
/* 532 */           writer.close();
/* 533 */         } catch (IOException e) {
/*     */           
/* 535 */           e.printStackTrace();
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void getQuery(final String query) {
/* 544 */     String COMMA = ", ";
/*     */     
/* 546 */     ExecutorService executor = Executors.newCachedThreadPool();
/* 547 */     Future<List<DailyInfoModel>> myFuture = executor.submit(new Callable<List<DailyInfoModel>>()
/*     */         {
/*     */           
/*     */           public List<DailyInfoModel> call() throws Exception
/*     */           {
/* 552 */             MySQLConnect.this.list.clear();
/* 553 */             List<DailyInfoModel> dataArray = new ArrayList<>();
/*     */             
/* 555 */             if (MySQLConnect.this.isConnected().booleanValue()) {
/* 556 */               Connection conn = DriverManager.getConnection(MySQLConnect.this.jdbcUrl, MySQLConnect.this.userid, MySQLConnect.this.password);
/* 557 */               Statement stmt = conn.createStatement();
/* 558 */               ResultSet rset = stmt.executeQuery(query);
/* 559 */               while (rset.next()) {
/* 560 */                 String stringOutput = "";
/* 561 */                 stringOutput = 
/* 562 */                   String.valueOf(rset.getString("DATE")) + ", " + 
/* 563 */                   rset.getString("DAY") + ", " + 
/* 564 */                   Integer.toString(rset.getInt("EVENTNUMBER")) + ", " + 
/* 565 */                   rset.getString("EVENTNAME") + ", " + 
/* 566 */                   rset.getString("TIME") + ", " + 
/* 567 */                   Double.toString(rset.getDouble("REGHOURS")) + ", " + 
/* 568 */                   Double.toString(rset.getDouble("OTHOURS"));
/*     */                 
/* 570 */                 dataArray.add(new DailyInfoModel(stringOutput));
/*     */               } 
/* 572 */               rset.close();
/* 573 */               conn.close();
/*     */             } 
/* 575 */             return dataArray;
/*     */           }
/*     */         });
/* 578 */     executor.shutdown();
/*     */     try {
/* 580 */       this.list = myFuture.get();
/* 581 */     } catch (InterruptedException e) {
/*     */       
/* 583 */       e.printStackTrace();
/* 584 */     } catch (ExecutionException e) {
/*     */       
/* 586 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\rober\OneDrive\Documents\MyApplications\StanfordTimeCard\stanfordTimecard.jar!\com\stanfordtimecard\maven\timecard\MySQLConnect.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */