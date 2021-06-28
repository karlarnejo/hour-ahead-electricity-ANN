package hourAheadPackage;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.CardLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.awt.event.ActionEvent;

public class loginReg extends JFrame {

	private JPanel contentPane;
	private JTextField usernameLogin;
	private JPasswordField passwordLogin;
	private JTextField usernameReg;
	private JPasswordField passwordReg;
	private JPasswordField passwordField_2;
	private static Connection con;
	
	String username = "";
	String password = "";
	
	mainFrame mainf = new mainFrame();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					loginReg frame = new loginReg();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public loginReg() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setResizable(false);
		contentPane.setLayout(new CardLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, "login");
		panel.setLayout(null);
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(115, 97, 67, 14);
		panel.add(lblUsername);
		
		usernameLogin = new JTextField();
		usernameLogin.setBounds(176, 94, 124, 20);
		panel.add(usernameLogin);
		usernameLogin.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(115, 136, 67, 14);
		panel.add(lblPassword);
		
		passwordLogin = new JPasswordField();
		passwordLogin.setBounds(176, 133, 124, 20);
		panel.add(passwordLogin);
		
		JButton loginBtn = new JButton("Login");
		loginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				username = usernameLogin.getText();
				password = passwordLogin.getText();

				if(login(username, password) == 1)
				{
					mainf.setVisible(true);
					dispose();
				}
				else
				{
					JOptionPane.showMessageDialog(loginBtn, "Incorrect login details.");
				}
			}
		});
		loginBtn.setBounds(214, 171, 86, 23);
		panel.add(loginBtn);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, "register");
		panel_1.setLayout(null);
		
		JLabel lblUsername_1 = new JLabel("Username:");
		lblUsername_1.setBounds(124, 63, 83, 14);
		panel_1.add(lblUsername_1);
		
		usernameReg = new JTextField();
		usernameReg.setBounds(217, 60, 107, 20);
		panel_1.add(usernameReg);
		usernameReg.setColumns(10);
		
		JLabel lblPassword_1 = new JLabel("Password:");
		lblPassword_1.setBounds(124, 98, 83, 14);
		panel_1.add(lblPassword_1);
		
		passwordReg = new JPasswordField();
		passwordReg.setBounds(217, 95, 107, 20);
		panel_1.add(passwordReg);
		
		JButton registerBtn = new JButton("Register");
		registerBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout c = (CardLayout)(contentPane.getLayout());
				c.show(contentPane, "login");
			}
		});
		registerBtn.setBounds(214, 191, 110, 23);
		panel_1.add(registerBtn);
		
		JLabel confirmPassReg = new JLabel("Confirm Password:");
		confirmPassReg.setBounds(124, 132, 149, 14);
		panel_1.add(confirmPassReg);
		
		passwordField_2 = new JPasswordField();
		passwordField_2.setBounds(217, 126, 107, 20);
		panel_1.add(passwordField_2);
		
		JComboBox<String> acctTypeReg = new JComboBox<String>();
		acctTypeReg.setModel(new DefaultComboBoxModel<String>(new String[] {"Regular User", "Developer"}));
		acctTypeReg.setBounds(217, 157, 107, 23);
		panel_1.add(acctTypeReg);
		
		JLabel lblAccountType = new JLabel("Account Type:");
		lblAccountType.setBounds(124, 157, 123, 14);
		panel_1.add(lblAccountType);
		
		JButton backBtn = new JButton("Back");
		backBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout c = (CardLayout)(contentPane.getLayout());
				c.show(contentPane, "login");
			}
		});
		backBtn.setBounds(118, 191, 89, 23);
		panel_1.add(backBtn);
	}
	public int login(String username, String password)
	{
		String user = "";
		String pass = "";
		String typeUser = "";
		int id = 0;
		
		con = DatabaseConnection.connectDB();
		try 
		{
			String query = String.format("SELECT username,"+" \"password\","+ " userId, usertype FROM "+ "\"user\""+ " WHERE username = '%s' AND password = '%s'", username, password);
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(query);
		
			while(rs.next())
			{
				user = rs.getString(1);
				pass = rs.getString(2);
				id = rs.getInt(3);
				typeUser = rs.getString(4);
			}
			if(user.equals(username) && pass.equals(password) && typeUser.equals("Admin"))
			{
				mainFrame.setUserID(id);
				Statement ins = con.createStatement();
 				String query_ins = String.format("INSERT INTO logs(userid, dateact, action) VALUES('%s', '%s', '%s')", mainFrame.getUserID(), "now()", "Login");
 				ins.executeUpdate(query_ins);				
				return 1;
			}
			else if(user.equals(username) && pass.equals(password) && typeUser.equals("Regular User"))
			{
				mainFrame.setUserID(id);
				mainFrame.trainNetworkBtn.setEnabled(false);
				mainFrame.evaluateNetworkBtn.setEnabled(false);
				Statement ins = con.createStatement();
 				String query_ins = String.format("INSERT INTO logs(userid, dateact, action) VALUES('%s', '%s', '%s')", mainFrame.getUserID(), "now()", "Login");
 				ins.executeUpdate(query_ins);				
				return 1;
			}
			}catch(Exception e)
			{
				return 0;
			}
		return 0;
	}
}
