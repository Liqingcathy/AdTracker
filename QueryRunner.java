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
 * and provides functions that will call the various functions in the QueryJDBC
 * class which will enable MYSQL queries to be executed. It also has functions
 * to provide the returned data from the Queries. Currently the eventHandlers in
 * QueryFrame call these functions in order to run the Queries.
 */
public class QueryRunner {
    
    public QueryRunner() {
        this.m_jdbcData = new QueryJDBC();
        m_updateAmount = 0;
        m_queryArray = new ArrayList<>();
        m_error="";
        
        this.m_projectTeamApplication="ADOPTIMIZER";

        /*
        Each row that is added to m_queryArray is a separate query. It does
        not work on Stored procedure calls. The 'new' Java keyword is a way of
        initializing the data that will be added to QueryArray.

        Format for each row of m_queryArray is: (QueryText, ParamaterLabelArray[],
        LikeParameterArray[], IsItActionQuery, IsItParameterQuery).

        QueryText is a String that represents your query.
            - Label Array: null if there is no parameters in your query,
            otherwise put in the parameter names.
            - LikeParameter Array: necessary to tell QueryRunner which parameter
            has a LIKE Clause. If you have no parameters, put in null. Put in
            false for parameters that don't use 'like' and true for ones that do.
            - IsItActionQuery: mark it true if it is, otherwise false.
            - IsItParameterQuery: mark it true if it is, otherwise false.
        */

        // Examples:
        // m_queryArray.add(new QueryData("Select * from contact where contact_id=?",
        // new String [] {"CONTACT_ID"}, new boolean [] {false},  false, true));
        // m_queryArray.add(new QueryData("Select * from contact where contact_name like ?",
        // new String [] {"CONTACT_NAME"}, new boolean [] {true}, false, true));
        // m_queryArray.add(new QueryData("insert into contact (contact_id, contact_name, contact_salary) values (?,?,?)",
        // new String [] {"CONTACT_ID", "CONTACT_NAME", "CONTACT_SALARY"}, new boolean [] {false, false, false}, true, true));

        // PRODUCT QUERIES

        // Search for a product.
        m_queryArray.add(new QueryData(
            "SELECT " +
                "seller_name, " +
                "product_name, product_rating, " +
                "product_reviews, product_price " +
            "FROM Seller \n" +
            "JOIN Product USING (seller_id) " +
            "WHERE seller_name LIKE CONCAT('%', ?, '%') " +
            "AND product_name LIKE CONCAT('%', ?, '%') " +
            "ORDER BY seller_name, product_name;",
            new String [] {"Seller", "Product"}, new boolean [] {true, true},  false, true));

        // Overview of product performance by seller.
        m_queryArray.add(new QueryData(
            "SELECT " +
                "seller_name, " +
                "round(avg(product_rating), 1) AVGRATING, " +
                "round(avg(product_reviews), 0) AVGREVIEWS, " +
                "round(avg(product_price), 2) AVGPRICE " +
            "FROM Seller " +
            "JOIN Product USING (seller_id) " +
            "GROUP BY seller_id " +
            "ORDER BY AVGRATING desc, AVGREVIEWS desc, AVGPRICE;",
            null, null, false, true));
        
        // Allows users to catch a glimpse of the top 5 rated products in a given
        // category
        // User input: outdoors, electronics, clothing
        m_queryArray.add(new QueryData(
            "SELECT P.product_id, product_name, seller_name, " +
                "product_description as description, product_price as price, " +
                "product_rating as rating, product_reviews as reviews, " +
                "C.manager_id, campaign_id " +
            "FROM Product P Join Seller USING (seller_id) Join Campaign C USING (seller_id) " +
            "WHERE product_description LIKE CONCAT('%', ?, '%') " +
            "Order By P.product_rating DESC, P.product_reviews DESC " +
            "LIMIT 5",
            new String[] {"Product Category"}, new boolean [] {true}, false, true));

        // Insert new product.
        m_queryArray.add(new QueryData(
                "INSERT INTO Product (product_name, seller_id, " +
                        "product_description, product_price) values (?,?,?,?);",
                new String [] {"product_name", "seller_id", "product_description", "product_price"},
                new boolean [] {false, false, false, false}, true, true));

        
        // CAMPAIGN QUERIES

        //Overview of top performing ad campaigns and ad groups
        m_queryArray.add(new QueryData(
            "	SELECT " +
                "campaign_id, " +
        	    "campaign_name, " +
        	    "ad_group_name, ad_group_impressions as impressions, " +
        	    "ad_group_clicks as clicks, ad_group_cpc as cpc, " +
        	    "ad_group_spends as spends, ad_group_sales as sales," +
        	    "ad_group_orders as orders, " +
        	    "round((ad_group_orders / ad_group_clicks)*100, 2) as \"conv rate(%)\"," +
        	    "ad_group_acos as ACOS, ad_group_roas as ROAS " +
        	"FROM Campaign " +
        	"JOIN Ad_Group USING(campaign_id) " +
        	"JOIN Ad_Group_Performance USING(ad_group_id) " +
        	"WHERE ad_group_acos < 0.3 or ad_group_roas > 0.5 " +
        	"ORDER BY ad_group_acos, ad_group_id;",
    		null, null, false, false));
        
        // Overview of top 5 performing managers by clicks. Can be deleted if we have enough other queries
        m_queryArray.add(new QueryData(
            "Select manager_id, manager_first_name, manager_last_name, " +
                "campaign_id, campaign_name, campaign_clicks " +
                "FROM Account_Manager " +
                "JOIN Campaign USING (manager_id)" +
                "JOIN Campaign_Performance USING (campaign_id) " +
                "WHERE campaign_clicks = (Select MAX(campaign_clicks) FROM Campaign_Performance" +
                                        " GROUP BY manager_ID)" +
                "ORDER BY campaign_clicks DESC",
                null, null, false, false));

        // Overview of top performing ad campaigns and ad groups.
        // User intput: ACOS, ROAS. Doesn't filter.
        // Might not be able to filer numbers - Nathan
        m_queryArray.add(new QueryData(
        	"	SELECT " +
        	    "campaign_id, " +
        	    "campaign_name, " +
        	    "ad_group_name, ad_group_impressions as impressions, " +
        	    "ad_group_clicks as clicks, ad_group_cpc as cpc, " +
        	    "ad_group_spends as spends, ad_group_sales as sales, " +
        	    "ad_group_orders as orders, " +
        	    "round((ad_group_orders / ad_group_clicks)*100, 2) as \"conv rate(%)\", " +
        	    "ad_group_acos as ACOS, ad_group_roas as ROAS " +
        	"FROM Campaign " +
        	"JOIN Ad_Group USING (campaign_id) " +
        	"JOIN Ad_Group_Performance USING (ad_group_id) " +
        	// Tried some things but still doesn't work.
            // UPDATE: Appears to be working as expected now
            "WHERE ad_group_acos > ? " +
                "AND ad_group_roas < ? " +
        	"ORDER BY ad_group_acos, ad_group_id;",
        	new String [] {"ACOS", "ROAS"}, new boolean [] {false, false},  false, true));
        
         // Allow user to search for open ad groups and ad group name 
         // containing the name of a targeted product and ad group type (e.g. 
         // User input: ad group name: tent, keyboard, shirt 
         //             ad group type (sponsored): brand, product
         m_queryArray.add(new QueryData(
                "SELECT ad_group_id, ad_group_name, ad_group_start, " +
                "ad_group_end, ad_group_impressions as impressions, ad_group_clicks as clicks, " +
                "ad_group_cpc as cpc, ad_group_ctr as 'ctr(%)', ad_group_sales as sales, " +
                "ad_group_spends as spends, ad_group_acos AS ACOS, ad_group_roas as ROAS " +
                "FROM Ad_Group " +
                "JOIN Ad_Group_Performance USING (ad_group_id) " +
                "WHERE ad_group_name LIKE CONCAT('%', ?, '%') AND ad_group_type LIKE CONCAT('%', ?, '%')  " +
                "HAVING ad_group_end IS NULL " +
                "ORDER BY sales DESC",
                new String [] {"Ad Group Name", "Ad Group Type"}, new boolean [] {true, true},
                false, true));

        // Top performing keyword.
        m_queryArray.add(new QueryData(
        	"	SELECT \r\n" +
        	    "ad_group_name, ad_group_budget, " +
        	    "keyword, keyword_impressions as impressions, " +
        	    "keyword_clicks as clicks , keyword_ctr as 'ctr(%)', keyword_cpc as cpc, " +
        	    "keyword_orders as orders, " +
        	    "round((keyword_orders / keyword_clicks)*100, 2) as 'conv rate(%)', " +
        	    "keyword_spends as spends , keyword_sales as sales, " +
        	    "keyword_acos as ACOS , keyword_roas as ROAS " +
        	"FROM Keyword " +
        	"JOIN Ad_Group USING (ad_group_id) " +
        	"JOIN Keyword_Performance USING (keyword_id) " +
        	"WHERE " +
        	    "keyword_ctr > 0.4 " +
        	    "AND keyword_acos < 0.7 " +
        	    "AND keyword_roas > 0.4 " +
        	"ORDER BY keyword_acos asc;",
        	null, null, false, false));
    
        // Good performing ads groups with sales greater than average.
        // User input: campaign_name with special strategy(competitor, defensive, generic)
        m_queryArray.add(new QueryData(
        	"SELECT " +
        	    "c.campaign_id, c.campaign_name, " +
        	    "a.ad_group_start, a.ad_group_name, " +
        	    "p.product_name, p.product_description as 'prod descript', " +
        	    "p.product_price as price, " +
        	    "pf.ad_group_orders as orders, pf.ad_group_sales as sales, " +
        	    "round((ad_group_sales / ad_group_orders), 0) as 'sales unit' " +
        	"FROM Campaign c " +
        	"JOIN Ad_Group a ON c.campaign_id = a.campaign_id " +
        	"JOIN Ad_Group_Performance pf ON a.ad_group_id = pf.ad_group_id " +
        	"JOIN Product p ON c.product_id = p.product_id " +
        	"WHERE campaign_name LIKE CONCAT('%', ?, '%') " +
        	"AND ad_group_sales > (" +
        	    "SELECT avg(ad_group_sales) as 'avg sales' " +
        	    "FROM Ad_Group_Performance) " +
        	"ORDER BY ad_group_sales desc;",
        	new String [] {"Campaign Name"}, new boolean [] {true}, false, true));
    }
    
