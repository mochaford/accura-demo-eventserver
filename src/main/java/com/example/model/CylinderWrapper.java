package com.example.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pio_cylinder_filling_level_history")
@org.hibernate.annotations.Entity(dynamicUpdate=false) 
public class CylinderWrapper implements Comparable {

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name="cylinderid")
	private String cylinderId;
	@Column(name="materialid")
	private String materialId;
	@Column(name="filling_level")
	private String fillingLevel;
	@Column(name="timestamp")
	private String timeStamp;
	@Column(name="locationid")
	private String locationId;
	private Integer duration;
	@Column(name="flag")
	private Integer flag;

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		
		try {
			CylinderWrapper wrapper = (CylinderWrapper) o;
			Long oldTime = formatDateByString(wrapper.getTimeStamp()).getTime();
			Long currentTime = formatDateByString(this.getTimeStamp()).getTime();
			Long sorts = (currentTime - oldTime);
			if (sorts == 0)
				return 0;
			if (sorts > 0)
				return 1;
		} catch (Exception e) {
			// TODO: handle exception
			
			e.printStackTrace();
		}
		 
		return -1;
	}

	public static Date formatDateByString(String source) throws ParseException {
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFillingLevel() {
		return fillingLevel;
	}

	public void setFillingLevel(String fillingLevel) {
		this.fillingLevel = fillingLevel;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	

	public Integer getDuration() {
		return duration;
	}

	
	 public void setDuration(Integer duration) {
		this.duration = duration;
	}
	

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		flag = 0;
		this.flag = flag;
	}

	@Override
	public String toString() {
		return "CylinderWrapper [id=" + id + ", cylinderId=" + cylinderId + ", materialId=" + materialId
				+ ", fillingLevel=" + fillingLevel + ", timeStamp=" + timeStamp + ", locationId=" + locationId
				+ ", duration=" + duration + ", flag=" + flag + "]";
	}
	

}
