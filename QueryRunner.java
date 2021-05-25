/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queryrunner;

import javax.net.ssl.SSLException;
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
        this.jdbcData = new QueryJDBC();
        updateAmount = 0;
        queryArray = new ArrayList<>();
        error="";
        
        this.projectTeamApplication="ADOPTIMIZER";

        /*
        Each row that is added to m_queryArray is a separate query. It does
        not work on stored procedure calls. The 'new' Java keyword is a way of
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

        // PRODUCT QUERIES

        // Search for a product.
        queryArray.add(new QueryData(
         "SELECT " +
                "seller_name, product_name, product_rating, \n\t" +
                "product_reviews product_price \n" +
         "FROM Seller \n" +
         "JOIN Product USING (seller_id) \n" +
         "WHERE seller_name LIKE CONCAT('%', ?, '%') \n\t" +
                "AND product_name LIKE CONCAT('%', ?, '%') \n" +
         "ORDER BY seller_name, product_name;",
         new String [] {"Seller", "Product"}, new boolean [] {true, true},
         false, true));

        // Overview of product performance by seller.
        queryArray.add(new QueryData(
        "SELECT " + 
               "seller_name, round(avg(product_rating), 1) AVGRATING, \n\t" +
               "round(avg(product_reviews), 0) AVGREVIEWS, \n\t" +
               "round(avg(product_price), 2) AVGPRICE \n" + 
        "FROM Seller \n" + 
        "JOIN Product USING (seller_id) \n" +
        "GROUP BY seller_id \n" +
        "ORDER BY AVGRATING desc, AVGREVIEWS desc, AVGPRICE;",
        null, null, false, true));
        
        // Allows users to catch a glimpse of the top 5 rated products in a given
        // category
        // User input: outdoors, electronics, clothing
        queryArray.add(new QueryData(
        "SELECT " + 
               "P.product_id, product_name, seller_name,\n\t" + 
               "product_description as description, product_price as price,\n\t" + 
               "product_rating as rating, product_reviews as reviews,\n\t" +
               "C.manager_id, campaign_id\n" +
        "FROM Product P Join Seller USING (seller_id) Join Campaign C USING (seller_id)\n" +
        "WHERE product_description LIKE CONCAT('%', ?, '%')\n" +
        "ORDER BY P.product_rating DESC, P.product_reviews DESC\n" +
        "LIMIT 5",
        new String[] {"Product Category"}, new boolean [] {true},
        false, true));

        // Insert new product.
        queryArray.add(new QueryData(
        "INSERT INTO Product \n\t" +
                "(product_name, seller_id, product_description, " +
                "product_price)\n" +
        "VALUES (?,?,?,?);",
        new String [] {"Product Name", "Seller ID",
                       "Product Description", "Product Price"},
        new boolean [] {false, false, false, false},
        true, true));

        // CAMPAIGN QUERIES

        //Overview of top performing ad campaigns and ad groups
        queryArray.add(new QueryData(
         "SELECT " +
                "campaign_id, campaign_name, ad_group_name, \n\t" +
                "ad_group_impressions as impressions, \n\t" +
                "ad_group_clicks as clicks, ad_group_cpc as cpc, \n\t" +
                "ad_group_spends as spends, ad_group_sales as sales, \n\t" +
                "ad_group_orders as orders, \n\t" +
                "round((ad_group_orders / ad_group_clicks)*100, 2) as \"conv rate(%)\", \n\t" +
                "ad_group_acos as ACOS, ad_group_roas as ROAS \n" +
        "FROM Campaign \n" +
        "JOIN Ad_Group USING(campaign_id) \n" +
        "JOIN Ad_Group_Performance USING(ad_group_id) \n" +
        "WHERE ad_group_acos < 0.3 or ad_group_roas > 0.5 \n" +
        "ORDER BY ad_group_acos, ad_group_id;",
    	null, null, false, false));
        
        // Overview of top 5 performing managers by clicks. Can be deleted if we have enough other queries
        queryArray.add(new QueryData(
         "Select " +
                 "manager_id, manager_first_name, manager_last_name, \n\t" +
                 "campaign_id, campaign_name, campaign_clicks \n" +
         "FROM Account_Manager \n" +
         "JOIN Campaign USING (manager_id)\n" +
         "JOIN Campaign_Performance USING (campaign_id) \n" +
         "WHERE campaign_clicks = (Select MAX(campaign_clicks) \n\t\t\t\t\t\t " +
                                  "FROM Campaign_Performance \n\t\t\t\t\t\t " +
                                  "GROUP BY manager_ID) \n" +
         "ORDER BY campaign_clicks DESC",
         null, null, false, false));

        // Overview of top performing ad campaigns and ad groups.
        // User intput: ACOS, ROAS. Doesn't filter.
        // Might not be able to filer numbers - Nathan
        queryArray.add(new QueryData(
         "SELECT " +
                 "campaign_id, campaign_name, ad_group_name, \n\t" +
                 "ad_group_impressions as impressions, \n\t" +
                 "ad_group_clicks as clicks, ad_group_cpc as cpc, \n\t" +
                 "ad_group_spends as spends, ad_group_sales as sales, \n\t" +
                 "ad_group_orders as orders, \n\t" +
                 "round((ad_group_orders / ad_group_clicks)*100, 2) " +
                 "as \"conv rate(%)\", \n\t" +
                 "ad_group_acos as ACOS, ad_group_roas as ROAS \n" +
         "FROM Campaign \n" +
         "JOIN Ad_Group USING (campaign_id) \n" +
         "JOIN Ad_Group_Performance USING (ad_group_id) \n" +
         "WHERE ad_group_acos > ? AND ad_group_roas < ? \n" +
         "ORDER BY ad_group_acos, ad_group_id;",
         new String [] {"ACOS", "ROAS"}, new boolean [] {false, false},
         false, true));
        
         // Allow user to search for open ad groups and ad group name 
         // containing the name of a targeted product and ad group type (e.g. 
         // User input: ad group name: tent, keyboard, shirt 
         //             ad group type (sponsored): brand, product
         queryArray.add(new QueryData(
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
        queryArray.add(new QueryData(
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
        queryArray.add(new QueryData(
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
        return queryArray.size();
    }
    
    public int GetParameterAmtForQuery(int queryChoice) {
        QueryData e = queryArray.get(queryChoice);
        return e.GetParmAmount();
    }
              
    public String GetParamText(int queryChoice, int paramNum) {
       QueryData e = queryArray.get(queryChoice);
       return e.GetParamText(paramNum);
    }   

    public String GetQueryText(int queryChoice) {
        QueryData e = queryArray.get(queryChoice);
        return e.GetQueryString();        
    }
    
    /**
     * Function will return how many rows were updated as a result
     * of the update query.
     * @return Returns how many rows were updated
     */
    public int GetUpdateAmount() {
        return updateAmount;
    }
    
    /**
     * Function will return ALL of the Column Headers from the query.
     * @return Returns array of column headers
     */
    public String [] GetQueryHeaders() {
        return jdbcData.GetHeaders();
    }
    
    /**
     * After the query has been run, all of the data has been captured into
     * a multi-dimensional string array which contains all the row's. For each
     * row it also has all the column data. It is in string format.
     * @return multi-dimensional array of String data based on the resultset 
     * from the query
     */
    public String[][] GetQueryData() {
        return jdbcData.GetData();
    }

    public String GetProjectTeamApplication() {
        return projectTeamApplication;
    }
    public boolean isActionQuery (int queryChoice) {
        QueryData e = queryArray.get(queryChoice);
        return e.IsQueryAction();
    }
    
    public boolean isParameterQuery(int queryChoice) {
        QueryData e = queryArray.get(queryChoice);
        return e.IsQueryParm();
    }
    
     
    public boolean ExecuteQuery(int queryChoice, String [] params) {
        boolean bOK = true;
        QueryData e = queryArray.get(queryChoice);
        bOK = jdbcData.ExecuteQuery(e.GetQueryString(), params,
                e.GetAllLikeParams());
        return bOK;
    }
    
    public boolean ExecuteUpdate(int queryChoice, String [] parms) {
        boolean bOK = true;
        QueryData e = queryArray.get(queryChoice);
        bOK = jdbcData.ExecuteUpdate(e.GetQueryString(), parms);
        updateAmount = jdbcData.GetUpdateCount();
        return bOK;
    }
      
    public boolean Connect(String szHost, String szUser, String szPass,
                           String szDatabase) {
        boolean bConnect = jdbcData.ConnectToDatabase(szHost, szUser, szPass,
                szDatabase);
        if (bConnect == false)
            error = jdbcData.GetError();
        return bConnect;
    }
    
    public boolean Disconnect() {
        // Disconnect the JDBCData Object
        boolean bConnect = jdbcData.CloseDatabase();
        if (!bConnect)
            error = jdbcData.GetError();
        return true;
    }
    
    public String GetError() {
        return error;
    }
 
    private QueryJDBC jdbcData;
    private String error;
    private String projectTeamApplication;
    private ArrayList<QueryData> queryArray;
    private int updateAmount;
            
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
                // TODO

                // Create Scanner object.
                Scanner keyboard = new Scanner(System.in);

                // Connect()
                queryrunner.Connect(
                        "database-1.crvrlpwsgqaw.us-east-1.rds.amazonaws.com",
                        "admin", "group3aws", "Group3");

                // n = GetTotalQueries()
                int n = queryrunner.GetTotalQueries();

                // Add empty line.
                System.out.println();

                // Iterate through queries.
                for (int i = 0; i < n; i++) {

                    // Initialize a parameter array to null.
                    String[] paramArray = {};

                    // Print query.
                    System.out.println(queryrunner.GetQueryText(i));

                    // Check if query has parameters.
                    if (queryrunner.isParameterQuery(i)) {
                        // amt = find out how many parameters it has
                        int amt = queryrunner.GetParameterAmtForQuery(i);

                        // Create a parameter array of strings for that amount
                        paramArray = new String[amt];

                        System.out.println();
                        // for (j=0; j< amt; j++)
                        for (int j = 0; j < amt; j++) {
                            // Get the parameter label for query and print it to
                            // console. Ask the user to enter a value
                            System.out.print(queryrunner.GetParamText(i, j) + ": ");

                            // Take the value and put it into parameter array
                            paramArray[j] = keyboard.nextLine();
                        }
                    }
                    // If it is an action query then
                    if (queryrunner.isActionQuery(i)) {
                        // call ExecuteUpdate to run the Query
                        queryrunner.ExecuteUpdate(i, paramArray);

                        // call GetUpdateAmount to find out how many rows
                        // were affected, and print that value
                        System.out.println(queryrunner.GetUpdateAmount() +
                                " rows affected.\n");

                    } else {
                        // call ExecuteQuery
                        queryrunner.ExecuteQuery(i, paramArray);

                        // call GetQueryData to get the results back
                        String[] headers = queryrunner.GetQueryHeaders();
                        String[][] data = queryrunner.GetQueryData();

                        // Print out all the results
                        System.out.println();
                        for (int h = 0; h < headers.length; h++) {
                            System.out.printf("%-32s", headers[h]);
                        }
                        System.out.println();

                        for (int r = 0; r < data.length; r++) {
                            for (int c = 0; c < data[0].length; c++) {
                                System.out.printf("%-32s", data[r][c]);
                            }
                            System.out.println();
                        }
                        System.out.println();
                    }
                }
                // Close Scanner.
                keyboard.close();

                System.out.println("\nErrors: " + queryrunner.GetError());

                // Disconnect()
                queryrunner.Disconnect();


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
