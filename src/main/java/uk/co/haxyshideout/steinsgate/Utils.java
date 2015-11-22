package uk.co.haxyshideout.steinsgate;

public class Utils {

	public static byte[] unscrambleHeader(byte[] fileData) {
		for(int i = 0; i < 100; i++) {
			fileData[i] = (byte)(fileData[i] ^ 0x64);
		}
		return fileData;
	}

	public static byte[] unscrambleAll(byte[] fileData) {
		for(int i = 0; i < fileData.length; i++) {
			fileData[i] = (byte)(fileData[i] ^ 0x64);
		}
		return fileData;
	}

}
