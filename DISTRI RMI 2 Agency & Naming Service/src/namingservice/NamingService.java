package namingservice;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import rental.ICarRentalCompany;

public class NamingService implements INamingService {
	
	public NamingService() {
		
	}
	
	Map<String, ICarRentalCompany> registeredCarRentalCompanies = new HashMap<String, ICarRentalCompany>();

	@Override
	public synchronized void register(String carRentalCompanyName, ICarRentalCompany carRentalCompany) 
			throws RemoteException {
		registeredCarRentalCompanies.put(carRentalCompanyName, carRentalCompany);
	}

	@Override
	public synchronized void unregister(String carRentalCompanyName)
			throws RemoteException, IllegalArgumentException {
		checkIfCompanyExists(carRentalCompanyName);
		registeredCarRentalCompanies.remove(carRentalCompanyName);
	}

	// copying the map lists into new lists is necessary because those are not serializable
	
	@Override
	public Collection<ICarRentalCompany> getAllRegisteredCarRentalCompanies()
			throws RemoteException {
		return new ArrayList<ICarRentalCompany>(registeredCarRentalCompanies.values());
	}

	@Override
	public Collection<String> getAllRegisteredCarRentalCompanyNames()
			throws RemoteException {
		return new ArrayList<String>(registeredCarRentalCompanies.keySet());
	}

	@Override
	public ICarRentalCompany getRegisteredCompany(String carRentalCompanyName)
			throws RemoteException, IllegalArgumentException {
		checkIfCompanyExists(carRentalCompanyName);
		return registeredCarRentalCompanies.get(carRentalCompanyName);
	}
	
	private void checkIfCompanyExists(String carRentalCompanyName)
			throws IllegalArgumentException{
		if (!registeredCarRentalCompanies.containsKey(carRentalCompanyName))
			throw new IllegalArgumentException("Company " + carRentalCompanyName + " does not exist.");
	}

}
