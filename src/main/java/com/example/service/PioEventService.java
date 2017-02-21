package com.example.service;

import java.util.List;
import java.util.Map;

import com.example.model.PioEvent;
import com.example.model.CylinderWrapper;
import com.example.model.PioCylinderHistory;

public interface PioEventService {

	List<PioEvent> sortAndGroupByEvent(Map<String,String> paramMap);
	int addHistory(CylinderWrapper history);
	boolean addEventList(List<PioEvent> list_pio);
	public String addHistoryListByJDBC(List<CylinderWrapper> list_history);
	public int addEventListByJDBC(List<PioEvent> list_pio);
	public String resetData();
	List<PioEvent> getEventListByParam(Map<String,String> paramMap) throws Exception;
}
