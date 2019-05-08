package com.novarise.webase.util;

public class HttpPostModel {

	private int responseCode;
	private String responseString;
	
	/**
	 * 设置HTTP状态值，200表请求成功
	 * @param responseCode
	 */
	public void set_ResponseCode(int responseCode){
		this.responseCode=responseCode;
	}
	
	/**
	 * 获取HTTP状态值，200表请求成功
	 * @return
	 */
	public int get_ResonseCode(){
		return this.responseCode;
	}
	
	/**
	 * 设置HTTP返回值
	 * @param responseString
	 */
	public void set_ResponseString(String responseString){
		this.responseString=responseString;
	}
	
	/**
	 * 获取HTTP返回值
	 * @return
	 */
	public String get_ResponseString(){
		return this.responseString;
	}
	
}