package uk.co.haxyshideout.steinsgate;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class PakExtractor {

	public static void extractPak(byte[] pakFileData, File extractFolder) throws Exception {

		ByteBuf buffer = Unpooled.copiedBuffer(pakFileData);

		buffer.skipBytes(0x38);
		int fileCount = readString8(buffer);
		System.out.println("File count: " + fileCount);

		FileEntry[] fileEntrys = new FileEntry[fileCount];
		//Read the file info table
		for (int i = 0; i < fileCount; i++) {
			FileEntry fileEntry = new FileEntry();
			fileEntry.fileName = readFileName(buffer);
			buffer.skipBytes(0x14);
			fileEntry.startPos = readString16(buffer);
			fileEntry.length = readString16(buffer);
			fileEntrys[i] = fileEntry;
			System.out.println("File Entry, name: " + fileEntry.fileName + " startPos: " + fileEntry.startPos + " length: " + fileEntry.length);
		}

		//read the file data
		for (int i = 0; i < fileCount; i++) {
			FileEntry fileEntry = fileEntrys[i];
			byte[] data = new byte[fileEntry.length];
			buffer.readBytes(data);
			System.out.println("Extracting " + fileEntry.fileName);
			FileUtils.writeByteArrayToFile(new File(extractFolder, fileEntry.fileName), data);
		}


	}

	static class FileEntry {
		String fileName;
		int startPos;
		int length;
	}

	public static String readFileName(ByteBuf buf) {
		byte[] data = new byte[12];
		buf.readBytes(data);
		return new String(data);
	}

	public static int readString8(ByteBuf buf) {
		byte[] data = new byte[8];
		buf.readBytes(data);
		String trimed = new String(data).trim();
		return Integer.parseInt(trimed);
	}

	public static int readString16(ByteBuf buf) {
		byte[] data = new byte[16];
		buf.readBytes(data);
		String trimed = new String(data).trim();
		return Integer.parseInt(trimed);
	}

}
