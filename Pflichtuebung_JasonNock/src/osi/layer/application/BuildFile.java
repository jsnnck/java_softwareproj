package osi.layer.application;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import edu.fra.uas.oop.Terminal;
import osi.layer.physical.PHY;

public class BuildFile {

	private DataTransfer applicationInstance;
	private HashMap<PHY, String> receiverAccess;
	
	public BuildFile(HashMap<PHY, String> receiverAccess, DataTransfer applicationLayer) {
		this.receiverAccess = receiverAccess;
		this.applicationInstance = applicationLayer;
	}

	public HashMap<PHY, String> getReceiverAccess() {
		return receiverAccess;
	}

	public void createFile(String fileName) {
		try {
			FileOutputStream fileCreator = new FileOutputStream(fileName + ".gif");
			byte[] buffer = applicationInstance.getRecvData();
			fileCreator.write(buffer);
			fileCreator.close();
			Terminal.printLine(fileName + ": " + buffer.length + " bytes");
			applicationInstance.resetRecvData();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

