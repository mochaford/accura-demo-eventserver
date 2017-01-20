package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.model.PioCylinderHistory;
import com.example.model.PioEvent;
import com.example.service.PioEventService;
import com.example.utils.JsonFormatUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("event")
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
	@RequestMapping(value = "/history", method = RequestMethod.POST)
	@ResponseBody
	public String addHistoryList(@RequestParam("param") String param) { //@RequestBody
		ResultMapper mapper = new ResultMapper();
		try {
			System.out.println("---addHistoryList----" + param);
			//JSONObject jsonobject = JSONObject.fromObject(param);
			//PioCylinderHistory user = (PioCylinderHistory) JSONObject.toBean(jsonobject, PioCylinderHistory.class);
			JSONArray jsonArray = JSONArray.fromObject(param);
			List<PioCylinderHistory> list_pioHistory = (List<PioCylinderHistory>) JSONArray.toCollection(jsonArray,PioCylinderHistory.class);
			boolean res = service.addHistoryList(list_pioHistory);
			//mapper.setResult(res);
			System.out.println("---res----" + res);
		} catch (Exception e) {
			e.printStackTrace();
			mapper.setResult(0);
			mapper.setContent("The Parameter is malformed");
			return "123";
		}
		JSONObject object = JSONObject.fromObject(mapper);
		System.out.println("---object----" + object.toString());
		return param;
	}
	@RequestMapping(value = "/pioEvent", method = RequestMethod.GET)
	@ResponseBody
	public String addEventList(@RequestParam("param") String param) { //@RequestBody
		ResultMapper mapper = new ResultMapper();
		try {
			System.out.println("---pioEvent----" + param);
			//JSONArray jsonArray = JSONArray.fromObject(param);
			//List<PioEvent> list_pioHistory = (List<PioEvent>) JSONArray.toCollection(jsonArray,PioCylinderHistory.class);
			List<PioEvent> list_pioHistory = service.sortAndGroupByEvent(JsonFormatUtils.parseJSON2MapString(param));
			
			boolean res = service.addEventList(list_pioHistory);
			//mapper.setResult(res);
			System.out.println("---res----" + res);
		} catch (Exception e) {
			e.printStackTrace();
			mapper.setResult(0);
			mapper.setContent("The Parameter is malformed");
			return "123";
		}
		JSONObject object = JSONObject.fromObject(mapper);
		System.out.println("---object----" + object.toString());
		return param;
	}

	class ResultMapper {
		public String result;
		public String content;

		public ResultMapper() {

		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getResult() {
			return result;
		}

		public void setResult(int result) {
			if (result == 0)
				this.result = "failure";
			else
				this.result = "success";
		}

	}
	public static void main(String[] args) {
		JSONObject jsonobject = JSONObject.fromObject("{\"id\":\"5\",\"name\":\"yaoyao\"}");
		PioCylinderHistory user = (PioCylinderHistory) JSONObject.toBean(jsonobject, PioCylinderHistory.class);
		System.out.println(user.getName());
	}
}
