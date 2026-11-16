
package com.StanfordTimeCard.StanfordTimeCard;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;


import com.toedter.calendar.JDateChooser;

public class PayrollQueryPanel extends JPanel{
	private static final long serialVersionUID = -1141796694623429571L;
	private JLabel lblMessage;
	private JLabel lblRegHoursData;
	private JLabel lblOTHoursData;
	private JDateChooser dateChooserPayRollBegin;
	private JDateChooser dateChooserPayRollEnd;
	private MySQLConnect mySQLDatabase;
	private final String PAYROLL_HOURS_DATABASE_TABLE = "dailyhours";
	private final String inputFile = "dailyhours.csv";
	private Boolean databaseStatus;


	public PayrollQueryPanel(final MySQLConnect mySQLDatabase, Boolean dStatus) {
		this.mySQLDatabase = mySQLDatabase;
		this.databaseStatus = dStatus;
		setBackground(Color.GREEN);
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		JLabel lblBeginningDate = new JLabel("Beginning Date");
		springLayout.putConstraint("North", lblBeginningDate, 51, "North", this);
		add(lblBeginningDate);

		JLabel lblEndingDate = new JLabel("Ending Date");
		springLayout.putConstraint("North", lblEndingDate, 0, "North", lblBeginningDate);
		springLayout.putConstraint("West", lblEndingDate, 58, "East", lblBeginningDate);
		add(lblEndingDate);

		JLabel lblPayrollPeriod = new JLabel("PAYROLL QUERY PANEL");
		springLayout.putConstraint("North", lblPayrollPeriod, 10, "North", this);
		springLayout.putConstraint("West", lblPayrollPeriod, 127, "West", this);
		lblPayrollPeriod.setFont(new Font("Tahoma", 1, 15));
		add(lblPayrollPeriod);

		this.dateChooserPayRollBegin = new JDateChooser();
		springLayout.putConstraint("North", (Component) this.dateChooserPayRollBegin, 6, "South",
				lblBeginningDate);
		springLayout.putConstraint("West", lblBeginningDate, 0, "West",
				(Component) this.dateChooserPayRollBegin);
		springLayout.putConstraint("West", (Component) this.dateChooserPayRollBegin, 9, "West", this);
		springLayout.putConstraint("East", (Component) this.dateChooserPayRollBegin, 98, "West", this);
		this.dateChooserPayRollBegin.setDateFormatString("MM/dd/yyyy");
		add((Component) this.dateChooserPayRollBegin);

		this.dateChooserPayRollEnd = new JDateChooser();
		springLayout.putConstraint("West", (Component) this.dateChooserPayRollEnd, -24, "West", lblEndingDate);
		springLayout.putConstraint("South", (Component) this.dateChooserPayRollEnd, 0, "South",
				(Component) this.dateChooserPayRollBegin);
		springLayout.putConstraint("East", (Component) this.dateChooserPayRollEnd, 7, "East", lblEndingDate);
		this.dateChooserPayRollEnd.setDateFormatString("MM/dd/yyyy");
		add((Component) this.dateChooserPayRollEnd);

		this.lblMessage = new JLabel("Outgoing Message");
		springLayout.putConstraint("North", this.lblMessage, 113, "North", this);
		springLayout.putConstraint("West", this.lblMessage, 10, "West", this);
		add(this.lblMessage);

		JLabel lblRegHours = new JLabel("Regular Hours : ");
		springLayout.putConstraint("North", lblRegHours, 14, "South", this.lblMessage);
		springLayout.putConstraint("West", lblRegHours, 11, "West", this);
		add(lblRegHours);

		this.lblRegHoursData = new JLabel("0.0");
		springLayout.putConstraint("North", this.lblRegHoursData, 0, "North", lblRegHours);
		springLayout.putConstraint("West", this.lblRegHoursData, 6, "East", lblRegHours);
		add(this.lblRegHoursData);

		JLabel lblOTHours = new JLabel("Overtime Hours : ");
		springLayout.putConstraint("East", this.lblRegHoursData, -24, "West", lblOTHours);
		springLayout.putConstraint("North", lblOTHours, 0, "North", lblRegHours);
		springLayout.putConstraint("West", lblOTHours, 88, "East", lblRegHours);
		add(lblOTHours);

		this.lblOTHoursData = new JLabel("0.0");
		springLayout.putConstraint("North", this.lblOTHoursData, 0, "North", lblRegHours);
		springLayout.putConstraint("West", this.lblOTHoursData, 6, "East", lblOTHours);
		add(this.lblOTHoursData);

		JButton btnResults = new JButton("Get Results");
		springLayout.putConstraint("North", btnResults, 0, "North", (Component) this.dateChooserPayRollBegin);
		springLayout.putConstraint("West", btnResults, 26, "East", (Component) this.dateChooserPayRollEnd);
		springLayout.putConstraint("East", btnResults, -66, "East", this);
		add(btnResults);

		btnResults.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				PayrollQueryPanel.this.lblRegHoursData.setText("0.0");
				PayrollQueryPanel.this.lblOTHoursData.setText("0.0");
				Date dateStartBegin = PayrollQueryPanel.this.dateChooserPayRollBegin.getDate();
				Date dateStartEnd = PayrollQueryPanel.this.dateChooserPayRollEnd.getDate();


				String beginDateString = TimePanel.getDateString(dateStartBegin);
				String endDateString = TimePanel.getDateString(dateStartEnd);
				List<DailyInfoModel> list = null;
				if (!databaseStatus) {

					list = PayrollQueryPanel.this.getDataFromFile(beginDateString, endDateString);
					System.out.println("Size of list is : " + list.size());

				}else {


					String result = "SELECT * FROM dailyhours WHERE  DATE >= '" +
							beginDateString + "' AND DATE <= '" + endDateString +
							"'  ORDER BY DATE DESC";
					System.out.println(result);

					mySQLDatabase.getDateRangeResults(result);
					PayrollQueryPanel.this.lblMessage.
					setText("The Bi-Weekly Payroll Hours  from " + beginDateString + " to " + endDateString + " are:");
					String title = "Pay Period From "+ beginDateString + " to " + endDateString;
					list = mySQLDatabase.getList();
					new MyTable(list, title);
					//getPayrollHours(list);

				}
				//mySQLDatabase.clearList();





				if (list.size() == 0) {
					JFrame frame = new JFrame();
					JOptionPane.showMessageDialog(frame,
							"No Data Available Between Date Range. Verify that the Beginning and Ending Dates are accurate",
							"Data Error",  0);
				} else {

					String title = "Pay Period for " + beginDateString + " to " + endDateString;

					PayrollQueryPanel.this.getPayrollHours(list);
				}
				if (PayrollQueryPanel.this.databaseStatus) {
					mySQLDatabase.clearList();
				}
			}
		});
	}

	void produceJSONDataFromList(List<DailyInfoModel> list) {
	}

	static void deleteDataFromServer() {
	}

	void getJSONData() {
	}

	static void sortByDateNWriteToFile(List<DailyInfoModel> dataArray) {
		if (dataArray == null) {
			return;
		}

		TimeCardSU.sortListByDate(dataArray);
		TimeCardSU.writeToFile(dataArray);
	}



	void setupPanel() {
		add(this.lblMessage);
		add(this.lblRegHoursData);
		add(this.lblOTHoursData);
	}


	void getPayrollHours(List<DailyInfoModel> list) {
		double rHours = 0.0D;
		double otHours = 0.0D;

		for (int i = 0; i < list.size(); i++) {
			rHours += ((DailyInfoModel) list.get(i)).getRhours();
			otHours += ((DailyInfoModel) list.get(i)).getOhours();
		}

		this.lblRegHoursData.setText(String.format("%.2f", new Object[] { Double.valueOf(rHours) }));
		this.lblOTHoursData.setText(String.format("%.2f", new Object[] { Double.valueOf(otHours) }));
	}

	public static class DailyInfoModelInDescendingOrderByDate implements Comparator<DailyInfoModel>
	{
		public int compare(DailyInfoModel a, DailyInfoModel b) {
			return b.getDateInt() - a.getDateInt();
		}
	}


	public static class DailyInfoModelInAscendingOrderByDate implements Comparator<DailyInfoModel>
	{
		public int compare(DailyInfoModel a, DailyInfoModel b) {
			return a.getDateInt() - b.getDateInt();
		}
	}
	public List<DailyInfoModel> getDataFromFile(String beginDateString, String endDateString) {
		BufferedReader fileReader = null;
		String str = "";
		List<DailyInfoModel> myList = new ArrayList<>();


		try {
			fileReader = new BufferedReader(new FileReader("dailyhours.csv"));

			str = fileReader.readLine();
			while ((str = fileReader.readLine()) != null) {
				DailyInfoModel data = new DailyInfoModel(str);
				myList.add(data);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error Reading The File " + e);
			e.printStackTrace();
		} finally {
			try {
				fileReader.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error Closing The File" + e);
				e.printStackTrace();
			}
		}
		if (myList.size() > 0) {

			sortByDateNWriteToFile(myList);

			List<DailyInfoModel> finalList = new ArrayList<>();
			for (int i = 0; i < myList.size(); i++) {
				if (((DailyInfoModel) myList.get(i)).getDateInt() >= TimeCardSU
						.convertDateStringToInt(beginDateString) && (
								(DailyInfoModel) myList.get(i)).getDateInt() <= TimeCardSU
								.convertDateStringToInt(endDateString))
				{
					finalList.add(myList.get(i));
				}
			}
			return finalList;
		}
		return null;
	}
}

