package session;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;

import rental.CarType;
import rental.ICarRentalCompany;

public interface IManagerSession extends Remote {

	public Collection<CarType> getCarTypes(String carRentalCompanyName) 
			throws RemoteException, IllegalArgumentException;
	
	public Map<String, Integer> getNumberOfReservationsAtCompany(String carRentalCompanyName)
			throws RemoteException, IllegalArgumentException;
	
	public int getNumberOfReservationsByClient(String clientName)
			throws RemoteException;
	
	public void registerCarRentalCompany(String carRentalCompanyName, ICarRentalCompany carRentalCompany)
			throws RemoteException;
	
	public void unregisterCarRentalCompany(String carRentalCompanyName)
			throws RemoteException, IllegalArgumentException;

	public String getMostPopularCarRentalCompany()
			throws RemoteException;

	public CarType getMostPopularCarTypeIn(String carRentalCompanyName)
			throws RemoteException;
}
