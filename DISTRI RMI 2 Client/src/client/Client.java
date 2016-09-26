package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import agency.ICarRentalAgency;
import rental.Car;
import rental.CarType;
import rental.ICarRentalCompany;
import rental.RentalServer;
import rental.Reservation;
import rental.ReservationConstraints;
import session.IManagerSession;
import session.IReservationSession;

public class Client extends AbstractScriptedTripTest<IReservationSession, IManagerSession> {
	
	/********
	 * MAIN *
	 ********/
	
	public static void main(String[] args) throws Exception {
		ICarRentalAgency agency = clientSetup("localhost", "agency");
		Client client = new Client("simpleTrips", agency);
		
		List<Car> cars = RentalServer.loadData("hertz.csv");
		ICarRentalCompany company1 = RentalServer.serverSetUp("Hertz", cars);
		cars = RentalServer.loadData("dockx.csv");
		ICarRentalCompany company2 = RentalServer.serverSetUp("Dockx", cars);
		RentalServer.main(new String[] {});
		IManagerSession ms = client.getNewManagerSession("manager");
		ms.registerCarRentalCompany("Hertz", company1);
		ms.registerCarRentalCompany("Dockx", company2);
		
		client.run();
	}
	
	public static ICarRentalAgency clientSetup(String host, String agencyName) {
		
		System.setSecurityManager(null);
		
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			ICarRentalAgency carRentalAgency = (ICarRentalAgency) registry.lookup(agencyName);
			return carRentalAgency;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/***************
	 * CONSTRUCTOR *
	 ***************/
	
	private ICarRentalAgency carRentalAgency;

	public Client(String scriptFile, ICarRentalAgency carRentalAgency) {
		super(scriptFile);
		this.carRentalAgency = carRentalAgency;
	}

	@Override
	protected IReservationSession getNewReservationSession(String name)
			throws Exception {
		return carRentalAgency.getReservationSession("reservation_" + name, name);
	}

	@Override
	protected IManagerSession getNewManagerSession(String name)
			throws Exception {
		return carRentalAgency.getManagerSession("manager_" + name);
	}

	@Override
	protected void checkForAvailableCarTypes(IReservationSession session,
			Date start, Date end) throws Exception {
		Map<String, Collection<CarType>> availableCarTypesPerCompany = session.getAvailableCarTypesAtCompanies(start, end);
        System.out.println("Checking for available types...");
        for (String company : availableCarTypesPerCompany.keySet()) {
            System.out.println("company: " + company);
            for (CarType carType : availableCarTypesPerCompany.get(company)) {
                System.out.println("- type: " + carType.getName());
            }
        }
	}

	@Override
	protected void addQuoteToSession(IReservationSession session, Date start,
			Date end, String carType, String carRentalName) throws Exception {
		ReservationConstraints reservationConstraints = new ReservationConstraints(start, end, carType);
		session.createQuote(reservationConstraints, carRentalName);
	}

	@Override
	protected List<Reservation> confirmQuotes(IReservationSession session)
			throws Exception {
		return session.confirmQuotes();
	}

	@Override
	protected int getNumberOfReservationsBy(IManagerSession ms,
			String clientName) throws Exception {
		return ms.getNumberOfReservationsByClient(clientName);
	}

	@Override
	protected int getNumberOfReservationsForCarType(IManagerSession ms,
			String carRentalCompanyName, String carType) throws Exception {
		return ms.getNumberOfReservationsAtCompany(carRentalCompanyName).get(carType);
	}

	@Override
	protected String getMostPopularCarRentalCompany(IManagerSession ms)
			throws Exception {
		return ms.getMostPopularCarRentalCompany();
	}

	@Override
	protected CarType getMostPopularCarTypeIn(IManagerSession ms,
			String carRentalCompanyName) throws Exception {
		return ms.getMostPopularCarTypeIn(carRentalCompanyName);
	}

}
