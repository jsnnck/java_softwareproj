package osi.layer.application;

import java.util.Arrays;

import edu.fra.uas.oop.Terminal;
import osi.layer.ConnectionlessServicePrimitives;
import osi.layer.Layer;
import osi.layer.transport.Transport;

public class DataTransfer extends Layer implements ConnectionlessServicePrimitives {

	private byte[] recvData;

	final static byte[] DATACOMPLETED = { 1 };
	final static int SEQUENCEBYTES = 1;
	final static int MAXFRAGMENTSIZE = 1464;
	final static int SDUOFFSET = 1;

	/**
	 * This method constructs an instance of the layer.<br>
	 * It sets this layers lower layer and defines this layer as an upper layer to
	 * the assigned lower layer
	 * 
	 * @param transportLayer is an access pointer to the lower layer
	 */
	public DataTransfer(Layer transportLayer) {
		super(transportLayer);
	}

	/**
	 * TODO
	 */
	@Override
	public String req(byte[]... params) {
		byte[] seqNo = params[0];
		byte[] sdu = params[1];
		int dataSize = sdu.length;
		byte[] transportDestPort = params[2];
		byte[] networkDestAddr = params[3];
		byte[] dataLinkDestAddr = params[4];
		byte[] pdu = new byte[dataSize + SEQUENCEBYTES];
		System.arraycopy(seqNo, 0, pdu, 0, SEQUENCEBYTES);
		System.arraycopy(sdu, 0, pdu, SEQUENCEBYTES, dataSize);
		return ((Transport) this.lowerLayer).req(transportDestPort, pdu, networkDestAddr, dataLinkDestAddr);
	}

	/**
	 * TODO
	 */
	@Override
	public byte[] ind(byte[] serviceDataUnit) {
		byte[] storeData = null;
		int dataSize = serviceDataUnit.length - SDUOFFSET;
		if (recvData != null) {
			int recvSize = recvData.length;
			// if data is stored received data
			storeData = new byte[dataSize + recvSize];
			// add new received data to temporary variable
			System.arraycopy(serviceDataUnit, SDUOFFSET, storeData, 0, dataSize);
			// store earlier received data in temporary variable
			System.arraycopy(recvData, 0, storeData, dataSize, recvSize);
			if (serviceDataUnit[0]==0) {
				for (int i = 0; i < storeData.length; i++) {
					//Terminal.printLine(i + ":  " + storeData[i]);
				}
			}
		} else {
			// if no data is received yet
			storeData = new byte[dataSize];
			// store new received data in temporary variable
			System.arraycopy(serviceDataUnit, SDUOFFSET, storeData, 0, dataSize);
		}
		// store received data in a private variable
		recvData = storeData;
		// check return conditions
		if (serviceDataUnit[0] == 0) {
			//for (int i = 0; i < recvData.length; i++) {
			//	Terminal.printLine(recvData[i]);
			//}
			return DATACOMPLETED;
		} else {
			return null;
		}
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public byte[] getRecvData() {
		return this.recvData;
	}

	/**
	 * TODO
	 */
	public void resetRecvData() {
		recvData = null;
	}

	/**
	 * TODO
	 * 
	 * @param data
	 * @param transportDestPort
	 * @param networkDestAddress
	 * @param dataLinkDestAddress
	 * @return
	 */
	public String sendData(byte[] data, byte[] transportDestPort, byte[] networkDestAddress,
			byte[] dataLinkDestAddress) {
		String fullLinecode = "";
		String singleLinecode = "";
		int dataSize = data.length;
		if (dataSize > MAXFRAGMENTSIZE) {
			int seqCount = (dataSize / MAXFRAGMENTSIZE);
			int remainingBytes = dataSize % MAXFRAGMENTSIZE;
			for (int i = seqCount; i >= 0; i--) {
				if (i > 0) {
					byte[] sdu = Arrays.copyOfRange(data, (i - 1) * MAXFRAGMENTSIZE + remainingBytes,
							i * MAXFRAGMENTSIZE + remainingBytes);
					byte[] seqNo = { (byte) i };
					singleLinecode = this.req(seqNo, sdu, transportDestPort, networkDestAddress, dataLinkDestAddress);
					fullLinecode = fullLinecode + singleLinecode + "\n";
				} else {
					byte[] sdu = Arrays.copyOfRange(data, 0, remainingBytes);
					byte[] seqNo = { (byte) i };
					singleLinecode = this.req(seqNo, sdu, transportDestPort, networkDestAddress, dataLinkDestAddress);
					fullLinecode = fullLinecode + singleLinecode;
				}
			}
		} else {
			byte[] sdu = data;
			byte[] seqNo = { 0 };
			fullLinecode = this.req(seqNo, sdu, transportDestPort, networkDestAddress, dataLinkDestAddress);
		}
		return fullLinecode;
	}
}
