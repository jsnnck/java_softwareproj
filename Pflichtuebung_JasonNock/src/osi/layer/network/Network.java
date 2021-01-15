package osi.layer.network;

import java.util.Arrays;

import osi.exception.NetworkException;
import osi.layer.ConnectionlessServicePrimitives;
import osi.layer.Layer;
import osi.layer.datalink.DataLink;
import osi.layer.transport.Transport;

/**
 * Second Layer of the given OSI model.<br>
 * This layer can provide or request services from its lower and upper layers.
 * This is used to send and receive data. Each instance of this layer stores
 * unique access information. This layer is essential for a secure data
 * transmission. This layer is essential for communication between two users.
 * 
 * @author Jason Nock
 * @version 2.7
 */
public class Network extends Layer implements ConnectionlessServicePrimitives {

	private byte[] sourceAddress;

	final static int ADDRESSBYTES = 4;
	final static int TPBYTES = 1;
	final static int PCIBYTES = 9;
	final static byte[] DATATYPE = {8, 0};
	final static int MAXDATASIZE = 1480;
	final static int MINDATASIZE = 8;

	/**
	 * This method constructs an instance of the layer.<br>
	 * The access pointer of the lower layer is given into the superclass
	 * constructor, which creates the construct of lower and upper class. It checks
	 * if the given access information has a valid length. If yes it stores into a
	 * local variable. If not it throws an exception.
	 * 
	 * @param sourceAddress contains sender and receiver access information
	 * @param dataLinkLayer is an access pointer to the lower layer
	 */
	public Network(byte[] sourceAddress, Layer dataLinkLayer) {
		super(dataLinkLayer);
		if (sourceAddress.length == ADDRESSBYTES) {
			this.sourceAddress = sourceAddress;
		} else {
			throw new NetworkException("Wrong size for address!!!");
		}
	}

	/**
	 * requests services from its lower layer and hands it its processed data.<br>
	 * This method ensures that the assigned lower layer is an instance of Data
	 * Link, if not it throws an exception. It sets the static value for this layers
	 * unique transport protocol. It checks that the given destination address, the
	 * upper layer protocol information and service data unit has a valid length, if
	 * not it throws an exception. It creates a new service data unit and hands it
	 * to the lower lower with the given data frame containing this layers protocol
	 * control information concatenated to the service data unit.
	 * 
	 * @param destinationAddress  contains the users input for the receivers address
	 *                            with size of 4 bytes. It gets processed into the
	 *                            layer PCI.
	 * @param transportProtocol   contains static protocol information of the upper
	 *                            layer with size of 1 bytes
	 * @param serviceDataUnit     contains the upper layers datagram with a size of
	 *                            8 - 1480 Bytes.
	 * @param dataLinkDestination contains the data link destination address with
	 *                            size of 6 bytes. This data gets processed in a
	 *                            lower layer
	 * @return <code>String</code> of encoded data to upper layer.
	 */
	public String req(byte[]... params) throws NetworkException {
		byte[] destinationAddress = params[0];
		byte[] transportProtocol = params[1];
		byte[] serviceDataUnit = params[2];
		byte[] dataLinkDestination = params[3];
		String encodedPackage = "";
		int dataSize = serviceDataUnit.length;
		byte[] pdu = new byte[PCIBYTES + dataSize];
		if (this.lowerLayer instanceof DataLink) {
			// set static value of data type
			// for data frame reasons we need to invert the bytes
			byte[] dataType = DATATYPE;
			if (dataSize <= MAXDATASIZE && dataSize >= MINDATASIZE) {
				if (destinationAddress.length == ADDRESSBYTES && transportProtocol.length == TPBYTES) {
					// append the given data to the required data frame
					System.arraycopy(sourceAddress, 0, pdu, 0, ADDRESSBYTES);
					System.arraycopy(destinationAddress, 0, pdu, ADDRESSBYTES, ADDRESSBYTES);
					System.arraycopy(transportProtocol, 0, pdu, 2 * ADDRESSBYTES, TPBYTES);
					System.arraycopy(serviceDataUnit, 0, pdu, PCIBYTES, dataSize);
					// pass the processed data to the lower layer, which provides services for this
					// layer and returns the data
					encodedPackage = ((DataLink) this.lowerLayer).req(dataLinkDestination, dataType, pdu);
				} else {
					if (destinationAddress.length != ADDRESSBYTES) {
						throw new NetworkException("Wrong size for address!!!");
					} else {
						throw new NetworkException("Wrong size for transport protocol!!!");
					}
				}
			} else {
				throw new NetworkException("Wrong size for service data unit!!!");
			}
		}
		return encodedPackage;
	}

	/**
	 * provides services for the lower layer and passes processed data to the upper
	 * layer<br>
	 * First it checks if the destination port matches this layers source port, if
	 * yes the PCI gets cut and if not it returns null, because the data is not
	 * intended for this receiver. If an upper layer exists it passes the processed
	 * data, if not it returns the processed data back to the lower layers.
	 * 
	 * @param serviceDataUnit contains the users input data with the header from all
	 *                        upper layers
	 * @return <code>byte[]</code> of users data input, if all circumstances are
	 *         true
	 */
	@Override
	public byte[] ind(byte[] serviceDataUnit) {
		int dataSize = serviceDataUnit.length;
		// copies the destination address
		byte[] transportSDU = new byte[dataSize - PCIBYTES];
		byte[] destinationAddress = Arrays.copyOfRange(serviceDataUnit, ADDRESSBYTES, 2 * ADDRESSBYTES);
		// compares if the source port matches the destination port
		if (Arrays.equals(destinationAddress, sourceAddress)) {
			// cuts this layers PCI
			transportSDU = Arrays.copyOfRange(serviceDataUnit, PCIBYTES, dataSize);
			if (this.upperLayer != null) {
				return ((Transport) this.upperLayer).ind(transportSDU);
			} else {
				return transportSDU;
			}
		} else {
			// returning null means the package is not intended for this receiver
			return null;
		}
	}

}
