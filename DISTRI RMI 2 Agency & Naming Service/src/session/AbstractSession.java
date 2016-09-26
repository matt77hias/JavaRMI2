package session;

import java.util.Date;

import namingservice.INamingService;

public class AbstractSession {
	
	protected String sessionId;
	
	protected Date creationDate;
	
	protected INamingService namingService;
	
	protected AbstractSession(INamingService namingService, String sessionId) {
		this.namingService = namingService;
		this.sessionId = sessionId;
		this.creationDate = new Date();
	}
	
	public INamingService getNamingService() {
		return this.namingService;
	}
	
	public String getSessionId() {
		return this.sessionId;
	}
	
	public Date getDate() {
		return this.creationDate;
	}
}
