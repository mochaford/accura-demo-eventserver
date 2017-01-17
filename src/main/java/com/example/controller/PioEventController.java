package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.model.PioCylinderHistory;
import com.example.service.PioEventService;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/event")
public class PioEventController {

	@Autowired
	private PioEventService service;

	@RequestMapping(value = "/history", method = RequestMethod.POST)
	public String addHistory(@RequestBody String body) {
		ResultMapper mapper = new ResultMapper();
		try {
			JSONObject jsonobject = JSONObject.fromObject(body);
			PioCylinderHistory user = (PioCylinderHistory) JSONObject.toBean(jsonobject, PioCylinderHistory.class);
			int res = service.addHistory(user);
			mapper.setResult(res);
			
		} catch (Exception e) {
			mapper.result = "failure";
			mapper.content = "The Parameter is malformed";
		}
		JSONObject object = JSONObject.fromObject(mapper);
		return object.toString();
	}
	@RequestMapping(value = "/history", method = RequestMethod.GET)
	public String sort(@RequestBody String body) {
		ResultMapper mapper = new ResultMapper();
		try {
			JSONObject jsonobject = JSONObject.fromObject(body);
			PioCylinderHistory user = (PioCylinderHistory) JSONObject.toBean(jsonobject, PioCylinderHistory.class);
			int res = service.addHistory(user);
			mapper.setResult(res);
			
		} catch (Exception e) {
			mapper.result = "failure";
			mapper.content = "The Parameter is malformed";
		}
		JSONObject object = JSONObject.fromObject(mapper);
		return object.toString();
	}

	class ResultMapper {
		public String result;
		public String content;

		public ResultMapper() {

		}

		public void setResult(int result) {
			if (result == 0)
				this.result = "failure";
			else
				this.result = "success";
		}

	}
}
