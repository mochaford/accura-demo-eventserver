package com.example.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JsonFormatUtils {

	public static Map<String, Object> parseJSON2Map(String jsonStr){  
        Map<String, Object> map = new HashMap<String, Object>();  
        //最外层解析  
        JSONObject json = JSONObject.fromObject(jsonStr);  
        for(Object k : json.keySet()){  
            Object v = json.get(k);   
            //如果内层还是数组的话，继续解析  
            if(v instanceof JSONArray){  
                List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();  
                Iterator<JSONObject> it = ((JSONArray)v).iterator();  
                while(it.hasNext()){  
                    JSONObject json2 = it.next();  
                    list.add(parseJSON2Map(json2.toString()));  
                }  
                map.put(k.toString(), list);  
            } else {  
                map.put(k.toString(), v);  
            }  
        }  
        return map;  
    }  
	 public static Map<String, String> parseJSON2MapString(String jsonStr){  
	        Map<String, String> map = new HashMap<String, String>();  
	        //最外层解析  
	        JSONObject json = JSONObject.fromObject(jsonStr);  
	        for(Object k : json.keySet()){ 
	            Object v = json.get(k);   
	            if(null!=v){
	            	map.put(k.toString(), v.toString());  
	            }
	        }  
	        return map;  
	    }
}
