package com.pcm.mina.service.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 聊天格式
 *
 * 	key:"", //handle处理类型
 * 	data:{
 * 		// key为client_message情况
 * 	    "sender";account,
 * 	    "receiver":account,
 * 	    "message":message,
 *
 * 	    // key为client_bind 或者 client_push 情况
 * 	    "message":message，
 * 	    "account":account,
 *
 * 	    //client_closs
 * 	}
 * 	message:"", //返回信息
 *  code:"", //返回码
 *	timestamp:"",//消息时间戳
 *
 * @author LVQIU
 * @Description 服务端接收消息对象
 */
public class IMPackage implements Serializable {

	private static final long serialVersionUID = 134512147711L;
	/**
	 * handle类型
	 */
	private String key;
	/**
	 * 返回码
	 */
	private String code;
	/**
	 * 返回说明
	 */
	private String message;
	/**
	 * 数据内容
	 */
	private HashMap<String, String> data;
	/**
	 * 时间戳
	 */
	private long timestamp;


	public IMPackage() {

		data = new HashMap<String, String>();
		timestamp = System.currentTimeMillis();
	}

	public String getKey() {
		return key;
	}

	public String get(String k) {
		return data.get(k);
	}

	public void put(String k, String v) {
		data.put(k, v);
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setKey(String key) {
		this.key = key;
	}


	public void remove(String k) {
		data.remove(k);
	}

	public HashMap<String, String> getData() {
		return data;
	}

	public void setData(HashMap<String, String> data) {
		this.data = data;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("<sent>");
		buffer.append("<code>").append(code).append("</code>");
		buffer.append("<message>").append(message).append("</message>");
		buffer.append("<key>").append(key).append("</key>");
		buffer.append("<timestamp>").append(timestamp).append("</timestamp>");
		buffer.append("<data>");
		for (String key : data.keySet()) {
			buffer.append("<" + key + ">").append(data.get(key)).append(
					"</" + key + ">");
		}
		buffer.append("</data>");
		buffer.append("</sent>");
		return buffer.toString();
	}

	public String toXmlString() {
		return toString();
	}
}
