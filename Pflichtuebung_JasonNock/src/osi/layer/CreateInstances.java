package osi.layer;

import java.util.HashMap;

import osi.layer.application.BuildFile;
import osi.layer.application.DataTransfer;
import osi.layer.datalink.DataLink;
import osi.layer.physical.PHY;
import osi.layer.transport.Transport;
import osi.linecode.Codec;
import osi.linecode.Codec4B5B;
import osi.layer.network.Network;

/**
 * This class contains the essential method for creating senders and receivers
 * instances.<br>
 * It provides the methods to create sender instances, receiver instances, an
 * HashMap with the receiver names and connects the input data, if the input
 * data Array is greater than 4 elements.
 * 
 * @author Jason Nock
 * @version 2.7
 */
public class CreateInstances {

	// stores the receivers access instances and the associated names
	private HashMap<PHY, String> receiverAccess = new HashMap<PHY, String>();

	/**
	 * generates a senders instance with the Transport Layer as access point<br>
	 * Firstly it creates an instance of the codec that should be used. After you
	 * create an instance of lowest layer, pass on the previous created instance as
	 * lower layer and automatically set the upper Layer within the constructor. You
	 * do this for each layer creating required layer hierarchy. Afterwards you take
	 * the highest layer access instance.
	 * 
	 * @param sourceInfo contains the users input data
	 * @return access pointer to the senders instance
	 */
	public DataTransfer createSenderInstance(String[] sourceInfo) {
		// assign codec in which the data is going to be transmitted
		Codec codec = new Codec4B5B();
		PHY physicalLayer = new PHY(codec);
		// instantiate a data link layer, set its source info, lower layer and assign
		// this layer as upper layer to the lower layer
		DataLink dataLinkLayer = new DataLink(sourceInfo[0].getBytes(), physicalLayer);
		// instantiate a network layer, set its source info, lower layer and assign
		// this layer as upper layer to the lower layer
		Network networkLayer = new Network(sourceInfo[1].getBytes(), dataLinkLayer);
		// instantiate a transport layer, set its source info, lower layer and assign
		// this layer as upper layer to the lower layer
		Transport transportLayer = new Transport(sourceInfo[2].getBytes(), networkLayer);
		// instantiate an application layer, set its lower layer and assign
		// this layer as upper layer to the lower layer
		DataTransfer applicationLayer = new DataTransfer(transportLayer);
		return applicationLayer;
	}

	/**
	 * adds a receiver instance access with the receiver name to a HashMap<br>
	 * Firstly it creates an instance of the codec that should be used. After you
	 * create an instance of the lowest layer, pass on the previous created instance
	 * as lower layer and automatically set the upper Layer withing the constructor.
	 * You do this for each layer creating required layer hierarchy. For the
	 * receiver you add the instance to access the receivers hierarchy to a HashMap
	 * with its associated name
	 * 
	 * @param sourceInfo contains the users input data
	 * @return <code>HashMap</code> with a new added receiver instance
	 */
	public BuildFile createReceiverInstance(String[] sourceInfo) {
		Codec codec = new Codec4B5B();
		// assign codec in which the data is going to be received
		PHY physicalLayer = new PHY(codec);
		// instantiate a data link layer, set its source info, lower layer and assign
		// this layer as upper layer to the lower layer
		DataLink dataLinkLayer = new DataLink(sourceInfo[1].getBytes(), physicalLayer);
		// instantiate a network layer, set its source info, lower layer and assign
		// this layer as upper layer to the lower layer
		Network networkLayer = new Network(sourceInfo[2].getBytes(), dataLinkLayer);
		// instantiate a transport layer, set its source info, lower layer and assign
		// this layer as upper layer to the lower layer
		Transport transportLayer = new Transport(sourceInfo[3].getBytes(), networkLayer);
		// instantiate an application layer, set its lower layer and assign
		// this layer as upper layer to the lower layer
		DataTransfer applicationLayer = new DataTransfer(transportLayer);
		// add the receiver access pointer to a HashMap as key with the receivers name
		// as value
		receiverAccess.put(physicalLayer, sourceInfo[0]);
		BuildFile builder = new BuildFile(receiverAccess, applicationLayer);
		return builder;
	}

	/**
	 * ensures that the input data contains the required data at the required
	 * spot.<br>
	 * If the Array of Strings is greater than 4 elements you need to reassemble the
	 * data input. In the Main Method the data input gets split after each space.
	 * When the input data contains spaces the data content get divided into more
	 * than the required elements. The last 3 spots in the Array contain the source
	 * addresses. Those get stored in the second to fourth Array spot. The previous
	 * Array spots (first to last but four) gets stored in the first array spot.
	 * This reassembled Array gets returned.
	 * 
	 * @param dataContent contains the full input data as Array of Strings
	 * @return <code>String[]</code> containing the input data in the right position
	 *         of the array
	 */
	public String[] connectData(String[] dataContent) {
		String[] connectedData = new String[4];
		// assign the data to the arrays first spot
		connectedData[0] = dataContent[0];
		for (int i = 1; i < dataContent.length - 3; i++) {
			connectedData[0] = connectedData[0] + " " + dataContent[i];
		}
		// assign the source addresses to required spots in within the array
		connectedData[1] = dataContent[dataContent.length - 3];
		connectedData[2] = dataContent[dataContent.length - 2];
		connectedData[3] = dataContent[dataContent.length - 1];
		return connectedData;
	}
}
