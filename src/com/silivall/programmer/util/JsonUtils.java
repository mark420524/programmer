package com.silivall.programmer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;



/**
 * 递归解析json字符串
 * @author mark
 *
 */
public class JsonUtils {

	/**
	 * 加密字符串解密为map
	 * @param param
	 * @param isEncrypt		是否加密
	 * @param isEncode		是否编码
	 * @return
	 * @throws JSONException
	 */
	public static Map<Object,Object> parseAPIResponseDataToMap(String param,boolean isEncrypt,boolean isEncode) throws JSONException {
		String plainText = null;
		if (isEncrypt) {
			//加密的字符需要解密
		}else{
			plainText = param;
		}
		JSONObject jsonObj = new JSONObject(plainText);
		return parseAPIResponseDataToMap(jsonObj,isEncode);
	}
	
	/**
	 * 解析jsonObject 成map 
	 * @param jsonObject		
	 * @param isEncode			是否编码	true 已编码,需解码
	 * @return
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public static Map<Object,Object> parseAPIResponseDataToMap(JSONObject jsonObject,boolean isEncode) throws JSONException {
		Map<Object, Object> paramMap = new HashMap<Object, Object>();
		if (jsonObject!=null) {
			Iterator<?> it = jsonObject.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object obj = jsonObject.get(key);
				if (obj==null) {
					paramMap.put(key, null);
				}else if(obj instanceof String) {
					if (obj.toString().length()>0) {
						if (isEncode) {
							//base 64 解码
							paramMap.put(key, Base64Util.decode(obj.toString()));
						}else{
							paramMap.put(key, obj);
						}
					}else{
						paramMap.put(key,null);
					}
				}else if(obj instanceof List){
					// 处理包含的List
					List<Object> listObj = parseJSONList((List<Object>)obj,isEncode);
					paramMap.put(key, listObj);
				}else if(obj instanceof JSONObject){
					Map<Object, Object> mapObj = parseJSONObject((JSONObject)obj,isEncode);
					paramMap.put(key, mapObj);
				}else {
					paramMap.put(key, obj);
				}
			}
		}
		return paramMap; 
	}   
	
	/**
	 * 将List<JSONObject>中的所有对象的所有数据解析到Map中，递归调用
	 * @param t_list
	 * @return
	 * @throws JSONException 
	 */
	@SuppressWarnings("unchecked")
	private static List<Object> parseJSONList(List<Object> t_list,boolean isEncode) throws JSONException{
		List<Object> listObj = new ArrayList<Object>();		
		if(t_list!=null){			
			for(Object t_obj : t_list){
				if(t_obj == null) continue;
				if(t_obj instanceof JSONObject){
					JSONObject jsonObject = (JSONObject)t_obj;								
					Iterator<?> it = jsonObject.keys();
					Map<String, Object> mapObj = new HashMap<String, Object>();
					while(it.hasNext()){
						String t_key = (String)it.next();
						Object t_obj1 = jsonObject.get(t_key);
						if(t_obj1==null){
							mapObj.put(t_key, null);
						}else if(t_obj1 instanceof String){
							if(t_obj1.toString().length()>0 ){
								if(isEncode){
									//Base64解码后放入Map
									mapObj.put(t_key, Base64Util.decode(t_obj1.toString()));
								}else{
									mapObj.put(t_key, t_obj1);
								}
							}else{
								mapObj.put(t_key, null);
							}
						}else if(t_obj1 instanceof List){
							// 处理包含的List
							List<Object> listObj1 = parseJSONList((List<Object>)t_obj1,isEncode);
							mapObj.put(t_key, listObj1);
						}else if(t_obj1 instanceof JSONObject){
							Map<Object, Object> mapObj1 = parseJSONObject((JSONObject)t_obj1,isEncode);
							mapObj.put(t_key, mapObj1);
						}else{
							mapObj.put(t_key, t_obj1);
						}
					}
					listObj.add(mapObj);
				}else{
					if (isEncode) {
//						Base64解码后放入Map
						listObj.add(Base64Util.decode(t_obj.toString()));
					}else{
						listObj.add(t_obj);
					}
				}
			}			
		}
		return listObj;
	}
	
	/**
	 * 将JSONObject对象中的所有数据解析到Map中，递归调用
	 * @param jsonObject
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Map<Object,Object> parseJSONObject(JSONObject jsonObject,boolean isEncode)  throws JSONException {
		Map<Object,Object> mapObj = new HashMap<Object,Object>();
		Iterator<?> it = jsonObject.keys();
		while(it.hasNext()){
			String t_key = (String)it.next();
			Object t_obj = jsonObject.get(t_key);
			if(t_obj==null){
				mapObj.put(t_key, null);
			}else if(t_obj instanceof String){
				if(t_obj.toString().length()>0){
					if (isEncode) {
						 //Base64解码后放入Map
						mapObj.put(t_key, Base64Util.decode(t_obj.toString()));
					}else{
						mapObj.put(t_key, t_obj);
					}
				}else{
					mapObj.put(t_key, null);
				}
			}else if(jsonObject.get(t_key) instanceof List){
				// 处理包含的List
				List<Object> listObj1 = parseJSONList((List<Object>)t_obj,isEncode);
				mapObj.put(t_key, listObj1);
			}else if(jsonObject.get(t_key) instanceof JSONObject){
				Map<Object, Object> mapObj1 = parseJSONObject((JSONObject)t_obj,isEncode);
				mapObj.put(t_key, mapObj1);
			}else {
				mapObj.put(t_key, t_obj);
			}
		}
		return mapObj;
	}
}
