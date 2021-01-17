package osi.layer.transport;

import java.math.BigInteger;
import java.util.Arrays;

import osi.exception.TransportException;
import osi.layer.ConnectionlessServicePrimitives;
import osi.layer.Layer;
import osi.layer.application.DataTransfer;
import osi.layer.network.Network;

/**
 * First Layer of the given OSI model.<br>
 * This layer can provide or request services from its lower and upper layers.
 * This is used to send and receive data. Each instance of this layer stores
 * unique access information. This layer is essential for a secure data
 * transmission. This layer is essential for end-to-end connection.
 * 
 * @author Jason Nock
 * @version 2.7
 */
public class Transport extends Layer implements ConnectionlessServicePrimitives {

	private byte[] sourcePort;

	final static int PORTBYTES = 2;
	final static int LENGTHBYTES = 2;
	final static int PCIBYTES = 6;
	final static byte[] TRANSPORTPROTOCOL = { 17 };
	final static int MAXDATASIZE = 1472;

	/**
	 * This method constructs an instance of the layer.<br>
	 * The access pointer of the lower layer is given into the superclass
	 * constructor, which creates the construct of lower and upper class. It checks
	 * if the given access information has a valid length. If yes it stores into a
	 * local variable. If not it throws an exception.
	 * 
	 * @param sourcePort   contains sender and receiver access information
	 * @param networkLayer is an access pointer to the lower layer
	 */
	public Transport(byte[] sourcePort, Layer networkLayer) {
		super(networkLayer);
		if (sourcePort.length == PORTBYTES) {
			this.sourcePort = sourcePort;
		} else {
			throw new TransportException("Wrong size for port!!!");
		}
	}

	/**
	 * requests services from its lower layer and hands it its processed data.<br>
	 * This method ensures that the assigned lower layer is an instance of Network,
	 * if not it throws an exception. It sets the static value for this layers
	 * unique transport protocol. It checks that the given destination port and
	 * service data unit has a valid length, if not it throws an exception.
	 * Afterwards it turns the size of the service data unit into a byte value. It
	 * creates a new service data unit and hands it to the lower lower with the
	 * given data frame containing this layers protocol control information
	 * concatenated to the service data unit.
	 * 
	 * @param destinationPort     contains the users input for the receivers port
	 *                            with size of 2 bytes. It gets processed into the
	 *                            layer PCI.
	 * @param serviceDataUnit     contains the user input datagram with a size of 0
	 *                            - 1472 Bytes.
	 * @param networkDestination  contains the network destination address with size
	 *                            of 4 bytes. This data gets processed in a lower
	 *                            layer
	 * @param dataLinkDestination contains the data link destination address with
	 *                            size of 6 bytes. This data gets processed in a
	 *                            lower layer
	 * @return <code>String</code> of encoded data to users interface.
	 */
	@Override
	public String req(byte[]... params) throws TransportException {
		byte[] destinationPort = params[0];
		byte[] serviceDataUnit = params[1];
		byte[] networkDestination = params[2];
		byte[] dataLinkDestination = params[3];
		String encodedPackage = "";
		int dataSize = serviceDataUnit.length;
		byte[] pdu = new byte[PCIBYTES + dataSize];
		if (this.lowerLayer instanceof Network) {
			// set static value for transport protocol
			byte[] transportProtocol = TRANSPORTPROTOCOL;
			if (serviceDataUnit.length <= MAXDATASIZE) {
				if (destinationPort.length == PORTBYTES) {
					byte[] sduLength = BigInteger.valueOf(dataSize).toByteArray();
					if (sduLength.length < LENGTHBYTES) {
						// if the data is smaller than 1 byte we have to swap the bytes spots within
						// the array
						sduLength = Arrays.copyOf(sduLength, LENGTHBYTES);
						byte storedValue = sduLength[0];
						sduLength[0] = sduLength[1];
						sduLength[1] = storedValue;
					}
					// append the given data to the required data frame
					System.arraycopy(sourcePort, 0, pdu, 0, PORTBYTES);
					System.arraycopy(destinationPort, 0, pdu, PORTBYTES, PORTBYTES);
					System.arraycopy(sduLength, 0, pdu, 2 * PORTBYTES, LENGTHBYTES);
					System.arraycopy(serviceDataUnit, 0, pdu, PCIBYTES, dataSize);
					// pass the processed data to the lower layer, which provides services for this
					// layer and returns the data
					encodedPackage = ((Network) this.lowerLayer).req(networkDestination, transportProtocol, pdu,
							dataLinkDestination);
				} else {
					throw new TransportException("Wrong size for port!!!");
				}
			} else {
				throw new TransportException("Wrong size for service data unit!!!");
			}
		}
		return encodedPackage;
	}

	/**
	 * provides services for the lower layer and passes it the encoded data to be
	 * decoded.<br>
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
		// copy the destination port into byte array
		byte[] destinationPort = Arrays.copyOfRange(serviceDataUnit, PORTBYTES, 2 * PORTBYTES);
		int dataSize = serviceDataUnit.length;
		byte[] sdu = new byte[dataSize - PCIBYTES];
		// compare if the source port matches the destination port
		if (Arrays.equals(sourcePort, destinationPort)) {
			// cuts this layers PCI
			sdu = Arrays.copyOfRange(serviceDataUnit, PCIBYTES, dataSize);
			// check if upper Layer exists
			if (this.upperLayer != null) {
				// pass the processed data to the upper layer
				return ((DataTransfer) this.upperLayer).ind(sdu);
			} else {
				return sdu;
			}
		} else {
			// returning null means the package is not intended for this receiver
			return null;
		}
	}
}
