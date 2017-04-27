package com.canadainc.common.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IOUtils
{
	public static String readFileUtf8(File f) throws IOException {
		return readFile( f.getPath(), StandardCharsets.UTF_8 );
	}

	public static String readFile(String path, Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}



	public static String readWin1256(File input) throws IOException
	{
		StringBuilder result = new StringBuilder();

		Reader r = new InputStreamReader(new FileInputStream(input), "Windows-1256");
		BufferedReader buffered = new BufferedReader(r);
		try {
			String line;
			while ((line = buffered.readLine()) != null) {
				result.append(line);
			}
		} finally {
			buffered.close();
		}

		return result.toString();
	}


	public static void writeFile(String path, String data)
	{
		BufferedWriter writer = null;
		try {
			File logFile = new File(path);
			writer = new BufferedWriter(new FileWriter(logFile));
			writer.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Close the writer regardless of what happens...
				writer.close();
			} catch (Exception e) {
			}
		}
	}



	public static final byte[] getByteArrayFromFile(File imgFile)
	{
		byte[] result=null;
		FileInputStream fileInStr=null;
		try{
			fileInStr=new FileInputStream(imgFile);
			long imageSize=imgFile.length();

			if(imageSize>Integer.MAX_VALUE){
				return null;    //image is too large
			}

			if(imageSize>0){
				result=new byte[(int)imageSize];
				fileInStr.read(result);
			}
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			try {
				fileInStr.close();
			} catch (Exception e) {
			}
		}
		return result;
	}


	public static boolean deleteDirectory(File directory)
	{
		if(directory.exists()){
			File[] files = directory.listFiles();
			if(null!=files){
				for(int i=0; i<files.length; i++) {
					if(files[i].isDirectory()) {
						deleteDirectory(files[i]);
					}
					else {
						files[i].delete();
					}
				}
			}
		}
		return(directory.delete());
	}
}