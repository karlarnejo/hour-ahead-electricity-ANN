package hourAheadPackage;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.MaskFormatter;

import java.awt.CardLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.awt.event.ActionEvent;

public class popUp extends JFrame {

	JPanel contentPane;
	private JFormattedTextField hiddenNeuronTextField;
	private JFormattedTextField learningTextField;
	private JFormattedTextField momentumTextField;
	
	public static String chosenAlgo = "";
	public static String chosenActiveFnc = "";
	public static String chosenTrainedANN = "";
	public static int hiddenNeurons = 0;
	public static double chosenLearningRate = 0;
	public static double chosenMomentum = 0;
	public static int chosenYearFrom = 0;
	public static int chosenMonthFrom = 0;
	public static int chosenDayFrom = 0;
	public static int chosenTimeFrom = 0;
	public static int chosenYearTo = 0;
	public static int chosenMonthTo = 0;
	public static int chosenDayTo = 0;
	public static int chosenTimeTo = 0;
	
	private JTextField yearFromTextField;
	private JTextField monthFromTextField;
	private JTextField dayFromTextField;
	private JTextField timeFromTextField;
	private JTextField yearToTextField;
	private JTextField monthToTextField;
	private JTextField dayToTextField;
	private JTextField timeToTextField;
	
	public static JButton btnNewButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					popUp frame = new popUp();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws ParseException 
	 */
	public popUp()   {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 425, 170);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setResizable(false);
		contentPane.setLayout(new CardLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, "hidden neurons");
		panel.setLayout(null);
		
		JLabel lblChooseHiddenNumber = new JLabel("Choose hidden number neurons");
		lblChooseHiddenNumber.setBounds(117, 11, 184, 14);
		panel.add(lblChooseHiddenNumber);
		
