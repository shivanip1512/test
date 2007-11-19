<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="manipBean" class="com.cannontech.stars.web.bean.ManipulationBean" scope="session"/>
<jsp:useBean id="inventoryBean" class="com.cannontech.stars.web.bean.InventoryBean" scope="session"/>

<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">
	
	<cti:includeCss link="/include/PurpleStyles.css"/>
	<div class="headerbar">
		<%@ include file="include/HeaderBar.jspf" %>
	</div>
 	<br clear="all"> 
 	
	<div class="standardpurplesidebox"> 
		<% String pageName = "InvenResultSet.jsp"; %>
		<div align="right">
			<%@ include file="include/Nav.jspf" %>
		</div>
	</div>

	<div class="standardcentralwhitebody">
		<div align="center"> <br>
            <% String header = "RESULTS"; %>
            <%@ include file="include/SearchBar.jspf" %>
            <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
            <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
    		<br clear="all">
    	</div>
    	
		<form name="MForm" method="post" action="<%=request.getContextPath()%>/servlet/InventoryManager">
	    	<input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>">
	    	<table width="80%" border="1" align="center" cellspacing="1" cellpadding="3" class="TableCell">
    			<tr> 
        			<td class="HeaderCell" width="100%">The following actions were applied to <c:out value="${manipBean.successes}"/> hardware entries.</td>
      			</tr>
      			<c:forEach var="actionEntry" items="${manipBean.actionsApplied}">
					<tr>		
						<td>
							<div align="left"><c:out value="${actionEntry}"/></div>
						<td>
					</tr>
				</c:forEach>
    		</table>
        	<br>
	        <c:if test="${manipBean.failures > 0}"> 
	        	<table width="80%" border="1" align="center" cellspacing="0" cellpadding="0" class="TableCell">
	    			<tr> 
	        			<td class="HeaderCell" width="100%"><c:out value="${manipBean.failures}"/> failures detected.</td>
	      			</tr>
	      			<c:forEach var="serial" items="${manipBean.failedManipulateResults}">
						<tr>		
							<td>
								<div align="left"><c:out value="${serial}"/></div>
							<td>
						</tr>
					</c:forEach>
	    		</table>
	    	</c:if>
        	<br>
        	<table width="80%" border="0" cellspacing="0" cellpadding="0" align="center" class="TableCell">
			 	<tr>
					<td width="100" align="right"> 
	                   	<input type="button" name="FilterReturn" value="Return to Filters" onclick="returnToFilters(this.form)">
	               	</td>
	               	<td width="500"> 
	                   	<input type="button" name="InvenReturn" value="View Inventory" onclick="returnToInven(this.form)">
	               	</td>
                </tr>
			</table>
        </form>
    </div>
    
    <script language="JavaScript">
		function init()
		{
		}
		
		function returnToFilters(form)
		{
		    form.REDIRECT.value += "/operator/Hardware/Filter.jsp";
		    form.submit();
		}
        
		function returnToInven(form)
		{
			if (!confirm("Existing filters may no longer return the same device set.  Continue to inventory view anyway?")) return;
		    form.REDIRECT.value += "/operator/Hardware/Inventory.jsp";
		    form.submit();
		}
	</script>
</cti:standardPage>          
