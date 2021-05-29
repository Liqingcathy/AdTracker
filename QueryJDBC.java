/*
 * Group 3
 * CPSC 5021, Seattle University
 * This is free and unencumbered software released into the public domain.
 */
package queryrunner;
import java.sql.Connection;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author mckeem
 */


public class QueryJDBC {

    public Connection conn = null;
    static final String DB_DRV = "com.mysql.jdbc.Driver";
    String error ="";
    String url;
    String user;
    String [] headers;
    String [][] allRows;
    int updateAmount = 0;
       
    QueryJDBC ()
    {
        updateAmount = 0;
    }
    
    public String GetError()
    {
        return error;
    }

    public String [] GetHeaders()
    {
        return this.headers;
    }
    
    public String [][] GetData()
    {
        return this.allRows;
    }
    
    public int GetUpdateCount()
    {
        return updateAmount;
    }
    
    // We think we can always setString on Parameters. Not sure if this is true.
    // GetString on Results is fine though
    public boolean ExecuteQuery(String szQuery, String [] params,
                                boolean [] likeParams) {
        PreparedStatement preparedStatement = null;        
        ResultSet resultSet = null;
        int nColAmt;
        boolean bOK = true;

        // Try to get the columns and the amount of columns
        try {
            preparedStatement = this.conn.prepareStatement(szQuery);

            int nParamAmount = params.length;

            for (int i=0; i < nParamAmount; i++) {
                String parm = params[i];
                if (likeParams[i] == true) {
                    parm += "%";
                }
                preparedStatement.setString(i+1, parm);
            }

            //preparedStatement.setString(1,  "%" + szContact + "%");
            resultSet = preparedStatement.executeQuery();

            ResultSetMetaData rsmd = resultSet.getMetaData(); 
            nColAmt = rsmd.getColumnCount();
            headers = new String [nColAmt];
            
            for (int i=0; i< nColAmt; i++) {
                headers[i] = rsmd.getColumnLabel(i+1);
            }
            int amtRow = 0;
            while(resultSet.next()){
                amtRow++; }

            if (amtRow > 0) {
                this.allRows = new String [amtRow][nColAmt];
                resultSet.beforeFirst();
                int nCurRow = 0;
                while(resultSet.next()) {
                    for (int i=0; i < nColAmt; i++) {
                       allRows[nCurRow][i] = resultSet.getString(i+1);
                    }
                    nCurRow++;
                }                                
            }
            else {
                this.allRows = new String [1][nColAmt];
                for (int i=0; i < nColAmt; i++)
                {
                   allRows[0][i] = "";
                }               
            }
            preparedStatement.close();
            resultSet.close();            

        } catch (SQLException ex) {
            bOK = false;
            this.error = "SQLException: " + ex.getMessage();
            this.error += "SQLState: " + ex.getSQLState();
            this.error += "VendorError: " + ex.getErrorCode();

            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;
        }
        return true;
    }

    
     public boolean ExecuteUpdate(String szQuery, String [] parms) {
        PreparedStatement preparedStatement = null;        

        boolean bOK = true;
        updateAmount =0;
        
        // Try to get the columns and the amount of columns
        try {
            preparedStatement=this.conn.prepareStatement(szQuery);

            int nParamAmount = parms.length;

            for (int i=0; i < nParamAmount; i++) {
                preparedStatement.setString(i+1, parms[i]);
            }
            updateAmount =preparedStatement.executeUpdate();
            preparedStatement.close();          

        } catch (SQLException ex) {
            bOK = false;
            this.error = "SQLException: " + ex.getMessage();
            this.error += "SQLState: " + ex.getSQLState();
            this.error += "VendorError: " + ex.getErrorCode();

            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;
        }
        return true;
    }
   
    
                 
    public boolean ConnectToDatabase(String host, String user, String pass,
                                     String database) {
        String url;
        url = "jdbc:mysql://";
        url += host;
        url +=":3306/";
        url += database;   
        url +="?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        try {
            Class.forName(DB_DRV).newInstance();
            conn = DriverManager.getConnection(url,user,pass);

        } catch (SQLException ex) {
            error = "SQLException: " + ex.getMessage() +
                    ex.getSQLState() + 
                    ex.getErrorCode();
            return false;

        } catch (Exception ex) {
            // handle the error
            error = "SQLException: " + ex.getMessage();
            return false;
        }
        return true;
    }

    /* Document this function
    // TODO    
    */
    public boolean CloseDatabase() {
        try {
            conn.close();

        } catch (SQLException ex) {
            error = "SQLException: " + ex.getMessage();
            error = "SQLState: " + ex.getSQLState();
            error = "VendorError: " + ex.getErrorCode();
            return false;

        } catch (Exception ex) {
            error = "Error was " + ex.toString();
            return false;
        }
        return true;
    }
}
