package com.example.service;

import java.util.List;
import java.util.Map;

import com.example.model.PioEvent;
import com.example.model.CylinderWrapper;
import com.example.model.PioCylinderHistory;

public interface PioEventService {

	List<PioEvent> sortAndGroupByEvent(Map<String,String> paramMap);
	int addHistory(PioCylinderHistory history);
	boolean addHistoryList(List<CylinderWrapper> list_pio);
	boolean addEventList(List<PioEvent> list_pio);
}
