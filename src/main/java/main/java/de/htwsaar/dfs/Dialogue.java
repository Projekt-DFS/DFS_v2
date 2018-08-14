package main.java.de.htwsaar.dfs;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.IOException;


/**
 * Main Class
 * Starts the user dialogue to manage the CAN
 * @author Phillip Persch *
 */
public class Dialogue {
	
	private Scanner input = new Scanner(System.in);
	private boolean bootstrapExists = false;
	private boolean peerExists = false;
	
     // Main menue
    private static final int I_AM_A_BOOTSTRAP     = 1;
    private static final int I_AM_A_PEER	      = 2;
    private static final int END                  = 0;
    
     // Submenue 1 (i_am_a_bootstrap)
    private static final int START_NEW_CAN_AS_BOOTSTRAP     = 1;
    private static final int STOP_CAN_AND_BOOTSTRAP	 	    = 2;
    private static final int ADD_USER           			= 3;
    private static final int REMOVE_USER	    			= 4;
    private static final int REMOVE_ALL_USERS				= 5;
    
    // Submenue 2 (i_am_a_peer)
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
    
    // Read input from main menue
    private int readFunctionMainMenue() {
        int readInput;
         System.out.print(I_AM_A_BOOTSTRAP   	 + ": I'm the bootstrap\n" +
            I_AM_A_PEER		                 	 + ": I'm a peer\n" +            
            END                           		 + ": END.\n" 
            + "\nYour input: ");
         readInput = input.nextInt();
        
        if (readInput < 0 || readInput > 2)
        	System.out.println("Bad input, try again");
        return readInput;
    }
    
    // Read input from bootstrap submenue
    private int readFunctionBootstrapMenue() {
    	
        int readInput;
        
        System.out.print(START_NEW_CAN_AS_BOOTSTRAP    	+ ": Start new CAN as bootstrap\n" +
            STOP_CAN_AND_BOOTSTRAP                     	+ ": Stop CAN and bootstrap\n" +
            ADD_USER                        			+ ": Add user\n" +
            REMOVE_USER                  				+ ": Remove user\n" +
            REMOVE_ALL_USERS							+ ": Remove all users\n" +
            END                           				+ ": END\n" +
            											 "\nYour input: ");
         readInput = input.nextInt();
        
        if (readInput < 0 || readInput > 5)
        	System.out.println("Bad input, try again");
        return readInput;
    }
    
    // Read input from peer submenue
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
    
    // Execute option in main menue
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
            
            case END:
            	System.out.println("\nStopped program");
            	System.exit(0);
         }
     }    
 
    // Execute option in bootstrap submenue
    private void executeFunctionBootstrapMenue(int func) throws IOException {
     	String username;
        switch (func) {
            case START_NEW_CAN_AS_BOOTSTRAP:
	            Thread bootstrapThread = new BootstrapThread();
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
                 
            case END:
            	System.out.println("Stopped all servers and dialogue.");
	            System.exit(0);
         }
    }    
    
    // Execute option in peer submenue
    private void executeFunctionPeerMenue(int func) {
         switch (func) {            
            
            case JOIN_CAN_AS_PEER:
 					System.out.print("Enter the bootstrap's IP -> ");
					String ip = input.next();
					
					if (ip.length() < 10) {
						System.out.println("The entered IP is too short.\n");
						break;
					}
					if (!ip.contains(".")) {
						System.out.println("The  IP must contain dots.\n");
						break;
					}				
											
					StartPeer.bootstrapIP = ip;
					Thread peerThread = new PeerThread();
		            peerThread.start(); // <-- hier wird der Peer tatsächlich gestartet.
		            peerExists = true;
		            break;
            
            case LEAVE_CAN_AS_PEER:
            	if (peerExists) {
            		System.out.print("Stopped peer and dialogue.");
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
 * @author Phillip Persch *
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
 * @author Phillip Persch *
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