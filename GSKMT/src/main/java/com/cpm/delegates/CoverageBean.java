package com.cpm.delegates;

public class CoverageBean
{
	protected int MID;
	protected String process_id;

	public String getCHECKOUT_IMG() {
		return CHECKOUT_IMG;
	}

	public void setCHECKOUT_IMG(String CHECKOUT_IMG) {
		this.CHECKOUT_IMG = CHECKOUT_IMG;
	}

	protected String CHECKOUT_IMG="";
	
	public String getProcess_id() {
		return process_id;
	}

	public void setProcess_id(String process_id) {
		this.process_id = process_id;
	}

	protected String storeId;
	protected String storename;
	
	public String getStorename() {
		return storename;
	}

	public void setStorename(String storename) {
		this.storename = storename;
	}

	protected String Remark;
	
	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	protected String userId;
	protected String app_version;
	protected String image_allow;
	
	public String getImage_allow() {
		return image_allow;
	}

	public void setImage_allow(String image_allow) {
		this.image_allow = image_allow;
	}

	public String getApp_version() {
		return app_version;
	}

	public void setApp_version(String app_version) {
		this.app_version = app_version;
	}

	protected String inTime;
	
	protected String outTime;
	
	protected String visitDate;
	
	protected String keycontactId;
	
	protected String isdDeploy;
	
	protected String uploadStatus;
	
	private String latitude;
	
	private String longitude;
	
	private String reasonid="";
	
	private String sub_reasonId="";
	
	public String getSub_reasonId() {
		return sub_reasonId;
	}

	public void setSub_reasonId(String sub_reasonId) {
		this.sub_reasonId = sub_reasonId;
	}

	private String reason="";
	
	private String status="N";
	
	private String image="";
	

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getMID() {
		return MID;
	}

	public void setMID(int mID) {
		MID = mID;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getInTime() {
		return inTime;
	}

	public void setInTime(String inTime) {
		this.inTime = inTime;
	}

	public String getOutTime() {
		return outTime;
	}

	public void setOutTime(String outTime) {
		this.outTime = outTime;
	}

	public String getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(String visitDate) {
		this.visitDate = visitDate;
	}

	public String getKeycontactId() {
		return keycontactId;
	}

	public void setKeycontactId(String keycontactId) {
		this.keycontactId = keycontactId;
	}

	public String getIsdDeploy() {
		return isdDeploy;
	}

	public void setIsdDeploy(String isdDeploy) {
		this.isdDeploy = isdDeploy;
	}

	public String getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(String uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getReasonid() {
		return reasonid;
	}

	public void setReasonid(String reasonid) {
		this.reasonid = reasonid;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	
	
}
