/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queryrunner;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * QueryRunner takes a list of Queries that are initialized in it's constructor
 * and provides functions that will call the various functions in the QueryJDBC class 
 * which will enable MYSQL queries to be executed. It also has functions to provide the
 * returned data from the Queries. Currently the eventHandlers in QueryFrame call these
 * functions in order to run the Queries.
 */
public class QueryRunner {

    
    public QueryRunner()
    {
        this.m_jdbcData = new QueryJDBC();
        m_updateAmount = 0;
        m_queryArray = new ArrayList<>();
        m_error="";
    
        
        // TODO - You will need to change the queries below to match your queries.
        
        // You will need to put your Project Application in the below variable
        
        this.m_projectTeamApplication="CITYELECTION";    // THIS NEEDS TO CHANGE FOR YOUR APPLICATION
        
        
        // Each row that is added to m_queryArray is a separate query. It does not work on Stored procedure calls.
        // The 'new' Java keyword is a way of initializing the data that will be added to QueryArray. Please do not change
        // Format for each row of m_queryArray is: (QueryText, ParamaterLabelArray[], LikeParameterArray[], IsItActionQuery, IsItParameterQuery)
        
        //    QueryText is a String that represents your query. It can be anything but Stored Procedure
        //    Parameter Label Array  (e.g. Put in null if there is no Parameters in your query, otherwise put in the Parameter Names)
        //    LikeParameter Array  is an array I regret having to add, but it is necessary to tell QueryRunner which parameter has a LIKE Clause. If you have no parameters, put in null. Otherwise put in false for parameters that don't use 'like' and true for ones that do.
        //    IsItActionQuery (e.g. Mark it true if it is, otherwise false)
        //    IsItParameterQuery (e.g.Mark it true if it is, otherwise false)

        // Hello this is a GitHub test! Hi Ben and Liqing!
        m_queryArray.add(new QueryData("Select * from Product where product_description=?", new String [] {"product_description"}, new boolean [] {false}, false, true));   // THIS NEEDS TO CHANGE FOR YOUR APPLICATION
        
        m_queryArray.add(new QueryData("SELECT P.product_id, product_name, seller_name, " +
            "product_description as description, product_price as price, product_rating as rating, " +
            "product_reviews as reviews, C.manager_id, campaign_id " +
            "FROM Product P Join Seller USING (seller_id) Join Campaign C USING (seller_id) " +
            "WHERE product_description LIKE ? " +
            "Order By P.product_rating DESC, P.product_reviews DESC " +
            "LIMIT 5",
            new String[] {"product_description"}, new boolean [] {true}, false, true));
        
        //m_queryArray.add(new QueryData("Select * from contact where contact_id=?", new String [] {"CONTACT_ID"}, new boolean [] {false},  false, true));        // THIS NEEDS TO CHANGE FOR YOUR APPLICATION
        //m_queryArray.add(new QueryData("Select * from contact where contact_name like ?", new String [] {"CONTACT_NAME"}, new boolean [] {true}, false, true));        // THIS NEEDS TO CHANGE FOR YOUR APPLICATION
        //m_queryArray.add(new QueryData("insert into contact (contact_id, contact_name, contact_salary) values (?,?,?)",new String [] {"CONTACT_ID", "CONTACT_NAME", "CONTACT_SALARY"}, new boolean [] {false, false, false}, true, true));// THIS NEEDS TO CHANGE FOR YOUR APPLICATION
        
        
         //Overview of Top Performing Ad Campaigns and Ad Groups
            m_queryArray.add(new QueryData(
        		"	SELECT \r\n" + 
        		"    campaign_id, \r\n" + 
        		"    campaign_name, \r\n" + 
        		"    ad_group_name, ad_group_impressions as impressions, \r\n" + 
        		"    ad_group_clicks as clicks, ad_group_cpc as cpc, \r\n" + 
        		"    ad_group_spends as spends, ad_group_sales as sales,\r\n" + 
        		"    ad_group_orders as orders, \r\n" + 
        		"    round((ad_group_orders / ad_group_clicks)*100, 2) as \"conv rate(%)\",\r\n" + 
        		"    ad_group_acos as ACOS, ad_group_roas as ROAS\r\n" + 
        		"FROM Campaign\r\n" + 
        		"JOIN Ad_Group USING(campaign_id)\r\n" + 
        		"JOIN Ad_Group_Performance USING(ad_group_id)\r\n" + 
        		"WHERE ad_group_acos < 0.3 or ad_group_roas > 0.5 \r\n" + 
        		"ORDER BY ad_group_acos, ad_group_id;",
    			null, null, false, false));
                       
    }
    
