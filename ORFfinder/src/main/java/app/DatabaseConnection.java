package app;

/**
 * @author Valerie Verhalle & Stephan Gui
 * @date 28-3-2019
 *
 */
import java.sql.*;
import java.util.ArrayList;
import static app.ORFfinder.DNA_Hashcode;
import static app.ORFfinder.resultsMap;
import static app.ORFfinder.sequence;

/**
 * this class is responseble for the connection with the owe7_pg10 database,
 * inserting all the information in to the tables of the database.
 */

public class DatabaseConnection {
    public static int startP;
    public static int stopP;
    public static String dnaSeq;
    public static String aaSeq;


    /**
     * @method this method makes a connection with the database and
     * inserts the information into the tables: "orf" and "inputsequence"
     */
    public void Connection(){
        System.out.println("Starting upload.");
        try(Connection conn = DriverManager.getConnection(
                "jdbc:mysql://hannl-hlo-bioinformatica-mysqlsrv.mysql.database.azure.com:3306/owe7_pg10" +
                        "?useUnicode=true&useJDBCCompliantTimezoneShift=true&" +
                        "useLegacyDatetimeCode=false&serverTimezone=UTC",
                "owe7_pg10@hannl-hlo-bioinformatica-mysqlsrv.mysql.database.azure.com",
                "blaat1234"))
        {

            final String inputsequence = "INSERT INTO inputsequence(DNAsequence, hashcode) VALUES (?,?)";
            final PreparedStatement pst3 = conn.prepareStatement(inputsequence);
            pst3.setString(1, sequence);
            pst3.setInt(2, DNA_Hashcode);
            pst3.executeUpdate();

            for (int j=0 ; j<resultsMap.values().size() ; j++){
                System.out.println(resultsMap.get(j+1));
                ArrayList<String> resultValue= resultsMap.get(j+1);

                startP = Integer.parseInt(resultValue.get(0));
                stopP  = Integer.parseInt(resultValue.get(1));
                dnaSeq = resultValue.get(2);
                aaSeq = resultValue.get(3);

                final String orf = "INSERT INTO orf(DNA_seq, startPosition, stopPosition, aa_seq, ORF_id, inputsequence_fk) VALUES(?,?,?,?,?,?)";
                final PreparedStatement pst2 = conn.prepareStatement(orf);

                pst2.setString(1, dnaSeq);
                pst2.setInt(2, startP);
                pst2.setInt(3, stopP);
                pst2.setString(4, aaSeq);
                pst2.setInt(5,(startP+stopP+dnaSeq).hashCode() );
                pst2.setInt(6, DNA_Hashcode);

                pst2.executeUpdate();
            }
            System.out.println("Update succesfull.");
        } catch (SQLIntegrityConstraintViolationException e){
            System.out.println("Data already exists in database, abborting upload.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}