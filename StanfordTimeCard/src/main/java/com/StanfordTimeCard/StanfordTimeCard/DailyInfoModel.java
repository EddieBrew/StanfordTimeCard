package com.StanfordTimeCard.StanfordTimeCard;

import java.util.Objects;

public class DailyInfoModel
implements Comparable<DailyInfoModel>
{
	private int dateInt;
	private String date;
	private String day;
	private Integer eventNumber;
	private String eventName;
	private String time;
	private double rhours;
	private double ohours;

	public DailyInfoModel() {}

	public DailyInfoModel(String date, String day, Integer eventNumber, String eventName, String time, double rhours, double ohours) {
		this.date = date;
		this.day = day;
		this.eventNumber = eventNumber;
		this.eventName = eventName;
		this.time = time;
		this.rhours = rhours;
		this.ohours = ohours;
		this.dateInt = TimeCardSU.convertDateStringToInt(date);
	}



	public DailyInfoModel(int dateInt, String date, String day, Integer eventNumber, String eventName, String time, double rhours, double ohours) {
		this.dateInt = dateInt;
		this.date = date;
		this.day = day;
		this.eventNumber = eventNumber;
		this.eventName = eventName;
		this.time = time;
		this.rhours = rhours;
		this.ohours = ohours;
	}



	public DailyInfoModel(String input) {
		parseIntoVariable(input);
	}


	private void parseIntoVariable(String input) {
		String COMMA_DELIMITER = ",";
		String[] databaseInput = input.split(",");


		for (int i = 0; i < databaseInput.length; i++) {
			switch (i) {
			case 0:
				this.date = databaseInput[i];
				this.dateInt = TimeCardSU.convertDateStringToInt(this.date);
				break;

			case 1:
				this.day = databaseInput[i].trim();
				break;

			case 2:
				this.eventNumber = Integer.valueOf(Integer.parseInt(databaseInput[i].trim()));
				break;
			case 3:
				this.eventName = databaseInput[i].trim();
				break;
			case 4:
				this.time = databaseInput[i].trim();
				break;
			case 5:
				this.rhours = Double.parseDouble(databaseInput[i].trim());
				break;
			case 6:
				this.ohours = Double.parseDouble(databaseInput[i].trim());
				break;
			} 
		} 
	}





	public String toString() {
		String SPACER = " || ";

		return 
				String.valueOf(Integer.toString(this.dateInt)) + " || " + 
				this.date + " || " + 
				this.day + " || " + 
				this.eventNumber + " || " + 
				this.eventName + " || " + 
				this.time + " || " + 
				Double.toString(this.rhours) + " || " + 
				Double.toString(this.ohours);
	}

	public String getDay() {
		return this.day;
	}

	public int getDateInt() {
		return this.dateInt;
	}
	public String getDate() {
		return this.date;
	}
	public Integer getEventNumber() {
		return this.eventNumber;
	}
	public String getEventName() {
		return this.eventName;
	}
	public String getTime() {
		return this.time;
	}
	public double getRhours() {
		return this.rhours;
	}
	public double getOhours() {
		return this.ohours;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public void setEventNumber(Integer eventNumber) {
		this.eventNumber = eventNumber;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public void setRhours(double rhours) {
		this.rhours = rhours;
	}
	public void setOhours(double ohours) {
		this.ohours = ohours;
	}



	public int hashCode() {
		return Objects.hash(new Object[] { this.date, this.day, this.eventNumber, this.eventName, this.time, Double.valueOf(this.rhours), Double.valueOf(this.ohours) });
	}


	public boolean equals(Object obj) {
		if (this == obj)
			return true; 
		if (obj == null)
			return false; 
		if (getClass() != obj.getClass())
			return false; 
		DailyInfoModel other = (DailyInfoModel)obj;
		return (Objects.equals(this.date, other.date) && 
				Objects.equals(this.day, other.day) && 
				Objects.equals(this.eventNumber, other.eventNumber) && 
				Objects.equals(this.eventName, other.eventName) && 
				Objects.equals(this.time, other.time) && 
				Objects.equals(Double.valueOf(this.rhours), Double.valueOf(other.rhours)) && 
				Objects.equals(Double.valueOf(this.ohours), Double.valueOf(other.ohours)));
	}



	public int compareTo(DailyInfoModel obj) {
		return this.dateInt - obj.dateInt;
	}
}


