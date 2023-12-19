package com.example.bd_project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
// créer une connection à la base
public class ConnectionDb {
     Connection con = null;
    public static Connection conDB()
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/centre_formation?autoReconnect=true&useSSL=false", "root", "");
            return con;
        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println("ConnectionDb : "+ex.getMessage());
            return null;
        }
    }

}