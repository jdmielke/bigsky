package bigsky.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import bigsky.Contact;
import bigsky.Global;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

public class EditContact {

	private JFrame frame;
	private JLabel lblFirstName;
	private JLabel lblLastName;
	private JTextField txtFirstName;
	private JTextField txtLastName;
	private JTextField txtPhone;
	private JTextField txtSecondPhone;
	private JLabel lblPhone;
	private JLabel lblSecondPhone;
	private JButton btnSubmit;
	private JButton btnCancel;
	private Contact contactToEdit;
	private int contactArrayNumber;
	private String oldName;
	private String oldFirstName;
	private String oldLastName;

	/**
	 * Create the application.
	 */
	public EditContact(Contact contact, int contactNumber, String selectedValue) {
		contactToEdit = contact;
		contactArrayNumber = contactNumber;
		oldName = selectedValue;
		oldFirstName = contact.getFirstName();
		oldLastName = contact.getLastName();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(EditContact.class.getResource("/bigsky/BlueText.gif")));

		frame.setSize(355, 301);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("Edit Contact");
        frame.setLocationRelativeTo(null);

		
		lblFirstName = new JLabel("First Name:");
		lblFirstName.setBounds(33, 35, 86, 16);
		frame.getContentPane().add(lblFirstName);
		
		lblLastName = new JLabel("Last Name:");
		lblLastName.setBounds(33, 80, 86, 16);
		frame.getContentPane().add(lblLastName);
		
		txtFirstName = new JTextField();
		txtFirstName.setBounds(136, 36, 134, 28);
		frame.getContentPane().add(txtFirstName);
		txtFirstName.setColumns(10);
		txtFirstName.setText(contactToEdit.getFirstName());
		
		txtLastName = new JTextField();
		txtLastName.setColumns(10);
		txtLastName.setBounds(136, 81, 134, 28);
		frame.getContentPane().add(txtLastName);
		txtLastName.setText(contactToEdit.getLastName());
		
		txtPhone = new JTextField();
		txtPhone.setColumns(10);
		txtPhone.setBounds(136, 122, 134, 28);
		frame.getContentPane().add(txtPhone);
		txtPhone.setText(contactToEdit.getPhoneNumber());
		
		txtSecondPhone = new JTextField();
		txtSecondPhone.setColumns(10);
		txtSecondPhone.setBounds(136, 171, 134, 28);
		frame.getContentPane().add(txtSecondPhone);
		txtSecondPhone.setText(contactToEdit.getSecondPhone());
		
		lblPhone = new JLabel("Phone:");
		lblPhone.setBounds(33, 128, 86, 16);
		frame.getContentPane().add(lblPhone);
		
		lblSecondPhone = new JLabel("Second Phone:");
		lblSecondPhone.setBounds(33, 177, 106, 16);
		frame.getContentPane().add(lblSecondPhone);
		
		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Contact validatedContact = validateContact(txtFirstName.getText(), txtLastName.getText(), txtPhone.getText(), txtSecondPhone.getText());
				if (validatedContact != null) {
					Global.contactAList.set(contactArrayNumber, validatedContact);
					Global.listModel.removeElement(oldName);
					addContactToListModel(txtFirstName.getText(), txtLastName.getText());
					frame.setVisible(false);
				}

				//TODO Place in correct order
				
			}
		});
		btnSubmit.setBounds(189, 230, 117, 29);
		frame.getContentPane().add(btnSubmit);
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.setVisible(false);
			}
		});
		btnCancel.setBounds(33, 230, 117, 29);
		frame.getContentPane().add(btnCancel);
		
	}
	public JFrame getFrmEditContact(){
		return frame;
	}
	
	private Contact validateContact(String firstName, String lastName, String phone, String secondPhone){
		if (firstName.equals("")) {
			if (lastName.equals("")) {
				if (phone.equals("")) {
					JOptionPane.showMessageDialog(null, "Please Enter a First Name, Last Name, or Phone Number");
					return null;
				}
				else {
					firstName = phone;
				}
			}
		}
		if (phone.equals("")){
			if (secondPhone.equals("")){
				JOptionPane.showMessageDialog(null, "Please Enter a Phone Number");
				return null;
			}
			else {
				phone = secondPhone;
				secondPhone = "";
			}
		}
		else {
			phone = phone.replaceAll("\\D+","");
			if (phone.length() != 10){
				JOptionPane.showMessageDialog(null, "Please Enter all Phone Numbers with 10 digits");
				return null;
			}
		}
		if (!secondPhone.equals("")){
			secondPhone = secondPhone.replaceAll("\\D+","");
			if (secondPhone.length() != 10){
				JOptionPane.showMessageDialog(null, "Please Enter all Phone Numbers with 10 digits");
				return null;
			}
		}
		if (!(oldFirstName.equals(firstName) && oldLastName.equals(lastName))){
			//check for duplicate naming
			for (int i = 0; i<Global.contactAList.size();i++){
				Contact con = Global.contactAList.get(i);
				if (con.getFirstName().equals(firstName)){
					if (con.getLastName().equals(lastName)){
						JOptionPane.showMessageDialog(null, "This name already exists. Please choose a new name");
						return null;
					}
				}
			}
		}
		
		return new Contact(firstName, lastName, phone, secondPhone);
	}
	
	private boolean addContactToListModel(String firstName, String lastName){
		String newEntry;
		if (!firstName.equals("")){
			if (!lastName.equals("")){
				newEntry = firstName + " " + lastName;
			}
			else {
				newEntry = firstName;
			}
			int j = Global.listModel.size()/2;
			j = getNewPositionBasedOnStringComparision(j, newEntry);
			Global.listModel.add(j, newEntry);
		}
		else if (!lastName.equals("")){
			newEntry = lastName;
			int j = Global.listModel.size()/2;
			j = getNewPositionBasedOnStringComparision(j, newEntry);
			Global.listModel.add(j, newEntry);
			return true;
		}
		return false;
	}
	
	private int getNewPositionBasedOnStringComparision(int j , String newEntry){
		String testEntry = newEntry.toLowerCase();
		String listEntry = (String)Global.listModel.get(j);
		while (testEntry.compareTo(listEntry.toLowerCase()) < 0 & j!=0){
			j = j /2;
			listEntry = (String)Global.listModel.get(j);
		}
		while (testEntry.compareTo(listEntry.toLowerCase()) > 0 & j < Global.listModel.size()-1){
			j++;
			listEntry = (String)Global.listModel.get(j);
		}
		return j;
	}
	
	
}
