package bigsky.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JPasswordField;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JButton;

import bigsky.Global;
import bigsky.TaskBar;
import bigsky.messaging.*;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField_1;
	private JLabel promptRegister;
	private JLabel wrongInfo;
	private JRadioButton saveInfo;
	private boolean hit = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
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
	public Login() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Login.class.getResource("/bigsky/BlueText.gif")));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
        JLabel image = new JLabel();
        contentPane = (JPanel) getContentPane();
       // contentPane.setLayout(new BorderLayout());
        setSize(new Dimension(576, 320));
        ImageIcon icon = new ImageIcon(this.getClass().getResource(
                "Login_Image.png"));
        image.setIcon(icon);       
        contentPane.add(image, java.awt.BorderLayout.CENTER);
        this.setLocationRelativeTo(null);

		textField = new JTextField();
		textField.setForeground(Color.WHITE);
		textField.setBackground(Color.GRAY);
		textField.setBounds(220, 130, 165, 28);
		image.add(textField);
		
		passwordField_1 = new JPasswordField();
		passwordField_1.setForeground(Color.WHITE);
		passwordField_1.setBackground(Color.GRAY);
		passwordField_1.setBounds(220, 180, 165, 28);
		image.add(passwordField_1);
		
		JLabel lblPhoneNumber = new JLabel("Phone Number");
		lblPhoneNumber.setForeground(Color.WHITE);
		lblPhoneNumber.setBounds(126, 135, 98, 16);
		image.add(lblPhoneNumber);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(126, 185, 98, 16);
		lblPassword.setForeground(Color.WHITE);
		image.add(lblPassword);
		
		JButton login = new JButton("Login");
		login.setBounds(200, 258, 90, 29);
		image.add(login);
		
		JButton register = new JButton("Register");
		register.setBounds(315, 258, 90, 29);
		image.add(register);
		
		wrongInfo = new JLabel("Wrong username or password");
		wrongInfo.setForeground(Color.RED);
		wrongInfo.setBounds(236, 233, 215, 14);
		image.add(wrongInfo);
		wrongInfo.setVisible(false);
		
		promptRegister = new JLabel("Would you like to register");
		promptRegister.setForeground(Color.RED);
		promptRegister.setBounds(259, 233, 192, 14);
		image.add(promptRegister);
		promptRegister.setVisible(false);
		
		saveInfo = new JRadioButton("Save Username/Password");
		saveInfo.setBackground(Color.WHITE);
		saveInfo.setForeground(Color.WHITE);
		saveInfo.setBounds(220, 213, 212, 23);
		image.add(saveInfo);
		saveInfo.setOpaque(false);

		
		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
            	            	
            	try {
					if(login()){

						dispose();
						TaskBar.putIconInSystemTray();
						if(TaskBar.messageHost==null){   
				   	   		TaskBar.messageHost = new MessageHost();
				   	   		TaskBar.messageHost.start();
				   	   		TaskBar.textManager = new TextMessageManager();
				   	   		TaskBar.textManager.start();
				        }			
						LoginInfo();
						systemPrefs();
						if(hit){
							saveInProp(getUsername(), "save", Global.ON);
						}
					}
					else{
						System.out.println("FAIL");
					}
				} catch (Exception e1) {}			
            }
        });
		

		
		register.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	dispose();
            	Register reg = new Register();
            	reg.setVisible(true);
            }
        });	
		
		//save password and username button
				saveInfo.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		            	  
		            	hit = true;
		            }
		        });	

	}
	
	private String getPassword(JPasswordField pass){
		String password = "";
		char [] word = pass.getPassword();
		for(int i = 0; i < word.length; i++){
			password = password + word[i];
		}
		return password;	
	}
	
	private String getUsername(){
		String user = textField.getText();
		//takes out all not  digits
		user = user.replaceAll("\\D+","");
		Global.username = user.trim();
		return  Global.username;	
	}
	
	
	//returns row number of users database row
	public boolean login(){
		
		try{
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://mysql.cs.iastate.edu/db30901", "adm309", "EXbDqudt4");
		Statement stmt = con.createStatement();
		String iP =InetAddress.getLocalHost().getHostAddress();		
		
		ResultSet rs = con.createStatement().executeQuery("select * from testTable where phoneNumber='" + getUsername() + "'");
	
		if(rs.next() == false){
			promptRegister.setVisible(true);
			rs.close();		
			con.close();
			return false;
		}
		if(rs.getString("password") == null){
			promptRegister.setVisible(true);
			rs.close();		
			con.close();
			return false;			
		}
			
		else if(!getUsername().equals(rs.getString("phonenumber")) || !getPassword(passwordField_1).equals(rs.getString("password"))){
			wrongInfo.setVisible(true);
			rs.close();		
			con.close();
			return false;
		}

		
		else if(!getUsername().equals(rs.getString("phonenumber")) && !getPassword(passwordField_1).equals(rs.getString("password"))){
			promptRegister.setVisible(true);
			rs.close();		
			con.close();
			return false;
		}

		
		stmt.executeUpdate("UPDATE testTable SET IP_Computer='" + iP + "' WHERE phoneNumber='" + getUsername() + "';");
		
		rs.close();		
		con.close();
		}
		
		catch(Exception e){
			System.out.println("Login fail" + e.getMessage());
		}
		
		return true;
		
	}
	/**
	 * initializes the preference file
	 */
	public void LoginInfo(){
		
		Properties prop = new Properties();
		prop.setProperty("username", getUsername());
		prop.setProperty("password", getPassword(passwordField_1));
		prop.setProperty("save", Global.OFF);
		prop.setProperty(Global.ONLINE,Global.ON);
		prop.setProperty(Global.AWAY, Global.OFF);
		prop.setProperty(Global.BUSY, Global.OFF);
		prop.setProperty(Global.NOTIFICATION,Global.ON);
		
		try {
			prop.store(new FileOutputStream(getUsername() + ".properties"),null);
			
		} catch (Exception e) {
			System.out.println("file load problem.");
		}
		
	}
	
	/**
	 * Generic method to store items in users preference file
	 * @param user
	 * @param property
	 * @param value
	 */
	public static void saveInProp(String user, String property, String value){
		
		Properties prop = new Properties();

		try {
			prop.load(new FileInputStream(user +".properties"));
		} catch (Exception e) {
			System.out.println("File load problem.");
		}
		
		prop.setProperty(property, value);
	
		try {
			prop.store(new FileOutputStream(user+".properties"),null);
			
		} catch (Exception e) {
			System.out.println("file store problem.");
		}
		
		
	}
	/**
	 * Sets up the system preference file
	 */
	public void systemPrefs(){
		
		Properties prop = new Properties();
		prop.setProperty("lastLoggedIn", getUsername());
		
		try {
			prop.store(new FileOutputStream("system.properties"),null);
			
		} catch (Exception e) {
			System.out.println("System store file problem");		
		}
		
	}
	
}
