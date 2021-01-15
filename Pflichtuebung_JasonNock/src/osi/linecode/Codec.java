package osi.linecode;

import osi.exception.DecodeException;

/**
 * <p>
 * Codec is a short name for coder-decoder.<br>
 * * Different codecs translate in different ways.
 */
public interface Codec {

	/**
	 * converts information by a set of specific rules
	 * 
	 * @param data to be converted
	 * @return <code>String</code> data in converted form
	 */
	String encode(byte[] data);

	/**
	 * decodes a stream of encoded data
	 * 
	 * @param data to be decoded
	 * @return <code>byte[]</code> decoded data
	 * @throws DecodeException - error that is thrown if data can not be decoded
	 */
	byte[] decode(String data) throws DecodeException;
}