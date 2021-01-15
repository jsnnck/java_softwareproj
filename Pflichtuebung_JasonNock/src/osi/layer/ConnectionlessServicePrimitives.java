package osi.layer;

/**
 * In a network layered architecture, when one layer requires another layer to
 * carry out a service,<br>
 * the communication between the layers is carried out by service
 * primitives.<br>
 * * <br>
 * In connectionless the data is transferred in one direction from source to
 * destination<br>
 * without checking that destination is still there or not or if it prepared to
 * * accept the message
 */
public interface ConnectionlessServicePrimitives {

	/**
	 * Request: A primitive sent by layer (N + 1) to layer N to request a
	 * service.<br>
	 * It invokes the service and passes any required parameters.
	 * 
	 * @param params - method can be called with zero or more arguments.<br>
	 *               As a result, params variable is implicitly declared as an array
	 *               of type byte[ ].<br>
	 *               Thus, inside the method, params variable is accessed using the
	 *               array syntax.<br>
	 *               In case of no arguments, the length of params is 0<br>
	 * @return string representation of the resulting line code
	 */
	String req(byte[]... params);

	/**
	 * Indication: A primitive returned to layer (N + l) from layer N<br>
	 * to advise of activation of a requested service or of an action initiated by
	 * the layer N service.
	 * 
	 * @param serviceDataUnit - unit of data that has been passed down from an OSI
	 *                        layer or sublayer to a lower layer.
	 * @return service data unit of this layer
	 */
	byte[] ind(byte[] serviceDataUnit);
}