package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteDatabaseLink {

    public final static String databaseLoc = "./recodata.sqlite";

    private Connection connection;

    public SQLiteDatabaseLink() throws DatabaseException
    {
        System.out.println("Creating database at location: " + databaseLoc);
        open();
        createTables();
    }

    private final void open() throws DatabaseException
    {
        try
        {
            // load the sqlite-JDBC driver using the class loader
            Class.forName("org.sqlite.JDBC");

            // create a database connection, will open the sqlite db if it
            // exists and create a new sqlite database if it does not exist
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + databaseLoc);

        } catch (ClassNotFoundException e)
        {
            throw new DatabaseException("Problem with Class.forName in SQLiteDatabase", e);
        } catch (SQLException e)
        {
            throw new DatabaseException(e);
        }
    }

    private final void createTables() throws DatabaseException
    {
        createClicksTable();
        createRecosTable();
        createUsagesTable();
        createResponsesTable();
    }

    private void createResponsesTable() throws DatabaseException {
        String sqlTableQuery = "CREATE TABLE IF NOT EXISTS Responses ( " +
                "response_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "participant_id INTEGER, " +
                "task_id INTEGER," +
                "response TEXT) ";

        executeStatementWithNoResults(makePreparedStatement(sqlTableQuery));
    }

    private void createUsagesTable() throws DatabaseException {
        String sqlTableQuery = "CREATE TABLE IF NOT EXISTS Usages ( " +
                "response_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "participant_id INTEGER, " +
                "task_id INTEGER," +
                "command_id TEXT) ";

        executeStatementWithNoResults(makePreparedStatement(sqlTableQuery));
    }

    private void createRecosTable() throws DatabaseException {
        String sqlTableQuery = "CREATE TABLE IF NOT EXISTS Recommendations ( " +
                "response_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "participant_id INTEGER, " +
                "task_id INTEGER," +
                "command_id TEXT," +
                "reco_type TEXT) ";

        executeStatementWithNoResults(makePreparedStatement(sqlTableQuery));
    }

    private void createClicksTable() throws DatabaseException {
        String sqlTableQuery = "CREATE TABLE IF NOT EXISTS Clicks ( " +
                "response_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "participant_id INTEGER, " +
                "task_id INTEGER," +
                "command_id TEXT," +
                "reco_type TEXT) ";

        executeStatementWithNoResults(makePreparedStatement(sqlTableQuery));
    }

    protected PreparedStatement makePreparedStatement(String statementQuery) throws DatabaseException
    {
        try
        {
            return connection.prepareStatement(statementQuery);
        } catch (SQLException e)
        {
            throw new DatabaseException("Problem compiling SQL to preparedStatement", e);
        }
    }

    protected void executeStatementWithNoResults(PreparedStatement statement) throws DatabaseException
    {
        try
        {
            statement.execute();
            statement.close();
        } catch (SQLException e)
        {
            throw new DatabaseException("Problem executing statement ", e);
        }

    }

    protected ResultSet executeWithResults(PreparedStatement statement) throws DatabaseException
    {
        ResultSet retVal = null;
        try
        {
            retVal = statement.executeQuery();

        } catch (SQLException e)
        {
            throw new DatabaseException("Problem with query", e);
        }
        return retVal;

    }

    public void sawClick(int participantId, int taskNumber, String commandId, String recoCondition) {
        String sqlTableQuery = "INSERT INTO Clicks ( " +
                "participant_id, " +
                "task_id," +
                "command_id," +
                "reco_type) VALUES (?,?,?,?) ";

        
        try (PreparedStatement ps = makePreparedStatement(sqlTableQuery);){
            
            ps.setInt(1, participantId);
            ps.setInt(2, taskNumber);
            ps.setString(3, commandId);
            ps.setString(4, recoCondition);
            
            executeStatementWithNoResults(ps);
        } catch (DatabaseException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void sawRecommendation(int participantId, int taskNumber, String commandId, String recoCondition) {
        String sqlTableQuery = "INSERT INTO Recommendations ( " +
                "participant_id, " +
                "task_id," +
                "command_id," +
                "reco_type) VALUES (?,?,?,?) ";

        
        try (PreparedStatement ps = makePreparedStatement(sqlTableQuery);){
            
            ps.setInt(1, participantId);
            ps.setInt(2, taskNumber);
            ps.setString(3, commandId);
            ps.setString(4, recoCondition);
            
            executeStatementWithNoResults(ps);
        } catch (DatabaseException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void sawUsage(int participantId, int taskNumber, String commandId) {
        String sqlTableQuery = "INSERT INTO Usage ( " +
                "participant_id, " +
                "task_id," +
                "command_id) VALUES (?,?,?) ";

        
        try (PreparedStatement ps = makePreparedStatement(sqlTableQuery);){
            
            ps.setInt(1, participantId);
            ps.setInt(2, taskNumber);
            ps.setString(3, commandId);
            
            executeStatementWithNoResults(ps);
        } catch (DatabaseException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void sawResponse(int participantId, int taskNumber, String response) {
        String sqlTableQuery = "INSERT INTO Responses ( " +
                "participant_id, " +
                "task_id," +
                "response) VALUES (?,?,?) ";

        
        try (PreparedStatement ps = makePreparedStatement(sqlTableQuery);){
            
            ps.setInt(1, participantId);
            ps.setInt(2, taskNumber);
            ps.setString(3, response);
            
            executeStatementWithNoResults(ps);
        } catch (DatabaseException | SQLException e) {
            e.printStackTrace();
        }
    }

}

class DatabaseException extends Exception {

    private static final long serialVersionUID = 1L;

    public DatabaseException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public DatabaseException(Throwable arg0) {
        super(arg0);
    }

}
