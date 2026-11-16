package com.StanfordTimeCard.StanfordTimeCard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TimeCardSU extends JFrame
implements ActionListener
{
	private final String CLASSNAME = "TimeCardSU";
	private final String TIMECARD_DATABASE = "dailyhours";
	private final String INPUTFILE = "dailyhours.csv";

	private static MySQLConnect mySQLDatabase;

	private JFrame frameStanford;

	private TimePanel timePanel;

	private PicturePanel picturePanel;

	private PayrollQueryPanel payrollQueryPanel;

	private Boolean databaseStatus;

	private JLabel lblDatabaseStatus;
	private JMenuBar menuBar;
	private ImagePanel panel;
	private JMenu myMenu; 
	private JMenuItem queries;
	private JMenuItem modify;
	private JMenuItem logout; 
	private JMenuItem download; 
	private JMenuItem uploadDataFile; 
	private JMenuItem uploadJSONFile;
	private JMenuItem about;
	private JMenuItem reformatDate;
	private JMenuItem reconnectToDatabase; 
	private static final long serialVersionUID = 1L;
	private final String BACKGROUND_PIC = "mesep_alt.jpg";

	TimeCardSU() {
		this.frameStanford = new JFrame();

		this.menuBar = new JMenuBar();
		this.menuBar.setLayout(new BorderLayout());
		this.menuBar.setFont(new Font("Segoe UI", 1, 12));
		this.menuBar.setBackground(Color.WHITE);


		this.myMenu = new JMenu("MENU");
		this.myMenu.setMnemonic(0);
		this.myMenu.getAccessibleContext().setAccessibleDescription("Get My Shit");
		this.myMenu.setBackground(new Color(50, 205, 50));
		this.menuBar.add(this.myMenu, "West");


		this.lblDatabaseStatus = new JLabel("NOT CONNECTED");
		this.lblDatabaseStatus.setBounds(156, 16, 159, 15);
		this.lblDatabaseStatus.setFont(new Font("Verdana", 1, 14));

		this.lblDatabaseStatus.setDisplayedMnemonic(65);

		this.menuBar.add(this.lblDatabaseStatus, "East");


		this.queries = new JMenuItem("Queries");
		this.modify = new JMenuItem("Modify Database Entries");
		this.download = new JMenuItem("Download From Server");
		this.uploadDataFile = new JMenuItem("Copy File Data To Database");
		this.uploadJSONFile = new JMenuItem("Copy JSON Data To Database");

		this.logout = new JMenuItem("Logout");
		this.about = new JMenuItem("About");
		this.reformatDate = new JMenuItem("Reformat Date");
		this.reconnectToDatabase = new JMenuItem("Reconnect To Database");
		this.reformatDate.setVisible(false);
		this.modify.setVisible(false);


		this.myMenu.add(this.queries);
		this.myMenu.add(this.modify);
		this.myMenu.add(this.download);
		this.myMenu.add(this.uploadDataFile);
		this.myMenu.add(this.uploadJSONFile);

		this.myMenu.add(this.reconnectToDatabase);
		this.myMenu.add(this.logout);
		this.myMenu.add(this.reformatDate);

		this.about.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {}
		});




		this.modify.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new ModifyDatabaseValues(mySQLDatabase.getURL(), mySQLDatabase.getID(), mySQLDatabase.getPW());
			}

		});


		this.queries.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				if(databaseStatus) {
					new QueryFilterTable(mySQLDatabase.getURL(), mySQLDatabase.getID(), mySQLDatabase.getPW());	
				}else {

					JFrame frame1 = new JFrame();
					String theMessage = "Database Is Not Connected. WouldYou like to see a Copy of all of the Payrol Entries?";
					int result = JOptionPane.showConfirmDialog(frame1, theMessage, "alert", 0);
					if (result == 0) {
						List<DailyInfoModel> list = getDataFromFile();
						String title = "Payroll Entries From Backup File";
						new MyTable(list, title);				            
					}
				}
			}});

		this.download.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TimeCardSU.this.downloadFromServerToFile();
			}
		});

		this.uploadDataFile.addActionListener(new ActionListener()
		{	           
			public void actionPerformed(ActionEvent e)
			{
				Thread databaseThread = new Thread(new Runnable()
				{
					public void run()
					{
						if (TimeCardSU.refreshDatabase()) {
							JOptionPane.showMessageDialog(null, "MYSQL database updated");
						} else {
							JOptionPane.showMessageDialog(null, "ERROR:::MYSQL database was not updated");
						} 
					}
				}, "Database Thread");
				System.out.println(String.valueOf(databaseThread.getName()) + " has statrted");
				databaseThread.start();
			}
		});



		this.uploadJSONFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				if(mySQLDatabase.updateJSONtoDatabase()) {

					JOptionPane.showMessageDialog(null, "MYSQL database updated with JSON Data");
				}else {
					JOptionPane.showMessageDialog(null, "ERROR: MYSQL database was not  updated with JSON Data");		
				}							
			}
		});


		this.reconnectToDatabase.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				TimeCardSU.this.mySQLDatabase = null;
				TimeCardSU.this.databaseStatus = TimeCardSU.this.sqlSignonCredential();

				if (TimeCardSU.this.databaseStatus.booleanValue()) {
					TimeCardSU.this.lblDatabaseStatus.setText("Database Connected");
					TimeCardSU.this.lblDatabaseStatus.setForeground(Color.green);		
					TimeCardSU.this.modify.setVisible(true);

				} else {
					TimeCardSU.this.lblDatabaseStatus.setText("Database Is Not Connected");
					TimeCardSU.this.lblDatabaseStatus.setForeground(Color.red);
					TimeCardSU.this.modify.setVisible(false);
				} 

				TimeCardSU.this.timePanel.setDatabaseStatus(TimeCardSU.this.mySQLDatabase, TimeCardSU.this.databaseStatus);
			}
		});

		this.logout.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println(TimeCardSU.this.databaseStatus);
				if (!TimeCardSU.this.databaseStatus.booleanValue()) {
					JOptionPane.showMessageDialog(null, "Database Not Connected. No Data Downlaoded to Google Drive. GoodBye");
					System.exit(0);
				} 

				JFrame frame1 = new JFrame();
				String theMessage = " Do You Want To Quit The Application?";
				int result = JOptionPane.showConfirmDialog(frame1, theMessage, "alert", 0);
				if (result == 0) {
					TimeCardSU.this.downloadFromServerToFile();
					//TimeCardSU.this.BackupFilesToComputer();
					//TimeCardSU.this.copyFileToGoogleDrive();
					System.exit(0);
				} else {
					System.exit(0);
				} 
			}
		});


		this.reformatDate.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				List<DailyInfoModel> list = TimeCardSU.this.getDataFromFile();
				TimeCardSU.replaceDataInFile(list, "dailyhours.csv");
				JOptionPane.showMessageDialog(null, "Data reformatted in  dailyhours.csv file");
			}
		});

		this.frameStanford.setResizable(false);
		this.frameStanford.setTitle("STANFORD'S PAYROLL ");
		this.frameStanford.getContentPane().setLayout(new GridLayout(2, 1));
		// this.frameStanford.setSize(450, 460);//x,y
		this.frameStanford.setJMenuBar(this.menuBar);

		setupMainFrameDisplay();

		//System.out.println(this.databaseStatus);
	}


	private void setupMainFrameDisplay() {
		this.databaseStatus = sqlSignonCredential();

		if (this.databaseStatus.booleanValue()) {
			this.lblDatabaseStatus.setText("DataBase Connected");
			this.lblDatabaseStatus.setForeground(Color.green);
			modify.setVisible(true);
		} else {
			this.lblDatabaseStatus.setText("Database Is Not Connected");
			this.lblDatabaseStatus.setForeground(Color.red);
			modify.setVisible(false);
		} 

		try {
			this.timePanel = new TimePanel(this.mySQLDatabase, this.databaseStatus);
			this.timePanel.setPreferredSize(new Dimension(450, 250)); 
			this.panel = new ImagePanel(BACKGROUND_PIC);
			this.panel.setPreferredSize(new Dimension(450, 250)); 

			//this.picturePanel = new PicturePanel();
			//this.picturePanel.setPreferredSize(new Dimension(450, 100)); 
			//this.payrollQueryPanel = new PayrollQueryPanel(this.mySQLDatabase, this.databaseStatus);
		} catch (IOException e) {

			e.printStackTrace();
		} 




		//this.panel = new ImagePanel(BACKGROUND_PIC);
		this.frameStanford.getContentPane().add(this.timePanel);
		this.frameStanford.getContentPane().add(this.panel);
		//this.frameStanford.getContentPane().add(this.timePanel);

		// this.frameStanford.getContentPane().add(this.picturePanel);


		// this.frameStanford.getContentPane().add(this.payrollQueryPanel);


		this.frameStanford.setResizable(false);
		this.frameStanford.pack();
		this.frameStanford.setVisible(true);

		this.frameStanford.setDefaultCloseOperation(3);
		//this.frameStanford.validate();
	}

	private Boolean sqlSignonCredential() {
		String credentialsFilename = "mysqlsignonstuff.txt";
		String DELIMITER = "%";
		String[] myDatastuff = getCredentialsFromFile("mysqlsignonstuff.txt").split("%");

		this.mySQLDatabase = new MySQLConnect(myDatastuff[0], myDatastuff[1], myDatastuff[2]);

		if (this.mySQLDatabase.isConnected().booleanValue()) {
			JOptionPane.showMessageDialog(null, "Database Connected");
			return Boolean.valueOf(true);
		} 
		JOptionPane.showMessageDialog(null, "TimeCardSU : Trouble Connecting to Database");
		return Boolean.valueOf(false);
	}

	public static class SortPayrollDescendingOrderByDate
	implements Comparator<DailyInfoModel>
	{
		public int compare(DailyInfoModel a, DailyInfoModel b) {
			int dateSelect = 1;
			return TimeCardSU.convertDateStringToInt(b.getDate()) - TimeCardSU.convertDateStringToInt(a.getDate());
		}
	}


	public void downloadFromServerToFile() {
		JFrame frame = new JFrame();
		String theMessage = " Download From Server? File Data Will Be Overwritten. Continue?";
		int result = JOptionPane.showConfirmDialog(frame, theMessage, "alert", 0);
		if (result == 0) {
			String query = "SELECT * FROM dailyhours ORDER BY DATE DESC";
			this.mySQLDatabase.getQuery(query);
			List<DailyInfoModel> myList = this.mySQLDatabase.getList();
			Collections.sort(myList, new SortDailyInfoModelInDescendingOrderByDate());
			//fixMyDate(myList);

			if (replaceDataInFile(myList, INPUTFILE)) {
				BackupFilesToComputer();
				JOptionPane.showMessageDialog(null, "SUCCESS: Data Downloaded to File and Backed Up to Computer");			
				if (createJSONFile(myList)) {
					JOptionPane.showMessageDialog(null, "SUCCESS: JSON File created ");
				} else {
					JOptionPane.showMessageDialog(null, "ERROR: Downloading JSON Data ");
				}

			} else {

				JOptionPane.showMessageDialog(null, "Database Not Downloaded and Saved.");
			}


			//sortByDateNWriteToFile(myList);
			this.mySQLDatabase.clearList();
		} 
	}







	public boolean createJSONFile(List<DailyInfoModel> data) {


		if (data.isEmpty()) {
			return false;
		}
		// Creates a json file with HomeData data
		try (FileWriter file = new FileWriter("homeData.json")) {
			file.write("{ \n");
			String str = " \"homeData\" ";
			file.write(str + ":[ \n");
			for (int i = 0; i < data.size(); i++) {
				ObjectMapper Obj = new ObjectMapper();
				String jsonStr = Obj.writeValueAsString(data.get(i));
				if(i == data.size()-1) {
					file.write(jsonStr + "\n");
				}else {
					file.write(jsonStr + ", \n");
				}

			}
			file.write("] \n" + " }");
		} catch (IOException e) {
			// TODO Auto-generated catch bljock
			e.printStackTrace();
		}
		return true;
	}




	static int convertDateStringToInt(String date) {
		String delimStr = "-";
		String[] words = date.split(delimStr);

		int intDate = Integer.parseInt(words[0]) * 10000 + Integer.parseInt(words[1]) * 100 + 
				Integer.parseInt(words[2]);

		return intDate;
	}

	private String getCredentialsFromFile(String inputFile) {
		int myMagicNumber = 36;
		String allData = null;
		int count = 1;
		BufferedReader bufferedReader = null;

		try {
			bufferedReader = new BufferedReader(new FileReader(inputFile));
			try {
				String data;
				while ((data = bufferedReader.readLine()) != null) {
					if (count == 36) {
						allData = data;
					}
					count++;
				} 
				if (count < 36) {
					JOptionPane.showMessageDialog(null, "TimeCardSU: Credentials can not be found.");
				}
			} catch (IOException e) {

				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "TimeCardSU: HomeMainGUI: Can not read  from file ");
			} 
		} catch (FileNotFoundException e) {
			String data;
			JOptionPane.showMessageDialog(null, 
					"TimeCardSU : Can not find MYSQL database \ncredential file. Program terminated ");
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {

				JOptionPane.showMessageDialog(null, "TimeCardSU :Error Closing The File" + e);
				e.printStackTrace();
			} 
		} 

		return allData;
	}

	static void sortByDateNWriteToFile(List<DailyInfoModel> dataArray) {
		if (dataArray == null) {
			return;
		}

		sortListByDate(dataArray);
		writeToFile(dataArray);
	}


	static void sortListByDate(List<DailyInfoModel> dataArray) {
		boolean swap = true;
		int j = 0;
		while (swap) {
			swap = false;
			j++;
			for (int i = 0; i < dataArray.size() - j; i++) {
				if (((DailyInfoModel)dataArray.get(i)).getDateInt() < ((DailyInfoModel)dataArray.get(i + 1)).getDateInt()) {
					DailyInfoModel s = dataArray.get(i);
					dataArray.set(i, dataArray.get(i + 1));
					dataArray.set(i + 1, s);
					swap = true;
				} 
			} 
		} 
	}

	static void writeToFile(List<DailyInfoModel> list) {
		BufferedWriter bw = null;

		String COMMA_DELIMITER = ",";
		String NEW_LINE_SEPARATOR = "\n";
		String inputFile = "dailyhours.csv";
		try {
			File file = new File("dailyhours.csv");





			if (!file.exists()) {
				file.createNewFile();
			}


			FileWriter fw = new FileWriter(file, false);
			bw = new BufferedWriter(fw);



			bw.write("DATE");
			bw.write(",");
			bw.write("DAY");
			bw.write(",");
			bw.write("EVENT NUMBER");
			bw.write(",");
			bw.write("EVENT NAME");
			bw.write(",");
			bw.write("TIME");
			bw.write(",");
			bw.write("REGULAR HOURS");
			bw.write(",");
			bw.write("OVERTIME HOURS");
			bw.write("\n");

			for (int i = 0; i < list.size(); i++) {
				bw.write(((DailyInfoModel)list.get(i)).getDate());
				bw.write(",");
				bw.write(((DailyInfoModel)list.get(i)).getDay());
				bw.write(",");
				bw.write(Integer.toString(((DailyInfoModel)list.get(i)).getEventNumber().intValue()));
				bw.write(",");
				bw.write(((DailyInfoModel)list.get(i)).getEventName());
				bw.write(",");
				bw.write(((DailyInfoModel)list.get(i)).getTime());
				bw.write(",");
				bw.write(Double.toString(((DailyInfoModel)list.get(i)).getRhours()));
				bw.write(",");
				bw.write(Double.toString(((DailyInfoModel)list.get(i)).getOhours()));
				bw.write("\n");
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {


			try {
				if (bw != null)
					bw.close(); 
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "Error Closing The File " + ex);
			} 
		} 
	}

	private void copyFileToGoogleDrive() {
		InputStream in = null;
		OutputStream out = null;
		File source = new File("dailyhours.csv");
		File dest = new File("M:\\My Drive\\Marlin Info\\dailyhours.csv");

		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
		} catch (Exception e) {

			JOptionPane.showMessageDialog(null, e.toString());
		} finally {

			try {
				in.close();
				if (out == null) {
					JOptionPane.showMessageDialog(null, "ERROR: Data Not copied to outfile");
				} else {
					out.close();
				}

			} catch (IOException e) {

				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.toString());
			} 
		} 
	}

	protected static boolean refreshDatabase() {
		if (mySQLDatabase.refreshDatabase(getDataFromFile()).booleanValue()) {
			return true;
		}
		return false;
	}

	private static List<DailyInfoModel> getDataFromFile() {
		BufferedReader fileReader = null;
		String inputFile = "dailyhours.csv";
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
			JOptionPane.showMessageDialog(null, "Error: Cannot find cost.csv file");
			e.printStackTrace();
		} finally {
			try {
				fileReader.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error Closing The File" + e);
			} 
		} 

		if (myList.size() > 0) {
			Collections.sort(myList, new SortDailyInfoModelInDescendingOrderByDate());
			return myList;
		} 
		return null;
	}

	public static class SortDailyInfoModelInAscendingOrderByDate
	implements Comparator<DailyInfoModel>
	{
		public int compare(DailyInfoModel a, DailyInfoModel b) {
			return TimeCardSU.convertDateStringToInt(a.getDate()) - TimeCardSU.convertDateStringToInt(b.getDate());
		}
	}

	public static class SortDailyInfoModelInDescendingOrderByDate
	implements Comparator<DailyInfoModel>
	{
		public int compare(DailyInfoModel a, DailyInfoModel b) {
			return TimeCardSU.convertDateStringToInt(b.getDate()) - TimeCardSU.convertDateStringToInt(a.getDate());
		}
	}

	public static String reformatDateString(String rawDate) {
		if (rawDate == null || rawDate.trim().isEmpty()) {
			return "";
		}

		// 1. Instantly strip any weird whitespaces, newlines, or tabs
		String cleanInput = rawDate.trim().replaceAll("\\s", "");

		// 2. Break down the components by the dashes
		String[] parts = cleanInput.split("-");
		if (parts.length != 3) {
			return cleanInput; // Fallback to raw data if it isn't an expected yyyy-mm-dd format
		}

		try {
			// 3. Clean and parse numbers (removes all excessive leading zeros)
			int year = Integer.parseInt(parts[0].replaceAll("\\D", ""));
			int month = Integer.parseInt(parts[1]);
			int day = Integer.parseInt(parts[2]);

			// 4. Force strict yyyy-MM-dd formatting with safe two-digit sizing
			return String.format("%04d-%02d-%02d", year, month, day);

		} catch (NumberFormatException e) {
			// Fallback safely if something entirely unexpected is inside the text string
			return cleanInput;
		}
	}



	public static Boolean replaceDataInFile(List<DailyInfoModel> list, String filename) {
		Boolean isWriteSuccess = Boolean.valueOf(false);
		BufferedWriter bw = null;

		String COMMA_DELIMITER = ",";
		String NEW_LINE_SEPARATOR = "\n";
		try {
			File file = new File(filename);





			if (!file.exists()) {
				file.createNewFile();
			}

			if (filename.equalsIgnoreCase(filename)) {
				FileWriter fw = new FileWriter(file, false);
				bw = new BufferedWriter(fw);
				bw.write("DATE");
				bw.write(",");
				bw.write("DAY");
				bw.write(",");
				bw.write("EVENT NUMBER");
				bw.write(",");
				bw.write("EVENT NAME");
				bw.write(",");
				bw.write("TIME");
				bw.write(",");
				bw.write("REGULAR HOURS");
				bw.write(",");
				bw.write("OVERTIME HOURS");
				bw.write("\n");
			}
			else {

				FileWriter fw = new FileWriter(file, true);
				bw = new BufferedWriter(fw);
			} 

			for (int i = 0; i < list.size(); i++) {
				bw.write(reformatDateString(((DailyInfoModel)list.get(i)).getDate()));
				bw.write(",");
				bw.write(((DailyInfoModel)list.get(i)).getDay());
				bw.write(",");
				bw.write(Integer.toString(((DailyInfoModel)list.get(i)).getEventNumber().intValue()));
				bw.write(",");
				bw.write(((DailyInfoModel)list.get(i)).getEventName());
				bw.write(",");
				bw.write(((DailyInfoModel)list.get(i)).getTime());
				bw.write(",");
				bw.write(Double.toString(((DailyInfoModel)list.get(i)).getRhours()));
				bw.write(",");
				bw.write(Double.toString(((DailyInfoModel)list.get(i)).getOhours()));
				bw.write("\n");
			} 
			isWriteSuccess = Boolean.valueOf(true);
		}
		catch (IOException ioe) {
			JOptionPane.showMessageDialog(null, "Data was not written to file. \nVerify the cost.csv file is closed");
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Error Closing The File " + e);
			} 
		} 
		return isWriteSuccess;
	}


	private void BackupFilesToComputer() {
		InputStream in = null;
		OutputStream out = null;
		File source = new File(INPUTFILE);

		// 💡 INJECTION POINT: Get the smart, cross-platform destination file directly
		File dest;
		try {
			dest = getCsvOutputFile();
			JOptionPane.showMessageDialog(null, "Destination file path: " + dest.getAbsolutePath());

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"ERROR: Could not resolve or create backup directory: " + e.getMessage());
			return; // Stop the execution if the folder setup fails
		}

		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "ERROR: File Copy Not Successful To Backup Destination");
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out == null) {
					JOptionPane.showMessageDialog(null, "ERROR: Data Not copied to outfile");
				} else {
					out.close();
				}
			} catch (IOException e2) {
				JOptionPane.showMessageDialog(null, "ERROR: File Copy Not Closed ");
			}
		}
	}






	public static File getCsvOutputFile() throws IOException {
		String customPath = System.getenv("APP_BACKUP_PATH");
		Path targetAddress ;

		if (customPath != null && !customPath.isEmpty()) {
			targetAddress = Path.of(customPath, "dailyhours.csv"); // Updated filename here
		} else {
			String userHome = System.getProperty("user.home");
			targetAddress = Path.of(userHome, "StanfordTimeCard", "dailyhours.csv"); // Updatedfilename here
		}

		try {
			if (Files.notExists(targetAddress.getParent())) {
				Files.createDirectories(targetAddress.getParent());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return targetAddress.toFile();
	}


	private void fixMyDate(List<DailyInfoModel> myList) {
		for(int i = 0; i < myList.size(); i++) {


			String newDate = fixDateString(myList.get(i).getDate());
			myList.get(i).setDate(newDate); 
			System.out.println(newDate); // Output: 2016-01-12
			//      	      
		}	 
	}


	public static String fixDateString(String badDate) {
		// 1. Split the string into [year, month, day]
		String[] parts = badDate.split("-");

		if (parts.length != 3) {
			return badDate; // Return original if it's not a valid 3-part date
		}

		// 2. Parse into integers (this automatically destroys all leading zeros)
		int year = Integer.parseInt(parts[0]);
		int month = Integer.parseInt(parts[1]);
		int day = Integer.parseInt(parts[2]);

		// 3. Format back. %02d forces exactly 2 digits (adds a single zero if it's a single digit)
		return String.format("%04d-%02d-%02d", year, month, day);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}


}


