package namingservice;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class NamingServiceServer {
	
	public static void main(String[] args) {
		serverSetUp();
	}
	
	public static void serverSetUp() {
        System.setSecurityManager(null);
        
		try {
            INamingService namingService = new NamingService();
            INamingService stub =
                (INamingService) UnicastRemoteObject.exportObject(namingService, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("namingservice", stub);
            System.out.println("Naming Service: namingservice bound.");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
