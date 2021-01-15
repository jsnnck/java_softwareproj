package osi.linecode;

import osi.exception.DecodeException;

/**
 * <p>
 * This class processes data to transmit it.<br>
 * You can either decode or encode the given data. <br>
 * When encoding the data, the public Method encode gets called, which calls a
 * private function to operate the encoding. In the private encodeOperation
 * Method the input data as Array of Bytes is converted into a String of its
 * assigned Hexadecimal values. This will be transformed into a String of its
 * assigned 4B5B Code values and returns the encoded data as String of 4B5B
 * code. <br>
 * When decoding the data the public Method decode gets called, which calls a
 * private function to operate the decoding. In the private decodeOperation
 * Method the input as String of 4B5B code gets checked if the input is valid
 * and converts the 4B5B code content into a String of its assigned Hexadecimal
 * values, converted into a Array of byte and returned.
 * 
 * @author Jason Nock
 * @version 2.7
 */
public class Codec4B5B implements Codec {

	/**
	 * 4B5B function quiet (for further operation)
	 */
	final static String CTRLQ = "00000";

	/**
	 * 4B5B function idle (for further operation)
	 */
	final static String CTRLI = "11111";

	/**
	 * 4B5B function start #1
	 */
	final static String CTRLJ = "11000";

	/**
	 * 4B5B function start #2
	 */
	final static String CTRLK = "10001";

	/**
	 * 4B5B function end
	 */
	final static String CTRLT = "01101";

	/**
	 * 4B5B function reset
	 */
	final static String CTRLR = "00111";

	/**
	 * 4B5B function set (for further operation)
	 */
	final static String CTRLS = "11001";

	/**
	 * 4B5B function halt (for further operation)
	 */
	final static String CTRLH = "00100";

	@Override
	public String encode(byte[] data) {
		return this.encodeOperation(data); // call of capsuled operation function
	}

	private String encodeOperation(byte[] data) {
		String hexaString = "";
		String tempString = "";
		int tempConvert;
		for (int i = 0; i < data.length; i++) {
			// byte[] is an Array of signed 8-Bit Values
			// for the conversion we need unsigned 8-Bit Values, if the byte value is
			// negative we need to convert it into an Integer
			if (data[i] < 0) {
				tempConvert = data[i] & 0xff;
			} else {
				tempConvert = data[i];
			}
			tempString = Integer.toHexString(tempConvert); // converts bytes of text input to Hexa String
			if (tempString.length() < 2) {
				tempString = "0" + tempString;
			}
			hexaString += tempString; // concatenates every Hexa String
		}
		return convertHexaIn4B5B(hexaString); // return the 4B5B code
	}

	private String convertHexaIn4B5B(String textAsHexa) {
		String coded4B5B = "";
		coded4B5B += CTRLJ + CTRLK; // append start condition to converted String
		for (int i = 0; i < textAsHexa.length(); i++) {
			// convert every Hexa Value into assigned 4B5B code and write it into converted
			// String
			switch (textAsHexa.charAt(i)) {
			case '0':
				coded4B5B += "11110";
				break;
			case '1':
				coded4B5B += "01001";
				break;
			case '2':
				coded4B5B += "10100";
				break;
			case '3':
				coded4B5B += "10101";
				break;
			case '4':
				coded4B5B += "01010";
				break;
			case '5':
				coded4B5B += "01011";
				break;
			case '6':
				coded4B5B += "01110";
				break;
			case '7':
				coded4B5B += "01111";
				break;
			case '8':
				coded4B5B += "10010";
				break;
			case '9':
				coded4B5B += "10011";
				break;
			case 'a':
				coded4B5B += "10110";
				break;
			case 'b':
				coded4B5B += "10111";
				break;
			case 'c':
				coded4B5B += "11010";
				break;
			case 'd':
				coded4B5B += "11011";
				break;
			case 'e':
				coded4B5B += "11100";
				break;
			case 'f':
				coded4B5B += "11101";
				break;
			}
		}
		coded4B5B += CTRLT + CTRLR; // append end condition to converted String
		return coded4B5B; // return converted 4B5B String
	}

