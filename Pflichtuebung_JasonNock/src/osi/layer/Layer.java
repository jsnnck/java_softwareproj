package osi.layer;

/**
 * <p>
 * The Open Systems Interconnection model (OSI model) is a conceptual model that
 * characterizes<br>
 * and standardizes the communication functions of a telecommunication or
 * computing system<br>
 * without regard to its underlying internal structure and technology.<br>
 * Its goal is the interoperability of diverse communication systems with
 * standard communication protocols.<br>
 * The model partitions a communication system into abstraction layers.<br>
 * <br>
 * A layer serves the layer above it and is served by the layer below it.<br>
 * For example, a layer that provides error-free communications across<br>
 * a network provides the path needed by applications above it,<br>
 * while it calls the next lower layer to send and receive packets that
 * constitute the contents of that path.
 * </p>
 */
public class Layer {

	/**
	 * upper layer for delegate method calls
	 */
	protected Layer upperLayer;

	/**
	 * lower layer for delegate method calls
	 */
	protected Layer lowerLayer;

	/**
	 * generates a new instance of a {@code Layer} object with a given sub layer
	 * 
	 * @param lowerLayer - the layer below this
	 */
	public Layer(Layer lowerLayer) {
		if (lowerLayer != null) {
			this.lowerLayer = lowerLayer;
			lowerLayer.setUpperLayer(this);
		}
	}

	/**
	 * with this the communication with the upper layer is realized
	 * 
	 * @param upperLayer - instance of the upper layer
	 */
	public void setUpperLayer(Layer upperLayer) {
		this.upperLayer = upperLayer;
	}

}