    public int GetTotalQueries() {
        return m_queryArray.size();
    }
    
    public int GetParameterAmtForQuery(int queryChoice) {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetParmAmount();
    }
              
    public String GetParamText(int queryChoice, int parmnum) {
       QueryData e=m_queryArray.get(queryChoice);        
       return e.GetParamText(parmnum); 
    }   

    public String GetQueryText(int queryChoice) {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetQueryString();        
    }
    
    /**
     * Function will return how many rows were updated as a result
     * of the update query.
     * @return Returns how many rows were updated
     */
    public int GetUpdateAmount() {
        return m_updateAmount;
    }
    
    /**
     * Function will return ALL of the Column Headers from the query.
     * @return Returns array of column headers
     */
    public String [] GetQueryHeaders() {
        return m_jdbcData.GetHeaders();
    }
    
    /**
     * After the query has been run, all of the data has been captured into
     * a multi-dimensional string array which contains all the row's. For each
     * row it also has all the column data. It is in string format.
     * @return multi-dimensional array of String data based on the resultset 
     * from the query
     */
    public String[][] GetQueryData() {
        return m_jdbcData.GetData();
    }

    public String GetProjectTeamApplication() {
        return m_projectTeamApplication;        
    }
    public boolean  isActionQuery (int queryChoice) {
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryAction();
    }
    
