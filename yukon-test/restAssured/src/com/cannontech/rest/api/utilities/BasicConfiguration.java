package com.cannontech.rest.api.utilities;

public class BasicConfiguration {

	
	private String configPropertyFilePath;
	private String endpoint;
	private String TestLogPath;
	private String log4jPath;
	
	private static BasicConfiguration basicConfiguraiton;

	private BasicConfiguration(){}
	/**
	 * @return singleton instance of Environment Configuration class
	 */
	public static BasicConfiguration getInstance(){
		if(basicConfiguraiton==null){
			basicConfiguraiton= new BasicConfiguration();
		}
		return basicConfiguraiton;
	}
	
	
	public String getconfigPropertyFilePath() {
		return configPropertyFilePath;
	}
	public void setconfigPropertyFilePath(String configPropertyFilePath) {
		this.configPropertyFilePath = configPropertyFilePath;
	}


	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}


	public String getTestLogPath() {
		return TestLogPath;
	}

	public void setTestLogPath(String TestLogPath) {
		this.TestLogPath = TestLogPath;
	}
	
	public String getlog4jPath() {
		return log4jPath;
	}

	public void setlog4jPath(String log4jPath) {
		this.log4jPath = log4jPath;
	}
	
}
