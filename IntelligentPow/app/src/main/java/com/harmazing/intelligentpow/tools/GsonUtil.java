package com.harmazing.intelligentpow.tools;

import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * 解析数据的工具类
 * @author Administrator
 *
 */
public class GsonUtil {
	/**
	 * 解析json数据,将json数据转换为bean对象
	 * @param content	需要解析的数据
	 * @param clazz		转化成bean对象类型
	 * @return	转化后的bean对象
	 */
	public static <T> T json2Bean(String content, Class<T> clazz) {
		Gson gson = new Gson();
		return gson.fromJson(content, clazz);
	}
    /**
     * 解析bean数据,将bean数据转换为json对象
     * @
     * @param clazz		转化成json对象类型
     * @return	转化后的String对象
     */
    public static String bean2Json(Object clazz){
        Gson gson = new Gson();
        return gson.toJson(clazz);
    }
}
