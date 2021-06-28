package hourAheadPackage;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.CardLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultCaret;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import javax.swing.JScrollPane;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.util.*;

public class mainFrame extends JFrame{

	private static Connection con;
	
	JPanel contentPane;
	static JTextArea consoleTextArea;
	static JTextArea resultTextArea;
	
	static JButton trainNetworkBtn;
	static JButton importDataBtn;
	static JButton evaluateNetworkBtn;
	static JButton predictBtn;
	
	static int userID = 0;
	
	public static int getUserID() {
		return userID;
	}
	public static void setUserID(int userIDparam) {
		userID = userIDparam;
	}
	
	popUp pop = new popUp();
	
	
	public static JButton getTrainNetworkBtn() {
		return trainNetworkBtn;
	}
	public static JButton getImportDataBtn() {
		return importDataBtn;
	}
	public static JButton getEvaluateNetworkBtn() {
		return evaluateNetworkBtn;
	}
	public static JButton getPredictBtn() {
		return predictBtn;
	}
	

	
	public JTextArea getConsoleTextArea() {
		return consoleTextArea;
	}

	public static void setConsoleTextArea(String stringAppend) {
		consoleTextArea.append(stringAppend);
	}

	public JTextArea getResultTextArea() {
		return resultTextArea;
	}

