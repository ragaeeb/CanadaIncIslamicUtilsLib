package com.canadainc.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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