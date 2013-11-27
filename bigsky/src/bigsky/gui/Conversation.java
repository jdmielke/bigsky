package bigsky.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

import bigsky.BlueTextRequest;
import bigsky.Contact;
import bigsky.Global;
import bigsky.TaskBar;
import bigsky.TextMessage;
import bigsky.messaging.TextMessageManager;

public class Conversation {
	
	//added variables from Jon
	private final JTextArea txtrEnterMessageHere = new JTextArea();
	public static ArrayList<JTextPane> textPanes = new ArrayList<JTextPane>();
	public static ArrayList<Contact> currentConvs = new ArrayList<Contact>(); 
	private static Contact me = new Contact("Jonathan", "Mielke", "6185204620", null);
	public static ArrayList<Integer> offset = new ArrayList<Integer>();

	
	

	private static BlueTextRequest rq;

	private JFrame frmBluetext;
	private JTextField txtSearch;
	
	private final int returnsNull = 99999;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

	}

	/**
	 * Create the application.
	 * @wbp.parser.entryPoint
	 */
	public Conversation() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */

	private void initialize() {
		frmBluetext = new JFrame();
		frmBluetext.setIconImage(Toolkit.getDefaultToolkit().getImage(Conversation.class.getResource("/bigsky/BlueText.gif")));
		frmBluetext.setTitle("BlueText");
		frmBluetext.setSize(new Dimension(800,650));
		frmBluetext.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frmBluetext.setLocationRelativeTo(null);
		
		JMenuBar menuBar = new JMenuBar();
		frmBluetext.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mnu_new_contact = new JMenuItem("New Contact");
		mnu_new_contact.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openNewContactWindow();
			}
		});
		mnFile.add(mnu_new_contact);

		JMenuItem mnu_new_conversation = new JMenuItem("New Conversation");
		mnu_new_conversation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startNewConv();
			}
		});
		mnu_new_conversation.addMouseListener(new MouseAdapter() {
		});
		mnFile.add(mnu_new_conversation);

		JMenuItem mnu_logout = new JMenuItem("Log Out");
		mnu_logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					TaskBar.logout();
				} catch (Exception e1) {}
			}
		});
		mnFile.add(mnu_logout);

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmEditContact = new JMenuItem("Edit Contact");
		mntmEditContact.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editContactAction();
				
			}
		});
		mnEdit.add(mntmEditContact);

		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmAboutBluetext = new JMenuItem("About BlueText");
		mnHelp.add(mntmAboutBluetext);

		JPanel panel = new JPanel();
		frmBluetext.getContentPane().add(panel);
		panel.setLayout(null);

		txtSearch = new JTextField();
		txtSearch.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String searchTerm = txtSearch.getText();
				searchContact(searchTerm);
			}
		});
		txtSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				txtSearch.setText("");
			}
		});

		txtSearch.setBounds(16, 6, 190, 29);
		panel.add(txtSearch);
		txtSearch.setText("Search");
		txtSearch.setColumns(10);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(16, 41, 188, 346);
		panel.add(scrollPane);
		Global.list.addMouseListener(new PopClickListener());
		Global.list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String selectedContact = (String)Global.list.getSelectedValue();
				if (selectedContact.contains("Create Contact")) {
					openNewContactWindow();
				}
			}
		});

		Global.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(Global.list);


		

//		JPanel panel_1 = new JPanel();
//		Global.conversationPane.addTab("New Conversation", null, panel_1, null);

		JPanel conversationPanel = new JPanel();
		conversationPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		conversationPanel.setBounds(226, 25, 490, 385);
		
		Global.conversationPane.setBounds(226, 0, 490, 35);
		conversationPanel.add(Global.conversationPane);
	
		panel.add(conversationPanel);
		conversationPanel.setLayout(new CardLayout(0, 0));

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_3.setBounds(16, 422, 190, 100);
		panel.add(panel_3);

		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				TextMessage text = new TextMessage(me,currentConvs.get(Global.conversationPane.getSelectedIndex()),txtrEnterMessageHere.getText());
            	try {
            		System.out.println(text.getReceiver().getFirstName());
					updateConv(text);
				} catch (BadLocationException e) {
					e.printStackTrace();
					System.out.println("updateConv in Conversation - FAILED");
				}
            	txtrEnterMessageHere.setText("");
			}
		});
		btnSend.setBounds(599, 493, 117, 29);
		panel.add(btnSend);
	
		
		txtrEnterMessageHere.setText("New Message...");
		txtrEnterMessageHere.setBounds(226, 429, 490, 93);
		panel.add(txtrEnterMessageHere);
	
		
		txtrEnterMessageHere.addKeyListener(new KeyAdapter()
	    {
	        public void keyPressed(KeyEvent evt)
	        {
	            if(evt.getKeyCode() == KeyEvent.VK_ENTER)
	            {
	            	TextMessage text = new TextMessage(me,currentConvs.get(Global.conversationPane.getSelectedIndex()),txtrEnterMessageHere.getText());
	            	try {
	            		System.out.println(text.getReceiver().getFirstName());
						updateConv(text);
					} catch (BadLocationException e) {
						e.printStackTrace();
						System.out.println("updateConv in Conversation - FAILED");
					}
	            	txtrEnterMessageHere.setText("");
	            	
	            }
	        }
	        public void keyReleased(KeyEvent evt){
	        	if(evt.getKeyCode() == KeyEvent.VK_ENTER){
	        		txtrEnterMessageHere.setText("");
	        	}
	        }
	    });
		
		txtrEnterMessageHere.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				txtrEnterMessageHere.setText("");
			}
		});
		

		JButton btn_select_contact = new JButton("Start New Convo");
		btn_select_contact.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!Global.list.isSelectionEmpty()){
					startNewConv();
				}
			}
		});
		btn_select_contact.setBounds(16, 388, 186, 29);
		panel.add(btn_select_contact);
		
	}
	private void editContactAction(){
		String selectedValue = (String)Global.list.getSelectedValue();
		if (selectedValue != null){
			int i = findContactInListModel(selectedValue);
			if (i != returnsNull){
				Contact selectedContactCon = Global.contactAList.get(i);
				EditContact editCon = new EditContact(selectedContactCon, i, selectedValue);
				editCon.getFrmEditContact().setVisible(true);
			}
			else System.out.println("Error in Edit Contact");
		}
		else{
			JOptionPane.showMessageDialog(null, "Please select a contact to edit.");
		}
	}

	private void openNewContactWindow(){
		NewContact newCon = new NewContact();
		newCon.getFrmNewContact().setVisible(true);
	}
	private void searchContact(String searchTerm){
		Global.listModel.removeAllElements();
		if (!searchTerm.equals("")){
			for (int i = 0; i < Global.contactAList.size()-1; i++){
				Contact con = Global.contactAList.get(i);
				String first = con.getFirstName();
				String last = con.getLastName();
				if (first.toLowerCase().contains(searchTerm.toLowerCase())) {
						addContactToListModel(first, last);
				}
				else if (last.toLowerCase().contains(searchTerm.toLowerCase())) {
					addContactToListModel(first, last);
				}
				else if ((first.toLowerCase() + " " + last.toLowerCase()).contains(searchTerm.toLowerCase())) {
					addContactToListModel(first, last);
				}
			}
		}
		else {
			for (int i = 0; i < Global.contactAList.size()-1; i++){
				Contact con = Global.contactAList.get(i);
				String firstName = con.getFirstName();
				String lastName = con.getLastName();
				addContactToListModel(firstName, lastName);
				sortListModel();
			}
		}
		if (Global.listModel.size() < 12){
			Global.listModel.addElement("Create Contact");
		}
	}
	
	private void addContactToListModel(String firstName, String lastName){
		if (!firstName.equals("")){
			String newEntry = firstName + " " + lastName;
			Global.listModel.addElement(newEntry);
		}
		else if (lastName.equals("")){
			String newEntry = lastName;
			Global.listModel.addElement(newEntry);
		}
	}
	
	private int findContactInListModel(String selectedValue){
		for (int i=0;i<Global.contactAList.size();i++){
			Contact con = Global.contactAList.get(i);
			if (con.getFirstName().equals(selectedValue)){
				return i;
			}
			else if (con.getLastName().equals(selectedValue)){
				return i;
			}
			else if ((con.getFirstName() + " " + con.getLastName()).equals(selectedValue)){
				return i;
			}
		}
		return returnsNull;
	}

	public JFrame getFrmBluetext() {
		return frmBluetext;
	}
	
	private void sortListModel(){
		String[] tempList = new String[Global.listModel.size()];
		for (int i=0; i<Global.listModel.size(); i++) {
			tempList[i] = (String)Global.listModel.get(i);
		}
		Global.listModel.removeAllElements();
		Arrays.sort(tempList);
		for (int i=0; i<tempList.length;i++){
			Global.listModel.addElement(tempList[i]);
		}
	}
	
	
	public static void createTab(Contact contact){
		JTextPane textPane = new JTextPane();
		DefaultCaret caret = (DefaultCaret)textPane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textPane.setFont(new Font("Franklin Gothic Medium", Font.PLAIN, 12));
		textPane.setEditable(false);
		textPanes.add(textPane);
		JScrollPane scroll = new JScrollPane(textPane);
		Global.conversationPane.addTab((String)Global.list.getSelectedValue(), null, scroll, null);
		Global.conversationPane.setSelectedIndex(Global.conversationPane.getTabCount()-1);
		offset.add(new Integer(0));
	}
	
	public static Contact getConvReceiver(String name){
		String first = "";
		String last = "";
		String phoneNumber = null;
		String secondPhone = null;
		Scanner scanner = new Scanner(name);
		first = scanner.next();
		if(scanner.hasNext()){
			last = scanner.next();
		}
		for(int i = 0; i < Global.contactAList.size(); i++){
			if(Global.contactAList.get(i).getFirstName().equalsIgnoreCase(first) && Global.contactAList.get(i).getLastName().equalsIgnoreCase(last)){
				phoneNumber = Global.contactAList.get(i).getPhoneNumber();
				if(Global.contactAList.get(i).getSecondPhone() != null){
					secondPhone = Global.contactAList.get(i).getSecondPhone();
				}
				break;
			}
		}
		Contact receiver = new Contact(first, last,phoneNumber,secondPhone);
		scanner.close();
		return receiver;
	}
	
	public static void startNewConv(){
		boolean match = false;
		System.out.println(getConvReceiver((String)Global.list.getSelectedValue()).getPhoneNumber());
		for(int i = 0; i < currentConvs.size(); i++){
			if(getConvReceiver((String)Global.list.getSelectedValue()).getFirstName().equals(currentConvs.get(i).getFirstName()) &&
					getConvReceiver((String)Global.list.getSelectedValue()).getPhoneNumber().equals(currentConvs.get(i).getPhoneNumber())){
				match = true;
				break;
			}
		}
		if(!match){
			rq = new BlueTextRequest(BlueTextRequest.REQUEST.CONTACT_CHAT_HISTORY, getConvReceiver((String)Global.list.getSelectedValue()));
			TaskBar.messageHost.sendObject(rq);
			
			currentConvs.add(getConvReceiver((String)Global.list.getSelectedValue()));
			createTab(getConvReceiver((String)Global.list.getSelectedValue()));
		}
	}
	
	public static void updateConv(TextMessage text) throws BadLocationException{
		
		int current = 0;
		int temp = 0;
		Contact you = null;
		boolean check1 = false;
		boolean check2 = false;
		boolean check3 = false;
		if(Global.conversationPane.getTabCount()!=0){
			current = Global.conversationPane.getSelectedIndex();
			temp = offset.get(current);
		}
		if(!text.getContent().trim().isEmpty() && text.getSender().getPhoneNumber().equalsIgnoreCase(me.getPhoneNumber())){
			textPanes.get(current).getDocument().insertString(offset.get(current), text.getSender().getFirstName() + ":\t" + text.getContent() + "\n\n", null);
			temp += (text.getSender().getFirstName() + ":\t" + text.getContent() + "\n\n").length();
			offset.set(current, temp);
			
			if(!TaskBar.doNotSend){
				TaskBar.outGoingInConv.add(text);
			}
			
			if(TaskBar.outGoingInConv.size() != 0){
				for(int i = 0; i < TaskBar.smallChatWindows.size();i++){
					if(TaskBar.outGoingInConv.get(0).getReceiver().getPhoneNumber().equals(TaskBar.smallChatWindows.get(i).getFromContact().getPhoneNumber())){
						TaskBar.doNotSend = true;
						TaskBar.smallChatWindows.get(i).receivedText(text);
						TaskBar.outGoingInConv.remove(0);
						TaskBar.doNotSend = false;
						check3 = true;
						break;
					}
				}
				if(check3 == false){
					TaskBar.smallChatWindows.add(new SmallChat(text.getSender(), text.getReceiver()));
					TaskBar.doNotSend = true;
					
					TaskBar.smallChatWindows.get(current).receivedText(text);
					
					TaskBar.outGoingInConv.remove(0);
					TaskBar.doNotSend = false;
				}
			}

			
			if(!TaskBar.doNotSend && TextMessageManager.sendTexts){
				TaskBar.messageHost.sendObject(text);
			}
			
			check1 = true;
		}
		else{
			for(int i = 0; i < currentConvs.size();i++){
				if(currentConvs.get(i).getPhoneNumber().equalsIgnoreCase(text.getSender().getPhoneNumber())){
					you = currentConvs.get(i);
					check2 = true;
					current = i;
					break;
				}
			}
		}	
		if(!text.getContent().trim().isEmpty() && check2){
			temp = offset.get(current);
			textPanes.get(current).getDocument().insertString(temp, text.getSender().getFirstName() + ":\t" + text.getContent() + "\n\n", null);
			temp += (text.getSender().getFirstName() + ":\t" + text.getContent() + "\n\n").length();
			offset.set(current, temp);
		}
		else if(!text.getContent().trim().isEmpty() && you == null && !check1){
			JTextPane textPane = new JTextPane();
			DefaultCaret caret = (DefaultCaret)textPane.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
			textPane.setFont(new Font("Franklin Gothic Medium", Font.PLAIN, 12));
			textPane.setEditable(false);
			textPanes.add(textPane);
			JScrollPane scroll = new JScrollPane(textPane);
			Global.conversationPane.addTab(text.getSender().getFirstName() + " " + text.getSender().getLastName(), null, scroll, null);
			Global.conversationPane.setSelectedIndex(Global.conversationPane.getTabCount()-1);
			offset.add(new Integer(0));
			current = offset.size() - 1;
			temp = offset.get(current);
			currentConvs.add(text.getSender());
			textPanes.get(current).getDocument().insertString(temp, text.getSender().getFirstName() + ":\t" + text.getContent() + "\n\n", null);
			temp += (text.getSender().getFirstName() + ":\t" + text.getContent() + "\n\n").length();
			offset.set(current, temp);
		};
	}
	
	

}



