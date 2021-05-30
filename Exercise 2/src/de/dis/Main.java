package de.dis;

import de.dis.data.Makler;
import de.dis.data.Apartment;

/**
 * Hauptklasse
 */
public class Main {
	/**
	 * Startet die Anwendung
	 */
	public static void main(String[] args) {
		showMainMenu();
	}
	
	/**
	 * Zeigt das Hauptmenü
	 */
	public static void showMainMenu() {
		//Menüoptionen
		final int MENU_MAKLER = 0;
		final int MENU_ESTATES = 1;
		final int MENU_CONTRACTS = 2;
		final int QUIT = 3;
		
		//Erzeuge Menü
		Menu mainMenu = new Menu("Hauptmenü");
		mainMenu.addEntry("Makler-Verwaltung", MENU_MAKLER);
		mainMenu.addEntry("Estate-Verwaltung", MENU_ESTATES);
		mainMenu.addEntry("Contract-Verwaltung", MENU_CONTRACTS);
		mainMenu.addEntry("Beenden", QUIT);
		
		//Verarbeite Eingabe
		while(true) {
			int response = mainMenu.show();
			
			switch(response) {
				case MENU_MAKLER:
					showMaklerMenu();
					break;
				case MENU_ESTATES:
					showMaklerMenu();
					break;
				case MENU_CONTRACTS:
					showMaklerMenu();
					break;
				case QUIT:
					return;
			}
		}
	}
	
	/**
	 * Zeigt die Maklerverwaltung
	 */
	public static void showMaklerMenu() {

		// only allow admin users to edit estate agents
		if(!"adminpassword".equals(FormUtil.readString("Enter Admin Password"))) {
			return;
		}

		//Menüoptionen
		final int NEW_MAKLER = 0;
		final int UPDATE_MAKLER = 1;
		final int DELETE_MAKLER = 2;
		final int BACK = 3;
		
		//Maklerverwaltungsmenü
		Menu maklerMenu = new Menu("Makler-Verwaltung");
		maklerMenu.addEntry("Neuer Makler", NEW_MAKLER);
		maklerMenu.addEntry("Makler bearbeiten", UPDATE_MAKLER);
		maklerMenu.addEntry("Makler löschen", DELETE_MAKLER);
		maklerMenu.addEntry("Zurück zum Hauptmenü", BACK);

		//Verarbeite Eingabe
		while(true) {
			int response = maklerMenu.show();
			
			switch(response) {
				case NEW_MAKLER:
					newMakler();
					break;
				case UPDATE_MAKLER:
					updateMakler();
					break;
				case DELETE_MAKLER:
					deleteMakler();
					break;
				case BACK:
					return;
			}
		}
	}
	
	/**
	 * Legt einen neuen Makler an, nachdem der Benutzer
	 * die entprechenden Daten eingegeben hat.
	 */
	public static void newMakler() {
		Makler m = new Makler();
		
		m.setName(FormUtil.readString("Name"));
		m.setAddress(FormUtil.readString("Adresse"));
		m.setLogin(FormUtil.readString("Login"));
		m.setPassword(FormUtil.readString("Passwort"));
		m.save();
		
		System.out.println("Makler mit der ID "+m.getId()+" wurde erzeugt.");
	}

	public static void updateMakler() {
		Makler m = Makler.load(FormUtil.readInt("Makler ID"));

		m.setName(FormUtil.readString("Change Name from " + m.getName()));
		m.setAddress(FormUtil.readString("Change Address from " + m.getAddress()));
		m.setLogin(FormUtil.readString("Change Login from " + m.getLogin()));
		m.setPassword(FormUtil.readString("Change Password to"));
		m.save();

		System.out.println("Makler mit der ID "+m.getId()+" wurde aktualisiert.");
	}

	public static void deleteMakler() {
		Makler m = Makler.load(FormUtil.readInt("Makler ID"));

		m.delete();
	}

	/**
	 * Zeigt die Estateverwaltung
	 */
	public static void showEstateMenu() {

		// login for estate agents
		String login = FormUtil.readString("Login");
		String password = FormUtil.readString("Password");

		Makler m = new Makler();

		m.authenticate(login, password);

		//Menüoptionen
		final int NEW_ESTATE = 0;
		final int UPDATE_ESTATE = 1;
		final int DELETE_ESTATE = 2;
		final int BACK = 3;

		//Maklerverwaltungsmenü
		Menu estateMenu = new Menu("Estate-Verwaltung");
		estateMenu.addEntry("Neues Estate", NEW_ESTATE);
		estateMenu.addEntry("Estate bearbeiten", UPDATE_ESTATE);
		estateMenu.addEntry("Estate löschen", DELETE_ESTATE);
		estateMenu.addEntry("Zurück zum Hauptmenü", BACK);

		//Verarbeite Eingabe
		while(true) {
			int response = estateMenu.show();

			switch(response) {
				case NEW_ESTATE:
					newEstate(m);
					break;
				case UPDATE_ESTATE:
					System.out.println("updateEstate(m)");
					break;
				case DELETE_ESTATE:
					System.out.println("deleteEstate(m)");
					break;
				case BACK:
					return;
			}
		}
	}

	/**
	 * Legt einen neues Estate an, nachdem der Benutzer
	 * die entprechenden Daten eingegeben hat.
	 */
	public static void newEstate(Makler m) {

		if(FormUtil.readBool("Enter y for New Apartment, any key for New House")) {
			Apartment a = new Apartment();

			a.setManager(m.getId());
			a.setArea(FormUtil.readInt("Area:"));
			a.setAddress(FormUtil.readString("Adresse"));
			a.setFloor(FormUtil.readInt("Floor"));
			a.setRent(FormUtil.readInt("Rent"));
			a.setBalcony(FormUtil.readBool("Enter y for Balcony, any key for None"));
			a.setKitchen(FormUtil.readBool("Enter y for Kitchen, any key for None"));

			// a.save();
			System.out.println("Apartment mit der ID "+m.getId()+" wurde erzeugt.");
		}
		// else: house



	}
}
