package osi.layer.datalink;

import java.util.Arrays;

import osi.exception.DataLinkException;
import osi.layer.ConnectionlessServicePrimitives;
import osi.layer.Layer;
import osi.layer.network.Network;
import osi.layer.physical.PHY;

/**
 * Third Layer of the given OSI model.<br>
 * This layer can provide or request services from its lower and upper layers.
 * This is used to send and receive data. Each instance of this layer stores
 * unique access information. This layer is essential for a secure data
 * transmission. This layer is essential for a secure data transmission
 * 
 * @author Jason Nock
 * @version 2.7
 */
public class DataLink extends Layer implements ConnectionlessServicePrimitives {

	private byte[] sourceAddress;
	
	final static int ADDRESSBYTES = 6;
	final static int DTBYTES = 2;
	final static int PCIBYTES = 14;
	final static int MAXDATASIZE = 1500;
	final static int MINDATASIZE = 46;

	/**
	 * This method constructs an instance of the layer.<br>
	 * The access pointer of the lower layer is given into the superclass
	 * constructor, which creates the construct of lower and upper class. It checks
	 * if the given access information has a valid length. If yes it stores into a
	 * local variable. If not it throws an exception.
	 * 
	 * @param sourceAddress contains sender and receiver access information
	 * @param physicalLayer is an access pointer to the lower layer
	 */
	public DataLink(byte[] sourceAddress, Layer physicalLayer) throws DataLinkException {
		super(physicalLayer);
		if (sourceAddress.length == ADDRESSBYTES) {
			this.sourceAddress = sourceAddress;
		} else {
			throw new DataLinkException("Wrong size for address!!!");
		}
	}
	
	/**
	 * requests services from its lower layer and hands it its processed data.<br>
	 * This method ensures that the assigned lower layer is an instance of PHY, if
	 * not it throws an exception. It checks that the given destination address, the
	 * upper layer protocol information and service data unit has a valid length, if
	 * not it throws an exception. If the service data unit is shorter than 46 bytes
	 * it gets filled up until it reached the required length. It creates a new
	 * service data unit and hands it to the lower lower with the given data frame
	 * containing this layers protocol control information concatenated to the
	 * service data unit.
	 * 
	 * @param destinationAddress contains the users input for the receivers address
	 *                           with size of 6 bytes. It gets processed into the
	 *                           layer PCI.
	 * @param dataType           contains static protocol information of the upper
	 *                           layer with size of 2 bytes
	 * @param serviceDataUnit    contains the upper layers datagram with a size of
	 *                           46 - 1500 Bytes.
	 * @return <code>String</code> of encoded data to upper layer.
	 */
	public String req(byte[]... params) throws DataLinkException {
		byte[] destinationAddress = params[0];
		byte[] dataType = params[1]; 
		byte[] serviceDataUnit = params[2];
		String encodedPackage = "";
		int dataSize = serviceDataUnit.length;
		if (this.lowerLayer instanceof PHY) {
			if (dataSize <= MAXDATASIZE) {
				if (destinationAddress.length == ADDRESSBYTES && dataType.length == DTBYTES) {
					byte[] sdu = serviceDataUnit;
					if (sdu.length < MINDATASIZE) {
						// elongate the sdu if the size is insufficient
						sdu = Arrays.copyOf(sdu, MINDATASIZE);
					}
					dataSize = sdu.length;
					byte[] pdu = new byte[PCIBYTES + dataSize];
					System.arraycopy(sourceAddress, 0, pdu, 0, ADDRESSBYTES);
					System.arraycopy(destinationAddress, 0, pdu, ADDRESSBYTES, ADDRESSBYTES);
					System.arraycopy(dataType, 0, pdu, 2 * ADDRESSBYTES, DTBYTES);
					System.arraycopy(sdu, 0, pdu, PCIBYTES, dataSize);
					// pass the processed data to the lower layer, which provides services for this
					// layer and returns the data
					encodedPackage = ((PHY) this.lowerLayer).transmit(pdu);
				} else {
					if (destinationAddress.length != ADDRESSBYTES) {
						throw new DataLinkException("Wrong size for address!!!");
					} else {
						throw new DataLinkException("Wrong size for transport protocol!!!");
					}
				}
			} else {
				throw new DataLinkException("Wrong size for service data unit!!!");
			}
		}
		return encodedPackage;
	}

	
	/**
	 * provides services for the lower layer and passes processed data to the upper
	 * layer<br>
	 * First it checks if the destination port matches this layers source port, if
	 * yes the PCI and the filler gets cut and if not it returns null, because the
	 * data is not intended for this receiver. If an upper layer exists it passes
	 * the processed data, if not it returns the processed data back to the lower
	 * layers.
	 * 
	 * @param serviceDataUnit contains the users input data with the header from all
	 *                        upper layers and a trailer if the data size is
	 *                        insufficient
	 * @return <code>byte[]</code> of users data input, if all circumstances are
	 *         true
	 */
	@Override
	public byte[] ind(byte[] serviceDataUnit) {
		int dataSize = serviceDataUnit.length;
		byte[] filledSDU = new byte[dataSize - PCIBYTES];
		// copies the destination address
		byte[] destinationAddress = Arrays.copyOfRange(serviceDataUnit, ADDRESSBYTES, 2 * ADDRESSBYTES);
		int cutIndex = 0;
		// compares if the source port matches the destination port
		if (Arrays.equals(destinationAddress, sourceAddress)) {
			// cut this layer PCI
			filledSDU = Arrays.copyOfRange(serviceDataUnit, PCIBYTES, dataSize);
			// determines SDU filler
			for (int i = filledSDU.length; i > 0; i--) {
				if (filledSDU[i - 1] != 0) {
					cutIndex = i;
					break;
				}
			}
			// cuts the SDU filler
			byte[] networkSDU = Arrays.copyOfRange(filledSDU, 0, cutIndex);
			if (this.upperLayer == null) {
				return networkSDU;
			} else {
				return ((Network) this.upperLayer).ind(networkSDU);
			}
		} else {
			// returning null means the package is not intended for this receiver
			return null;
		}
	}
}
