
package se.kkapp.bean;

import com.mysql.jdbc.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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
}
