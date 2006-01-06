<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="inventoryBean" class="com.cannontech.stars.web.bean.InventoryBean" scope="session"/>

<cti:standardPage title="Energy Services Operations Center" module="stars">
	
	<link rel="stylesheet" href="../../include/PurpleStyles.css" type="text/css">
	<div class="headerbar">
		<%@ include file="include/HeaderBar.jsp" %>
	</div>
 	<br clear="all"> 
 	
    <div class="standardpurplesidebox"> 
	</div>

	<div class="standardcentralwhitebody">
		<div align="center"> <br>
            <% String header = "MANIPULATE INVENTORY"; %>
            <%@ include file="include/SearchBar.jsp" %>
    		<br clear="all">
    	</div>
    	
		<form name="MForm" method="post" action="<%=request.getContextPath()%>/servlet/InventoryManager" onsubmit="prepareSubmit(this)">
	    	<input type="hidden" name="action" value="">
	    	
	    	
	    	
	    	<table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
            	<tr>
                	<td width="290" align="right"> 
                    	<input type="submit" name="Submit" value="Submit">
                  	</td>
                  	<td width="205"> 
                    	<input type="reset" name="Reset" value="Reset" onclick="location.reload()">
                  	</td>
                  	<td width="75" align="right"> 
                    	<input type="button" name="Back" value="Back" onclick="if (warnUnsavedChanges()) location.href='Inventory.jsp'">
                  	</td>
              	</tr>
        	</table>
		</form>
    </div>
    
    <script language="JavaScript">
	</script>
</cti:standardPage>          
