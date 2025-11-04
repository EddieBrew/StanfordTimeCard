/*     */ package com.stanfordtimecard.maven.timecard;
/*     */ 
/*     */ import com.toedter.calendar.JDateChooser;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Font;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.SpringLayout;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PayrollQueryPanel
/*     */   extends JPanel
/*     */ {
/*     */   private static final long serialVersionUID = -1141796694623429571L;
/*     */   private JLabel lblMessage;
/*     */   private JLabel lblRegHoursData;
/*     */   private JLabel lblOTHoursData;
/*     */   private JDateChooser dateChooserPayRollBegin;
/*     */   private JDateChooser dateChooserPayRollEnd;
/*     */   private MySQLConnect mySQLDatabase;
/*  51 */   private final String PAYROLL_HOURS_DATABASE_TABLE = "dailyhours";
/*  52 */   private final String inputFile = "dailyhours.csv";
/*     */   private Boolean databaseStatus;
/*     */   
/*     */   public PayrollQueryPanel(final MySQLConnect mySQLDatabase, Boolean dStatus) {
/*  56 */     this.mySQLDatabase = mySQLDatabase;
/*  57 */     this.databaseStatus = dStatus;
/*  58 */     setBackground(Color.GREEN);
/*  59 */     SpringLayout springLayout = new SpringLayout();
/*  60 */     setLayout(springLayout);
/*     */     
/*  62 */     JLabel lblBeginningDate = new JLabel("Beginning Date");
/*  63 */     springLayout.putConstraint("North", lblBeginningDate, 51, "North", this);
/*  64 */     add(lblBeginningDate);
/*     */     
/*  66 */     JLabel lblEndingDate = new JLabel("Ending Date");
/*  67 */     springLayout.putConstraint("North", lblEndingDate, 0, "North", lblBeginningDate);
/*  68 */     springLayout.putConstraint("West", lblEndingDate, 58, "East", lblBeginningDate);
/*  69 */     add(lblEndingDate);
/*     */     
/*  71 */     JLabel lblPayrollPeriod = new JLabel("PAYROLL QUERY PANEL");
/*  72 */     springLayout.putConstraint("North", lblPayrollPeriod, 10, "North", this);
/*  73 */     springLayout.putConstraint("West", lblPayrollPeriod, 127, "West", this);
/*  74 */     lblPayrollPeriod.setFont(new Font("Tahoma", 1, 15));
/*  75 */     add(lblPayrollPeriod);
/*     */     
/*  77 */     this.dateChooserPayRollBegin = new JDateChooser();
/*  78 */     springLayout.putConstraint("North", (Component)this.dateChooserPayRollBegin, 6, "South", lblBeginningDate);
/*  79 */     springLayout.putConstraint("West", lblBeginningDate, 0, "West", (Component)this.dateChooserPayRollBegin);
/*  80 */     springLayout.putConstraint("West", (Component)this.dateChooserPayRollBegin, 9, "West", this);
/*  81 */     springLayout.putConstraint("East", (Component)this.dateChooserPayRollBegin, 98, "West", this);
/*  82 */     this.dateChooserPayRollBegin.setDateFormatString("MM/dd/yyyy");
/*  83 */     add((Component)this.dateChooserPayRollBegin);
/*     */     
/*  85 */     this.dateChooserPayRollEnd = new JDateChooser();
/*  86 */     springLayout.putConstraint("West", (Component)this.dateChooserPayRollEnd, -24, "West", lblEndingDate);
/*  87 */     springLayout.putConstraint("South", (Component)this.dateChooserPayRollEnd, 0, "South", (Component)this.dateChooserPayRollBegin);
/*  88 */     springLayout.putConstraint("East", (Component)this.dateChooserPayRollEnd, 7, "East", lblEndingDate);
/*  89 */     this.dateChooserPayRollEnd.setDateFormatString("MM/dd/yyyy");
/*  90 */     add((Component)this.dateChooserPayRollEnd);
/*     */     
/*  92 */     this.lblMessage = new JLabel("Outgoing Message");
/*  93 */     springLayout.putConstraint("North", this.lblMessage, 113, "North", this);
/*  94 */     springLayout.putConstraint("West", this.lblMessage, 10, "West", this);
/*  95 */     add(this.lblMessage);
/*     */     
/*  97 */     JLabel lblRegHours = new JLabel("Regular Hours : ");
/*  98 */     springLayout.putConstraint("North", lblRegHours, 14, "South", this.lblMessage);
/*  99 */     springLayout.putConstraint("West", lblRegHours, 11, "West", this);
/* 100 */     add(lblRegHours);
/*     */     
/* 102 */     this.lblRegHoursData = new JLabel("0.0");
/* 103 */     springLayout.putConstraint("North", this.lblRegHoursData, 0, "North", lblRegHours);
/* 104 */     springLayout.putConstraint("West", this.lblRegHoursData, 6, "East", lblRegHours);
/* 105 */     add(this.lblRegHoursData);
/*     */     
/* 107 */     JLabel lblOTHours = new JLabel("Overtime Hours : ");
/* 108 */     springLayout.putConstraint("East", this.lblRegHoursData, -24, "West", lblOTHours);
/* 109 */     springLayout.putConstraint("North", lblOTHours, 0, "North", lblRegHours);
/* 110 */     springLayout.putConstraint("West", lblOTHours, 88, "East", lblRegHours);
/* 111 */     add(lblOTHours);
/*     */     
/* 113 */     this.lblOTHoursData = new JLabel("0.0");
/* 114 */     springLayout.putConstraint("North", this.lblOTHoursData, 0, "North", lblRegHours);
/* 115 */     springLayout.putConstraint("West", this.lblOTHoursData, 6, "East", lblOTHours);
/* 116 */     add(this.lblOTHoursData);
/*     */     
/* 118 */     JButton btnResults = new JButton("Get Results");
/* 119 */     springLayout.putConstraint("North", btnResults, 0, "North", (Component)this.dateChooserPayRollBegin);
/* 120 */     springLayout.putConstraint("West", btnResults, 26, "East", (Component)this.dateChooserPayRollEnd);
/* 121 */     springLayout.putConstraint("East", btnResults, -66, "East", this);
/* 122 */     add(btnResults);
/*     */     
/* 124 */     btnResults.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent arg0)
/*     */           {
/* 128 */             PayrollQueryPanel.this.lblRegHoursData.setText("0.0");
/* 129 */             PayrollQueryPanel.this.lblOTHoursData.setText("0.0");
/* 130 */             Date dateStartBegin = PayrollQueryPanel.this.dateChooserPayRollBegin.getDate();
/* 131 */             Date dateStartEnd = PayrollQueryPanel.this.dateChooserPayRollEnd.getDate();
/*     */ 
/*     */             
/* 134 */             String beginDateString = TimePanel.getDateString(dateStartBegin);
/* 135 */             String endDateString = TimePanel.getDateString(dateStartEnd);
/* 136 */             List<DailyInfoModel> list = null;
/* 137 */             if (!PayrollQueryPanel.this.databaseStatus.booleanValue()) {
/*     */               
/* 139 */               list = PayrollQueryPanel.this.getDataFromFile(beginDateString, endDateString);
/* 140 */               System.out.println("Size of list is : " + list.size());
/*     */             
/*     */             }
/*     */             else {
/*     */ 
/*     */               
/* 146 */               String result = "SELECT * FROM dailyhours WHERE  DATE >= '" + beginDateString + "' AND DATE <= '" + endDateString + "'  ORDER BY DATE DESC";
/*     */               
/* 148 */               mySQLDatabase.getDateRangeResults(result);
/* 149 */               PayrollQueryPanel.this.lblMessage.setText("The Bi-Weekly Payroll Hours  from " + beginDateString + " to " + 
/* 150 */                   endDateString + " are:");
/*     */ 
/*     */               
/* 153 */               list = mySQLDatabase.getList();
/*     */             } 
/*     */ 
/*     */             
/* 157 */             if (list.size() == 0) {
/* 158 */               JFrame frame = new JFrame();
/* 159 */               JOptionPane.showMessageDialog(frame, 
/* 160 */                   "No Data Available Between Date Range. Verify that the Beginning and Ending Dates are accurate", 
/* 161 */                   "Data Error", 
/* 162 */                   0);
/*     */             } else {
/*     */               
/* 165 */               String title = "Pay Period for " + beginDateString + " to " + endDateString;
/*     */               
/* 167 */               PayrollQueryPanel.this.getPayrollHours(list);
/*     */             } 
/* 169 */             if (PayrollQueryPanel.this.databaseStatus.booleanValue()) {
/* 170 */               mySQLDatabase.clearList();
/*     */             }
/*     */           }
/*     */         });
/*     */   }
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
/*     */   void produceJSONDataFromList(List<DailyInfoModel> list) {}
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
/*     */   static void deleteDataFromServer() {}
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
/*     */   void getJSONData() {}
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
/*     */   static void sortByDateNWriteToFile(List<DailyInfoModel> dataArray) {
/* 239 */     if (dataArray == null) {
/*     */       return;
/*     */     }
/*     */     
/* 243 */     TimeCardSU.sortListByDate(dataArray);
/* 244 */     TimeCardSU.writeToFile(dataArray);
/*     */   }
/*     */ 
/*     */   
/*     */   void setupPanel() {
/* 249 */     add(this.lblMessage);
/* 250 */     add(this.lblRegHoursData);
/* 251 */     add(this.lblOTHoursData);
/*     */   }
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
/*     */   void getPayrollHours(List<DailyInfoModel> list) {
/* 267 */     double rHours = 0.0D;
/* 268 */     double otHours = 0.0D;
/*     */     
/* 270 */     for (int i = 0; i < list.size(); i++) {
/* 271 */       rHours += ((DailyInfoModel)list.get(i)).getRhours();
/* 272 */       otHours += ((DailyInfoModel)list.get(i)).getOhours();
/*     */     } 
/*     */     
/* 275 */     this.lblRegHoursData.setText(String.format("%.2f", new Object[] { Double.valueOf(rHours) }));
/* 276 */     this.lblOTHoursData.setText(String.format("%.2f", new Object[] { Double.valueOf(otHours) }));
/*     */   }
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
/*     */   public static class DailyInfoModelInDescendingOrderByDate
/*     */     implements Comparator<DailyInfoModel>
/*     */   {
/*     */     public int compare(DailyInfoModel a, DailyInfoModel b) {
/* 294 */       return b.getDateInt() - a.getDateInt();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class DailyInfoModelInAscendingOrderByDate
/*     */     implements Comparator<DailyInfoModel>
/*     */   {
/*     */     public int compare(DailyInfoModel a, DailyInfoModel b) {
/* 310 */       return a.getDateInt() - b.getDateInt();
/*     */     }
/*     */   }
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
/*     */   public List<DailyInfoModel> getDataFromFile(String beginDateString, String endDateString) {
/* 325 */     BufferedReader fileReader = null;
/* 326 */     String str = "";
/* 327 */     List<DailyInfoModel> myList = new ArrayList<>();
/*     */ 
/*     */     
/*     */     try {
/* 331 */       fileReader = new BufferedReader(new FileReader("dailyhours.csv"));
/*     */       
/* 333 */       str = fileReader.readLine();
/* 334 */       while ((str = fileReader.readLine()) != null) {
/* 335 */         DailyInfoModel data = new DailyInfoModel(str);
/* 336 */         myList.add(data);
/*     */       } 
/* 338 */     } catch (Exception e) {
/* 339 */       JOptionPane.showMessageDialog(null, "Error Reading The File " + e);
/* 340 */       e.printStackTrace();
/*     */     } finally {
/*     */       try {
/* 343 */         fileReader.close();
/* 344 */       } catch (IOException e) {
/* 345 */         JOptionPane.showMessageDialog(null, "Error Closing The File" + e);
/* 346 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/* 349 */     if (myList.size() > 0) {
/*     */       
/* 351 */       sortByDateNWriteToFile(myList);
/*     */       
/* 353 */       List<DailyInfoModel> finalList = new ArrayList<>();
/* 354 */       for (int i = 0; i < myList.size(); i++) {
/* 355 */         if (((DailyInfoModel)myList.get(i)).getDateInt() >= TimeCardSU.convertDateStringToInt(beginDateString) && (
/* 356 */           (DailyInfoModel)myList.get(i)).getDateInt() <= TimeCardSU.convertDateStringToInt(endDateString))
/*     */         {
/* 358 */           finalList.add(myList.get(i)); } 
/*     */       } 
/* 360 */       return finalList;
/*     */     } 
/* 362 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\rober\OneDrive\Documents\MyApplications\StanfordTimeCard\stanfordTimecard.jar!\com\stanfordtimecard\maven\timecard\PayrollQueryPanel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */