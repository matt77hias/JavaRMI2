package session;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

public interface IReservationSession extends Remote {
	
	public void createQuote(ReservationConstraints constraints, String carRentalCompanyName)
			throws RemoteException, ReservationException;
	
	public List<Reservation> confirmQuotes()
			throws RemoteException, ReservationException;
	
	public Collection<Quote> getCurrentQuotes()
			throws RemoteException;
	
	public Collection<String> getAllCarRentalCompanies()
			throws RemoteException;
	
	public Map<String,Collection<CarType>> getAvailableCarTypesAtCompanies(Date start, Date end) 
			throws RemoteException;

	public String getClientName()
			throws RemoteException;

}
