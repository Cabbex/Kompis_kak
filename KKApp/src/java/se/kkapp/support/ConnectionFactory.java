/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kkapp.support;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Casper
 */
public class ConnectionFactory {
    public static Connection createConnection() throws Exception{
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = (Connection)DriverManager.getConnection("jdbc:mysql://localhost/kompis_kak", "root","");
        return connection;
    }
}
