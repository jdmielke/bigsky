package bigsky.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JPasswordField;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.SystemColor;

public class Register extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField firstName;
	private JTextField lastName;
	private JTextField primaryPhone;
	private JPasswordField password;
	private JPasswordField confirmPassword;
	private JLabel confirmPasswordIncorrect;
	private JLabel passwordIncorrect;
	private JLabel usernameIncorrect;
	private JLabel firstNameIncorrect;
	private JLabel lastNameIncorrect;
	private JLabel usernameAlreadyRegistered;
	private JLabel finishLogin;
	private JButton Login;
	private JButton Register;
	private JLabel requiredToRegister;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Register frame = new Register();
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
	public Register() {
		JFrame frame = new JFrame();
		setIconImage(Toolkit.getDefaultToolkit().getImage(Register.class.getResource("/bigsky/BlueText.gif")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(521, 434);
		getContentPane().setLayout(null);
        this.setLocationRelativeTo(null);
		contentPane = new JPanel();

		firstName = new JTextField();
		firstName.setForeground(SystemColor.inactiveCaptionText);
		firstName.setBounds(260, 69, 134, 28);
		getContentPane().add(firstName);
		firstName.setColumns(10);
		
		JLabel lblFirstName = new JLabel("First Name *");
		lblFirstName.setBounds(5, 75, 161, 16);
		getContentPane().add(lblFirstName);
		
		JLabel lblLastName = new JLabel("Last Name *");
		lblLastName.setBounds(5, 120, 161, 16);
		getContentPane().add(lblLastName);
		
		lastName = new JTextField();
		lastName.setForeground(SystemColor.inactiveCaptionText);
		lastName.setColumns(10);
		lastName.setBounds(260, 114, 134, 28);
		getContentPane().add(lastName);
		
		JLabel lblPrimaryPhoneNumber = new JLabel("Primary Phone Number (Username) *");
		lblPrimaryPhoneNumber.setBounds(5, 27, 250, 16);
		getContentPane().add(lblPrimaryPhoneNumber);
		
		primaryPhone = new JTextField();
		primaryPhone.setForeground(SystemColor.inactiveCaptionText);
		primaryPhone.setColumns(10);
		primaryPhone.setBounds(260, 21, 134, 28);
		getContentPane().add(primaryPhone);
		
		JLabel lblPassword = new JLabel("Password *");
		lblPassword.setBounds(5, 194, 202, 16);
		getContentPane().add(lblPassword);
		
		password = new JPasswordField();
		password.setToolTipText("\n");
		password.setBounds(260, 188, 134, 28);
		getContentPane().add(password);
		
		JLabel lblConfirmPassword = new JLabel("Confirm Password *");
		lblConfirmPassword.setBounds(5, 233, 212, 16);
		getContentPane().add(lblConfirmPassword);
		
		confirmPassword = new JPasswordField();
		confirmPassword.setBounds(260, 227, 134, 28);
		getContentPane().add(confirmPassword);
		
		Register = new JButton("Register");
		Register.setBounds(188, 328, 117, 29);
		getContentPane().add(Register);
		
		usernameIncorrect = new JLabel("incorrect");
		usernameIncorrect.setForeground(Color.RED);
		usernameIncorrect.setBounds(404, 28, 60, 14);
		getContentPane().add(usernameIncorrect);
		usernameIncorrect.setVisible(false);
		
		firstNameIncorrect = new JLabel("incorrect");
		firstNameIncorrect.setForeground(Color.RED);
		firstNameIncorrect.setBounds(404, 76, 60, 14);
		getContentPane().add(firstNameIncorrect);
		firstNameIncorrect.setVisible(false);
		
		lastNameIncorrect = new JLabel("incorrect");
		lastNameIncorrect.setForeground(Color.RED);
		lastNameIncorrect.setBounds(404, 121, 60, 14);
		getContentPane().add(lastNameIncorrect);
		lastNameIncorrect.setVisible(false);
		
		passwordIncorrect = new JLabel("incorrect");
		passwordIncorrect.setForeground(Color.RED);
		passwordIncorrect.setBounds(404, 195, 60, 14);
		getContentPane().add(passwordIncorrect);
		passwordIncorrect.setVisible(false);
		
		confirmPasswordIncorrect = new JLabel("incorrect");
		confirmPasswordIncorrect.setForeground(Color.RED);
		confirmPasswordIncorrect.setBounds(404, 234, 60, 14);
		getContentPane().add(confirmPasswordIncorrect);
		confirmPasswordIncorrect.setVisible(false);
		
		usernameAlreadyRegistered = new JLabel("Username Already Registered");
		usernameAlreadyRegistered.setForeground(Color.RED);
		usernameAlreadyRegistered.setBounds(188, 303, 246, 14);
		getContentPane().add(usernameAlreadyRegistered);
		usernameAlreadyRegistered.setVisible(false);
		
		
		Login = new JButton("Login");
		Login.setBounds(188, 328, 117, 29);
		getContentPane().add(Login);
		Login.setVisible(false);
		
		finishLogin = new JLabel("Login to finish registrtion");
		finishLogin.setForeground(Color.BLUE);
		finishLogin.setBounds(188, 303, 171, 14);
		getContentPane().add(finishLogin);
		finishLogin.setVisible(false);
		
		requiredToRegister = new JLabel("* Required to register");
		requiredToRegister.setForeground(Color.RED);
		requiredToRegister.setBounds(182, 367, 212, 16);
		getContentPane().add(requiredToRegister);
		requiredToRegister.setVisible(false);
		
	
	
		Register.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        
	        	if(registerChecks()){

					try {
						putInSystem();
						
						Register.setVisible(false);
						Login.setVisible(true);
						finishLogin.setVisible(true);
						
						
					} catch (Exception e1) {
						System.out.println("Register error");
					}
					        		
				}
     
	        }
	    });
		
		Login.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	dispose();
	        	Login log = new Login();
	        	log.setVisible(true);
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
		String user = primaryPhone.getText();
		//takes out all not  digits
		user = user.replaceAll("\\D+","");
		return  user.trim();	
	}
	private String getFirstName(){
		String user = firstName.getText();
		return  user.trim();	
		}
	private String getLastName(){
		String user = lastName.getText();
		return  user.trim();	
		}
		
	
	private boolean registerChecks(){
		int count = 0;
		String pass = getPassword(password);
		String confirmPass = getPassword(confirmPassword);
		String username = getUsername();
		String firstName = getFirstName();
		String lastName = getLastName();
		
		passwordIncorrect.setVisible(false);
		confirmPasswordIncorrect.setVisible(false);
		usernameIncorrect.setVisible(false);
		lastNameIncorrect.setVisible(false);
		requiredToRegister.setVisible(false);
		try{
			
		
		if(isInSystem()){
			usernameAlreadyRegistered.setVisible(true);
			return false;
		}
		}
		catch(Exception e){
			System.out.println("Register Checks error" + e.getMessage());
		}
		if(pass == null || confirmPass == null || pass.equals("")|| confirmPass.equals("")|| !pass.equals(confirmPass)){
			passwordIncorrect.setVisible(true);
			confirmPasswordIncorrect.setVisible(true);
			count++;
		}
		
		if(username == null || username.length() != 10){
			usernameIncorrect.setVisible(true);
			count++;
		}
		
		if(firstName == null || firstName.equals("")){
			firstNameIncorrect.setVisible(true);
			count++;
		}
		
		if( lastName == null || lastName.equals("")){
			lastNameIncorrect.setVisible(true);
			count++;
		}
		
		if(count == 0){
			return true;
		}
		
		return false;
	}
	
	private void putInSystem(){
		try{
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://mysql.cs.iastate.edu/db30901", "adm309", "EXbDqudt4");
		Statement stmt = con.createStatement();
		
		String query = "INSERT INTO testTable  (phoneNumber, lastName, firstName, password) VALUES " +
				"('" + getUsername() + "', '" + getLastName() + "', '" + getFirstName() + "','" + getPassword(password) +
				"')";
	
		stmt.executeUpdate(query);
		con.close();	
		}
		catch(Exception e){
			System.out.println("put in system error");
		}
	}
	
	
	private boolean isInSystem(){
		
		try{
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://mysql.cs.iastate.edu/db30901", "adm309", "EXbDqudt4");
		Statement stmt = con.createStatement();
	
		ResultSet rs = con.createStatement().executeQuery("select * from testTable where phoneNumber='" + getUsername() + "'");
		//boolean temp = rs.next();
		if(rs.next() == true){
			
			rs.close();		
			con.close();
			return true;
		}
		
		rs.close();		
		con.close();
		}
		catch(Exception e){
			System.out.println("in system error");
		}
		return false;
	}
}
