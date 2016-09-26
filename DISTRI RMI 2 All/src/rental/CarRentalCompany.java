package rental;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CarRentalCompany implements ICarRentalCompany {

	private static Logger logger = Logger.getLogger(CarRentalCompany.class.getName());
	
	private String name;
	private List<Car> cars;
	private Map<String,CarType> carTypes = new HashMap<String, CarType>();

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public CarRentalCompany(String name, List<Car> cars) {
		logger.log(Level.INFO, "<{0}> Car Rental Company {0} starting up...", name);
		setName(name);
		this.cars = cars;
		for(Car car:cars)
			carTypes.put(car.getType().getName(), car.getType());
	}

	@Override
	public String getName()
			throws RemoteException {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	@Override
	public Collection<CarType> getAllCarTypes()
			throws RemoteException {
		return carTypes.values();
	}
	
	@Override
	public CarType getCarType(String carTypeName)
			throws RemoteException {
		if(carTypes.containsKey(carTypeName))
			return carTypes.get(carTypeName);
		throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
	}
	
	@Override
	public boolean isAvailable(String carTypeName, Date start, Date end)
			throws RemoteException {
		logger.log(Level.INFO, "<{0}> Checking availability for car type {1}", new Object[]{name, carTypeName});
		if(carTypes.containsKey(carTypeName))
			return getAvailableCarTypes(start, end).contains(carTypes.get(carTypeName));
		throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
	}
	
	@Override
	public Set<CarType> getAvailableCarTypes(Date start, Date end)
			throws RemoteException {
		Set<CarType> availableCarTypes = new HashSet<CarType>();
		for (Car car : cars) {
			if (car.isAvailable(start, end)) {
				availableCarTypes.add(car.getType());
			}
		}
		return availableCarTypes;
	}
	
	/*********
	 * CARS *
	 *********/
	
	private Car getCar(int uid) {
		for (Car car : cars) {
			if (car.getId() == uid)
				return car;
		}
		throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
	}
	
	private List<Car> getAvailableCars(String carType, Date start, Date end) {
		List<Car> availableCars = new LinkedList<Car>();
		for (Car car : cars) {
			if (car.getType().getName().equals(carType) && car.isAvailable(start, end)) {
				availableCars.add(car);
			}
		}
		return availableCars;
	}

	@Override
	public Quote createQuote(ReservationConstraints constraints, String client)
			throws ReservationException, RemoteException {
		logger.log(Level.INFO, "<{0}> Creating tentative reservation for {1} with constraints {2}", 
                        new Object[]{name, client, constraints.toString()});
		
		CarType type = getCarType(constraints.getCarType());
		
		if(!isAvailable(constraints.getCarType(), constraints.getStartDate(), constraints.getEndDate()))
			throw new ReservationException("<" + name
				+ "> No cars available to satisfy the given constraints.");
		
		double price = calculateRentalPrice(type.getRentalPricePerDay(),constraints.getStartDate(), constraints.getEndDate());
		
		return new Quote(client, constraints.getStartDate(), constraints.getEndDate(), getName(), constraints.getCarType(), price);
	}

	// Implementation can be subject to different pricing strategies
	private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
		return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime())
						/ (1000 * 60 * 60 * 24D));
	}

	@Override
	public boolean canConfirmQuote(Quote quote) throws RemoteException {
		List<Car> availableCars = getAvailableCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate());
        return !availableCars.isEmpty();
	}

	@Override
	public synchronized Reservation confirmQuote(Quote quote) 
			throws ReservationException, RemoteException {
		logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[]{name, quote.toString()});
		List<Car> availableCars = getAvailableCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate());
		if(availableCars.isEmpty())
			throw new ReservationException("Reservation failed, all cars of type " + quote.getCarType()
	                + " are unavailable from " + quote.getStartDate() + " to " + quote.getEndDate());
		Car car = availableCars.get((int)(Math.random()*availableCars.size()));
		
		Reservation res = new Reservation(quote, car.getId());
		car.addReservation(res);
		return res;
	}

	@Override
	public synchronized void cancelReservation(Reservation res) 
			throws RemoteException {
		logger.log(Level.INFO, "<{0}> Cancelling reservation {1}", new Object[]{name, res.toString()});
		getCar(res.getCarId()).removeReservation(res);
	}
	
	@Override
	public List<Reservation> getReservationsBy(String clientName)
			throws RemoteException {
		
		List<Reservation> reservations = new ArrayList<Reservation>();
		for (Car car : this.cars) {
			reservations.addAll(car.getAllReservationsBy(clientName));
		}
		return reservations;
	}
	
	@Override
	public int getNumberOfReservationsForCarType(String carType)
			throws RemoteException {
		
		int number = 0;
		for (Car car : this.cars) {
			if (car.isCarType(carType)) {
				number += car.getNumberOfReservations();
			}
		}
		return number;
	}
	
	@Override
	public int getTotalNumberOfReservations()
			throws RemoteException {
		int totalNumberOfReservations = 0;
		for (CarType carType : getAllCarTypes()) {
			totalNumberOfReservations += getNumberOfReservationsForCarType(carType.getName());
		}
		return totalNumberOfReservations;
	}
	
	@Override
	public Map<String, Integer> getNumberOfReservationsPerCarType()
			throws RemoteException {
		Map<String, Integer> numberOfReservationsPerCarType = new HashMap<String, Integer>();
		for (CarType carType : getAllCarTypes()) {
			numberOfReservationsPerCarType.put(carType.getName(),
					getNumberOfReservationsForCarType(carType.getName()));
		}
		return numberOfReservationsPerCarType;
	}
	
	@Override
	public CarType getMostPopularCarType()
			throws RemoteException {
		return Collections.max(getAllCarTypes(),
				new Comparator<CarType>() {
					@Override
					public int compare(CarType o1, CarType o2) {
						try {
							return Integer.compare(CarRentalCompany.this.getNumberOfReservationsForCarType(o1.getName()),
									CarRentalCompany.this.getNumberOfReservationsForCarType(o2.getName()));
						} catch (RemoteException e) {
							throw new IllegalArgumentException(e);
						}
					}
				});
	}
}