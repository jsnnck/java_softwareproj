package osi.layer.physical;

import osi.layer.Layer;
import osi.layer.datalink.DataLink;
import osi.linecode.Codec;

/**
 * <p>
 * Fourth Layer of the given OSI model.<br>
 * This layer can provide or request services from its lower and upper layers.
 * This is used to send and receive data. Each instance of this layer can store
 * unique codec to translate data in different ways. This layer is essential for
 * transmitting and receiving data.
 * 
 * @author Jason Nock
 * @version 2.7
 */
public class PHY extends Layer {

	private Codec codec;
	private byte[] decodedPackage;

	/**
	 * Sets a codec for the layers instance.<br>
	 * You can assign different codecs to different layers.
	 * 
	 * @param codec determines in which code the given data gets encoded
	 */
	public PHY(Codec codec) {
		// since we dont have a lower layer we just pass null
		super(null);
		// set the codec of this instance
		this.codec = codec;
	}

	/**
	 * transmits data to encoder.
	 * 
	 * @param data text input to be converted into 4B5B code
	 * @return <code>String</code> as 4B5B code of encoded text
	 */
	public String transmit(byte[] data) {
		return codec.encode(data);
	}

	/**
	 * receives data of decoder and stores it into a local variable.<br>
	 * it checks if the upper layer is set. Then it decodes the transmitted data and
	 * hands it to the upper layer, which provides a service for this layer, by
	 * processing the data. The processed data, that was returned by the upper layer
	 * gets stored into a local variable
	 * 
	 * @param data contains transmitted 4B5B code input to be converted into text
	 */
	public void receive(String data) {
		if (this.upperLayer != null) {
			decodedPackage = ((DataLink) this.upperLayer).ind(codec.decode(data));
		} else {
			// invalid receiver instance
			decodedPackage = null;
		}
	}

	/**
	 * grants access to the this layer private variable decodedPackage
	 * 
	 * @return the current value of the variable decoded Package
	 */
	public byte[] getDecodedPackage() {
		return decodedPackage;
	}
}
