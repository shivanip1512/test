<%@ include file="user_header.jsp" %>
<%@ include file="user_trendingheader.jsp" %>  

<link rel="stylesheet" href="../demostyle.css" type="text/css">
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
<%@ include file="../trendingsummary.jsp" %>
<%
             }
             else
             if( tab.equalsIgnoreCase("tab") )
             {
              %>
<%@ include file="../trendingtabular.jsp" %>
<%
             }
             else // "graph" is default
             {
              %>
<img src="/servlet/GraphGenerator?<%="db=" + dbAlias + "&gdefid=" + graphDefinitionId + "&width=" + width + "&height=" + height + "&format=pmg&start=" + dateFormat.format(start) + "&end=" + dateFormat.format(stop)+ "&model=" + modelType%>"> 

<%
             }
          %><a href="<%= referrer %>" class = "Link1"><span class = "Main">Back</span></a> 