    public boolean isParameterQuery(int queryChoice) {
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryParm();
    }
    
     
    public boolean ExecuteQuery(int queryChoice, String [] parms) {
        boolean bOK = true;
        QueryData e=m_queryArray.get(queryChoice);        
        bOK = m_jdbcData.ExecuteQuery(e.GetQueryString(), parms,
                e.GetAllLikeParams());
        return bOK;
    }
    
     public boolean ExecuteUpdate(int queryChoice, String [] parms) {
        boolean bOK = true;
        QueryData e=m_queryArray.get(queryChoice);        
        bOK = m_jdbcData.ExecuteUpdate(e.GetQueryString(), parms);
        m_updateAmount = m_jdbcData.GetUpdateCount();
        return bOK;
    }   
    
      
    public boolean Connect(String szHost, String szUser, String szPass,
                           String szDatabase) {
        boolean bConnect = m_jdbcData.ConnectToDatabase(szHost, szUser, szPass,
                szDatabase);
        if (bConnect == false)
            m_error = m_jdbcData.GetError();        
        return bConnect;
    }
    
    public boolean Disconnect() {
        // Disconnect the JDBCData Object
        boolean bConnect = m_jdbcData.CloseDatabase();
        if (!bConnect)
            m_error = m_jdbcData.GetError();
        return true;
    }
    
    public String GetError() {
        return m_error;
    }
 
    private QueryJDBC m_jdbcData;
    private String m_error;    
    private String m_projectTeamApplication;
    private ArrayList<QueryData> m_queryArray;  
    private int m_updateAmount;
            
    /**
     * @param args the command line arguments.
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