    //Overview of Top Performing Ad Campaigns and Ad Groups (user intput- ACOS, ROAS)(doesn't filter)
        m_queryArray.add(new QueryData(
        		"	SELECT \r\n" + 
        		"    campaign_id, \r\n" + 
        		"    campaign_name, \r\n" + 
        		"    ad_group_name, ad_group_impressions as impressions, \r\n" + 
        		"    ad_group_clicks as clicks, ad_group_cpc as cpc, \r\n" + 
        		"    ad_group_spends as spends, ad_group_sales as sales,\r\n" + 
        		"    ad_group_orders as orders, \r\n" + 
        		"    round((ad_group_orders / ad_group_clicks)*100, 2) as \"conv rate(%)\",\r\n" + 
        		"    ad_group_acos as ACOS, ad_group_roas as ROAS\r\n" + 
        		"FROM Campaign\r\n" + 
        		"JOIN Ad_Group USING(campaign_id)\r\n" + 
        		"JOIN Ad_Group_Performance USING(ad_group_id)\r\n" + 
        		"WHERE (ad_group_acos = ?) or (ad_group_roas = ? \r\n)" + 
        		"ORDER BY ad_group_acos, ad_group_id;",
        		new String [] {"ACOS", "ROAS"}, new boolean [] {false},  false, true));  
        
       
    //top performing keyword
         //top performing keyword
        m_queryArray.add(new QueryData(
        		"	SELECT \r\n" + 
        		"	 ad_group_name, ad_group_budget,\r\n" + 
        		"    keyword, keyword_impressions as impressions, \r\n" + 
        		"    keyword_clicks as clicks , keyword_ctr as 'ctr(%)', keyword_cpc as cpc,\r\n" + 
        		"    keyword_orders as orders, \r\n" + 
        		"    round((keyword_orders / keyword_clicks)*100, 2) as 'conv rate(%)',\r\n" + 
        		"    keyword_spends as spends , keyword_sales as sales, \r\n" + 
        		"    keyword_acos as ACOS , keyword_roas as ROAS\r\n" + 
        		"FROM Keyword\r\n" + 
        		"JOIN Ad_Group USING(ad_group_id)\r\n" + 
        		"JOIN Keyword_Performance USING (keyword_id)\r\n" + 
        		"WHERE \r\n" + 
        		"	keyword_ctr > 0.4 \r\n" + 
        		"	AND keyword_acos < 0.7 \r\n" + 
        		"	AND keyword_roas > 0.4\r\n" + 
        		"ORDER BY keyword_acos asc;",
        		null, null, false, false));
    
    //Good Performing Ads Groups with Sales Greater Than Average (user input: campaign strategy) doesn't filter
    m_queryArray.add(new QueryData(
        		"SELECT \r\n" + 
        		"    c.campaign_id, c.campaign_name, \r\n" + 
        		"    a.ad_group_start, a.ad_group_name, \r\n" + 
        		"    p.product_name, p.product_description as 'prod descript', \r\n" + 
        		"    p.product_price as price,\r\n" + 
        		"    pf.ad_group_orders as orders, pf.ad_group_sales as sales, \r\n" + 
        		"    round((ad_group_sales / ad_group_orders), 0) as 'sales unit'\r\n" + 
        		"FROM Campaign c \r\n" + 
        		"JOIN Ad_Group a ON c.campaign_id = a.campaign_id\r\n" + 
        		"JOIN Ad_Group_Performance pf ON a.ad_group_id = pf.ad_group_id\r\n" + 
        		"JOIN Product p ON c.product_id = p.product_id\r\n" + 
        		"WHERE (campaign_name like ?))  \r\n" + 
        		"AND ad_group_sales > (\r\n" + 
        		"	SELECT \r\n" + 
        		"		avg(ad_group_sales) as 'avg sales'\r\n" + 
        		"    FROM \r\n" + 
        		"		Ad_Group_Performance\r\n" + 
        		")\r\n" + 
        		"ORDER BY ad_group_sales desc;",
        		new String [] {"Campaign Strategy"}, new boolean [] {true}, false, true));
    

    public int GetTotalQueries()
    {
        return m_queryArray.size();
    }
    
    public int GetParameterAmtForQuery(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetParmAmount();
    }
              
    public String GetParamText(int queryChoice, int parmnum)
    {
       QueryData e=m_queryArray.get(queryChoice);        
       return e.GetParamText(parmnum); 
    }   

    public String GetQueryText(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetQueryString();        
    }
    
    /**
     * Function will return how many rows were updated as a result
     * of the update query
     * @return Returns how many rows were updated
     */
    
