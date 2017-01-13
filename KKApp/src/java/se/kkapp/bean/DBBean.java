
package se.kkapp.bean;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.json.JsonArray;
import se.kkapp.support.ConnectionFactory;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
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
            while(data.next()){
                String name = data.getString("name");
                String desc = data.getString("description");
                
                JAB.add(Json.createObjectBuilder()
                        .add("name", name)
                        .add("desc", desc));
            }
            connection.close();
            return JAB.build();
        } catch (Exception e) {
            System.out.println("Fail på getRecept: "+ e);
        }
        return null;
    }

    public boolean removeRecept(int id) {
        try {
            Connection connection = ConnectionFactory.createConnection();
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM recept WHERE recept.ID = ?");
            stmt.setInt(1, id);
            connection.close();
            return true;
        } catch (Exception ex) {
            System.out.println("RemoveRecept error: "+ex);
        }
        return false;
    }
}
