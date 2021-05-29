/*
 * Group 3
 * CPSC 5021, Seattle University
 * This is free and unencumbered software released into the public domain.
 */
package queryrunner;

/**
 * The Class QueryData creates query objects
 * with the associated parameters.
 *
 * @author mckeem, hadley
 */
public class QueryData {

    /**
     * Instantiates a new query data.
     *
     * @param title description of query
     * @param query the query
     * @param parms the parameters
     * @param likeparms the parameters using like
     * @param isAction the is action
     * @param isParm the is parameters
     */
     QueryData(String title, String query, String[] parms,
               boolean [] likeparms, boolean isAction, boolean isParm) {
         queryTitle = title;
         queryString = query;
         arrayParms = parms;
         arrayLikeParms = likeparms;
         this.isAction = isAction;
         isParms = isParm;
    }

    /**
     * Gets the query string.
     *
     * @return the string
     */
    String GetQueryString()
    {
        return queryString;
    }

    /**
     * Gets the query title.
     *
     * @return the title
     */
    String GetTitle() { return queryTitle; }

    /**
     * Gets the amount of parameters.
     *
     * @return the parameters
     */
    int GetParmAmount() {
        if (arrayParms == null)
            return 0;
        else
            return arrayParms.length;
    }


    /**
     * Gets the parameters text.
     *
     * @param index the index
     * @return the string
     */
    String GetParamText(int index) {
        return arrayParms[index];
    }

    /**
     * Gets the like parameters.
     *
     * @param index the index
     * @return true, if successful
     */
    boolean GetLikeParam(int index) {
        return arrayLikeParms[index];
    }

    /**
     * Gets the all parameters that use like.
     *
     * @return the boolean[]
     */
    boolean [] GetAllLikeParams() {
        return arrayLikeParms;
    }

    /**
     * Checks if action query (insert or update).
     *
     * @return true, if action
     */
    boolean IsQueryAction() {
        return isAction;
    }

    /**
     * Checks if is query parameter.
     *
     * @return true, if it contains parameters
     */
    boolean IsQueryParm()
    {
        return isParms;
    }

    private String queryTitle;          // Description of query
    private String queryString;         // Query string
    private String [] arrayParms;       // Query parameters
    private boolean isAction;           // Is it an action query?
    private boolean isParms;            // Does query have parameters?
    private boolean [] arrayLikeParms;  // Does query have like parameters?
}
