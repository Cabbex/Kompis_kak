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
            String sql = "SELECT * FROM inforecept";
            ResultSet data = stmt.executeQuery(sql);
            JsonArrayBuilder JAB = Json.createArrayBuilder();
            while (data.next()) {
                int id = data.getInt("ID");
                String name = data.getString("ReceptName");
                String desc = data.getString("description");
                String tag = data.getString("tagname");
                String author = data.getString("author");

                JAB.add(Json.createObjectBuilder()
                        .add("id", id)
                        .add("name", name)
                        .add("desc", desc)
                        .add("tagnamn", tag)
                        .add("author", author));
            }
            connection.close();
            return JAB.build();
        } catch (Exception e) {
            System.out.println("Fail på getRecept: " + e);
        }
        return null;
    }

    public JsonArray getIngrediense(int id) {
        try {
            Connection connection = ConnectionFactory.createConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM infoingrediense WHERE recept_id = ?");
            stmt.setInt(1, id);
            ResultSet data = stmt.executeQuery();
            JsonArrayBuilder JAB = Json.createArrayBuilder();
            while (data.next()) {
                String name = data.getString("name");
                String desc = data.getString("amount");

                JAB.add(Json.createObjectBuilder()
                        .add("name", name)
                        .add("amount", desc));
            }
            connection.close();
            return JAB.build();
        } catch (Exception e) {
            System.out.println("Fail på getIngrediense: " + e);
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

    /*
        Used for testing!
        [
            {
            "recipe_id": 2,
            "amount": "4 tsk",
            "ingrediens": 1
            },
            {
            "recipe_id": 3,
            "amount": "5 tsk",
            "ingrediens": 1
            }
    ]
     */
    public boolean postIngrediens(String body) {
        try {
            Connection connection = ConnectionFactory.createConnection();
            PreparedStatement stmt;
            JsonReader jsr = Json.createReader(new StringReader(body));
            JsonArray jsondata = jsr.readArray();
            int size = jsondata.size();
            for (int i = 1; i <= size; i++) {
                JsonObject jsono = jsondata.getJsonObject(i - 1);
                int recipe_id = jsono.getInt("recipe_id");
                String amount = jsono.getString("amount");
                int ingrediant_id = jsono.getInt("ingrediens");
                stmt = connection.prepareStatement("INSERT INTO `recept_collective`(`recept_id`, `amount`, `ing_id`) VALUES (?,?,?)");
                stmt.setInt(1, recipe_id);
                stmt.setString(2, amount);
                stmt.setInt(3, ingrediant_id);
                stmt.executeUpdate();
            }
            return true;
        } catch (Exception exception) {
            System.out.println("postIngrediens: " + exception);
        }
        return false;
    }

    /*  Used for testing! 
    [
            {
            "amount": "4 tsk",
            "ingrediense": 1
            },
            {
            "amount": "5 tsk",
            "ingrediense": 1
            }
        ]
     */
    public boolean putIngrediense(String body, int id) {
        try {
            Connection connection = ConnectionFactory.createConnection();
            PreparedStatement stmt;
            JsonReader jsr = Json.createReader(new StringReader(body));
            JsonArray jsondata = jsr.readArray();
            int size = jsondata.size();
            for (int i = 1; i <= size; i++) {
                JsonObject jsono = jsondata.getJsonObject(i - 1);
                String amount = jsono.getString("amount");
                int ingrediant_id = jsono.getInt("ingrediense");
                stmt = connection.prepareStatement("UPDATE `recept_collective` SET `recept_id`=?,`amount`=?,`ing_id`=? WHERE recept_collective.recept_id = ? AND recept_collective.ing_id = ?");
                stmt.setInt(1, id);
                stmt.setString(2, amount);
                stmt.setInt(3, ingrediant_id);
                stmt.setInt(4, id);
                stmt.setInt(5, ingrediant_id);
                stmt.executeUpdate();
            }
            return true;
        } catch (Exception exception) {
            System.out.println("putIngrediense: " + exception);
        }
        return false;
    }

    public JsonArray getAllIngrediense() {
        try {
            Connection connection = ConnectionFactory.createConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ingrediense");
            ResultSet data = stmt.executeQuery();
            JsonArrayBuilder JAB = Json.createArrayBuilder();
            while (data.next()) {
                String name = data.getString("name");
                int id = data.getInt("id");

                JAB.add(Json.createObjectBuilder()
                        .add("id", id)
                        .add("ingredienseName", name));
            }
            connection.close();
            return JAB.build();
        } catch (Exception e) {
            System.out.println("Fail på getAllIngrediense: " + e);
        }
        return null;
    }

    public JsonArray getTags() {
        try {
            Connection connection = ConnectionFactory.createConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM tag");
            ResultSet data = stmt.executeQuery();
            JsonArrayBuilder JAB = Json.createArrayBuilder();
            while (data.next()) {
                String name = data.getString("name");
                String desc = data.getString("description");
                int id = data.getInt("id");

                JAB.add(Json.createObjectBuilder()
                        .add("id", id)
                        .add("tagname", name)
                        .add("desc", desc));
            }
            connection.close();
            return JAB.build();
        } catch (Exception e) {
            System.out.println("Fail på getTags: " + e);
        }
        return null;
    }
}
