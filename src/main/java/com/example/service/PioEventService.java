package com.example.service;

import java.util.List;
import java.util.Map;

import com.example.model.PioEvent;
import com.example.model.PioCylinderHistory;

public interface PioEventService {

	List<PioEvent> sortAndGroupByEvent(Map<String,String> paramMap);
	int addHistory(PioCylinderHistory history);
}
