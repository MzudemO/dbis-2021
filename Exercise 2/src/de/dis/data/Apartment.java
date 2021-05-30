package de.dis.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Apartment
 */
public class Apartment {
    private int id = -1;

    private int manager;
    private float area;
    private String address;
    private int floor;
    private float rent;
    private boolean balcony;
    private boolean kitchen;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getManager() {
        return manager;
    }

    public void setManager(int manager) {
        this.manager = manager;
    }

    public float getArea() {
        return area;
    }

    public void setArea(float area) {
        this.area = area;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public float getRent() {
        return rent;
    }

    public void setRent(float rent) {
        this.rent = rent;
    }

    public boolean isBalcony() {
        return balcony;
    }

    public void setBalcony(boolean balcony) {
        this.balcony = balcony;
    }

    public boolean isKitchen() {
        return kitchen;
    }

    public void setKitchen(boolean kitchen) {
        this.kitchen = kitchen;
    }


    /**
     * Lädt einen Makler aus der Datenbank
     * @param id ID des zu ladenden Maklers
     * @return Makler-Instanz
     */
    public static Apartment load(int id) {
        try {
            // Hole Verbindung
            Connection con = DbConnectionManager.getInstance().getConnection();

            // Erzeuge Anfrage
            String selectSQL = "SELECT * FROM apartment WHERE id = ?";
            PreparedStatement pstmt = con.prepareStatement(selectSQL);
            pstmt.setInt(1, id);

            // Führe Anfrage aus
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Apartment ts = new Apartment();
                ts.setId(id);
                ts.setManager(rs.getInt("managed_by"));
                ts.setArea(rs.getFloat("square_area"));
                ts.setAddress(rs.getString("address"));
                ts.setFloor(rs.getInt("floor"));
                ts.setRent(rs.getFloat("rent"));
                ts.setBalcony(rs.getBoolean("balcony"));
                ts.setBalcony(rs.getBoolean("kitchen"));

                rs.close();
                pstmt.close();
                return ts;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
