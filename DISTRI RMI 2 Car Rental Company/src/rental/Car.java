package rental;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Car {

    private int id;
    private CarType type;
    private List<Reservation> reservations;

    /***************
     * CONSTRUCTOR *
     ***************/
    
    public Car(int uid, CarType type) {
    	this.id = uid;
        this.type = type;
        this.reservations = new ArrayList<Reservation>();
    }

    /******
     * ID *
     ******/
    
    public int getId() {
    	return id;
    }
    
    /************
     * CAR TYPE *
     ************/
    
    public CarType getType() {
        return type;
    }
    
    public boolean isCarType(String name) {
    	return getType().isCarTypeName(name);
    }

    /****************
     * RESERVATIONS *
     ****************/

    public boolean isAvailable(Date start, Date end) {
        if(!start.before(end))
            throw new IllegalArgumentException("Illegal given period");

        for(Reservation reservation : reservations) {
            if(reservation.getEndDate().before(start) || reservation.getStartDate().after(end))
                continue;
            return false;
        }
        return true;
    }
    
    public void addReservation(Reservation res) {
        reservations.add(res);
    }
    
    public void removeReservation(Reservation reservation) {
        // equals-method for Reservation is required!
        reservations.remove(reservation);
    }
    
    public List<Reservation> getAllReservationsBy(String carRenter) {
    	List<Reservation> reservationsBy = new ArrayList<Reservation>();
    	for (Reservation reservation : this.reservations) {
    		if (reservation.getCarRenter().equals(carRenter)) {
    			reservationsBy.add(reservation);
    		}
    	}
    	return Collections.unmodifiableList(reservationsBy);
    }
    
    public List<Reservation> getAllReservations() {
    	return Collections.unmodifiableList(this.reservations);
    }
    
    public int getNumberOfReservations() {
    	return this.reservations.size();
    }
}