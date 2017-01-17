package com.example.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="pio_event_1")
public class PioEvent {

	@Id
	@GeneratedValue
	private String id;
	private String event;
	private String entityType;
	private String entityId;
	
	private String targetEntityId;
	private String properties;
	private String eventTime;
	
	private String eventTimeZone;
	private String tags;
	private String prid;
	private String creationTime;
	private String creationTimeZone;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getEntityType() {
		return entityType;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public String getTargetEntityId() {
		return targetEntityId;
	}
	public void setTargetEntityId(String targetEntityId) {
		this.targetEntityId = targetEntityId;
	}
	public String getProperties() {
		return properties;
	}
	public void setProperties(String properties) {
		this.properties = properties;
	}
	public String getEventTime() {
		return eventTime;
	}
	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}
	public String getEventTimeZone() {
		return eventTimeZone;
	}
	public void setEventTimeZone(String eventTimeZone) {
		this.eventTimeZone = eventTimeZone;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getPrid() {
		return prid;
	}
	public void setPrid(String prid) {
		this.prid = prid;
	}
	public String getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}
	public String getCreationTimeZone() {
		return creationTimeZone;
	}
	public void setCreationTimeZone(String creationTimeZone) {
		this.creationTimeZone = creationTimeZone;
	}
	
}
