/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queryrunner;

import java.util.ArrayList;

/**
 *
 * @author mckeem
 */
public class QueryData {
     QueryData() {
    }
//    QueryData(String query)
//    {
//        m_queryString = query;
//    }
    
    QueryData(String query, String[] parms, boolean [] likeparms,
              boolean isAction, boolean isParm) {
        queryString = query;
        arrayParms = parms;
        arrayLikeParms = likeparms;
        isAction = isAction;
        isParms = isParm;
    }
    
//    void Set(String query, ArrayList<String>parms, boolean isAction, boolean isParm)
//    {
//        m_queryString = query;
//        m_arrayParms = parms;
//        m_isAction = isAction;
//        m_isParms = isParm;
//    }
    
    String GetQueryString()
    {
        return queryString;
    }
    
    int GetParmAmount() {
        if (arrayParms == null)
            return 0;
        else
            return arrayParms.length;
    }
    
  
    String GetParamText(int index)
    {
        return arrayParms[index];
    }
    
    boolean GetLikeParam(int index)
    {
        return arrayLikeParms[index];
    }
    
    boolean [] GetAllLikeParams()
    {
        return arrayLikeParms;
    }
    
    boolean IsQueryAction()
    {
        return isAction;
    }
    
    boolean IsQueryParm()
    {
        return isParms;
    }
     
    private String queryString;
    private String [] arrayParms;
    private boolean isAction;
    private boolean isParms;
    private boolean [] arrayLikeParms;
}
