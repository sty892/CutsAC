package ac.cust.custac.manager.violationdatabase;

import ac.cust.custac.player.CustACPlayer;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface ViolationDatabase {

    void connect() throws SQLException;

    void logAlert(CustACPlayer player, String custacVersion, String verbose, String checkName, int vls);

    int getLogCount(UUID player);

    List<Violation> getViolations(UUID player, int page, int limit);

    void disconnect();

}
