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
    
        //One table of clicks, recos, responses, usage
    //Columns PID, Task, [thing]
    
    
    private final void open() throws DatabaseException
    {
        try
        {
            // load the sqlite-JDBC driver using the class loader
            Class.forName("org.sqlite.JDBC");

            // create a database connection, will open the sqlite db if it
            // exists and create a new sqlite database if it does not exist
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + databaseLoc);

        }
        catch (ClassNotFoundException e)
        {
            throw new DatabaseException("Problem with Class.forName in SQLiteDatabase", e);
        }
        catch (SQLException e)
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
                "task_id INTEGER) ";

            // execute the query
            PreparedStatement statement = makePreparedStatement(sqlTableQuery);
            executeStatementWithNoResults(statement);
    }

    protected PreparedStatement makePreparedStatement(String statementQuery) throws DatabaseException
    {
        try
        {
            return connection.prepareStatement(statementQuery);
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Problem compiling SQL to preparedStatement",e);
        }
    }

    protected void executeStatementWithNoResults(PreparedStatement statement) throws DatabaseException
    {
        try
        {
            statement.execute();
            statement.close();
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Problem executing statement ",e);
        }

    }

    protected ResultSet executeWithResults(PreparedStatement statement) throws DatabaseException
    {
        ResultSet retVal = null;
        try
        {
            retVal = statement.executeQuery();      

        }
        catch (SQLException e)
        {
            throw new DatabaseException("Problem with query", e);
        }
        return retVal;

    }

    private void createUsagesTable() {
        // TODO Auto-generated method stub
        
    }

    private void createRecosTable() {
        // TODO Auto-generated method stub
        
    }

    private void createClicksTable() {
        // TODO Auto-generated method stub
        
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

