package main.java.de.htwsaar.dfs;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import main.java.de.htwsaar.dfs.utils.StaticFunctions;

import java.io.IOException;


/**
 * Main Class
 * Starts the user dialogue to manage the CAN
 */
public class Dialogue {
	
	private Scanner input = new Scanner(System.in);
	public static String ip = "";
	private boolean bootstrapExists = false;
	private boolean peerExists = false;
	
     // Main menu
    private static final int I_AM_A_BOOTSTRAP     = 1;
    private static final int I_AM_A_PEER	      = 2;
    private static final int PRINT_CAN			  = 3;
    private static final int END                  = 0;
    
     // Submenu 1 (i_am_a_bootstrap)
    private static final int START_NEW_CAN_AS_BOOTSTRAP     = 1;
    private static final int STOP_CAN_AND_BOOTSTRAP	 	    = 2;
    private static final int ADD_USER           			= 3;
    private static final int REMOVE_USER	    			= 4;
    private static final int REMOVE_ALL_USERS				= 5;
    private static final int LIST_ALL_IMAGES				= 6;
    private static final int LIST_USER_IMAGES				= 7;
    
    
    // Submenu 2 (i_am_a_peer)
    private static final int JOIN_CAN_AS_PEER  				= 1;
    private static final int LEAVE_CAN_AS_PEER          	= 2;
    
    
     public void start() {
        int func = -1;
        
        while (func != END){
            try {
                func = readFunctionMainMenue();
                executeFunctionMainMenue(func);
             } catch (InputMismatchException e) {
                input.nextLine();
                System.out.println("\nBad input\n");
             } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
            
    }
     
    
    
     
 //-----------------------------Take user input---------------------------------------
    
    // Read input from main menu
    private int readFunctionMainMenue() {
        int readInput;
         System.out.print(I_AM_A_BOOTSTRAP   	 + ": I'm the bootstrap\n" +
            I_AM_A_PEER		                 	 + ": I'm a peer\n" +  
            PRINT_CAN							 + ": Print existing CAN\n" +
            END                           		 + ": END.\n" 
            + "\nYour input: ");
         readInput = input.nextInt();
        
        if (readInput < 0 || readInput > 3)
        	System.out.println("Bad input, try again");
        return readInput;
    }
    
    // Read input from bootstrap submenu
    private int readFunctionBootstrapMenue() {
    	
        int readInput;
        
        System.out.print(START_NEW_CAN_AS_BOOTSTRAP    	+ ": Start new CAN as bootstrap\n" +
            STOP_CAN_AND_BOOTSTRAP                     	+ ": Stop CAN and bootstrap\n" +
            ADD_USER                        			+ ": Add user\n" +
            REMOVE_USER                  				+ ": Remove user\n" +
            REMOVE_ALL_USERS							+ ": Remove all users\n" +
            LIST_ALL_IMAGES								+ ": Print all image names\n" + 
            LIST_USER_IMAGES							+ ": Print image names of a user\n" +
            END                           				+ ": END\n" +
            											 "\nYour input: ");
         readInput = input.nextInt();
        
        if (readInput < 0 || readInput > 7)
        	System.out.println("Bad input, try again");
        return readInput;
    }
    
    // Read input from peer submenu
    private int readFunctionPeerMenue() {
        int readInput;
         System.out.print(
            JOIN_CAN_AS_PEER                        	+ ": Join the existing CAN\n" +
            LEAVE_CAN_AS_PEER                  			+ ": Leave current CAN\n" +            
            END                           				+ ": END\n" 
            											+ "\nYour input: ");
         readInput = input.nextInt();
        
        if (readInput < 0 || readInput > 2)
        	System.out.println("Bad input, try again");
        return readInput;
    }
    
    
    
    
    
    
    
//-----------------------------Execute the selected option---------------------------------------
    
    // Execute option in main menu
    private void executeFunctionMainMenue(int func) throws IOException {
     	int submenue;
    	
        switch (func) {
            case I_AM_A_BOOTSTRAP:
            	if (peerExists) {
            		System.out.println("No, you're a peer already.\n");
            		break;
            	}
            	submenue  = readFunctionBootstrapMenue();
            	executeFunctionBootstrapMenue(submenue);
            	break;
             
            case I_AM_A_PEER:
            	if (bootstrapExists) {
            		System.out.println("No, you're a bootstrap already.\n");
            		break;
            	}
            	submenue = readFunctionPeerMenue();
            	executeFunctionPeerMenue(submenue);
            	break;
            	
            case PRINT_CAN:
            	if (bootstrapExists) {
            		System.out.println("Bootstrap: " + StartBootstrap.bootstrap + "\n");
            	} else if (peerExists) {
            		System.out.println("Peer: " + StartPeer.peer + "\n");
            	} else {
            		System.out.println("\nNo CAN found. You are neither a bootstrap nor a peer.\n");
            	}            		
            	break;
            
            case END:
            	System.out.println("\nStopped program");
            	System.exit(0);
         }
     }    
 
    // Execute option in bootstrap submenu
    private void executeFunctionBootstrapMenue(int func) throws IOException {
     	String username;
        switch (func) {
            case START_NEW_CAN_AS_BOOTSTRAP:
	            Thread bootstrapThread = new BootstrapThread();
	            System.out.print("Select IP manually? (y/n) -> ");
	            String selectIP = input.next();
	            
	            // manual selection of IP
	            if (selectIP.equals("y")) {
	            	ArrayList<String> ips = StaticFunctions.getAllIPs();
	            	for (int i = 0; i < ips.size(); i++) {
	            		System.out.println(i + ":\t" + ips.get(i));
	            	}
	            	System.out.print("Select IP by index -> ");
	            	int index = input.nextInt();
	            	if (index >= ips.size() || index < 0) {
	            		System.out.println("Invalid index.\n");
	            		break;
	            	}
	            	ip = ips.get(index);
	            	
	            }
	            
	            // automatic selection of IP
	            else {
	            	ip = StaticFunctions.getRightIP();
	            }
	            StaticFunctions.saveIps(ip, ip);
	            
	            bootstrapThread.start(); // <-- hier wird der Bootstrap tatsächlich gestartet.
	            bootstrapExists = true;
	            break;
             
            case STOP_CAN_AND_BOOTSTRAP:
            	if (bootstrapExists) {
            		System.out.println("\nStopped bootstrap and dialogue.");
            		System.exit(0);
            	}
            	else {
            		System.out.println("\nBootstrap was never started. Choose another operation.\n");
    	            break;
            	}
	           	            
            case ADD_USER:
            	if (!bootstrapExists) {
            		printServiceNotStarted("bootstrap");
            		break;
            	}
            	System.out.print("Enter username -> ");
            	username = input.next();
            	System.out.print("Enter password -> ");
            	String password = input.next();
            	StartBootstrap.bootstrap.createUser(username, password);
            	System.out.println("\n User " + username + " created.");
            	break;
            
            case REMOVE_USER:
            	if (!bootstrapExists) {
            		printServiceNotStarted("bootstrap");
            		break;
            	}
            	System.out.print("\nEnter username -> ");
            	username = input.next();
            	System.out.println(StartBootstrap.bootstrap.deleteUser(username));            	
	            break;
	            
            case REMOVE_ALL_USERS:
            	if (!bootstrapExists) {
            		printServiceNotStarted("bootstrap");
            		break;
            	}
            	StartBootstrap.bootstrap.dumpUsers();
            	System.out.println("\nDeleted all users.");
            	break;
                 
            case LIST_ALL_IMAGES:
            	if (!bootstrapExists) {
            		printServiceNotStarted("bootstrap");
            		break;
            	}
            	System.out.println(StartBootstrap.bootstrap.listImageNames() + "\n");
            	break;
            	
            case LIST_USER_IMAGES:
            	if (!bootstrapExists) {
            		printServiceNotStarted("bootstrap");
            		break;
            	}
            	System.out.print("Enter user name -> ");
            	String user = input.next();
            	System.out.println(StartBootstrap.bootstrap.listImageNames(user) + "\n");
            	break;
            	
            case END:
            	System.out.println("Stopped all servers and dialogue.");
	            System.exit(0);
         }
    }    
    
    // Execute option in peer submenu
    private void executeFunctionPeerMenue(int func) {
         switch (func) {            
            
            case JOIN_CAN_AS_PEER:
 								
					
					System.out.print("Select IP manually? (y/n) -> ");
		            String selectIP = input.next();
		            
		            // manual selection of IP
		            if (selectIP.equals("y")) {
		            	ArrayList<String> ips = StaticFunctions.getAllIPs();
		            	for (int i = 0; i < ips.size(); i++) {
		            		System.out.println(i + ":\t" + ips.get(i));
		            	}
		            	System.out.print("Select IP by index -> ");
		            	int index = input.nextInt();
		            	if (index >= ips.size() || index < 0) {
		            		System.out.println("Invalid index.\n");
		            		break;
		            	}
		            	ip = ips.get(index);
		            	
		            }
		            
		            // automatic selection of IP
		            else {
		            	ip = StaticFunctions.getRightIP();
		            }
					
		            String[] tmpArray;
		            System.out.print("Enter the bootstrap's IP -> ");
					String bootstrapIp = input.next();
					tmpArray = bootstrapIp.split("[.]");
					
					if (tmpArray.length != 4) {
						System.out.println("The  IP must contain 3 dots.\n");
						break;
					}
					if (bootstrapIp.equals(ip)) {
						System.out.println("The bootstrap's IP must not be the same as the peer's IP.\n");
						break;
					}	
		            
					
					StaticFunctions.saveIps(ip, bootstrapIp);
					//StartPeer.bootstrapIP = ip;
					Thread peerThread = new PeerThread();
		            peerThread.start(); // <-- hier wird der Peer tatsächlich gestartet.
		            peerExists = true;
		            break;
            
            case LEAVE_CAN_AS_PEER:
            	if (peerExists) {
            		System.out.print("Stopped peer and dialogue.");
            		StartPeer.peer.leaveNetwork();
            		System.exit(0);
            	} else {
            		printServiceNotStarted("peer");
    	            break;
            	}
            		
            
            case END:            	
	            System.out.println("\nStopped all servers and dialogue.");
	            System.exit(0);
         }
    }    
    
    private void printServiceNotStarted(String type) {    	
    		System.out.println("\n" + type + " was never started. Choose another operation.\n");    	
    }
	
	
    /**
     * Main method.
     * @param args
     */    
	public static void main(String[] args) {
		new Dialogue().start();
	}
    
    
	
}

/** 
 * Own Thread class for bootstrap, to allow bootstrap and dialogue to run simultaneously.
 * Only starts a bootstrap.
 */ 
class BootstrapThread extends Thread {
	@Override
	public void run() {
		try {
			StartBootstrap.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
 
/** 
 * Own Thread class for peer, to allow peer and dialogue to run simultaneously.
 * Only starts a peer.
 */
class PeerThread extends Thread {
	@Override
	public void run() {
		try {
			StartPeer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
} 
