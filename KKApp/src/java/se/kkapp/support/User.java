/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kkapp.support;

import com.mysql.jdbc.Connection;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.HttpHeaders;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author bjca97001
 */
public class User {

    public static boolean authenticate(HttpHeaders httpHeader, int assumedPrivileges) {
        try {
            List<String> authHeader = httpHeader.getRequestHeader(HttpHeaders.AUTHORIZATION);
            String header = authHeader.get(0);
            header = header.substring(header.indexOf(" ") + 1);
            byte[] decoded = Base64.getDecoder().decode(header);
            String userPass = new String(decoded);
            String username = userPass.substring(0, userPass.indexOf(":"));
            String password = userPass.substring(userPass.indexOf(":") + 1);

            Connection connection = ConnectionFactory.createConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user WHERE name = ?");
            stmt.setString(1, username);
            ResultSet data = stmt.executeQuery();
            data.next();
            String hashedPass = data.getString("password");
            System.out.println("Privileges: " + data.getInt("Privileges"));
            System.out.println("Name: " + username);
            System.out.println("Password: " + password);
            System.out.println("Password: " + data.getString("password"));
            if (data.getInt("Privileges") >= assumedPrivileges) {
                connection.close();
                return BCrypt.checkpw(password, hashedPass);
            } else {
                connection.close();
                return false;
            }

        } catch (Exception e) {
            System.out.println("Auth: " + e);
        }
        return false;
    }

    /*            [{
    "name": "Casper",
    "password": "Passwooord"
    }]*/
    public static boolean createUser(String body) {
        JsonReader jsr = Json.createReader(new StringReader(body));
        JsonArray jsondata = jsr.readArray();
        JsonObject jsono = jsondata.getJsonObject(0);
        String username = jsono.getString("name");
        String password = jsono.getString("password");
        String hashpw = BCrypt.hashpw(password, BCrypt.gensalt());
        try {
            Connection connection = ConnectionFactory.createConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT user.name FROM user WHERE user.name = ?");
            stmt.setString(1, username);
            ResultSet data = stmt.executeQuery();
            data.next();
            if (!(data.getString("name").equals(username))) {
                stmt = connection.prepareStatement("INSERT INTO `user`(`ID`, `Name`, `password`, `Privileges`, `Fav_ID`) VALUES (null,?,?,?,0) ");
                stmt.setString(1, username);
                stmt.setString(2, hashpw);
                stmt.setInt(3, 1);
                stmt.executeUpdate();
                connection.close();
                return true;

            }
            connection.close();
            return false;
        } catch (Exception e) {
            System.out.println("user" + e);
        }
        return false;
    }
}
