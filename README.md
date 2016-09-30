# JavaRMI2
Course Distributed Systems: Java RMI 2

Team:
* [Matthias Moulin](https://github.com/matt77hias) (Computer Science)
* [Ruben Pieters](https://github.com/rubenpieters) (Computer Science)

# Design
## Remotely accessible classes
This section describes which classes are declared remotely, or stated more formally, the classes that implement the interface (`java.rmi.remote`). Instances of remotely accessible classes are never returned by value, but always by remote reference. This means that accessing and modifying a remote instance does not result in an inconsistent clone of the remote instance. The following interfaces (and as result all the implementing classes) in our project(s) extend the `java.rmi.remote` interface:

* `ICarRentalAgency`: This is an interface for accessing the car rental agency. A client uses the agency to request a session;
*	`IReservationSession`: This is an interface for accessing a reservation session. A reservation session manages mainly the creating and confirming of quotes;
*	`IManagerSession`: This is an interface for accessing a manager session. The manager session allows the registering and unregistering of car rental companies. In addition it provides methods to request data from the car rental companies;
*	`INamingService`: This is an interface for accessing the naming service. The naming service is used to register and unregister car rental companies (remote instances) and to find a specific car rental company by name (remote instance);
*	`ICarRentalCompany`: This is an interface for accessing a particular car rental company. A reservation session uses a car rental company for making quotes and reservations. A manager session uses a car rental company for gathering statistics and registering and unregistering the car rental company to the naming service of the agency.

We prefer to work with interfaces, so that when creating the different .jar files for the client project and different server projects (because they need the interface definition of their remotely accessible classes), the actual implementation is hidden.
The implementing classes need to be remotely accessible because they are located on a different Virtual Machine (and probably also physical machine) than the ‘clients’ of those classes. Secondly, no return by value of instances of these classes is allowed. If this would be the case, the effect of a method invocation on such an instance would only have a local effect (on the clone of the remote instance) and not an effect on the actual remote instance, which results in a useless method invocation in the context of a rental service.

##Serializable classes
This section describes which classes are serializable, or stated more formally, the classes that implement the interface (`java.io.serializable`). Instances of serializable classes are returned by value. This means that accessing and modifying a serializable instance (retrieved from a method invocation on some remote instance) results in an inconsistent clone regarding to the original serializable instance (the one returned) located at some other Virtual Machine. The following classes (beside the `Java` primitives, `String` class and some used collections) in our project implement the `java.io.serializable` interface:

*	`Quote` (and so `Reservation`): This is a class representing a quote (not confirmed reservation).
* `ReservationConstraints`: The constraints for a reservation;
*	`ReservationException`: This is an exception that is thrown when some conflict or problem with a not confirmed reservation occurs during the processing for a reservation session;
*	`CarType`: This is a class representing a car type.

We want these classes to be serializable, because clients are allowed to view and modify all the properties of these classes, but aren’t allowed to modify the properties of the (original) instances stored at the car rental servers. An instance of a `ReservationConstraints` is created by the client. We don’t want the agency / session server to access a remote instance at the client’s side and to obtain its properties via remote method invocations for performance reasons. This means that the `ReservationConstraints` class may not implement the `java.rmi.remote` interface. So we sent a clone. When a `ReservationException` is thrown by a car rental server, we don’t want the catcher at some other server to access the car rental server for the properties of the `Exception` for performance reasons. This means that the `ReservationException` class may not implement the `java.rmi.remote` interface. So we sent a clone. The same arguments hold for the `Quote`, `Reservation` and `CarType` classes. The general idea is: we give you a clone of some (data) instance so that you can query the instance when you want and how you want, but without modifying the original instance and without keeping a server busy.

##The registration of remote objects via the built-in RMI registry
The agency server and naming service are registered via the built-in RMI registry. The agency server has to look-up the naming service. Car rental companies are registered in the naming service via a manager session obtained from the agency server. Clients and managers have to look-up the agency server in order to start a session.

##The location of the remote objects
Each car rental company (‘Dockx’ and ‘Hertz’) runs on a different server. Each `CarRentalCompany` object is hosted at some server. It is also possible for hosting different `CarRentalCompany` objects at the same server. The NamingService object is hosted at a naming server. The `CarRentalAgency` object is hosted at a server. All the `ManagerSession` and `ReservationSession` objects are also hosted at the server of the `CarRentalAgency` object.
We could also use a separate session server(s) for hosting all the `ManagerSession` and `ReservationSession` objects in order to decrease to load on the agency (proxy) server. This depends on the actual context in which the application will be deployed.

##Life cycle management of sessions
The agency server stores reservation and manager sessions in a map with their `sessionID` as key. If a client wants to start a new session, he gives a `sessionID` to the agency server with the request. If the `sessionID` exists, the agency server returns a remote reference to the existing session to the client. Otherwise, the agency server creates a new session, stores the new session and returns a remote reference to that new session to the client.

Every session, that is created, holds its date of creation. The system administrator of the agency server could delete all sessions created before some given date. The dates of sessions are not updated if a client reuses an existing session. For the purpose of a car rental agency, it does not make sense that a client can keep his session alive with intermediate periods of inactivation.

Clients have to remember their `sessionID` if they want to restart an existing session. There are no security features implemented for preventing clients and managers to access an existing session that is not theirs.

##Synchronisation
All remotely and concurrently accessible mutator-methods of the `NamingService` (NS), `CarRentalAgency` (CA) and `CarRentalCompany` (CRC) class are synchronized:

* NS: `synchronized void register(String carRentalCompanyName, ICarRentalCompany carRentalCompany)`: registering a car rental company to the naming service.
*	NS: `synchronized void unregister(String carRentalCompanyName)`: unregistering a car rental company to the naming service.
*	CA: The map for active reservation sessions and active manager sessions can only be accessed by one thread while removing a session. A client cannot access a session, while the agency system administrator is deleting that session.
*	CRC: `synchronized Reservation confirmQuote(Quote quote)`
*	CRC: `synchronized void cancelReservation(Reservation res)`

##UML Class Diagram

<p align="center"><img src="https://github.com/matt77hias/JavaRMI2/blob/master/res/UML Class Diagram.jpg" ></p>

UML Class Diagram of all the important classes of the project(s). The `Quote`, `Reservation`, `CarType`, `Car`, `ReservationConstraints`, `ReservationException` classes are not represented, because they are trivial. The classes that contain the main methods for each server are also not represented. The `AbstractClient` class and the methods of the extending `Client` class are not represented as well.

##UML Deployment Diagram

<p align="center"><img src="https://github.com/matt77hias/JavaRMI2/blob/master/res/UML%20Deployment Diagram.jpg" ></p>

##UML Sequence Diagram

<p align="center"><img src="https://github.com/matt77hias/JavaRMI2/blob/master/res/UML Sequence Diagram.jpg" ></p>

###Scenarios
* 1 The car renter starts a new session.
* 2 The car renter wants to reserve cars at two car rental companies: A and B:
  *  (a) The car renter checks the availability of a car type at car rental company A and notices
that there is a car available.
  *  (b) The car renter creates a quote at car rental company A.
  *  (c) The car renter checks the availability of a car type at car rental company B and notices
that there is a car available.
  *  The car renter creates a quote at car rental company B.
* 3A Alternative A: reservation succeeds
The car renter wants to confirm these quotes and closes the session:
  *  (a) The session confirms the quote at car rental company A.
  * (b) The session confirms the quote at car rental company B.
* 4A The reservation succeeded.
* 3B Alternative B: reservation fails
The car renter wants to finalize these bookings and closes the session:
  * (a) The session confirms the quote at car rental company A.
  * (b) The session confirms the quote at car rental company B, but an error occurs: no car of
the required car type is available any more.
  * (c) The session cancels the reservation at car rental company A.
* 4B The reservation failed, the car renter is notified of this failure.
