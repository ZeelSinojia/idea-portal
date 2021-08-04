package com.ideaportal.models;

public class ResponseMessage<T> 
{
	private int status;				//Status Code of the response
	private String statusText;		//Status Description of the response
	private T result;				//Result of the request
    private String token;			//JWT Token
    private int totalElements;		//Elements in a list (if a list is passed as a response
    private Object object;

	public ResponseMessage(int status, String statusText, T result) {
		super();
		this.status = status;
		this.statusText = statusText;
		this.result = result;
	}

	public ResponseMessage(int status, String statusText) {
		super();
		this.status = status;
		this.statusText = statusText;
	}

	   
	public ResponseMessage(int status, String statusText, T result, String token) {
		super();
		this.status = status;
		this.statusText = statusText;
		this.result = result;
		this.token = token;
	}
	
	public ResponseMessage(int status, String statusText, T result, String token, Object object) {
		super();
		this.status = status;
		this.statusText = statusText;
		this.result = result;
		this.token = token;
		this.object = object;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public ResponseMessage() {
		super();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public int getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(int totalElements) {
		this.totalElements = totalElements;
	}

	@Override
	public String toString() {
		return "ResponseMessage [status=" + status + ", statusText=" + statusText + ", result=" + result + "]";
	}
}
