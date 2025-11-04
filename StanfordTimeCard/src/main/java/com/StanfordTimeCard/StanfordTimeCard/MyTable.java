/*    */ package com.stanfordtimecard.maven.timecard;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JScrollPane;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.SwingUtilities;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MyTable
/*    */   extends JFrame
/*    */ {
/*    */   public MyTable(List<DailyInfoModel> myList, String tableTitle) {
/* 21 */     String[] columns = {
/* 22 */         "DATE", "DAY", "ENUM", "ENAME", "TIME", "REG", "OT"
/*    */       };
/*    */ 
/*    */     
/* 26 */     int rows = myList.size();
/* 27 */     int COLS = 7;
/* 28 */     Object[][] data = new Object[rows][7];
/* 29 */     for (int i = 0; i < rows; i++) {
/* 30 */       for (int j = 0; j < 7; j++) {
/*    */         
/* 32 */         switch (j) { case 0:
/* 33 */             data[i][j] = ((DailyInfoModel)myList.get(i)).getDate(); break;
/*    */           case 1:
/* 35 */             data[i][j] = ((DailyInfoModel)myList.get(i)).getDay(); break;
/*    */           case 2:
/* 37 */             data[i][j] = ((DailyInfoModel)myList.get(i)).getEventNumber(); break;
/*    */           case 3:
/* 39 */             data[i][j] = ((DailyInfoModel)myList.get(i)).getEventName(); break;
/*    */           case 4:
/* 41 */             data[i][j] = ((DailyInfoModel)myList.get(i)).getTime(); break;
/*    */           case 5:
/* 43 */             data[i][j] = Double.valueOf(((DailyInfoModel)myList.get(i)).getRhours()); break;
/*    */           case 6:
/* 45 */             data[i][j] = Double.valueOf(((DailyInfoModel)myList.get(i)).getOhours());
/*    */             break; }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/*    */       } 
/*    */     } 
/* 56 */     JTable table = new JTable(data, (Object[])columns);
/* 57 */     table.getColumnModel().getColumn(0).setPreferredWidth(250);
/* 58 */     table.getColumnModel().getColumn(1).setPreferredWidth(100);
/* 59 */     table.getColumnModel().getColumn(2).setPreferredWidth(100);
/* 60 */     table.getColumnModel().getColumn(3).setPreferredWidth(500);
/* 61 */     table.getColumnModel().getColumn(4).setPreferredWidth(200);
/* 62 */     table.getColumnModel().getColumn(5).setPreferredWidth(100);
/* 63 */     table.getColumnModel().getColumn(6).setPreferredWidth(100);
/*    */ 
/*    */     
/* 66 */     add(new JScrollPane(table));
/* 67 */     setTitle(tableTitle);
/* 68 */     setDefaultCloseOperation(2);
/* 69 */     pack();
/* 70 */     setVisible(true);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void main(String[] args) {
/* 78 */     SwingUtilities.invokeLater(new Runnable()
/*    */         {
/*    */           public void run() {
/* 81 */             List<DailyInfoModel> myList = new ArrayList<>();
/*    */             
/* 83 */             DailyInfoModel myInfo = new DailyInfoModel("20180901, 09/01/2018, SUN, 23456, Foothills, 0600-1230, 8.8, 0.2");
/* 84 */             myList.add(myInfo);
/* 85 */             myInfo = new DailyInfoModel("20180902, 09/02/2018, MON, 23456, Foothills, 0600-1230, 8.8, 0.2");
/* 86 */             myList.add(myInfo);
/*    */             
/* 88 */             myInfo = new DailyInfoModel("20180903, 09/03/2018, TUE, 23456, Foothills, 0600-1230, 8.8, 0.2");
/* 89 */             myList.add(myInfo);
/*    */           }
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Users\rober\OneDrive\Documents\MyApplications\StanfordTimeCard\stanfordTimecard.jar!\com\stanfordtimecard\maven\timecard\MyTable.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */