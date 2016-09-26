package agency;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import session.IManagerSession;
import session.IReservationSession;
import session.ManagerSession;
import session.ReservationSession;

import namingservice.INamingService;

public class CarRentalAgency implements ICarRentalAgency {
	
	private Map<String, ReservationSession> activeReservationSessions = new HashMap<String, ReservationSession>();
	private Map<String, ManagerSession> activeManagerSessions = new HashMap<String, ManagerSession>();
	
	private final INamingService namingService;
	
	public CarRentalAgency(INamingService namingService) 
		throws IllegalArgumentException{
		
		if (namingService == null) {
			throw new IllegalArgumentException();
		}
		
		this.namingService = namingService;
	}

	@Override
	public IReservationSession getReservationSession(String sessionId, String clientName)
			throws RemoteException, IllegalArgumentException {
		
		if (sessionId == null) {
			throw new IllegalArgumentException();
		}
		IReservationSession session = this.activeReservationSessions.get(sessionId);
		
		if (session != null) {
			return session;
		} else {
			ReservationSession newSession = new ReservationSession(this.namingService, sessionId, clientName);
			this.activeReservationSessions.put(sessionId, newSession);
			return (IReservationSession) UnicastRemoteObject.exportObject(newSession, 0);
		}
	}

	@Override
	public IManagerSession getManagerSession(String sessionId)
			throws RemoteException, IllegalArgumentException {
		
		if (sessionId == null) {
			throw new IllegalArgumentException();
		}
		
		IManagerSession session = this.activeManagerSessions.get(sessionId);
		
		if (session != null) {
			return session;
		} else {
			ManagerSession newSession = new ManagerSession(this.namingService, sessionId);
			this.activeManagerSessions.put(sessionId, newSession);
			return (IManagerSession) UnicastRemoteObject.exportObject(newSession, 0);
		}
	}

	@Override
	public void terminateReservationSession(String sessionId) 
			throws RemoteException {
		synchronized (this.activeReservationSessions) {
			this.activeReservationSessions.remove(sessionId);
		}
	}
	
	@Override
	public void terminateManagerSession(String sessionId) 
			throws RemoteException {
		synchronized (this.activeManagerSessions) {
			this.activeManagerSessions.remove(sessionId);
		}
	}
	
	public void terminateInactiveReservationSessions(Date before) {
		Object[] reservationSessions = this.activeReservationSessions.values().toArray();
		for (int i=0; i<reservationSessions.length; i++) {
			ReservationSession temp = (ReservationSession) reservationSessions[i];
			if (temp.getDate().before(before)) {
				try {
					terminateReservationSession(temp.getSessionId());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void terminateInactiveManagerSessions(Date before) {
		Object[] managerSessions = this.activeManagerSessions.values().toArray();
		for (int i=0; i<managerSessions.length; i++) {
			ManagerSession temp = (ManagerSession) managerSessions[i];
			if (temp.getDate().before(before)) {
				try {
					terminateManagerSession(temp.getSessionId());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}	
}
