package com.example.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
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
			mapper.setResult(res + "");
			mapper.setBody("");
			
		} catch (Exception e) {
			mapper.setResult("0");
			mapper.setBody("The Parameter is malformed");
			e.printStackTrace();
		}
		System.out.println("---mapper--" + mapper);
		//JSONObject object = JSONObject.fromObject(mapper);
		//return object.toString();
		return null;
	}

	/**
	 * upload file to history table
	 * */
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
			String res = service.addHistoryListByJDBC(list_pioHistory);
			if("success".equals(res)){
				mapper = new ResultMapper("success","");
			}else{
				mapper = new ResultMapper("error",res);
			}
			System.out.println("---res----" + res);
		} catch (Exception e) {
			e.printStackTrace();
			mapper.setResult("0");
			mapper.setBody(e.getMessage());
			return "123";
		}
		JSONObject object = JSONObject.fromObject(mapper);
		System.out.println("---object----" + object.toString());
		return object.toString();
	}
	/**
	 * transfor history data to event_1
	 * */
	@RequestMapping(value = "/pioEvent", method = RequestMethod.POST)
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
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					List<PioEvent> list_pioHistory = service.sortAndGroupByEvent(null);
					System.out.println("---list_pioHistory----" + list_pioHistory);
					int res = service.addEventListByJDBC(list_pioHistory);
					//mapper.setResult(res);
					System.out.println("---res----" + res);
				}
			}).start();
			mapper = new ResultMapper("success","");
			JSONObject object = JSONObject.fromObject(mapper);
			System.out.println("---object----" + object.toString());
			return object.toString();
		} catch (Exception e) {
			System.out.println("--addEventList exception" + e.getMessage());
			e.printStackTrace();
			mapper.setResult("0");
			mapper.setBody(e.getMessage());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();  
			return "123";
		}
		//JSONObject object = JSONObject.fromObject(mapper);
		//System.out.println("---object----" + object.toString());
	}
	/**
	 * reset history and event table
	 * 
	 * */
	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	@ResponseBody
	public String resetData() { //@RequestBody
		ResultMapper mapper = null;
		try {
			//JSONArray jsonArray = JSONArray.fromObject(param);
			//List<PioEvent> list_pioHistory = (List<PioEvent>) JSONArray.toCollection(jsonArray,PioCylinderHistory.class);
			Map<String,String> paramMap = new HashMap();
			
			String resetResult = service.resetData();
			System.out.println("-resetData--resetResult----" + resetResult);
			if("success".equals(resetResult)){
				mapper = new ResultMapper("success","");
			}else{
				mapper = new ResultMapper("error",resetResult);
			}
			
		} catch (Exception e) {
			System.out.println("--addEventList exception" + e.getMessage());
			e.printStackTrace();
			mapper = new ResultMapper("error",e.getMessage());
			mapper.setBody(e.getMessage());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();  
		}
		JSONObject object = JSONObject.fromObject(mapper);
		return object.toString();
		//JSONObject object = JSONObject.fromObject(mapper);
		//System.out.println("---object----" + object.toString());
	}
	
	public class ResultMapper {
		private  String result;
		private  String body;

		public ResultMapper() {
			
		}
		public ResultMapper(String result,String body) {
			this.result = result;
			this.body = body;
		}
		
		public String getResult() {
			return result;
		}

		public String getBody() {
			return body;
		}
		public void setBody(String body) {
			this.body = body;
		}
		public void setResult(String result) {
			System.out.println("---set--" + result);
			if ("0".equals(result))
				this.result = "failure";
			else
				this.result = "success";
			System.out.println("---set-body-" + this.body);
		}

	}
	public static void main(String[] args) {
		ResultMapper mapper = new PioEventController().new ResultMapper("error","error1");
		
		JSONObject object = JSONObject.fromObject(mapper);
		System.out.println("---object----" + object.toString());
		
		/**
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
		 * */
		
	}
}
