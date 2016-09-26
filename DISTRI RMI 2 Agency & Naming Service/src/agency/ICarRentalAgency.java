package agency;

import java.rmi.Remote;
import java.rmi.RemoteException;

import session.IManagerSession;
import session.IReservationSession;

public interface ICarRentalAgency extends Remote {
	
	public IReservationSession getReservationSession(String sessionId,
			String clientName) throws RemoteException, NullPointerException;
	
	public IManagerSession getManagerSession(String sessionId)
			throws RemoteException;
	
	public void terminateReservationSession(String sessionId)
			throws RemoteException;
	
	public void terminateManagerSession(String sessionId)
			throws RemoteException;
}
