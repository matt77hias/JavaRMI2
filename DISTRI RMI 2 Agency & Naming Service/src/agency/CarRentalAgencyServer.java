package agency;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import namingservice.INamingService;

public class CarRentalAgencyServer {
	
	public static void main(String[] args) {
		serverSetUp("localhost");
	}
	
	public static void serverSetUp(String host) {
        System.setSecurityManager(null);
        
		try {
            ICarRentalAgency agency = new CarRentalAgency(namingSetup(host));
            ICarRentalAgency stub =
                ( ICarRentalAgency) UnicastRemoteObject.exportObject(agency, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("agency", stub);
            System.out.println("Agency server: agency server bound.");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	public static INamingService namingSetup(String host) {
		System.setSecurityManager(null);
		
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			INamingService namingService = (INamingService) registry.lookup("namingservice");
			return namingService;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
