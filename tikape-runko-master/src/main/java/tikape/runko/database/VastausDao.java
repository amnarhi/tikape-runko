package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Vastaus;
import tikape.runko.domain.Kysymys;
import tikape.runko.database.KysymysDao;

public class VastausDao implements Dao<Vastaus, Integer> {

    private Database database;

    public VastausDao(Database database) {
        this.database = database;
    }

    @Override
    public Vastaus findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Vastaus WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String vastausteksti = rs.getString("vastausteksti");
        Boolean oikein = rs.getBoolean("oikein");
        Integer kysymys_id = rs.getInt("kysymys_id");

        Vastaus v = new Vastaus(id, vastausteksti, oikein, kysymys_id);

        rs.close();
        stmt.close();
        connection.close();

        return v;
    }

    @Override
    public List<Vastaus> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Vastaus");

        ResultSet rs = stmt.executeQuery();
        List<Vastaus> vastaukset = new ArrayList<>();
        while (rs.next()) {

            Integer id = rs.getInt("id");
            Vastaus v = this.findOne(id);
            vastaukset.add(v);
        }
        rs.close();
        stmt.close();
        connection.close();

        return vastaukset;
    }

    public List<Vastaus> findAllWhere(Integer k_id) throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Vastaus WHERE kysymys_id = ?");
        stmt.setObject(1, k_id);

        ResultSet rs = stmt.executeQuery();
        List<Vastaus> vastaukset = new ArrayList<>();
        while (rs.next()) {

            Integer id = rs.getInt("id");
            Vastaus v = this.findOne(id);
            vastaukset.add(v);
        }
        rs.close();
        stmt.close();
        connection.close();

        return vastaukset;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM Vastaus WHERE id = ?");
        stmt.setObject(1, key);
        stmt.executeUpdate();
    }

    public void saveOrUpdate(Vastaus vastaus) throws SQLException {

        Connection connection = database.getConnection();

        for (Vastaus n : this.findAll()) {
            if (n.getVastausteksti().equals(vastaus.getVastausteksti())) {
                PreparedStatement stmt = connection.prepareStatement("UPDATE Vastaus SET vastausteksti = ?, oikein = ?"
                        + " WHERE id = ?");
                stmt.setObject(1, vastaus.getVastausteksti());
                stmt.setObject(2, vastaus.getOikein());
                stmt.setObject(3, vastaus.getId());
                stmt.executeUpdate();
                return;

            }
        }

        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Vastaus (vastausteksti, oikein, kysymys_id)"
                + " VALUES (?, ?, ?)");
        stmt.setObject(1, vastaus.getVastausteksti());
        stmt.setObject(2, vastaus.getOikein());
        stmt.setObject(3, vastaus.getKysymys_id());
        stmt.executeUpdate();

    }

}
