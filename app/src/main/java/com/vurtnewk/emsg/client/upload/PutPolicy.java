package com.vurtnewk.emsg.client.upload;


public class PutPolicy {
	private String scope = "emsg";
	
	private long deadline;
	
	private int insertOnly = 0;
	
	private String saveKey;
	
	private String endUser;
	
	private String returnUrl;
	
	private String returnBody;
	
	private String callbackUrl;
	
	private String callbackBody;
	
	private String persistentOps;
	
	private String persistentNotifyUrl;
	
	private String persistentPipeline;
	
	private long fsizeLimit = 0;
	
	private int detectMime = 0;
	
	private String mimeLimit;

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public long getDeadline() {
		return deadline;
	}

	public void setDeadline(long deadline) {
		this.deadline = deadline;
	}

	public int getInsertOnly() {
		return insertOnly;
	}

	public void setInsertOnly(int insertOnly) {
		this.insertOnly = insertOnly;
	}

	public String getSaveKey() {
		return saveKey;
	}

	public void setSaveKey(String saveKey) {
		this.saveKey = saveKey;
	}

	public String getEndUser() {
		return endUser;
	}

	public void setEndUser(String endUser) {
		this.endUser = endUser;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getReturnBody() {
		return returnBody;
	}

	public void setReturnBody(String returnBody) {
		this.returnBody = returnBody;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getCallbackBody() {
		return callbackBody;
	}

	public void setCallbackBody(String callbackBody) {
		this.callbackBody = callbackBody;
	}

	public String getPersistentOps() {
		return persistentOps;
	}

	public void setPersistentOps(String persistentOps) {
		this.persistentOps = persistentOps;
	}

	public String getPersistentNotifyUrl() {
		return persistentNotifyUrl;
	}

	public void setPersistentNotifyUrl(String persistentNotifyUrl) {
		this.persistentNotifyUrl = persistentNotifyUrl;
	}

	public String getPersistentPipeline() {
		return persistentPipeline;
	}

	public void setPersistentPipeline(String persistentPipeline) {
		this.persistentPipeline = persistentPipeline;
	}

	public long getFsizeLimit() {
		return fsizeLimit;
	}

	public void setFsizeLimit(long fsizeLimit) {
		this.fsizeLimit = fsizeLimit;
	}

	public int getDetectMime() {
		return detectMime;
	}

	public void setDetectMime(int detectMime) {
		this.detectMime = detectMime;
	}

	public String getMimeLimit() {
		return mimeLimit;
	}

	public void setMimeLimit(String mimeLimit) {
		this.mimeLimit = mimeLimit;
	}

}
