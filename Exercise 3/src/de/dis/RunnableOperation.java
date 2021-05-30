package de.dis;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RunnableOperation implements Runnable {

    public final char readwrite;
    public final String op;
    public final Connection c;

    public RunnableOperation(Connection connection, char readwrite, String operation) {

        this.readwrite = readwrite;
        this.op = operation;
        this.c = connection;
    }

    public void run() {
        Statement st;
        try {
            st = c.createStatement();
            ResultSet rs = null;
            if (readwrite == 'r') {
                rs = st.executeQuery(op);
            } else if (readwrite == 'w') {
                st.execute(op);
            } else if (readwrite == 'c') {
                c.commit();
            } else if (readwrite == 'l') {
                st.executeQuery(op);
            }
            System.out.println(Thread.currentThread().getName() + "sql = " + op);
            if (rs != null) {
                while (rs.next()) {
                    System.out.println(rs.getString("name"));
                }
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