    public int GetUpdateAmount()
    {
        return m_updateAmount;
    }
    
    /**
     * Function will return ALL of the Column Headers from the query
     * @return Returns array of column headers
     */
    public String [] GetQueryHeaders()
    {
        return m_jdbcData.GetHeaders();
    }
    
    /**
     * After the query has been run, all of the data has been captured into
     * a multi-dimensional string array which contains all the row's. For each
     * row it also has all the column data. It is in string format
     * @return multi-dimensional array of String data based on the resultset 
     * from the query
     */
    public String[][] GetQueryData()
    {
        return m_jdbcData.GetData();
    }

    public String GetProjectTeamApplication()
    {
        return m_projectTeamApplication;        
    }
    public boolean  isActionQuery (int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryAction();
    }
    
    public boolean isParameterQuery(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryParm();
    }
    
     
    public boolean ExecuteQuery(int queryChoice, String [] parms)
    {
        boolean bOK = true;
        QueryData e=m_queryArray.get(queryChoice);        
        bOK = m_jdbcData.ExecuteQuery(e.GetQueryString(), parms, e.GetAllLikeParams());
        return bOK;
    }
    
     public boolean ExecuteUpdate(int queryChoice, String [] parms)
    {
        boolean bOK = true;
        QueryData e=m_queryArray.get(queryChoice);        
        bOK = m_jdbcData.ExecuteUpdate(e.GetQueryString(), parms);
        m_updateAmount = m_jdbcData.GetUpdateCount();
        return bOK;
    }   
    
      
    public boolean Connect(String szHost, String szUser, String szPass, String szDatabase)
    {

        boolean bConnect = m_jdbcData.ConnectToDatabase(szHost, szUser, szPass, szDatabase);
        if (bConnect == false)
            m_error = m_jdbcData.GetError();        
        return bConnect;
    }
    
    public boolean Disconnect()
    {
        // Disconnect the JDBCData Object
        boolean bConnect = m_jdbcData.CloseDatabase();
        if (!bConnect)
            m_error = m_jdbcData.GetError();
        return true;
    }
    
    public String GetError()
    {
        return m_error;
    }
 
    private QueryJDBC m_jdbcData;
    private String m_error;    
    private String m_projectTeamApplication;
    private ArrayList<QueryData> m_queryArray;  
    private int m_updateAmount;
            
    /**
     * @param args the command line arguments
     */
    

    
    public static void main(String[] args) {
        // TODO code application logic here

        final QueryRunner queryrunner = new QueryRunner();
        
        if (args.length == 0) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new QueryFrame(queryrunner).setVisible(true);
                }            
            });
        } else {
            if (args[0].equals ("-console")) {
            	System.out.println("Nothing has been implemented yet. Please implement the necessary code");
               // TODO 
                // You should code the following functionality:

                //    You need to determine if it is a parameter query. If it is, then
                //    you will need to ask the user to put in the values for the Parameters in your query
                //    you will then call ExecuteQuery or ExecuteUpdate (depending on whether it is an action query or regular query)
                //    if it is a regular query, you should then get the data by calling GetQueryData. You should then display this
                //    output. 
                //    If it is an action query, you will tell how many row's were affected by it.
                // 
                //    This is Psuedo Code for the task:  
                //    Connect()
                //    n = GetTotalQueries()
                //    for (i=0;i < n; i++)
                //    {
                //       Is it a query that Has Parameters
                //       Then
                //           amt = find out how many parameters it has
                //           Create a paramter array of strings for that amount
                //           for (j=0; j< amt; j++)
                //              Get The Paramater Label for Query and print it to console. Ask the user to enter a value
                //              Take the value you got and put it into your parameter array
                //           If it is an Action Query then
                //              call ExecuteUpdate to run the Query
                //              call GetUpdateAmount to find out how many rows were affected, and print that value
                //           else
                //               call ExecuteQuery 
                //               call GetQueryData to get the results back
                //               print out all the results
                //           end if
                //      }
                //    Disconnect()


                // NOTE - IF THERE ARE ANY ERRORS, please print the Error output
                // NOTE - The QueryRunner functions call the various JDBC Functions that are in QueryJDBC. If you would rather code JDBC
                // functions directly, you can choose to do that. It will be harder, but that is your option.
                // NOTE - You can look at the QueryRunner API calls that are in QueryFrame.java for assistance. You should not have to 
                //    alter any code in QueryJDBC, QueryData, or QueryFrame to make this work.
//                System.out.println("Please write the non-gui functionality");
                
            }
        }
 
    }    
}
