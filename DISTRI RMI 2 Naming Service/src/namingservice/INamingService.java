package namingservice;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

import rental.ICarRentalCompany;

public interface INamingService extends Remote {
	
	public void register(String carRentalCompanyName, ICarRentalCompany carRentalCompany)
		throws RemoteException;
	
	public void unregister(String carRentalCompanyName)
			throws RemoteException, IllegalArgumentException;
	
	public Collection<String> getAllRegisteredCarRentalCompanyNames()
		throws RemoteException;
	
	public Collection<ICarRentalCompany> getAllRegisteredCarRentalCompanies()
		throws RemoteException;
	
	public ICarRentalCompany getRegisteredCompany(String carRentalCompanyName)
		throws RemoteException, IllegalArgumentException;
	
	

}
