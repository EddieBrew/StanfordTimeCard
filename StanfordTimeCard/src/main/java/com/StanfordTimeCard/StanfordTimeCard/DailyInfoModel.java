/*     */ package com.StanfordTimeCard.StanfordTimeCard;
/*     */ 
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DailyInfoModel
/*     */   implements Comparable<DailyInfoModel>
/*     */ {
/*     */   private int dateInt;
/*     */   private String day;
/*     */   private String date;
/*     */   private Integer eventNumber;
/*     */   private String eventName;
/*     */   private String time;
/*     */   private double rhours;
/*     */   private double ohours;
/*     */   
/*     */   public DailyInfoModel() {}
/*     */   
/*     */   public DailyInfoModel(String date, String day, Integer eventNumber, String eventName, String time, double rhours, double ohours) {
/*  27 */     this.date = date;
/*  28 */     this.day = day;
/*  29 */     this.eventNumber = eventNumber;
/*  30 */     this.eventName = eventName;
/*  31 */     this.time = time;
/*  32 */     this.rhours = rhours;
/*  33 */     this.ohours = ohours;
/*  34 */     this.dateInt = TimeCardSU.convertDateStringToInt(date);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DailyInfoModel(int dateInt, String date, String day, Integer eventNumber, String eventName, String time, double rhours, double ohours) {
/*  40 */     this.dateInt = dateInt;
/*  41 */     this.date = date;
/*  42 */     this.day = day;
/*  43 */     this.eventNumber = eventNumber;
/*  44 */     this.eventName = eventName;
/*  45 */     this.time = time;
/*  46 */     this.rhours = rhours;
/*  47 */     this.ohours = ohours;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DailyInfoModel(String input) {
/*  53 */     parseIntoVariable(input);
/*     */   }
/*     */ 
/*     */   
/*     */   private void parseIntoVariable(String input) {
/*  58 */     String COMMA_DELIMITER = ",";
/*  59 */     String[] databaseInput = input.split(",");
/*     */ 
/*     */     
/*  62 */     for (int i = 0; i < databaseInput.length; i++) {
/*  63 */       switch (i) {
/*     */         case 0:
/*  65 */           this.date = databaseInput[i];
/*  66 */           this.dateInt = TimeCardSU.convertDateStringToInt(this.date);
/*     */           break;
/*     */         
/*     */         case 1:
/*  70 */           this.day = databaseInput[i].trim();
/*     */           break;
/*     */         
/*     */         case 2:
/*  74 */           this.eventNumber = Integer.valueOf(Integer.parseInt(databaseInput[i].trim()));
/*     */           break;
/*     */         case 3:
/*  77 */           this.eventName = databaseInput[i].trim();
/*     */           break;
/*     */         case 4:
/*  80 */           this.time = databaseInput[i].trim();
/*     */           break;
/*     */         case 5:
/*  83 */           this.rhours = Double.parseDouble(databaseInput[i].trim());
/*     */           break;
/*     */         case 6:
/*  86 */           this.ohours = Double.parseDouble(databaseInput[i].trim());
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*  97 */     String SPACER = " || ";
/*     */     
/*  99 */     return 
/* 100 */       String.valueOf(Integer.toString(this.dateInt)) + " || " + 
/* 101 */       this.date + " || " + 
/* 102 */       this.day + " || " + 
/* 103 */       this.eventNumber + " || " + 
/* 104 */       this.eventName + " || " + 
/* 105 */       this.time + " || " + 
/* 106 */       Double.toString(this.rhours) + " || " + 
/* 107 */       Double.toString(this.ohours);
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
/*     */   public String getDay() {
/* 135 */     return this.day;
/*     */   }
/*     */   
/*     */   public int getDateInt() {
/* 139 */     return this.dateInt;
/*     */   }
/*     */   public String getDate() {
/* 142 */     return this.date;
/*     */   }
/*     */   public Integer getEventNumber() {
/* 145 */     return this.eventNumber;
/*     */   }
/*     */   public String getEventName() {
/* 148 */     return this.eventName;
/*     */   }
/*     */   public String getTime() {
/* 151 */     return this.time;
/*     */   }
/*     */   public double getRhours() {
/* 154 */     return this.rhours;
/*     */   }
/*     */   public double getOhours() {
/* 157 */     return this.ohours;
/*     */   }
/*     */   public void setDay(String day) {
/* 160 */     this.day = day;
/*     */   }
/*     */   public void setDate(String date) {
/* 163 */     this.date = date;
/*     */   }
/*     */   public void setEventNumber(Integer eventNumber) {
/* 166 */     this.eventNumber = eventNumber;
/*     */   }
/*     */   public void setEventName(String eventName) {
/* 169 */     this.eventName = eventName;
/*     */   }
/*     */   public void setTime(String time) {
/* 172 */     this.time = time;
/*     */   }
/*     */   public void setRhours(double rhours) {
/* 175 */     this.rhours = rhours;
/*     */   }
/*     */   public void setOhours(double ohours) {
/* 178 */     this.ohours = ohours;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 184 */     return Objects.hash(new Object[] { this.date, this.day, this.eventNumber, this.eventName, this.time, Double.valueOf(this.rhours), Double.valueOf(this.ohours) });
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 189 */     if (this == obj)
/* 190 */       return true; 
/* 191 */     if (obj == null)
/* 192 */       return false; 
/* 193 */     if (getClass() != obj.getClass())
/* 194 */       return false; 
/* 195 */     DailyInfoModel other = (DailyInfoModel)obj;
/* 196 */     return (Objects.equals(this.date, other.date) && 
/* 197 */       Objects.equals(this.day, other.day) && 
/* 198 */       Objects.equals(this.eventNumber, other.eventNumber) && 
/* 199 */       Objects.equals(this.eventName, other.eventName) && 
/* 200 */       Objects.equals(this.time, other.time) && 
/* 201 */       Objects.equals(Double.valueOf(this.rhours), Double.valueOf(other.rhours)) && 
/* 202 */       Objects.equals(Double.valueOf(this.ohours), Double.valueOf(other.ohours)));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTo(DailyInfoModel obj) {
/* 208 */     return this.dateInt - obj.dateInt;
/*     */   }
/*     */ }


/* Location:              C:\Users\rober\OneDrive\Documents\MyApplications\StanfordTimeCard\stanfordTimecard.jar!\com\stanfordtimecard\maven\timecard\DailyInfoModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */