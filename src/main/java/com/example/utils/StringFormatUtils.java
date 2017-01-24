package com.example.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import sun.misc.BASE64Encoder;

public class StringFormatUtils {
	
	public static Timestamp getTimesstampByString(String time){
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		ts = Timestamp.valueOf(time);
		return ts;
	}
	
	 public static String EncoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException{
       
		 try {
	            MessageDigest md=MessageDigest.getInstance("md5");
	            byte[] md5=    md.digest();
	            
	            //转化为明文
	            BASE64Encoder encoder=new BASE64Encoder();
	            return encoder.encode(md5);
	        } catch (NoSuchAlgorithmException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        return null;
    }
	 public static void main(String[] args) throws Exception{
		 System.out.println(UUID.randomUUID().toString());
		System.out.println(EncoderByMd5(new Date().getTime() + ""));
		System.out.println(getShortUuid());
	}
	 
	 public static String[] chars = new String[]
		{
		    "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
		    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
		    "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V","W", "X", "Y", "Z"
		}; 
	 public static String getShortUuid()
    { 
        StringBuffer stringBuffer = new StringBuffer(); 
        String uuid = UUID.randomUUID().toString().replace("-", ""); 
        for (int i = 0; i < 10; i++)
        { 
            String str      = uuid.substring(i * 2, i * 2 + 4); 
            int strInteger  = Integer.parseInt(str, 16); 
            stringBuffer.append(chars[strInteger % 0x3E]); 
        } 
         
        return stringBuffer.toString(); 
    }  
	 public static String getStringFromTimestamp(Timestamp ts){
		 DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		 String tsStr = ""; 
		 if(ts != null)
			 tsStr = sdf.format(ts); 
		 return tsStr;
	 }

}
