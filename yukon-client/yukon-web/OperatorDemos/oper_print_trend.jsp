<%@ include file="oper_header.jsp" %>
<%@ include file="oper_trendingheader.jsp" %>  
<%
             if( graphDefinitionId <= 0 )
             {
            %>
            <p>
              <center>
                No Data Set Selected 
               </center> 
           </p>
            
<%
             }
             else             
             //Check to see which tab is selected (tab paramater) and show the appropriate content
             if( tab.equalsIgnoreCase("summary") )
             {
              %>
<%@ include file="/trendingsummary.jsp" %>
<%
             }
             else
             if( tab.equalsIgnoreCase("tab") )
             {
              %>
<%@ include file="/trendingtabular.jsp" %>
<%
             }
             else // "graph" is default
             {
              %>
<img src="/servlet/GraphGenerator?<%="db=" + dbAlias + "&gdefid=" + graphDefinitionId + "&width=" + width + "&height=" + height + "&format=gif&start=" + dateFormat.format(start) + "&end=" + dateFormat.format(stop)+ "&model=" + modelType%>"> 

<%
             }
          %>
<a href="<%= referrer %>"><font color="#000000" face="Arial, Helvetica, sans-serif" size="2">Back</font></a> 
