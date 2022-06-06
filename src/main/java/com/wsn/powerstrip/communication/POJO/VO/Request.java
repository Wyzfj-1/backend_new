package com.wsn.powerstrip.communication.POJO.VO;

/**
 * 
 *
 * @author liufenglin 
 * @email liufenglin@163.com
 * @date 2018年7月12日
 * @version 
 */
public class Request {
	private String appId;
	
	private String secretKey;
	
	private String mobiles;
	
	private String content;
	
	private String digest;

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String edited;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getMobiles() {
		return mobiles;
	}

	public void setMobiles(String mobiles) {
		this.mobiles = mobiles;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getEdited() {
		return edited;
	}

	public void setEdited(String edited) {
		this.edited = edited;
	}

	@Override
	public String toString() {
		return "Request [appId=" + appId + ", secretKey=" + secretKey + ", mobiles=" + mobiles + ", content=" + content
				+ ", digest=" + digest + ", edited=" + edited + "]";
	}

	
}
