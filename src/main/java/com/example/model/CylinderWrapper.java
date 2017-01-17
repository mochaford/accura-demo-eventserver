package com.example.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CylinderWrapper implements Comparable {

	private String cylinderId;
	private String materialId;
	private String accountId;
	private String fillStatus;
	private String timeStamp;
	private String countryCode;
	private Integer duration;
    
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		try {
			CylinderWrapper wrapper = (CylinderWrapper)o;
			Long oldTime = formatDateByString(wrapper.timeStamp).getTime();
	        Long currentTime = formatDateByString(timeStamp).getTime();
	        Long sorts = (currentTime-oldTime);
	        if (sorts == 0) return 0;
	        if (sorts > 0) return 1;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        return -1;       
	}

	public Date formatDateByString(String source) throws ParseException{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date strDate = formatter.parse(source);
		return strDate;
	}
	public static void main(String[] args) throws ParseException {
		String main = "2016-05-10 00:00:00";
		Date a = new CylinderWrapper().formatDateByString(main);
		System.out.println(a.getTime());
	}

	public String getCylinderId() {
		return cylinderId;
	}

	public void setCylinderId(String cylinderId) {
		this.cylinderId = cylinderId;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getFillStatus() {
		return fillStatus;
	}

	public void setFillStatus(String fillStatus) {
		this.fillStatus = fillStatus;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}
}