	@Override
	public byte[] decode(String data) throws DecodeException {
		return this.decodeOperation(data); // call of capsuled operation function
	}

	private byte[] decodeOperation(String data) {
		if (data.startsWith(CTRLJ + CTRLK)) { // check if input start with starting condition
			data = data.substring(10); // cut start condition
			if (data.endsWith(CTRLT + CTRLR)) { // check if input ends with ending condition
				data = data.substring(0, data.length() - 10); // cut end condition
				if (data.length() % 5 == 0) { // check if input length is multiple of 5 (4B5B length equals 5 chars)
					String textAsHexa = "";
					textAsHexa = convert4B5BInHexa(data);
					// split text bytes into Array of single chars, every spot in array holds hexa
					// value of a single character
					String[] singleChar = textAsHexa.split(" ");
					byte[] textContent = new byte[singleChar.length];
					// go through each array spot and convert hexa string value into integer
					for (int i = 0; i < singleChar.length; i++) {
						// the converted integer matches the ASCII value of the text
						textContent[i] = (byte) Integer.parseInt(singleChar[i], 16);
					}
					return textContent;
				} else { // input length invalid
					throw new DecodeException("Wrong data size!!!");
				}
			} else { // end condition missing
				throw new DecodeException("EndDelimiter missing!!!");
			}
		} else { // start condition missing
			throw new DecodeException("StartDelimiter missing!!!");
		}
	}

	private String convert4B5BInHexa(String encoded4B5B) {
		String textAsHexa = "";
		String single4B5BChar = "";
		for (int i = 0; i < encoded4B5B.length() / 5; i++) { // go through whole 4B5B input
			single4B5BChar = "";
			for (int indexCounter = 0; indexCounter < 5; indexCounter++) {
				// cache 5 indexes of full 4B5B code into temporary string single4B5BChar
				// (4B5B length equals 5 chars)
				single4B5BChar += encoded4B5B.charAt(indexCounter + (i * 5));
			}
			switch (single4B5BChar) {
			// check temporary String of single 4B5B codesection and convert it into Hexa
			// Value
			case "11110":
				textAsHexa += "0";
				break;
			case "01001":
				textAsHexa += "1";
				break;
			case "10100":
				textAsHexa += "2";
				break;
			case "10101":
				textAsHexa += "3";
				break;
			case "01010":
				textAsHexa += "4";
				break;
			case "01011":
				textAsHexa += "5";
				break;
			case "01110":
				textAsHexa += "6";
				break;
			case "01111":
				textAsHexa += "7";
				break;
			case "10010":
				textAsHexa += "8";
				break;
			case "10011":
				textAsHexa += "9";
				break;
			case "10110":
				textAsHexa += "a";
				break;
			case "10111":
				textAsHexa += "b";
				break;
			case "11010":
				textAsHexa += "c";
				break;
			case "11011":
				textAsHexa += "d";
				break;
			case "11100":
				textAsHexa += "e";
				break;
			case "11101":
				textAsHexa += "f";
				break;
			case CTRLQ:
				// declare decoding of 4B5B control functions only relevant for further
				// operation
				textAsHexa += "Q";
				break;
			case CTRLI:
				textAsHexa += "I";
				break;
			case CTRLJ:
				textAsHexa += "J";
				break;
			case CTRLK:
				textAsHexa += "K";
				break;
			case CTRLT:
				textAsHexa += "T";
				break;
			case CTRLR:
				textAsHexa += "R";
				break;
			case CTRLS:
				textAsHexa += "S";
				break;
			case CTRLH:
				textAsHexa += "H";
				break;
			default:
				// throw Exception if 4B5B code does not match a valid value or function
				throw new DecodeException("Undefined 4B5B Code");
			}
			if ((i + 1) % 2 == 0) {
				// sets spacebar after 2 chars
				// Every ASCII char can be display by 2 hexa chars
				textAsHexa += " ";
			}
		}
		return textAsHexa;
	}

}