		MaskFormatter formatter = null;
		try {
			formatter = new MaskFormatter("#");
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		hiddenNeuronTextField = new JFormattedTextField(formatter);
		hiddenNeuronTextField.setBounds(156, 36, 89, 20);
		panel.add(hiddenNeuronTextField);
		hiddenNeuronTextField.setColumns(10);
		
		JButton btnAccept = new JButton("Accept");
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String hiddenNeuronsString = hiddenNeuronTextField.getText();
				
				if(hiddenNeuronsString.equals(""))
				{
	            	JOptionPane.showMessageDialog(null, "No hidden neuron value.", "Error",
                            JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					hiddenNeurons = Integer.parseInt(hiddenNeuronsString);
					CardLayout c = (CardLayout)(contentPane.getLayout());
					c.show(contentPane, "training algo");
				}
			}
		});
		btnAccept.setBounds(156, 67, 89, 23);
		panel.add(btnAccept);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, "training algo");
		panel_1.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Choose Training Algorithm");
		lblNewLabel.setBounds(10, 11, 184, 14);
		panel_1.add(lblNewLabel);
		
		JComboBox<String> activationComboBox = new JComboBox<String>();
		activationComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Sigmoid", "Gaussian"}));
		activationComboBox.setBounds(257, 36, 132, 20);
		panel_1.add(activationComboBox);
		
		JComboBox<String> trainingAlgoComboBox = new JComboBox<String>();
		trainingAlgoComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Resilient Propagation", "Back Propagation", "Quick Propagation"}));
		trainingAlgoComboBox.setBounds(10, 36, 174, 20);
		panel_1.add(trainingAlgoComboBox);
		
		JButton btnAccept_1 = new JButton("Accept");
		btnAccept_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				chosenAlgo = (String) trainingAlgoComboBox.getSelectedItem();
				chosenActiveFnc = (String) activationComboBox.getSelectedItem();
				
				if(chosenAlgo.equals("") || chosenActiveFnc.equals(""))
				{
	            	JOptionPane.showMessageDialog(null, "No traning algorithm/activation function selected.", "Error",
                            JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					chosenAlgo = (String) trainingAlgoComboBox.getSelectedItem();

					if(chosenAlgo.equals("Quick Propagation"))
					{
						momentumTextField.disable();
						momentumTextField.setText("N/A");
					}
					else if(chosenAlgo.equals("Resilient Propagation"))
					{
						momentumTextField.disable();
						momentumTextField.setText("N/A");
						learningTextField.disable();
						learningTextField.setText("N/A");
					}
					
					CardLayout c = (CardLayout)(contentPane.getLayout());
					c.show(contentPane, "learning momentum");
				}
			}
		});
		btnAccept_1.setBounds(200, 87, 81, 23);
		panel_1.add(btnAccept_1);
		
		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				CardLayout c = (CardLayout)(contentPane.getLayout());
				c.show(contentPane, "hidden neurons");
				
				chosenAlgo = "";
				chosenActiveFnc = "";
			}
		});
		btnBack.setBounds(107, 87, 81, 23);
		panel_1.add(btnBack);
		
		JLabel lblChooseActivationFunction = new JLabel("Choose Activation Function");
		lblChooseActivationFunction.setHorizontalAlignment(SwingConstants.RIGHT);
		lblChooseActivationFunction.setBounds(215, 11, 174, 14);
		panel_1.add(lblChooseActivationFunction);
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, "learning momentum");
		panel_2.setLayout(null);
		
		JLabel lblLearningRate = new JLabel("Learning Rate");
		lblLearningRate.setBounds(84, 11, 118, 14);
		panel_2.add(lblLearningRate);
		
		//MaskFormatter formatterLearn = new MaskFormatter("####");
		learningTextField = new JFormattedTextField();
		learningTextField.setBounds(84, 31, 52, 20);
		panel_2.add(learningTextField);
		learningTextField.setColumns(10);
		
		JLabel lblMomentum = new JLabel("Momentum");
		lblMomentum.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMomentum.setBounds(223, 11, 87, 14);
		panel_2.add(lblMomentum);
		
		//MaskFormatter formatterMomentum = new MaskFormatter("####");
		momentumTextField = new JFormattedTextField();
		momentumTextField.setBounds(258, 31, 52, 20);
		panel_2.add(momentumTextField);
		momentumTextField.setColumns(10);
		
		JPanel panel_4 = new JPanel();
		contentPane.add(panel_4, "trainedNetworkPanel");
		panel_4.setLayout(null);
		
		JButton btnSelect = new JButton("Select");
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				(new Thread()
				{
					public void run()
					{

						JFileChooser chooser = new JFileChooser("C:\\Final Eclipse workspace\\Hour_Ahead\\trainedNetworks");
						
						chooser.setDialogTitle("Choose Trained Network File");
			            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			            chooser.setAcceptAllFileFilterUsed(false);
			            chooser.addChoosableFileFilter(new FileNameExtensionFilter("*.eg", "eg"));

			            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			            {
			            	chosenTrainedANN = chooser.getSelectedFile().getAbsolutePath();
			                //replace forward slash with backslash
			            	chosenTrainedANN = (chosenTrainedANN.replace("\\", "/"));
			            	
			            	if(chosenAlgo.equals("Predict"))
			            	{
			    				(new Thread()
			    				{
			    					public void run()
			    					{
			    						hourAheadLoad.predict(popUp.chosenTrainedANN);
			    					}
			    				}).start();
			            		dispose();
			            	}
			            	else
			            	{
				            	//show the query card.
								CardLayout c = (CardLayout)(contentPane.getLayout());
								c.show(contentPane, "query");
			            	}
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
		btnSelect.setBounds(155, 49, 89, 23);
		panel_4.add(btnSelect);
		
		JLabel lblChooseTrainedNetwork = new JLabel("Choose Trained Network");
		lblChooseTrainedNetwork.setHorizontalAlignment(SwingConstants.CENTER);
		lblChooseTrainedNetwork.setBounds(124, 24, 155, 14);
		panel_4.add(lblChooseTrainedNetwork);
		
		JPanel panel_3 = new JPanel();
		contentPane.add(panel_3, "query");
		panel_3.setLayout(null);
		
		JButton btnBack_1 = new JButton("Back");
		btnBack_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				momentumTextField.enable();
				momentumTextField.setText("");
				learningTextField.enable();
				learningTextField.setText("");
				
				//reset the values if back is pressed
				chosenMomentum = 0;
				chosenLearningRate = 0;
				
				CardLayout c = (CardLayout)(contentPane.getLayout());
				c.show(contentPane, "training algo");
			}
		});
		btnBack_1.setBounds(84, 67, 81, 23);
		panel_2.add(btnBack_1);
		
		JButton btnAccept_2 = new JButton("Accept");
		btnAccept_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String chosenLearningRateString = learningTextField.getText();
				String chosenMomentumString = momentumTextField.getText();
	
				//check if learning rate and momentum are filled for the appropriate training algo.
				if(chosenAlgo.equals("Back Propagation") && (chosenLearningRateString.equals("") || chosenMomentumString.equals("")))
				{
	            	JOptionPane.showMessageDialog(null, "No learning rate/momentum selected.", "Error",
                            JOptionPane.ERROR_MESSAGE);
				}
				else if(chosenAlgo.equals("Quick Propagation") && chosenLearningRateString.equals(""))
				{
					JOptionPane.showMessageDialog(null, "No momentum selected.", "Error",
                            JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					//convert the learning rate and momentum for the appropriate training algo since getText() returns string.
					if(chosenAlgo.equals("Quick Propagation"))
					{
						chosenLearningRate = Double.parseDouble(chosenLearningRateString);
					}
					else if(chosenAlgo.equals("Resilient Propagation"))
					{
						chosenLearningRate = 0;
						chosenMomentum = 0;
					}
					else if(chosenAlgo.equals("Back Propagation"))
					{
						chosenLearningRate = Double.parseDouble(chosenLearningRateString);
						chosenMomentum = Double.parseDouble(chosenMomentumString);
					}
					
					CardLayout c = (CardLayout)(contentPane.getLayout());
					c.show(contentPane, "query");
				}
			}
		});
		btnAccept_2.setBounds(229, 67, 81, 23);
		panel_2.add(btnAccept_2);
		
		JLabel lblYearFrom = new JLabel("Year From");
		lblYearFrom.setBounds(10, 11, 106, 14);
		panel_3.add(lblYearFrom);
		
		JLabel lblMonthFrom = new JLabel("Month From");
		lblMonthFrom.setBounds(69, 11, 102, 14);
		panel_3.add(lblMonthFrom);
		
		JLabel lblDayFrom = new JLabel("Day From");
		lblDayFrom.setBounds(138, 11, 102, 14);
		panel_3.add(lblDayFrom);
		
		JLabel lblTimeFrom = new JLabel("Time From");
		lblTimeFrom.setBounds(197, 11, 92, 14);
		panel_3.add(lblTimeFrom);
		
		JLabel lblYearTo = new JLabel("Year To");
		lblYearTo.setBounds(10, 62, 106, 14);
		panel_3.add(lblYearTo);
		
		JLabel lblMonthTo = new JLabel("Month To");
		lblMonthTo.setBounds(69, 62, 102, 14);
		panel_3.add(lblMonthTo);
		
		JLabel lblDayTo = new JLabel("Day To");
		lblDayTo.setBounds(138, 62, 102, 14);
		panel_3.add(lblDayTo);
		
		JLabel lblTimeTo = new JLabel("Time To");
		lblTimeTo.setBounds(197, 62, 92, 14);
		panel_3.add(lblTimeTo);
		
		yearFromTextField = new JTextField();
		yearFromTextField.setBounds(10, 31, 49, 20);
		panel_3.add(yearFromTextField);
		yearFromTextField.setColumns(10);
		
		monthFromTextField = new JTextField();
		monthFromTextField.setColumns(10);
		monthFromTextField.setBounds(69, 31, 49, 20);
		panel_3.add(monthFromTextField);
		
		dayFromTextField = new JTextField();
		dayFromTextField.setColumns(10);
		dayFromTextField.setBounds(138, 31, 49, 20);
		panel_3.add(dayFromTextField);
		
		timeFromTextField = new JTextField();
		timeFromTextField.setColumns(10);
		timeFromTextField.setBounds(199, 31, 90, 20);
		panel_3.add(timeFromTextField);
		
		yearToTextField = new JTextField();
		yearToTextField.setColumns(10);
		yearToTextField.setBounds(10, 87, 49, 20);
		panel_3.add(yearToTextField);
		
		monthToTextField = new JTextField();
		monthToTextField.setColumns(10);
		monthToTextField.setBounds(69, 87, 49, 20);
		panel_3.add(monthToTextField);
		
		dayToTextField = new JTextField();
		dayToTextField.setColumns(10);
		dayToTextField.setBounds(138, 87, 49, 20);
		panel_3.add(dayToTextField);
		
		timeToTextField = new JTextField();
		timeToTextField.setColumns(10);
		timeToTextField.setBounds(199, 87, 90, 20);
		panel_3.add(timeToTextField);
		
		btnNewButton = new JButton("Back");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
								
				//if string is not blank, go back to prev for training button
				if(!chosenTrainedANN.equals(""))
				{
					//reset this parameter when back is pressed.
					chosenTrainedANN = "";
					
					CardLayout c = (CardLayout)(contentPane.getLayout());
					c.show(contentPane, "trainedNetworkPanel");
				}
				else
				{
					//reset these parameters when Back is pressed.
					chosenLearningRate = 0;
					chosenMomentum = 0;
					//chosenAlgo = "";

					CardLayout c = (CardLayout)(contentPane.getLayout());
					c.show(contentPane, "learning momentum");
				}
			}
		});
		btnNewButton.setBounds(305, 30, 84, 23);
		panel_3.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Accept");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String chosenYearFromString = yearFromTextField.getText();
				String chosenMonthFromString = monthFromTextField.getText();
				String chosenDayFromString = dayFromTextField.getText();
				String chosenTimeFromString = timeFromTextField.getText();
				String chosenYearToString = yearToTextField.getText();
				String chosenMonthToString = monthToTextField.getText();
				String chosenDayToString = dayToTextField.getText();
				String chosenTimeToString = timeToTextField.getText();
				
				chosenYearFrom = Integer.parseInt(chosenYearFromString);
				chosenMonthFrom = Integer.parseInt(chosenMonthFromString);
				chosenDayFrom = Integer.parseInt(chosenDayFromString);
				//convert the normal time input into integer input
				chosenTimeFrom = mainFrame.time_conv(chosenTimeFromString);
				chosenYearTo = Integer.parseInt(chosenYearToString);
				chosenMonthTo = Integer.parseInt(chosenMonthToString);
				chosenDayTo = Integer.parseInt(chosenDayToString);
				//convert the normal time input into integer input
				chosenTimeTo = mainFrame.time_conv(chosenTimeToString);
				
				if(chosenAlgo.equals("Quick Propagation"))
				{					
					//to update textarea in real time
					(new Thread()
					{
						public void run()
						{
							hourAheadLoad.querySelector(chosenYearFrom, chosenMonthFrom, chosenDayFrom, chosenTimeFrom, chosenYearTo, chosenMonthTo, chosenDayTo, chosenTimeTo);
			                hourAheadLoad.trainQuickProp(hourAheadLoad.trainingData(), chosenLearningRate, hiddenNeurons, chosenActiveFnc);
						}
					}).start();
					
					dispose();
				}
				else if(chosenAlgo.equals("Resilient Propagation"))
				{
					//to update textarea in real time
					(new Thread()
					{
						public void run()
						{
							hourAheadLoad.querySelector(chosenYearFrom, chosenMonthFrom, chosenDayFrom, chosenTimeFrom, chosenYearTo, chosenMonthTo, chosenDayTo, chosenTimeTo);
			                hourAheadLoad.trainResilient(hourAheadLoad.trainingData(), hiddenNeurons, chosenActiveFnc);
						}
					}).start();
					
					dispose();
				}
				else if(chosenAlgo.equals("Back Propagation"))
				{	
					//to update textarea in real time
					(new Thread()
					{
						public void run()
						{
							hourAheadLoad.querySelector(chosenYearFrom, chosenMonthFrom, chosenDayFrom, chosenTimeFrom, chosenYearTo, chosenMonthTo, chosenDayTo, chosenTimeTo);
			                hourAheadLoad.trainBackProp(hourAheadLoad.trainingData(), chosenLearningRate, chosenMomentum, hiddenNeurons, chosenActiveFnc);
						}
					}).start();
					
					dispose();
				}
				else if(chosenAlgo.equals("Eval"))
				{
					(new Thread()
					{
						public void run()
						{
							hourAheadLoad.querySelector(chosenYearFrom, chosenMonthFrom, chosenDayFrom, chosenTimeFrom, chosenYearTo, chosenMonthTo, chosenDayTo, chosenTimeTo);
							hourAheadLoad.evaluate(hourAheadLoad.trainingData(), chosenTrainedANN, hourAheadLoad.queryCount(chosenYearFrom, chosenMonthFrom, chosenDayFrom, chosenTimeFrom, chosenYearTo, chosenMonthTo, chosenDayTo, chosenTimeTo));
						}
					}).start();
					
					dispose();
				}
			}
		});
		btnNewButton_1.setBounds(305, 86, 84, 23);
		panel_3.add(btnNewButton_1);
	}
}
