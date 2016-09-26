package session;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import namingservice.INamingService;
import rental.CarType;
import rental.ICarRentalCompany;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

public class ReservationSession extends AbstractSession implements IReservationSession {
	
	private Collection<Quote> currentQuotes = new ArrayList<Quote>();
	private String clientName;
	
	public ReservationSession(INamingService namingService, String sessionId, String clientName) {
		super(namingService, sessionId);
		this.clientName = clientName;
	}
	
	@Override
	public String getClientName() 
			throws RemoteException {
		return clientName;
	}

	@Override
	public void createQuote(ReservationConstraints constraints, String carRentalCompanyName) 
			throws RemoteException, ReservationException {
		ICarRentalCompany carRentalCompany = namingService.getRegisteredCompany(carRentalCompanyName);
		Quote quote = carRentalCompany.createQuote(constraints, this.clientName);
		currentQuotes.add(quote);
	}

    private List<Reservation> confirmedQuotesAsReservations = new ArrayList<Reservation>();
    
    @Override
    public List<Reservation> confirmQuotes() throws ReservationException, RemoteException {
        confirmedQuotesAsReservations.clear();
        
        for (Quote quote : currentQuotes) {
            confirmQuote(quote);
        }
        currentQuotes.clear();
        return confirmedQuotesAsReservations;
    }
    
    private void confirmQuote(Quote quote) throws ReservationException, RemoteException {
        String carRentalCompanyName = quote.getRentalCompany();
        ICarRentalCompany carRentalCompany = namingService.getRegisteredCompany(carRentalCompanyName);
        if (carRentalCompany.canConfirmQuote(quote)) {
            confirmQuoteAsReservation(quote, carRentalCompany);
        } else {
            cancelReservations();
            currentQuotes.clear();
            throw new ReservationException("Quotes could not be confirmed due to conflict found on quote " + quote.toString());
        }
    }
    
    private void confirmQuoteAsReservation(Quote quote, ICarRentalCompany carRentalCompany) throws ReservationException, RemoteException {
        Reservation reservation = carRentalCompany.confirmQuote(quote);
        confirmedQuotesAsReservations.add(reservation);
    }

    private void cancelReservations() throws RemoteException {
        for (Reservation reservation : confirmedQuotesAsReservations) {
            String carRentalCompanyName = reservation.getRentalCompany();
            ICarRentalCompany carRentalCompany = namingService.getRegisteredCompany(carRentalCompanyName);
            carRentalCompany.cancelReservation(reservation);
        }
    }

	@Override
	public Collection<Quote> getCurrentQuotes() throws RemoteException {
		return currentQuotes;
	}

	@Override
	public Collection<String> getAllCarRentalCompanies() throws RemoteException {
		return namingService.getAllRegisteredCarRentalCompanyNames();
	}

    @Override
    public Map<String,Collection<CarType>> getAvailableCarTypesAtCompanies(Date start, Date end) throws RemoteException  {
        Map<String, Collection<CarType>> availableCarTypesPerCompany = new HashMap<String, Collection<CarType>>();
        
        
        Iterator<ICarRentalCompany> carRentalCompanyIterator = namingService.getAllRegisteredCarRentalCompanies().iterator();
        while (carRentalCompanyIterator.hasNext()) {
            ICarRentalCompany carRentalCompany = carRentalCompanyIterator.next();
            Collection<CarType> availableCarTypeAtCompany = getAvailableCarTypesAtCompany(start, end, carRentalCompany);
            availableCarTypesPerCompany.put(carRentalCompany.getName(), availableCarTypeAtCompany);
        }
        
        return availableCarTypesPerCompany;
    }
    
    private Collection<CarType> getAvailableCarTypesAtCompany(Date start, Date end, ICarRentalCompany carRentalCompany) throws RemoteException {
        return carRentalCompany.getAvailableCarTypes(start, end);
    }

}
