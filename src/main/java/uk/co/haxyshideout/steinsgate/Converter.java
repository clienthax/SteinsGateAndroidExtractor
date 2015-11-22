package uk.co.haxyshideout.steinsgate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Converter {

	public static void main(String[] args) throws Exception {
		new Converter();
	}

	public Converter() throws Exception {


		JOptionPane.showMessageDialog(null, "Choose main.1.com.mages.steinsgate.obb");
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.showOpenDialog(null);
		File selectedFile = fileChooser.getSelectedFile();//main.1.com.mages.steinsgate.obb

		ZipFile zipFile = new ZipFile(selectedFile);
		File outputFolder = new File(selectedFile, "output");
		outputFolder.mkdirs();

		Predicate<ZipEntry> isOggSound = zipEntry -> zipEntry.getName().contains("sounds/");
		Predicate<ZipEntry> isMovie = zipEntry -> zipEntry.getName().contains("movies/");
		Predicate<ZipEntry> isImage = zipEntry -> zipEntry.getName().contains("images/");
		Predicate<ZipEntry> isScript = zipEntry -> zipEntry.getName().contains("scripts/");
		Predicate<ZipEntry> isVoicePak = zipEntry -> zipEntry.getName().endsWith(".pak");
		ArrayList<String> unsupportedFiles = new ArrayList<>();

		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = entries.nextElement();
			if(zipEntry.isDirectory())
				continue;
			byte[] data = extractEntry(zipFile, zipEntry);
			System.out.println("Extracting "+zipEntry.getName());
			if(isOggSound.test(zipEntry)) {
				File outFile = new File(outputFolder, zipEntry.getName().replace(".sdt", ".ogg"));
				outFile.getParentFile().mkdirs();
				FileUtils.writeByteArrayToFile(outFile, data);
			} else if(isMovie.test(zipEntry)) {
				File outFile = new File(outputFolder, zipEntry.getName().replace(".sdt", ".mov"));
				outFile.getParentFile().mkdirs();
				data = Utils.unscrambleHeader(data);
				FileUtils.writeByteArrayToFile(outFile, data);
			} else if(isImage.test(zipEntry)) {
				File outFile = new File(outputFolder, zipEntry.getName().replace(".sdt", ".bmp"));
				outFile.getParentFile().mkdirs();
				data = Utils.unscrambleHeader(data);
				FileUtils.writeByteArrayToFile(outFile, data);
			} else if(isVoicePak.test(zipEntry)) {
				File outFolder = new File(outputFolder, "voices/"+zipEntry.getName().substring(0, zipEntry.getName().indexOf(".")));
				outFolder.getParentFile().mkdirs();
				PakExtractor.extractPak(data, outFolder);
			} else if(isScript.test(zipEntry)) {
				File outFile = new File(outputFolder, zipEntry.getName());
				outFile.getParentFile().mkdirs();
				FileUtils.writeByteArrayToFile(outFile, Utils.unscrambleAll(data));
			} else {
				unsupportedFiles.add("Unknown file! "+zipEntry.getName());
			}
		}

		for(String line : unsupportedFiles) {
			System.out.println(line);
		}

	}


	private byte[] extractEntry(ZipFile zipFile, ZipEntry zipEntry) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		InputStream inputStream = zipFile.getInputStream(zipEntry);
		while((len = inputStream.read(buffer)) >= 0) {
			outputStream.write(buffer, 0, len);
		}
		inputStream.close();
		outputStream.close();
		return outputStream.toByteArray();
	}
}
