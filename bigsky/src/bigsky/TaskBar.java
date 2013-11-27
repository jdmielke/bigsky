package bigsky;

import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.*;
import bigsky.gui.*;
import bigsky.messaging.MessageHost;
import bigsky.messaging.TextMessageManager;

public class TaskBar
{
	public static Queue<TextMessage> myTextQueue = new Queue<TextMessage>();
	public static ArrayList<TextMessage> myTextArray = new ArrayList<TextMessage>();
	public static TrayIcon notification = new TrayIcon(new ImageIcon(TaskBar.class.getResource("BlueText.gif"), "tray icon").getImage());
	public static ArrayList<SmallChat> smallChatWindows = new ArrayList<SmallChat>();
	public static Contact me = new Contact("me", "me","me","");
	public static Contact you = new Contact("Andy", "G",    "+1 5072542815", null);
	public static final TrayIcon trayIcon = createTrayIconImage();
	private static final SystemTray tray = SystemTray.getSystemTray();
	public static MessageHost messageHost = null;
	public static ConcurrentLinkedQueue<Contact> incomingContactQueue = new ConcurrentLinkedQueue<Contact>(); 
	public static TextMessageManager textManager = null;
	public static Conversation convo;
	public static ArrayList<TextMessage> outGoingInConv = new ArrayList<TextMessage>();
	public static ArrayList<TextMessage> outGoingInSmall = new ArrayList<TextMessage>();
	public static boolean doNotSend = false;

    public static void main(String[] args) {
        try {
        	UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        UIManager.put("swing.boldMetal", Boolean.FALSE);
                
        // Checks to see if the user setting is to save username and password
        if(savedInfo(Global.save, Global.ON)){
        	TaskBar.putIconInSystemTray();
        	try {
				automaticIP();
			} catch (Exception e) {}
			if(messageHost==null){
	   	   		messageHost = new MessageHost();
	   	   		messageHost.start();
	   	   		textManager = new TextMessageManager();
	   	   		textManager.start();
	        }
        }

        else{
        	Login login = new Login();
           	login.setVisible(true);
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	initialize(); 
            }
        });
    }

    private static void initialize() {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }

        final PopupMenu menu = new PopupMenu();

        //shows full image in taskbar
        trayIcon.setImageAutoSize(true);

        //  menu items
        MenuItem conversation = new MenuItem("Open BlueText");
        MenuItem smallChat = new MenuItem("Side Chat");
        MenuItem logout = new MenuItem("Log out");
        MenuItem exitItem = new MenuItem("Exit");

        //Adding  menu items
        menu.add(conversation);
        menu.add(smallChat);
        menu.add(logout);
        menu.add(exitItem);
        trayIcon.setPopupMenu(menu);

        conversation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	convo.getFrmBluetext().setVisible(true);
            }
        });

        smallChat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	System.out.println("small chat windows: " +  smallChatWindows.size());
            	for(int i = 0; i < smallChatWindows.size(); i++){
            		smallChatWindows.get(i).getFrmBluetext().setVisible(true);
            	}
            }
        });
        
        logout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
					logout();
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0);
            }
        });
    }
    /**
     * Puts the tray icon into the system tray
     */
    public static void putIconInSystemTray(){
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
    }
    /**
     * Obtain system tray icon
     * @return tray icon
     */
    protected static TrayIcon createTrayIconImage() {
       TrayIcon tray;
   	   URL imageURL = TaskBar.class.getResource("BlueText.gif");
       Image icon = new ImageIcon(imageURL, "tray icon").getImage();

        if (imageURL == null) {
            System.err.println("Resource not found: " + "BlueText.gif");
            return null;
        } else {
        	tray = new TrayIcon(icon);
            return tray;
        }
    }
    /**
     * Determines if the user has told the system to save his/her information
     * @return true if user told system to save his/her information
     */
    public static boolean savedInfo(String property, String compare ){

		Properties prop = new Properties();

		try {
			prop.load(new FileInputStream(lastLoggedIn() +".properties"));
		} catch (Exception e) {
			System.out.println("No previous login file located.");
		}

		if(compare.equals(prop.getProperty(property))){
			Global.username = lastLoggedIn();
			return true;
		}

		return false;
	}
    /**
     * Method to load system properties to see which user was logged in last
     * @return String lastloggedin
     */
    public static String lastLoggedIn(){
		
		Properties prop = new Properties();

		try {
			prop.load(new FileInputStream("system.properties"));
		} catch (Exception e) {
			System.out.println("No previous system properties located, using defaults.");
		}
		
		return (String) prop.get("lastLoggedIn");
	}
    /**
     * Logout of current session.Closes connection between phone and computer
     * Brings to login screen
     * @throws Exception
     */
    public static void logout(){
    	
    	Frame j = new Frame();
    	@SuppressWarnings("static-access")
    	Frame[] frames = j.getFrames();
    	System.out.println(frames.length);
    	for(int i = 0; i < frames.length; i ++){
    		frames[i].dispose();
    	}
    	
    	tray.remove(trayIcon);
    	messageHost.closeHost();
    	messageHost = null;
    	reLogin();
    	Properties prop = new Properties();

		try {
			prop.load(new FileInputStream(lastLoggedIn() +".properties"));
		} catch (Exception e) {
			System.out.println("loading file problem.");
		}
		
		prop.setProperty("save", Global.OFF);
	
		try {
			prop.store(new FileOutputStream(lastLoggedIn() +".properties"),null);
			
		} catch (Exception e) {
			System.out.println("storing file problem");

		}
		
    }
    /**
     * After logout this is called for the relogin screen to load 
     */
    public static void reLogin(){
    	
		Login login = new Login();
       	login.setVisible(true);
    }
    /**
     * Puts IP address in sql database when user has automatic login
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnknownHostException
     */
    public static void automaticIP(){
    	try {
			Class.forName("com.mysql.jdbc.Driver");
	
		Connection con = DriverManager.getConnection("jdbc:mysql://mysql.cs.iastate.edu/db30901", "adm309", "EXbDqudt4");
		Statement stmt = con.createStatement();
		String iP =InetAddress.getLocalHost().getHostAddress();	
		stmt.executeUpdate("UPDATE testTable SET IP_Computer='" + iP + "' WHERE phoneNumber='" + lastLoggedIn() + "';");
    	} catch (Exception e) {
    		System.out.println("Automatic login fail\n" + e.getMessage());
    	}
		
    }
}

class Queue<T>{
	protected LinkedList<T> list;

	public Queue(){
		list = new LinkedList<T>();
	}

	public void add(T element){
		list.add(element);
	}

	public T removeFirst(){
		return list.removeFirst();
	}

	public boolean isEmpty(){
		if(list.isEmpty()){
			return true;
		}
		else
			return false;
	}
}
