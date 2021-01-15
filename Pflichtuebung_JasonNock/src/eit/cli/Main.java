package eit.cli;

import java.util.HashMap;
import java.util.Set;

import edu.fra.uas.oop.Terminal;
import osi.layer.CreateInstances;
import osi.layer.application.DataTransfer;
import osi.layer.physical.PHY;

/**
 * <p>
 * The Main class defines the programs users interface. <br>
 * In here the user can control the program.
 * 
 * @author Jason Nock
 * @version 2.7
 */
public class Main {

	/**
	 * reads and writes data.<br>
	 * This Method is the programs Main method. In here the program is controlled.
	 * You can put in commands and data into the console and read its outputs.
	 * 
	 * @param args contain the command line arguments
	 */
	public static void main(String[] args) {
		
		CreateInstances creator = new CreateInstances();
		DataTransfer senderInstance = null;
		HashMap<PHY, String> receiverAccess = null;
		String[] input;
		String[] dataContent;
		boolean prgRun = true;
		while (prgRun == true) { // end program with quit
			input = Terminal.readLine().split(" ", 2); // split input into command and data content
			if (input[0].equals("sender")) { // check if sender is commanded
				dataContent = input[1].split(" ", 3);
				if (dataContent.length == 3) {
					// create an access instance of the sender
					senderInstance = creator.createSenderInstance(dataContent);
				} else {
					Terminal.printError("unknown command");
				}
			} else if (input[0].equals("receiver")) { // check if receiver is commanded
				if (input[1].split(" ").length >= 4) {
					dataContent = creator.connectData(input[1].split(" "));
					// adds an receiver name to a HashMap with the receiver
					receiverAccess = creator.createReceiverInstance(dataContent);
				} else {
					Terminal.printError("unknown command");
				}
			} else if (input[0].equals("send")) {
				if (input[1].split(" ").length >= 4) {
					dataContent = creator.connectData(input[1].split(" "));
					// ensures that a sender and receiver instance exists to prevent null pointer
					if (senderInstance != null && receiverAccess != null) {
						// transmitting the data
						String receiverName = "";
						String outputText = "";
						String transmitData = senderInstance.req(dataContent[1].getBytes(), dataContent[0].getBytes(),
								dataContent[2].getBytes(), dataContent[3].getBytes());
						// creating a set of all PHY access pointers
						Set<PHY> receiverInstances = receiverAccess.keySet();
						// going through each receiver instance
						for (PHY instance : receiverInstances) {
							instance.receive(transmitData);
							// if the decoded Package is not equal to null the value is valid
							if (instance.getDecodedPackage() != null) {
								// assign receiver name and transmitted text
								outputText = new String(instance.getDecodedPackage());
								receiverName = receiverAccess.get(instance);
								// print the successfully received message
								Terminal.printLine(receiverName + ": " + outputText);
							}
						}
					}
				} else {
					Terminal.printError("unknown command");
				}
			} else if (input[0].equals("quit")) { // check if end of program is commanded
				prgRun = false; // end program
			} else {
				Terminal.printError("unknown command");
			}
		}

	}

}
