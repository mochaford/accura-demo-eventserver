package com.example.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.model.CylinderWrapper;
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
	public String addHistory(@RequestParam("param") String body) {
		ResultMapper mapper = new ResultMapper();
		try {
			System.out.println("---body--" + body);
			JSONObject jsonobject = JSONObject.fromObject(body);
			CylinderWrapper user = (CylinderWrapper) JSONObject.toBean(jsonobject, CylinderWrapper.class);
			System.out.println("---user--" + user);
			int res = service.addHistory(user);
			mapper.setResult(res);
			mapper.setContent("");
			
		} catch (Exception e) {
			mapper.setResult(0);
			mapper.setContent("The Parameter is malformed");
			e.printStackTrace();
		}
		System.out.println("---mapper--" + mapper);
		//JSONObject object = JSONObject.fromObject(mapper);
		//return object.toString();
		return null;
	}

	
	@RequestMapping(value = "/historyList", method = RequestMethod.POST)
	@ResponseBody
	public String addHistoryList(@RequestParam("param") String param) { //@RequestBody
		ResultMapper mapper = new ResultMapper();
		try {
			System.out.println("---addHistoryList----" + param);
			//JSONObject jsonobject = JSONObject.fromObject(param);
			//PioCylinderHistory user = (PioCylinderHistory) JSONObject.toBean(jsonobject, PioCylinderHistory.class);
			JSONArray jsonArray = JSONArray.fromObject(param);
			System.out.println("---jsonArray----" + jsonArray);
			List<CylinderWrapper> list_pioHistory = (List<CylinderWrapper>) JSONArray.toCollection(jsonArray,CylinderWrapper.class);
			int res = service.addHistoryListByJDBC(list_pioHistory);
			System.out.println("---res----" + res);
		} catch (Exception e) {
			e.printStackTrace();
			mapper.setResult(0);
			mapper.setContent("The Parameter is malformed");
			return "123";
		}
		//JSONObject object = JSONObject.fromObject(mapper);
		//System.out.println("---object----" + object.toString());
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
			Map<String,String> paramMap = new HashMap();
			if(param != null){
				paramMap = JsonFormatUtils.parseJSON2MapString(param);
			}
			List<PioEvent> list_pioHistory = service.sortAndGroupByEvent(paramMap);
			
			int res = service.addEventListByJDBC(list_pioHistory);
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
		
		String param = "[{\"cylinderId\":\"1\",\"materialId\":\"1\",\"accountId\":\"1\",\"fillStatus\":\"1\",\"timeStamp\":\"2016-05-10 00:00:00\",\"countryCode\":\"1\",\"duration\":2,\"flag\":2}];";
		JSONObject jo1 = new JSONObject();
		JSONArray jsonArray = new JSONArray();
        
        
        jo1.put("cylinderId", "1");
		jo1.put("materialId", "1");
		jo1.put("accountId", "1");
		jo1.put("fillStatus", "1");
		jo1.put("timeStamp", "2016-05-10 00:00:00");
		jo1.put("countryCode", "1");
		jo1.put("duration", 2);
		jo1.put("flag", 2);
		jsonArray.add(0, jo1.toString());
		System.out.println("---" + jsonArray);
		List<CylinderWrapper> list_pioHistory = (List<CylinderWrapper>) JSONArray.toCollection(jsonArray,CylinderWrapper.class);
		System.out.println("----list_pioHistory :" + list_pioHistory);
		
	}
}
