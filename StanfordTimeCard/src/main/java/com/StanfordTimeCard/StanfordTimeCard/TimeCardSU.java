/*     */ package com.StanfordTimeCard.StanfordTimeCard;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;

/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuBar;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JOptionPane;

/*     */ public class TimeCardSU extends JFrame
/*     */   implements ActionListener
/*     */ {
/*  58 */   private final String CLASSNAME = "TimeCardSU";
/*  59 */   private final String PAYROLL_HOURS_DATABASE_TABLE = "dailyhours";
/*     */   
/*     */   private static MySQLConnect mySQLDatabase;
/*     */   
/*     */   private JFrame frameStanford;
/*     */   
/*     */   private TimePanel timePanel;
/*     */   
/*     */   private PicturePanel picturePanel;
/*     */   
/*     */   private PayrollQueryPanel payrollQueryPanel;
/*     */   
/*     */   private Boolean databaseStatus;
/*     */   
/*     */   private JLabel lblDatabaseStatus;
/*     */   private JMenuBar menuBar;
/*     */   
/*     */   TimeCardSU() {
/*  77 */     this.frameStanford = new JFrame();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  83 */     this.menuBar = new JMenuBar();
/*  84 */     this.menuBar.setLayout(new BorderLayout());
/*  85 */     this.menuBar.setFont(new Font("Segoe UI", 1, 12));
/*  86 */     this.menuBar.setBackground(Color.WHITE);
/*     */ 
/*     */     
/*  89 */     this.myMenu = new JMenu("MENU");
/*  90 */     this.myMenu.setMnemonic(0);
/*  91 */     this.myMenu.getAccessibleContext().setAccessibleDescription("Get My Shit");
/*  92 */     this.myMenu.setBackground(new Color(50, 205, 50));
/*  93 */     this.menuBar.add(this.myMenu, "West");
/*     */ 
/*     */     
/*  96 */     this.lblDatabaseStatus = new JLabel("NOT CONNECTED");
/*  97 */     this.lblDatabaseStatus.setBounds(156, 16, 159, 15);
/*  98 */     this.lblDatabaseStatus.setFont(new Font("Verdana", 1, 14));
/*     */     
/* 100 */     this.lblDatabaseStatus.setDisplayedMnemonic(65);
/*     */     
/* 102 */     this.menuBar.add(this.lblDatabaseStatus, "East");
/*     */ 
/*     */ 
/*     */     
/* 106 */     this.download = new JMenuItem("Download From Server");
/* 107 */     this.upload = new JMenuItem("Copy File Data To Database");
/* 108 */     this.logout = new JMenuItem("Logout");
/* 109 */     this.about = new JMenuItem("About");
/* 110 */     this.reformatDate = new JMenuItem("Reformat Date");
/* 111 */     this.reconnectToDatabase = new JMenuItem("Reconnect To Database");
/* 112 */     this.reformatDate.setVisible(false);
/*     */     
/* 114 */     this.myMenu.add(this.download);
/* 115 */     this.myMenu.add(this.upload);
/*     */     
/* 117 */     this.myMenu.add(this.reconnectToDatabase);
/* 118 */     this.myMenu.add(this.logout);
/* 119 */     this.myMenu.add(this.reformatDate);
/*     */     
/* 121 */     this.about.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {}
/*     */         });
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 140 */     this.download.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e)
/*     */           {
/* 144 */             TimeCardSU.this.downloadFromServerToFile();
/*     */           }
/*     */         });
/*     */     
/* 148 */     this.upload.addActionListener(new ActionListener()
/*     */         {
/*     */ 
/*     */           
/*     */           public void actionPerformed(ActionEvent e)
/*     */           {
/* 154 */             Thread databaseThread = new Thread(new Runnable()
/*     */                 {
/*     */                   public void run()
/*     */                   {
/* 158 */                     if (TimeCardSU.refreshDatabase()) {
/* 159 */                       JOptionPane.showMessageDialog(null, "MYSQL database updated");
/*     */                     } else {
/* 161 */                       JOptionPane.showMessageDialog(null, "ERROR:::MYSQL database was not updated");
/*     */                     } 
/*     */                   }
/* 164 */                 }, "Database Thread");
/* 165 */             System.out.println(String.valueOf(databaseThread.getName()) + " has statrted");
/* 166 */             databaseThread.start();
/*     */           }
/*     */         });
/*     */     
/* 170 */     this.reconnectToDatabase.addActionListener(new ActionListener()
/*     */         {
/*     */           
/*     */           public void actionPerformed(ActionEvent e)
/*     */           {
/* 175 */             TimeCardSU.this.mySQLDatabase = null;
/* 176 */             TimeCardSU.this.databaseStatus = TimeCardSU.this.sqlSignonCredential();
/*     */             
/* 178 */             if (TimeCardSU.this.databaseStatus.booleanValue()) {
/* 179 */               TimeCardSU.this.lblDatabaseStatus.setText("Database Connected");
/* 180 */               TimeCardSU.this.lblDatabaseStatus.setForeground(Color.green);
/*     */             } else {
/* 182 */               TimeCardSU.this.lblDatabaseStatus.setText("Database Is Not Connected");
/* 183 */               TimeCardSU.this.lblDatabaseStatus.setForeground(Color.red);
/*     */             } 
/*     */             
/* 186 */             TimeCardSU.this.timePanel.setDatabaseStatus(TimeCardSU.this.mySQLDatabase, TimeCardSU.this.databaseStatus);
/*     */           }
/*     */         });
/*     */     
/* 190 */     this.logout.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e)
/*     */           {
/* 194 */             System.out.println(TimeCardSU.this.databaseStatus);
/* 195 */             if (!TimeCardSU.this.databaseStatus.booleanValue()) {
/* 196 */               JOptionPane.showMessageDialog(null, "Database Not Connected. No Data Downlaoded to Google Drive. GoodBye");
/* 197 */               System.exit(0);
/*     */             } 
/*     */             
/* 200 */             JFrame frame1 = new JFrame();
/* 201 */             String theMessage = " Do You Want To Quit The Application?";
/* 202 */             int result = JOptionPane.showConfirmDialog(frame1, theMessage, "alert", 0);
/* 203 */             if (result == 0) {
/* 204 */               TimeCardSU.this.downloadFromServerToFile();
/* 205 */               TimeCardSU.this.copyFileToGoogleDrive();
/* 206 */               System.exit(0);
/*     */             } else {
/* 208 */               System.exit(0);
/*     */             } 
/*     */           }
/*     */         });
/*     */ 
/*     */     
/* 214 */     this.reformatDate.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e)
/*     */           {
/* 218 */             List<DailyInfoModel> list = TimeCardSU.this.getDataFromFile();
/* 219 */             TimeCardSU.replaceDataInFile(list, "dailyhours.csv");
/* 220 */             JOptionPane.showMessageDialog(null, "Data reformatted in  dailyhours.csv file");
/*     */           }
/*     */         });
/*     */     
/* 224 */     this.frameStanford.setResizable(false);
/* 225 */     this.frameStanford.setTitle("STANFORD'S PAYROLL ");
/* 226 */     this.frameStanford.getContentPane().setLayout(new GridLayout(3, 1));
/* 227 */     this.frameStanford.setSize(600, 700);
/* 228 */     this.frameStanford.setJMenuBar(this.menuBar);
/*     */     
/* 230 */     setupMainFrameDisplay();
/* 231 */     System.out.println(this.databaseStatus);
/*     */   }
/*     */   private JMenu myMenu; private JMenuItem logout; private JMenuItem download; private JMenuItem upload; private JMenuItem about; private JMenuItem reformatDate; private JMenuItem reconnectToDatabase; private static final long serialVersionUID = 1L;
/*     */   private void setupMainFrameDisplay() {
/* 235 */     this.databaseStatus = sqlSignonCredential();
/*     */     
/* 237 */     if (this.databaseStatus.booleanValue()) {
/* 238 */       this.lblDatabaseStatus.setText("DataBase Connected");
/* 239 */       this.lblDatabaseStatus.setForeground(Color.green);
/*     */     } else {
/* 241 */       this.lblDatabaseStatus.setText("Database Is Not Connected");
/* 242 */       this.lblDatabaseStatus.setForeground(Color.red);
/*     */     } 
/*     */     
/*     */     try {
/* 246 */       this.timePanel = new TimePanel(this.mySQLDatabase, this.databaseStatus);
/* 247 */       this.payrollQueryPanel = new PayrollQueryPanel(this.mySQLDatabase, this.databaseStatus);
/* 248 */     } catch (IOException e) {
/*     */       
/* 250 */       e.printStackTrace();
/*     */     } 
/*     */     
/* 253 */     this.picturePanel = new PicturePanel();
/*     */     
/* 255 */     this.frameStanford.getContentPane().add(this.timePanel);
/* 256 */     this.frameStanford.getContentPane().add(this.picturePanel);
/* 257 */     this.frameStanford.getContentPane().add(this.payrollQueryPanel);
/* 258 */     this.frameStanford.setDefaultCloseOperation(3);
/* 259 */     this.frameStanford.setVisible(true);
/*     */     
/* 261 */     this.frameStanford.validate();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void actionPerformed(ActionEvent e) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Boolean sqlSignonCredential() {
/* 273 */     String credentialsFilename = "mysqlsignonstuff.txt";
/* 274 */     String DELIMITER = "%";
/* 275 */     String[] myDatastuff = getCredentialsFromFile("mysqlsignonstuff.txt").split("%");
/*     */     
/* 277 */     this.mySQLDatabase = new MySQLConnect(myDatastuff[0], myDatastuff[1], myDatastuff[2]);
/*     */     
/* 279 */     if (this.mySQLDatabase.isConnected().booleanValue()) {
/* 280 */       JOptionPane.showMessageDialog(null, "Database Connected");
/* 281 */       return Boolean.valueOf(true);
/*     */     } 
/* 283 */     JOptionPane.showMessageDialog(null, "TimeCardSU : Trouble Connecting to Database");
/* 284 */     return Boolean.valueOf(false);
/*     */   }
/*     */   
/*     */   public static class SortPayrollDescendingOrderByDate
/*     */     implements Comparator<DailyInfoModel>
/*     */   {
/*     */     public int compare(DailyInfoModel a, DailyInfoModel b) {
/* 291 */       int dateSelect = 1;
/* 292 */       return TimeCardSU.convertDateStringToInt(b.getDate()) - TimeCardSU.convertDateStringToInt(a.getDate());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void downloadFromServerToFile() {
/* 298 */     JFrame frame = new JFrame();
/* 299 */     String theMessage = " Download From Server? File Data Will Be Overwritten. Continue?";
/* 300 */     int result = JOptionPane.showConfirmDialog(frame, theMessage, "alert", 0);
/* 301 */     if (result == 0) {
/* 302 */       String query = "SELECT * FROM dailyhours ORDER BY DATE DESC";
/* 303 */       this.mySQLDatabase.getQuery(query);
/* 304 */       List<DailyInfoModel> myList = this.mySQLDatabase.getList();
/* 305 */       Collections.sort(myList, new SortDailyInfoModelInDescendingOrderByDate());
/* 306 */       sortByDateNWriteToFile(myList);
/* 307 */       this.mySQLDatabase.clearList();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void downloadFromServerToFile1() {
/* 314 */     JFrame frame = new JFrame();
/* 315 */     String theMessage = " Download From Server? File Data Will Be Overwritten. Continue?";
/* 316 */     int result = JOptionPane.showConfirmDialog(frame, theMessage, "alert", 
/* 317 */         0);
/* 318 */     if (result == 0) {
/* 319 */       this.mySQLDatabase.getQuery();
/*     */     }
/*     */   }
  
/*     */   static int convertDateStringToInt(String date) {
/* 335 */     String delimStr = "-";
/* 336 */     String[] words = date.split(delimStr);
/*     */     
/* 338 */     int intDate = Integer.parseInt(words[0]) * 10000 + Integer.parseInt(words[1]) * 100 + 
/* 339 */       Integer.parseInt(words[2]);
/*     */     
/* 341 */     return intDate;
/*     */   }
  
/*     */   private String getCredentialsFromFile(String inputFile) {
/* 355 */     int myMagicNumber = 36;
/* 356 */     String allData = null;
/* 357 */     int count = 1;
/* 358 */     BufferedReader bufferedReader = null;
/*     */     
/*     */     try {
/* 361 */       bufferedReader = new BufferedReader(new FileReader(inputFile));
/*     */       try {
/*     */         String data;
/* 364 */         while ((data = bufferedReader.readLine()) != null) {
/* 365 */           if (count == 36) {
/* 366 */             allData = data;
/*     */           }
/* 368 */           count++;
/*     */         } 
/* 370 */         if (count < 36) {
/* 371 */           JOptionPane.showMessageDialog(null, "TimeCardSU: Credentials can not be found.");
/*     */         }
/* 373 */       } catch (IOException e) {
/*     */         
/* 375 */         e.printStackTrace();
/* 376 */         JOptionPane.showMessageDialog(null, "TimeCardSU: HomeMainGUI: Can not read  from file ");
/*     */       } 
/* 378 */     } catch (FileNotFoundException e) {
/*     */       String data;
/* 380 */       JOptionPane.showMessageDialog(null, 
/* 381 */           "TimeCardSU : Can not find MYSQL database \ncredential file. Program terminated ");
/* 382 */       e.printStackTrace();
/*     */     } finally {
/*     */       try {
/* 385 */         bufferedReader.close();
/* 386 */       } catch (IOException e) {
/*     */         
/* 388 */         JOptionPane.showMessageDialog(null, "TimeCardSU :Error Closing The File" + e);
/* 389 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/*     */     
/* 393 */     return allData;
/*     */   }

/*     */   static void sortByDateNWriteToFile(List<DailyInfoModel> dataArray) {
/* 406 */     if (dataArray == null) {
/*     */       return;
/*     */     }
/*     */     
/* 410 */     sortListByDate(dataArray);
/* 411 */     writeToFile(dataArray);
/*     */   }
/*     */ 

/*     */   static void sortListByDate(List<DailyInfoModel> dataArray) {
/* 424 */     boolean swap = true;
/* 425 */     int j = 0;
/* 426 */     while (swap) {
/* 427 */       swap = false;
/* 428 */       j++;
/* 429 */       for (int i = 0; i < dataArray.size() - j; i++) {
/* 430 */         if (((DailyInfoModel)dataArray.get(i)).getDateInt() < ((DailyInfoModel)dataArray.get(i + 1)).getDateInt()) {
/* 431 */           DailyInfoModel s = dataArray.get(i);
/* 432 */           dataArray.set(i, dataArray.get(i + 1));
/* 433 */           dataArray.set(i + 1, s);
/* 434 */           swap = true;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }

/*     */   static void writeToFile(List<DailyInfoModel> list) {
/* 450 */     BufferedWriter bw = null;
/*     */     
/* 452 */     String COMMA_DELIMITER = ",";
/* 453 */     String NEW_LINE_SEPARATOR = "\n";
/* 454 */     String inputFile = "dailyhours.csv";
/*     */     try {
/* 456 */       File file = new File("dailyhours.csv");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 462 */       if (!file.exists()) {
/* 463 */         file.createNewFile();
/*     */       }
/*     */ 
/*     */       
/* 467 */       FileWriter fw = new FileWriter(file, false);
/* 468 */       bw = new BufferedWriter(fw);
/*     */ 
/*     */ 
/*     */       
/* 472 */       bw.write("DATE");
/* 473 */       bw.write(",");
/* 474 */       bw.write("DAY");
/* 475 */       bw.write(",");
/* 476 */       bw.write("EVENT NUMBER");
/* 477 */       bw.write(",");
/* 478 */       bw.write("EVENT NAME");
/* 479 */       bw.write(",");
/* 480 */       bw.write("TIME");
/* 481 */       bw.write(",");
/* 482 */       bw.write("REGULAR HOURS");
/* 483 */       bw.write(",");
/* 484 */       bw.write("OVERTIME HOURS");
/* 485 */       bw.write("\n");
/*     */       
/* 487 */       for (int i = 0; i < list.size(); i++) {
/* 488 */         bw.write(((DailyInfoModel)list.get(i)).getDate());
/* 489 */         bw.write(",");
/* 490 */         bw.write(((DailyInfoModel)list.get(i)).getDay());
/* 491 */         bw.write(",");
/* 492 */         bw.write(Integer.toString(((DailyInfoModel)list.get(i)).getEventNumber().intValue()));
/* 493 */         bw.write(",");
/* 494 */         bw.write(((DailyInfoModel)list.get(i)).getEventName());
/* 495 */         bw.write(",");
/* 496 */         bw.write(((DailyInfoModel)list.get(i)).getTime());
/* 497 */         bw.write(",");
/* 498 */         bw.write(Double.toString(((DailyInfoModel)list.get(i)).getRhours()));
/* 499 */         bw.write(",");
/* 500 */         bw.write(Double.toString(((DailyInfoModel)list.get(i)).getOhours()));
/* 501 */         bw.write("\n");
/*     */       }
/*     */     
/* 504 */     } catch (IOException ioe) {
/* 505 */       ioe.printStackTrace();
/*     */     } finally {
/*     */ 
/*     */       
/*     */       try {
/* 510 */         if (bw != null)
/* 511 */           bw.close(); 
/* 512 */       } catch (Exception ex) {
/* 513 */         JOptionPane.showMessageDialog(null, "Error Closing The File " + ex);
/*     */       } 
/*     */     } 
/*     */   }

/*     */   private void copyFileToGoogleDrive() {
/* 522 */     InputStream in = null;
/* 523 */     OutputStream out = null;
/* 524 */     File source = new File("dailyhours.csv");
/* 525 */     File dest = new File("M:\\My Drive\\Marlin Info\\dailyhours.csv");
/*     */     
/*     */     try {
/* 528 */       in = new FileInputStream(source);
/* 529 */       out = new FileOutputStream(dest);
/* 530 */       byte[] buffer = new byte[1024];
/*     */       int length;
/* 532 */       while ((length = in.read(buffer)) > 0) {
/* 533 */         out.write(buffer, 0, length);
/*     */       }
/* 535 */     } catch (Exception e) {
/*     */       
/* 537 */       JOptionPane.showMessageDialog(null, e.toString());
/*     */     } finally {
/*     */       
/*     */       try {
/* 541 */         in.close();
/* 542 */         if (out == null) {
/* 543 */           JOptionPane.showMessageDialog(null, "ERROR: Data Not copied to outfile");
/*     */         } else {
/* 545 */           out.close();
/*     */         }
/*     */       
/* 548 */       } catch (IOException e) {
/*     */         
/* 550 */         e.printStackTrace();
/* 551 */         JOptionPane.showMessageDialog(null, e.toString());
/*     */       } 
/*     */     } 
/*     */   }
   
/*     */   protected static boolean refreshDatabase() {
/* 561 */     if (mySQLDatabase.refreshDatabase(getDataFromFile()).booleanValue()) {
/* 562 */       return true;
/*     */     }
/* 564 */     return false;
/*     */   }

/*     */   private static List<DailyInfoModel> getDataFromFile() {
/* 600 */     BufferedReader fileReader = null;
/* 601 */     String inputFile = "dailyhours.csv";
/* 602 */     String str = "";
/* 603 */     List<DailyInfoModel> myList = new ArrayList<>();
/*     */ 
/*     */     
/*     */     try {
/* 607 */       fileReader = new BufferedReader(new FileReader("dailyhours.csv"));
/*     */ 
/*     */       
/* 610 */       str = fileReader.readLine();
/* 611 */       while ((str = fileReader.readLine()) != null) {
/* 612 */         DailyInfoModel data = new DailyInfoModel(str);
/* 613 */         myList.add(data);
/*     */       } 
/* 615 */     } catch (Exception e) {
/* 616 */       JOptionPane.showMessageDialog(null, "Error: Cannot find cost.csv file");
/* 617 */       e.printStackTrace();
/*     */     } finally {
/*     */       try {
/* 620 */         fileReader.close();
/* 621 */       } catch (IOException e) {
/* 622 */         JOptionPane.showMessageDialog(null, "Error Closing The File" + e);
/*     */       } 
/*     */     } 
/*     */     
/* 626 */     if (myList.size() > 0) {
/* 627 */       Collections.sort(myList, new SortDailyInfoModelInDescendingOrderByDate());
/* 628 */       return myList;
/*     */     } 
/* 630 */     return null;
/*     */   }

/*     */   public static class SortDailyInfoModelInAscendingOrderByDate
/*     */     implements Comparator<DailyInfoModel>
/*     */   {
/*     */     public int compare(DailyInfoModel a, DailyInfoModel b) {
/* 647 */       return TimeCardSU.convertDateStringToInt(a.getDate()) - TimeCardSU.convertDateStringToInt(b.getDate());
/*     */     }
/*     */   }

/*     */   public static class SortDailyInfoModelInDescendingOrderByDate
/*     */     implements Comparator<DailyInfoModel>
/*     */   {
/*     */     public int compare(DailyInfoModel a, DailyInfoModel b) {
/* 662 */       return TimeCardSU.convertDateStringToInt(b.getDate()) - TimeCardSU.convertDateStringToInt(a.getDate());
/*     */     }
/*     */   }
  
/*     */   public static String reformatDateString(String date) {
/* 669 */     String newDate = null;
/* 670 */     String DELIMITER = "-";
/* 671 */     String[] oldDate = date.split(DELIMITER);
/* 672 */     String month = null;
/* 673 */     String day = null;
/*     */     
/* 675 */     month = oldDate[1]; day = oldDate[2];
/*     */     
/* 677 */     if (Integer.parseInt(oldDate[1]) < 10) {
/* 678 */       month = "0" + oldDate[1];
/*     */     } else {
/* 680 */       month = oldDate[1];
/*     */     } 
/* 682 */     if (Integer.parseInt(oldDate[2]) < 10)
/* 683 */     { day = "0" + oldDate[2]; }
/* 684 */     else { day = 
/* 685 */         oldDate[2]; }
/*     */ 
/*     */     
/* 688 */     newDate = String.valueOf(oldDate[0]) + "-" + month + "-" + day;
/* 689 */     return newDate;
/*     */   }

/*     */   public static Boolean replaceDataInFile(List<DailyInfoModel> list, String filename) {
/* 703 */     Boolean isWriteSuccess = Boolean.valueOf(false);
/* 704 */     BufferedWriter bw = null;
/*     */     
/* 706 */     String COMMA_DELIMITER = ",";
/* 707 */     String NEW_LINE_SEPARATOR = "\n";
/*     */     try {
/* 709 */       File file = new File(filename);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 715 */       if (!file.exists()) {
/* 716 */         file.createNewFile();
/*     */       }
/*     */       
/* 719 */       if (filename.equalsIgnoreCase(filename)) {
/* 720 */         FileWriter fw = new FileWriter(file, false);
/* 721 */         bw = new BufferedWriter(fw);
/* 722 */         bw.write("DATE");
/* 723 */         bw.write(",");
/* 724 */         bw.write("DAY");
/* 725 */         bw.write(",");
/* 726 */         bw.write("EVENT NUMBER");
/* 727 */         bw.write(",");
/* 728 */         bw.write("EVENT NAME");
/* 729 */         bw.write(",");
/* 730 */         bw.write("TIME");
/* 731 */         bw.write(",");
/* 732 */         bw.write("REGULAR HOURS");
/* 733 */         bw.write(",");
/* 734 */         bw.write("OVERTIME HOURS");
/* 735 */         bw.write("\n");
/*     */       }
/*     */       else {
/*     */         
/* 739 */         FileWriter fw = new FileWriter(file, true);
/* 740 */         bw = new BufferedWriter(fw);
/*     */       } 
/*     */       
/* 743 */       for (int i = 0; i < list.size(); i++) {
/* 744 */         bw.write(reformatDateString(((DailyInfoModel)list.get(i)).getDate()));
/* 745 */         bw.write(",");
/* 746 */         bw.write(((DailyInfoModel)list.get(i)).getDay());
/* 747 */         bw.write(",");
/* 748 */         bw.write(Integer.toString(((DailyInfoModel)list.get(i)).getEventNumber().intValue()));
/* 749 */         bw.write(",");
/* 750 */         bw.write(((DailyInfoModel)list.get(i)).getEventName());
/* 751 */         bw.write(",");
/* 752 */         bw.write(((DailyInfoModel)list.get(i)).getTime());
/* 753 */         bw.write(",");
/* 754 */         bw.write(Double.toString(((DailyInfoModel)list.get(i)).getRhours()));
/* 755 */         bw.write(",");
/* 756 */         bw.write(Double.toString(((DailyInfoModel)list.get(i)).getOhours()));
/* 757 */         bw.write("\n");
/*     */       } 
/* 759 */       isWriteSuccess = Boolean.valueOf(true);
/*     */     }
/* 761 */     catch (IOException ioe) {
/* 762 */       JOptionPane.showMessageDialog(null, "Data was not written to file. \nVerify the cost.csv file is closed");
/*     */     } finally {
/*     */       try {
/* 765 */         if (bw != null) {
/* 766 */           bw.close();
/*     */         }
/* 768 */       } catch (Exception e) {
/* 769 */         JOptionPane.showMessageDialog(null, "Error Closing The File " + e);
/*     */       } 
/*     */     } 
/* 772 */     return isWriteSuccess;
/*     */   }
/*     */ }


/* Location:              C:\Users\rober\OneDrive\Documents\MyApplications\StanfordTimeCard\stanfordTimecard.jar!\com\stanfordtimecard\maven\timecard\TimeCardSU.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */