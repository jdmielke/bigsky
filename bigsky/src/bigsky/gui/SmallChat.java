package bigsky.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;

import bigsky.Contact;
import bigsky.Global;
import bigsky.TaskBar;
import bigsky.TextMessage;
import bigsky.messaging.TextMessageManager;

import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SmallChat  {

	private JFrame frmBluetext;
	public static final JTextField textField = new JTextField();
	private JButton btnName;
	private JButton btnNewButton;
	private ArrayList<TextMessage> myTextHistory = new ArrayList<TextMessage>();
	
	
	private JButton send;
	private JScrollPane scrollPane;
	private JTextPane textPane;
	DefaultCaret caret;

	
	private int offset = 0;
	private int textCount = -1;
	private static int windowNum = 0;
	private int winNum = 0;
	
	private Contact me;
	private Contact you;
	private TextMessage sent;
	private JTextField textField_1;
	private JMenu settings;
	private JMenu status;
	private JMenu notification;
	private JMenu fontSize;
	private JRadioButtonMenuItem notificationON;
	private JRadioButtonMenuItem notificationOFF;
	private ButtonGroup notiGroup;
	private JMenuBar menuBar;
	private JRadioButtonMenuItem online;
	private JRadioButtonMenuItem away;
	private JRadioButtonMenuItem busy;
	private ButtonGroup statusGroup;

	//private TrayIcon notification = new TrayIcon(new ImageIcon(TaskBar.class.getResource("BlueText.gif"), "tray icon").getImage());
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SmallChat window = new SmallChat(new Contact("Jonathan", "Mielke", "6185204620", ""), new Contact("Friendly", "Friend", "55555555555", ""));
					window.frmBluetext.setVisible(true);
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
	}

	/**
	 * initiallizes a quick chat window
	 * @param me
	 * @param you
	 */
	public SmallChat(Contact me, Contact you) {
		initialize();
		
		this.me = me;
		this.you = you;
		winNum = windowNum;
		windowNum++;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//final SystemTray tray = SystemTray.getSystemTray();
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		
		send = new JButton("Send");
		
		frmBluetext = new JFrame();
		frmBluetext.setIconImage(Toolkit.getDefaultToolkit().getImage(SmallChat.class.getResource("/bigsky/BlueText.gif")));
		frmBluetext.getRootPane().setDefaultButton(send);
		frmBluetext.setResizable(false);
		frmBluetext.getContentPane().setBackground(Color.DARK_GRAY);
		frmBluetext.setTitle("BlueText");
		frmBluetext.setBounds(gd.getDisplayMode().getWidth() - 243, gd.getDisplayMode().getHeight() - 385, 236, 340);
		frmBluetext.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmBluetext.getContentPane().setLayout(null);
		
		frmBluetext.getContentPane().add(send);
		
		
		
		textField.setBounds(0, 289, 230, 23);
		frmBluetext.getContentPane().add(textField);
		textField.setColumns(10);
		
		textPane = new JTextPane();
		caret = (DefaultCaret)textPane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textPane.setFont(new Font("Franklin Gothic Medium", Font.PLAIN, 12));
		textPane.setEditable(false);

		
		scrollPane = new JScrollPane(textPane);
		scrollPane.setBounds(0, 24, 230, 264);
		frmBluetext.getContentPane().add(scrollPane);
		scrollPane.setViewportView(textPane);


		//Beginning of Settings Menu Bar
			menuBar = new JMenuBar();
			menuBar.setBounds(169, 0, 60, 23);
			frmBluetext.getContentPane().add(menuBar);
			
			settings = new JMenu("Settings");
			menuBar.add(settings);
			
			//Notifications menu
			notification = new JMenu("Notifications");
			settings.add(notification);
					
			notificationON = new JRadioButtonMenuItem("On");
			notificationOFF = new JRadioButtonMenuItem("Off");
			
			notification.add(notificationON);
			notification.add(notificationOFF);
			// Adding buttons to group so only 1 radio button can be selected at any times
			notiGroup = new ButtonGroup();
			notiGroup.add(notificationON);
			notiGroup.add(notificationOFF);
			
			//Status Menu
			status = new JMenu("Status");
			settings.add(status);
				
			online = new JRadioButtonMenuItem("Online");
			away = new JRadioButtonMenuItem("Away");
			busy = new JRadioButtonMenuItem("Busy");
			
			status.add(online);
			status.add(away);
			status.add(busy);
					
			//Adding buttons to group so that only 1 radio button can be selected at any time.
			statusGroup = new ButtonGroup();
			statusGroup.add(online);
			statusGroup.add(away);
			statusGroup.add(busy);
			
			//Choose font size of small chat				
			fontSize = new JMenu("Font Size");
			settings.add(fontSize);
			
			textField_1 = new JTextField();
			fontSize.add(textField_1);
			textField_1.setColumns(10);
			
			
			checkButtons();
		
		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				sent = new TextMessage(me, you, textField.getText());

				try {			
					updateConv(sent);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
					System.out.println("updateConv in SmallChat - FAILED");
				} 
				textField.setText("");
			}
		});
		
		//Notification setting
		notificationON.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(notificationON.isSelected()){
					Login.saveInProp(Global.username,Global.NOTIFICATION, Global.ON);
					notificationOFF.setSelected(false);
				}				
			}
		});
		//Notification setting
				notificationOFF.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(notificationOFF.isSelected()){
							Login.saveInProp(Global.username,Global.NOTIFICATION, Global.OFF);
							notificationON.setSelected(false);
						}						
					}
				});
		//Status settings
		online.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(online.isSelected()){
					Login.saveInProp(Global.username,Global.ONLINE, Global.ON);
					Login.saveInProp(Global.username,Global.BUSY, Global.OFF);
					Login.saveInProp(Global.username,Global.AWAY, Global.OFF);
	
				}				
			}
		});
		
		away.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(away.isSelected()){
					Login.saveInProp(Global.username,Global.AWAY, Global.ON);
					Login.saveInProp(Global.username,Global.BUSY, Global.OFF);
					Login.saveInProp(Global.username,Global.ONLINE, Global.OFF);
				}
				
			}
		});
		
		busy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(busy.isSelected()){
					Login.saveInProp(Global.username,Global.BUSY, Global.ON);
					Login.saveInProp(Global.username,Global.ONLINE, Global.OFF);
					Login.saveInProp(Global.username,Global.AWAY, Global.OFF);
				}
				
			}
		});
		
		//Font Size setting
		textField_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (textField_1.getText().matches("[0-9]+")){
					textPane.setFont(new Font("Franklin Gothic Medium", Font.PLAIN, Integer.valueOf(textField_1.getText())));
				}
			}
		});
		

	}
	
	/**
	 * Checks(selects) the buttons that are set in the preference file
	 */
	private void checkButtons(){
		Properties prop = new Properties();
		String compare = Global.ON;
		String s = Global.username;
	
		try {
			prop.load(new FileInputStream(Global.username +".properties"));
		} catch (Exception e) {
			System.out.println("file load problem.");
		}
		
			
			if(prop.getProperty(Global.NOTIFICATION).equals(Global.ON)){
				notificationON.setSelected(true);
			}
			else if(prop.getProperty(Global.NOTIFICATION).equals(Global.OFF)){
				notificationOFF.setSelected(true);
			}
			
			if(prop.getProperty(Global.ONLINE).equals(Global.ON)){
				online.setSelected(true);
			}
			else if(prop.getProperty(Global.AWAY).equals(Global.ON)){
				away.setSelected(true);
			}
			else if(prop.getProperty(Global.BUSY).equals(Global.ON)){
				busy.setSelected(true);
			}
		}
	
	
	protected void updateConv(TextMessage text) throws BadLocationException{
		boolean check = false;
		
		if(!text.getContent().trim().isEmpty() && text.getSender().getPhoneNumber().equalsIgnoreCase(me.getPhoneNumber())){
			textPane.getDocument().insertString(offset, text.getSender().getFirstName() + ":\t" + text.getContent() + "\n\n", null);
			offset += (text.getSender().getFirstName() + ":\t" + text.getContent() + "\n\n").length();
			textCount++;
			myTextHistory.add(text);
			you.setSecondPhone("");
			text.setReceiver(you);
			
			if(!TaskBar.doNotSend){
				TaskBar.outGoingInSmall.add(text);
			}
			
			if(TaskBar.outGoingInSmall.size() != 0 && TextMessageManager.sendTexts){
				for(int i = 0; i < Conversation.currentConvs.size();i++){
					if(TaskBar.outGoingInSmall.get(0).getReceiver().getPhoneNumber().equalsIgnoreCase(Conversation.currentConvs.get(i).getPhoneNumber())){
						TaskBar.doNotSend = true;
						Conversation.updateConv(text);
						TaskBar.outGoingInSmall.remove(0);
						TaskBar.doNotSend = false;
						check = true;
						break;
					}
				}
				if(check == false && TextMessageManager.sendTexts){
					TaskBar.doNotSend = true;
					
					JTextPane textPane = new JTextPane();
					DefaultCaret caret = (DefaultCaret)textPane.getCaret();
					caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
					textPane.setFont(new Font("Franklin Gothic Medium", Font.PLAIN, 12));
					textPane.setEditable(false);
					Conversation.textPanes.add(textPane);
					JScrollPane scroll = new JScrollPane(textPane);
					Global.conversationPane.addTab(text.getReceiver().getFirstName() + " " + text.getReceiver().getLastName(), null, scroll, null);
					Global.conversationPane.setSelectedIndex(Global.conversationPane.getTabCount()-1);
					Conversation.offset.add(new Integer(0));
					Conversation.currentConvs.add(text.getReceiver());
					
					Conversation.updateConv(text);
					TaskBar.outGoingInSmall.remove(0);
					TaskBar.doNotSend = false;
				}
			}

			if(!TaskBar.doNotSend && TextMessageManager.sendTexts){
				TaskBar.messageHost.sendObject(text);
			}

		}
		
			
		if(!text.getContent().trim().isEmpty() && text.getSender().getPhoneNumber().equals(you.getPhoneNumber())){
			textPane.getDocument().insertString(offset, text.getSender().getFirstName() + ":\t" + text.getContent() + "\n\n", null);
			offset += (text.getSender().getFirstName() + ":\t" + text.getContent() + "\n\n").length();
		}
	}

	public Contact getLocalContact()
	{
		return me;
	}
	
	public Contact getFromContact(){
		return you;
	}
	
	public void receivedText(TextMessage text) throws BadLocationException{
		updateConv(text);
	}

	public int getMyTextCount(){
		return textCount;
	}
	
	public ArrayList<TextMessage> getMyHistory(){
		return myTextHistory;
	}
	
	public int getChatNumber(){
		return winNum;
	}
	
	public JFrame getFrmBluetext() {
		return frmBluetext;
	}
	
	
	
	
}
