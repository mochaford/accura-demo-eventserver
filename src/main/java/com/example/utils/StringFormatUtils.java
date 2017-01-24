package com.example.utils;

import java.sql.Timestamp;

public class StringFormatUtils {
	
	public static Timestamp getTimesstampByString(String time){
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		ts = Timestamp.valueOf(time);
		return ts;
	}

}
