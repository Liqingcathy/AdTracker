/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queryrunner;

/**
 *
 * @author mckeem
 */
public class QueryData {
     QueryData(String title, String query, String[] parms,
               boolean [] likeparms, boolean isAction, boolean isParm) {
         queryTitle = title;
         queryString = query;
         arrayParms = parms;
         arrayLikeParms = likeparms;
         this.isAction = isAction;
         isParms = isParm;
    }

    String GetQueryString()
    {
        return queryString;
    }

    // New method.
    String GetTitle() { return queryTitle; }
    
    int GetParmAmount() {
        if (arrayParms == null)
            return 0;
        else
            return arrayParms.length;
    }
    
  
    String GetParamText(int index) {
        return arrayParms[index];
    }
    
    boolean GetLikeParam(int index) {
        return arrayLikeParms[index];
    }
    
    boolean [] GetAllLikeParams() {
        return arrayLikeParms;
    }
    
    boolean IsQueryAction() {
        return isAction;
    }
    
    boolean IsQueryParm()
    {
        return isParms;
    }

    private String queryTitle;
    private String queryString;
    private String [] arrayParms;
    private boolean isAction;
    private boolean isParms;
    private boolean [] arrayLikeParms;
}