	public static void setResultTextArea(String stringAppend) {
		resultTextArea.append(stringAppend);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainFrame window = new mainFrame();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	/*public mainFrame() {
		initialize();
	}*/

	/**
	 * Initialize the contents of the frame.
	 */
	public mainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 656, 452);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setResizable(false);
		setTitle("Hour-Ahead Electricity Load Forecasting System using Artificial Neural Network");
		contentPane.setLayout(null);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 317, 630, 95);
		contentPane.add(scrollPane_1);
		
		consoleTextArea = new JTextArea();
		consoleTextArea.setEditable(false);
		scrollPane_1.setViewportView(consoleTextArea);
		DefaultCaret caretConsole = (DefaultCaret)consoleTextArea.getCaret();
		caretConsole.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JLabel consoleLbl = new JLabel("Console");
		scrollPane_1.setColumnHeaderView(consoleLbl);
		consoleLbl.setBackground(new Color(128, 128, 128));
		
		importDataBtn = new JButton("Import Data");
		importDataBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				con = DatabaseConnection.connectDB();
	                
				try
				{
					Statement ins = con.createStatement();
	 				String query_ins = String.format("INSERT INTO logs(userid, dateact, action) VALUES('%s', '%s', '%s')", userID, "now()", "Import Data");
	 				ins.executeUpdate(query_ins);
				}
				catch(Exception ee)
				{
					ee.printStackTrace();
				}
				
				(new Thread()
				{
					public void run()
					{
						String directory = "";
						int counter = 0;
						
						JFileChooser chooser = new JFileChooser("C:\\Final Eclipse workspace\\Hour_Ahead");

						chooser.setDialogTitle("Choose CSV file");
			            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			            chooser.setAcceptAllFileFilterUsed(false);
			            chooser.addChoosableFileFilter(new FileNameExtensionFilter("*.csv", "csv"));
			            

			            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			            {
			            	directory = chooser.getSelectedFile().getAbsolutePath();
			                //replace forward slash with backslash
			            	directory = (directory.replace("\\", "/"));	 
			                
			            	//insert raw csv
			                consoleTextArea.append("Inserting raw data...\n");
			                
			                try (
			     	               Reader reader = Files.newBufferedReader(Paths.get(directory));
			     	               CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
			     	           ) {
			     	               for (CSVRecord csvRecord : csvParser) {
			     	                   // Accessing Values by Column Index
			     	                   String BDate = csvRecord.get(0);
			     	                   String TIME = csvRecord.get(1);
			     	                   String KW1 = csvRecord.get(2);
			     	                   String KW2 = csvRecord.get(3);
			     	                   String KW3 = csvRecord.get(4);
			     	                   String KWSUM = csvRecord.get(5);
			     	                   
			     	                Statement ins = con.createStatement();
			     	 				String query_ins = String.format("INSERT INTO raw(userid, bdate, \"time\", kw_del_1, kw_del_2, kw_del_3, kw_del_sum) VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s')", userID, BDate, TIME, KW1, KW2, KW3, KWSUM);
			     	 				ins.executeUpdate(query_ins);
			     	 				counter++;
			     	               }
			     	           }
							 catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			               
			                consoleTextArea.append("Inserted " + counter + " rows..." + "\n");
						    consoleTextArea.append("Truncating normalized table...\n");
						    truncate(con);
						    restart_seq(con);	
			                consoleTextArea.append("Breaking down BDate and Time...\n");
			                //update the raw table with the un-normalized parameters.
			                update_unnormalized();
			                consoleTextArea.append("Finished insertion of raw data...\n");
			                //normalize load, and factors that affect it.
			                consoleTextArea.append("Normalizing data...\n");
			                normalize_exp.main(null);
			                consoleTextArea.append("Finished Normalizing.\n");
			            }
			            else
			            {
			            	JOptionPane.showMessageDialog(null, "No selection", "Error",
		                            JOptionPane.ERROR_MESSAGE);	            
			            }
					}
				}).start();
			}
		});
		importDataBtn.setBounds(10, 11, 150, 49);
		contentPane.add(importDataBtn);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 83, 630, 223);
		contentPane.add(scrollPane);
		
		resultTextArea = new JTextArea();
		resultTextArea.setEditable(false);
		scrollPane.setViewportView(resultTextArea);
		DefaultCaret caretResult = (DefaultCaret)resultTextArea.getCaret();
		caretResult.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JLabel resultLbl = new JLabel("Result");
		scrollPane.setColumnHeaderView(resultLbl);
		resultLbl.setBackground(Color.GRAY);
		
		trainNetworkBtn = new JButton("Train");
		trainNetworkBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				con = DatabaseConnection.connectDB();
                
				try
				{
					Statement ins = con.createStatement();
	 				String query_ins = String.format("INSERT INTO logs(userid, dateact, action) VALUES('%s', '%s', '%s')", userID, "now()", "Train");
	 				ins.executeUpdate(query_ins);
				}
				catch(Exception ee)
				{
					ee.printStackTrace();
				}
				resultTextArea.setText("");
                pop.setVisible(true);
				CardLayout c = (CardLayout)(pop.contentPane.getLayout());
				c.show(pop.contentPane, "hidden neurons");
			}
		});
		trainNetworkBtn.setBounds(170, 11, 150, 49);
		contentPane.add(trainNetworkBtn);
		
		evaluateNetworkBtn = new JButton("Evaluate");
		evaluateNetworkBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				con = DatabaseConnection.connectDB();
                
				try
				{
					Statement ins = con.createStatement();
	 				String query_ins = String.format("INSERT INTO logs(userid, dateact, action) VALUES('%s', '%s', '%s')", userID, "now()", "Evaluate");
	 				ins.executeUpdate(query_ins);
				}
				catch(Exception ee)
				{
					ee.printStackTrace();
				}
				
				resultTextArea.setText("");
                pop.setVisible(true);
                
                popUp.chosenAlgo = "Eval";
                
                //if X is pressed, reset the chosenAlgo to default
				pop.addWindowListener(new WindowAdapter()
				{
				    public void windowClosing(WindowEvent e)
				    {
				    	popUp.chosenAlgo = "";
				    }
				});
                
				CardLayout c = (CardLayout)(pop.contentPane.getLayout());
				c.show(pop.contentPane, "trainedNetworkPanel");
			}
		});
		evaluateNetworkBtn.setToolTipText("Developer Optimization");
		evaluateNetworkBtn.setBounds(330, 11, 150, 49);
		contentPane.add(evaluateNetworkBtn);
		
		predictBtn = new JButton("Predict");
		predictBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				con = DatabaseConnection.connectDB();
                
				try
				{
					Statement ins = con.createStatement();
	 				String query_ins = String.format("INSERT INTO logs(userid, dateact, action) VALUES('%s', '%s', '%s')", userID, "now()", "Predict");
	 				ins.executeUpdate(query_ins);
				}
				catch(Exception ee)
				{
					ee.printStackTrace();
				}
				
				//clean textarea
				resultTextArea.setText("");
                pop.setVisible(true);
                
                popUp.chosenAlgo = "Predict";
                
                //if X is pressed, reset the chosenAlgo to default
				pop.addWindowListener(new WindowAdapter()
				{
				    public void windowClosing(WindowEvent e)
				    {
				    	popUp.chosenAlgo = "";
				    }
				});
                
				CardLayout c = (CardLayout)(pop.contentPane.getLayout());
				c.show(pop.contentPane, "trainedNetworkPanel");
			}
		});
		predictBtn.setBounds(490, 11, 150, 49);
		contentPane.add(predictBtn);
	}
	
	public static int truncate(Connection connect)
	{
		int result = 0;
		
		try
		{
			Statement s = connect.createStatement();
	    	result = s.executeUpdate("TRUNCATE " + "normalized");
	    	return result;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
    	return result;
	}
	public static int restart_seq(Connection connect)
	{
		int result = 0;
		
		try
		{
			Statement s = connect.createStatement();
	    	result = s.executeUpdate("ALTER SEQUENCE " + "normalized_normalizedid_seq " + "RESTART WITH 1");
	    	return result;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
    	return result;
	}
	public static void update_unnormalized()
	{
		con = DatabaseConnection.connectDB();
		Date raw_date;
		String time_temp = "";
		int id;
		
        try
        {
			String query_id_unnormalized = String.format("select rawid, bdate, time from raw where day_of_week_raw is null and weekend_day_raw is null and type_of_day_raw is null and week_num_raw is null and time_raw is null and day_date_raw is null and month_raw is null and year_raw is null order by rawid");
			Statement s_id_unnormalized = con.createStatement();
			ResultSet rs_id_unnormalized = s_id_unnormalized.executeQuery(query_id_unnormalized);
			
			while(rs_id_unnormalized.next())
			{
				id = rs_id_unnormalized.getInt(1);
				raw_date = rs_id_unnormalized.getDate(2);
				time_temp = rs_id_unnormalized.getString(3);
				
				Statement st_ideal = con.createStatement();
				String query_deal1 = String.format("UPDATE raw SET day_of_week_raw = %s, weekend_day_raw = %s, type_of_day_raw = %s, week_num_raw = %s, time_raw = %s, day_date_raw = %s, month_raw = %s, year_raw = %s WHERE rawid = %s", day_of_week(raw_date), holiday_check(raw_date), weekendOrWeekday(raw_date), weekNum(raw_date), time_conv(time_temp), day_date(raw_date), month(raw_date), year(raw_date), id);
				//Executing the update
				st_ideal.executeUpdate(query_deal1);
			}
			
        }catch(Exception e2)
        {
        	e2.printStackTrace();
        }
	}
	public static int day_of_week(Date raw_date)
	{		
		Calendar c = Calendar.getInstance();
		c.setTime(raw_date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
				
		//1 = sun, 2 = mon,..so on.	
		return dayOfWeek;
	}
	public static int weekendOrWeekday(Date raw_date)
	{
		//0 for weekend, 1 for weekday
		
		Calendar c = Calendar.getInstance();
		c.setTime(raw_date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		
		if(dayOfWeek == 1 || dayOfWeek == 7)
		{
			return 0;
		}
		else
			return 1;		
	}
	public static int weekNum(Date raw_date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(raw_date);
		int dayOfWeek = c.get(Calendar.WEEK_OF_YEAR);
		
		return dayOfWeek;
	}
	public static int holiday_check(Date raw_date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(raw_date);
		
		//new year day
		if(c.get(Calendar.MONTH) == Calendar.JANUARY && c.get(Calendar.DAY_OF_MONTH) == 1)
		{
			return 1;
		}
		//araw ng kagitingan
		else if(c.get(Calendar.MONTH) == Calendar.APRIL && c.get(Calendar.DAY_OF_MONTH) == 9)
		{
			return 1;
		}
		//labor day
		else if(c.get(Calendar.MONTH) == Calendar.MAY && c.get(Calendar.DAY_OF_MONTH) == 1)
		{
			return 1;
		}
		//desperas pista
		else if(c.get(Calendar.MONTH) == Calendar.SEPTEMBER && c.get(Calendar.DAY_OF_MONTH) == 28)
		{
			return 1;
		}
		//pista
		else if(c.get(Calendar.MONTH) == Calendar.SEPTEMBER && c.get(Calendar.DAY_OF_MONTH) == 29)
		{
			return 1;
		}
		//independence day
		else if(c.get(Calendar.MONTH) == Calendar.JUNE && c.get(Calendar.DAY_OF_MONTH) == 12)
		{
			return 1;
		}
		//national heroes day
		else if(c.get(Calendar.MONTH) == Calendar.AUGUST && c.get(Calendar.DAY_OF_MONTH) == 27)
		{
			return 1;
		}
		//bonifacio day
		else if(c.get(Calendar.MONTH) == Calendar.NOVEMBER && c.get(Calendar.DAY_OF_MONTH) == 30)
		{
			return 1;
		}
		//christmas
		else if(c.get(Calendar.MONTH) == Calendar.DECEMBER && c.get(Calendar.DAY_OF_MONTH) == 25)
		{
			return 1;
		}
		//rizal day
		else if(c.get(Calendar.MONTH) == Calendar.DECEMBER && c.get(Calendar.DAY_OF_MONTH) == 30)
		{
			return 1;
		}
		//edsa rev
		else if(c.get(Calendar.MONTH) == Calendar.FEBRUARY && c.get(Calendar.DAY_OF_MONTH) == 25)
		{
			return 1;
		}
		//ninoy aquino
		else if(c.get(Calendar.MONTH) == Calendar.AUGUST && c.get(Calendar.DAY_OF_MONTH) == 21)
		{
			return 1;
		}
		//all saints day
		else if(c.get(Calendar.MONTH) == Calendar.NOVEMBER && c.get(Calendar.DAY_OF_MONTH) == 1)
		{
			return 1;
		}
		//all souls day
		else if(c.get(Calendar.MONTH) == Calendar.NOVEMBER && c.get(Calendar.DAY_OF_MONTH) == 2)
		{
			return 1;
		}
		//immaculate conception
		else if(c.get(Calendar.MONTH) == Calendar.DECEMBER && c.get(Calendar.DAY_OF_MONTH) == 8)
		{
			return 1;
		}
		//new years eve
		else if(c.get(Calendar.MONTH) == Calendar.DECEMBER && c.get(Calendar.DAY_OF_MONTH) == 31)
		{
			return 1;
		}
		else
			return 0;
	}
	public static int time_conv(String time)
	{
		if(time.equals("00:15:00"))
		{
			return 2;
		}
		else if(time.equals("00:30:00"))
		{
			return 3;
		}
		else if(time.equals("00:45:00"))
		{
			return 4;
		}
		else if(time.equals("01:00:00"))
		{
			return 5;
		}
		else if(time.equals("01:15:00"))
		{
			return 6;
		}
		else if(time.equals("01:30:00"))
		{
			return 7;
		}
		else if(time.equals("01:45:00"))
		{
			return 8;
		}
		else if(time.equals("02:00:00"))
		{
			return 9;
		}
		else if(time.equals("02:15:00"))
		{
			return 10;
		}
		else if(time.equals("02:30:00"))
		{
			return 11;
		}
		else if(time.equals("02:45:00"))
		{
			return 12;
		}
		else if(time.equals("03:00:00"))
		{
			return 13;
		}
		else if(time.equals("03:15:00"))
		{
			return 14;
		}
		else if(time.equals("03:30:00"))
		{
			return 15;
		}
		else if(time.equals("03:45:00"))
		{
			return 16;
		}
		else if(time.equals("04:00:00"))
		{
			return 17;
		}
		else if(time.equals("04:15:00"))
		{
			return 18;
		}
		else if(time.equals("04:30:00"))
		{
			return 19;
		}
		else if(time.equals("04:45:00"))
		{
			return 20;
		}
		else if(time.equals("05:00:00"))
		{
			return 21;
		}
		else if(time.equals("05:15:00"))
		{
			return 22;
		}
		else if(time.equals("05:30:00"))
		{
			return 23;
		}
		else if(time.equals("05:45:00"))
		{
			return 24;
		}
		else if(time.equals("06:00:00"))
		{
			return 25;
		}
		else if(time.equals("06:15:00"))
		{
			return 26;
		}
		else if(time.equals("06:30:00"))
		{
			return 27;
		}
		else if(time.equals("06:45:00"))
		{
			return 28;
		}
		else if(time.equals("07:00:00"))
		{
			return 29;
		}
		else if(time.equals("07:15:00"))
		{
			return 30;
		}
		else if(time.equals("07:30:00"))
		{
			return 31;
		}
		else if(time.equals("07:45:00"))
		{
			return 32;
		}
		else if(time.equals("08:00:00"))
		{
			return 33;
		}
		else if(time.equals("08:15:00"))
		{
			return 34;
		}
		else if(time.equals("08:30:00"))
		{
			return 35;
		}
		else if(time.equals("08:45:00"))
		{
			return 36;
		}
		else if(time.equals("09:00:00"))
		{
			return 37;
		}
		else if(time.equals("09:15:00"))
		{
			return 38;
		}
		else if(time.equals("09:30:00"))
		{
			return 39;
		}
		else if(time.equals("09:45:00"))
		{
			return 40;
		}
		else if(time.equals("10:00:00"))
		{
			return 41;
		}
		else if(time.equals("10:15:00"))
		{
			return 42;
		}
		else if(time.equals("10:30:00"))
		{
			return 43;
		}
		else if(time.equals("10:45:00"))
		{
			return 44;
		}
		else if(time.equals("11:00:00"))
		{
			return 45;
		}
		else if(time.equals("11:15:00"))
		{
			return 46;
		}
		else if(time.equals("11:30:00"))
		{
			return 47;
		}
		else if(time.equals("11:45:00"))
		{
			return 48;
		}
		else if(time.equals("12:00:00"))
		{
			return 49;
		}
		else if(time.equals("12:15:00"))
		{
			return 50;
		}
		else if(time.equals("12:30:00"))
		{
			return 51;
		}
		else if(time.equals("12:45:00"))
		{
			return 52;
		}
		else if(time.equals("13:00:00"))
		{
			return 53;
		}
		else if(time.equals("13:15:00"))
		{
			return 54;
		}
		else if(time.equals("13:30:00"))
		{
			return 55;
		}
		else if(time.equals("13:45:00"))
		{
			return 56;
		}
		else if(time.equals("14:00:00"))
		{
			return 57;
		}
		else if(time.equals("14:15:00"))
		{
			return 58;
		}
		else if(time.equals("14:30:00"))
		{
			return 59;
		}
		else if(time.equals("14:45:00"))
		{
			return 60;
		}
		else if(time.equals("15:00:00"))
		{
			return 61;
		}
		else if(time.equals("15:15:00"))
		{
			return 62;
		}
		else if(time.equals("15:30:00"))
		{
			return 63;
		}
		else if(time.equals("15:45:00"))
		{
			return 64;
		}
		else if(time.equals("16:00:00"))
		{
			return 65;
		}
		else if(time.equals("16:15:00"))
		{
			return 66;
		}
		else if(time.equals("16:30:00"))
		{
			return 67;
		}
		else if(time.equals("16:45:00"))
		{
			return 68;
		}
		else if(time.equals("17:00:00"))
		{
			return 69;
		}
		else if(time.equals("17:15:00"))
		{
			return 70;
		}
		else if(time.equals("17:30:00"))
		{
			return 71;
		}
		else if(time.equals("17:45:00"))
		{
			return 72;
		}
		else if(time.equals("18:00:00"))
		{
			return 73;
		}
		else if(time.equals("18:15:00"))
		{
			return 74;
		}
		else if(time.equals("18:30:00"))
		{
			return 75;
		}
		else if(time.equals("18:45:00"))
		{
			return 76;
		}
		else if(time.equals("19:00:00"))
		{
			return 77;
		}
		else if(time.equals("19:15:00"))
		{
			return 78;
		}
		else if(time.equals("19:30:00"))
		{
			return 79;
		}
		else if(time.equals("19:45:00"))
		{
			return 80;
		}
		else if(time.equals("20:00:00"))
		{
			return 81;
		}
		else if(time.equals("20:15:00"))
		{
			return 82;
		}
		else if(time.equals("20:30:00"))
		{
			return 83;
		}
		else if(time.equals("20:45:00"))
		{
			return 84;
		}
		else if(time.equals("21:00:00"))
		{
			return 85;
		}
		else if(time.equals("21:15:00"))
		{
			return 86;
		}
		else if(time.equals("21:30:00"))
		{
			return 87;
		}
		else if(time.equals("21:45:00"))
		{
			return 88;
		}
		else if(time.equals("22:00:00"))
		{
			return 89;
		}
		else if(time.equals("22:15:00"))
		{
			return 90;
		}
		else if(time.equals("22:30:00"))
		{
			return 91;
		}
		else if(time.equals("22:45:00"))
		{
			return 92;
		}
		else if(time.equals("23:00:00"))
		{
			return 93;
		}
		else if(time.equals("23:15:00"))
		{
			return 94;
		}
		else if(time.equals("23:30:00"))
		{
			return 95;
		}
		else if(time.equals("23:45:00"))
		{
			return 96;
		}
		else if(time.equals("00:00:00"))
		{
			return 1;
		}
		else
			return 0;
	}
	public static int day_date(Date raw_date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(raw_date);
		int day_date = c.get(Calendar.DAY_OF_MONTH);
		
		return day_date;
	}
	public static int month(Date raw_date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(raw_date);
		int month = c.get(Calendar.MONTH);
		
		return month+1;
	}
	public static int year(Date raw_date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(raw_date);
		int year = c.get(Calendar.YEAR);
		
		return year;
	}
}
