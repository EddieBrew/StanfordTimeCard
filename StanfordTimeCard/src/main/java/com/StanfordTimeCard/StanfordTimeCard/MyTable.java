package com.StanfordTimeCard.StanfordTimeCard;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;


public class MyTable
extends JFrame
{
	public MyTable(List<DailyInfoModel> myList, String tableTitle) {
		String[] columns = {
				"DATE", "DAY", "ENUM", "ENAME", "TIME", "REG", "OT"
		};

		int rows = myList.size();
		int COLS = 7;
		Object[][] data = new Object[rows][7];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < 7; j++) {

				switch (j) { case 0:
					data[i][j] = ((DailyInfoModel)myList.get(i)).getDate(); break;
				case 1:
					data[i][j] = ((DailyInfoModel)myList.get(i)).getDay(); break;
				case 2:
					data[i][j] = ((DailyInfoModel)myList.get(i)).getEventNumber(); break;
				case 3:
					data[i][j] = ((DailyInfoModel)myList.get(i)).getEventName(); break;
				case 4:
					data[i][j] = ((DailyInfoModel)myList.get(i)).getTime(); break;
				case 5:
					data[i][j] = Double.valueOf(((DailyInfoModel)myList.get(i)).getRhours()); break;
				case 6:
					data[i][j] = Double.valueOf(((DailyInfoModel)myList.get(i)).getOhours());
					break; }
			} 
		} 
		JTable table = new JTable(data, (Object[])columns);
		table.getColumnModel().getColumn(0).setPreferredWidth(250);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
		table.getColumnModel().getColumn(3).setPreferredWidth(500);
		table.getColumnModel().getColumn(4).setPreferredWidth(200);
		table.getColumnModel().getColumn(5).setPreferredWidth(100);
		table.getColumnModel().getColumn(6).setPreferredWidth(100);


		add(new JScrollPane(table));
		setTitle(tableTitle);
		setDefaultCloseOperation(2);
		pack();
		setVisible(true);
	}





	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run() {
				List<DailyInfoModel> myList = new ArrayList<>();

				DailyInfoModel myInfo = new DailyInfoModel("20180901, 09/01/2018, SUN, 23456, Foothills, 0600-1230, 8.8, 0.2");
				myList.add(myInfo);
				myInfo = new DailyInfoModel("20180902, 09/02/2018, MON, 23456, Foothills, 0600-1230, 8.8, 0.2");
				myList.add(myInfo);

				myInfo = new DailyInfoModel("20180903, 09/03/2018, TUE, 23456, Foothills, 0600-1230, 8.8, 0.2");
				myList.add(myInfo);
			}
		});
	}
}


