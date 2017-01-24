package com.example.service;

import java.util.List;
import java.util.Map;

import com.example.model.PioEvent;
import com.example.model.CylinderWrapper;
import com.example.model.PioCylinderHistory;

public interface PioEventService {

	List<PioEvent> sortAndGroupByEvent(Map<String,String> paramMap);
	int addHistory(CylinderWrapper history);
	boolean addHistoryList(List<CylinderWrapper> list_pio);
	boolean addEventList(List<PioEvent> list_pio);
	public int addHistoryListByJDBC(List<CylinderWrapper> list_history);
	public int addEventListByJDBC(List<PioEvent> list_pio);
}
