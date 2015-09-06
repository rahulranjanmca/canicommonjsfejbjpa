package com.canigenus.common.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Response<T> {
	
	public Response(){}
	
	public Response(boolean success, T object, Message... messages){
		this.success=success;
		this.object=object;
		this.messages.addAll(Arrays.asList(messages));
	}
	public Response(boolean success, T object, List<Message> messages){
		this.success=success;
		this.object=object;
		this.messages=messages;
	}
	
	public Response(boolean success, T object){
		this.success=success;
		this.object=object;
	}
	
	private boolean success;
	
	private T object;
	
	private List<Message> messages=new ArrayList<>();

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	

}
