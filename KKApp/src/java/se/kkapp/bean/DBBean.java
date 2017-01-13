package se.kkapp.bean;

import com.mysql.jdbc.Connection;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.ejb.Stateless;
import javax.json.JsonArray;
import se.kkapp.support.ConnectionFactory;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 *
 * @author Casper
 */
@Named
@Stateless
public class DBBean {

    public JsonArray getRecept() {
        try {
            Connection connection = ConnectionFactory.createConnection();
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM recept";
            ResultSet data = stmt.executeQuery(sql);
            JsonArrayBuilder JAB = Json.createArrayBuilder();
            while (data.next()) {
                String name = data.getString("name");
                String desc = data.getString("description");

                JAB.add(Json.createObjectBuilder()
                        .add("name", name)
                        .add("desc", desc));
            }
            connection.close();
            return JAB.build();
        } catch (Exception e) {
            System.out.println("Fail på getRecept: " + e);
        }
        return null;
    }

    public boolean removeRecept(int id) {
        try {
            Connection connection = ConnectionFactory.createConnection();
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM recept WHERE recept.ID = ?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt = connection.prepareStatement("DELETE FROM `recept_collective` WHERE recept_collective.recept_id = ?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            connection.close();
            return true;
        } catch (Exception ex) {
            System.out.println("RemoveRecept error: " + ex);
        }
        return false;
    }

    /*
        Used for testing!
        [{
            "rname": "Pannkaka",
            "description": "mm pannkaka",
            "author": "Casper Bjork",
            "tag": "Efterrätt"
        }]
     */
    public boolean postRecept(String body) {
        JsonReader jsr = Json.createReader(new StringReader(body));
        JsonArray jsondata = jsr.readArray();
        JsonObject jsono = jsondata.getJsonObject(0);
        String rname = jsono.getString("rname");
        String desc = jsono.getString("description");
        String author = jsono.getString("author");
        String tag = jsono.getString("tag");
        try {
            Connection connection = ConnectionFactory.createConnection();
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO `recept`(`ID`, `name`, `description`, `author_id`, `tag_id`) "
                    + "VALUES (null,?,?,(SELECT user.ID FROM user WHERE user.Name = " + "'" + author + "'" + "),(SELECT tag.ID FROM tag WHERE tag.name = " + "'" + tag + "'" + "))");
            stmt.setString(1, rname);
            stmt.setString(2, desc);
            stmt.executeUpdate();
            connection.close();
            return true;
        } catch (Exception ex) {
            System.out.println("POSTRecept error: " + ex);
        }
        return false;
    }

    public boolean putRecept(String body, int id) {
        JsonReader jsr = Json.createReader(new StringReader(body));
        JsonArray jsondata = jsr.readArray();
        JsonObject jsono = jsondata.getJsonObject(0);
        String rname = jsono.getString("rname");
        String desc = jsono.getString("description");
        String author = jsono.getString("author");
        String tag = jsono.getString("tag");
        try {
            Connection connection = ConnectionFactory.createConnection();
            PreparedStatement stmt = connection.prepareStatement("UPDATE `recept` SET `ID`=?,`name`= ?,`description`=?,"
                    + "`author_id`= (SELECT user.ID FROM user WHERE user.Name = " + "'" + author + "'" + ") ,"
                    + "`tag_id`= (SELECT tag.ID FROM tag WHERE tag.name = " + "'" + tag + "'" + ") WHERE recept.ID = ?");
            stmt.setInt(1, id);
            stmt.setString(2, rname);
            stmt.setString(3, desc);
            stmt.setInt(4, id);
            stmt.executeUpdate();
            connection.close();
            return true;
        } catch (Exception ex) {
            System.out.println("PUTRecept error: " + ex);
        }
        return false;
    }
}
