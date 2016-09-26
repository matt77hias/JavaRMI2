package session;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import namingservice.INamingService;
import rental.CarType;
import rental.ICarRentalCompany;

public class ManagerSession extends AbstractSession implements IManagerSession {
	
	public ManagerSession(INamingService namingService, String sessionId) {
		super(namingService, sessionId);
	}

	@Override
	public Collection<CarType> getCarTypes(String carRentalCompanyName)
			throws RemoteException, IllegalArgumentException {
		ICarRentalCompany carRentalCompany = namingService.getRegisteredCompany(carRentalCompanyName);
		return new ArrayList<CarType>(carRentalCompany.getAllCarTypes());
	}

	@Override
	public Map<String, Integer> getNumberOfReservationsAtCompany(
			String carRentalCompanyName)
					throws RemoteException, IllegalArgumentException {
		ICarRentalCompany carRentalCompany = namingService.getRegisteredCompany(carRentalCompanyName);
		return carRentalCompany.getNumberOfReservationsPerCarType();
	}

	@Override
	public int getNumberOfReservationsByClient(String clientName)
			throws RemoteException {
		int totalNumberOfReservationsByClient = 0;
		for (ICarRentalCompany carRentalCompany : namingService.getAllRegisteredCarRentalCompanies()) {
			totalNumberOfReservationsByClient += carRentalCompany.getReservationsBy(clientName).size();
		}
		return totalNumberOfReservationsByClient;
	}

	@Override
	public String getMostPopularCarRentalCompany() throws RemoteException {
		ICarRentalCompany mostPopularCompany = Collections.max(namingService.getAllRegisteredCarRentalCompanies()
				, new Comparator<ICarRentalCompany>() {

			@Override
			public int compare(ICarRentalCompany o1, ICarRentalCompany o2) {
				try {
					return Integer.compare(o1.getTotalNumberOfReservations(), o2.getTotalNumberOfReservations());
				} catch (RemoteException e) {
					throw new IllegalArgumentException(e);
				}
			}
		});
		return mostPopularCompany.getName();
	}

	@Override
	public void registerCarRentalCompany(String carRentalCompanyName,
			ICarRentalCompany carRentalCompany) throws RemoteException {
		namingService.register(carRentalCompanyName, carRentalCompany);
	}

	@Override
	public void unregisterCarRentalCompany(String carRentalCompanyName)
			throws RemoteException, IllegalArgumentException {
		namingService.unregister(carRentalCompanyName);
	}
	
	@Override
	public CarType getMostPopularCarTypeIn(String carRentalCompanyName) throws RemoteException {
		ICarRentalCompany carRentalCompany = namingService.getRegisteredCompany(carRentalCompanyName);
		return carRentalCompany.getMostPopularCarType();
	}
}
