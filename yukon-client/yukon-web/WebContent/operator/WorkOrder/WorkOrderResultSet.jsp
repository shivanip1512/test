<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="woManipulationBean" class="com.cannontech.stars.web.bean.ManipulationBean" scope="session"/>
<jsp:useBean id="workOrderBean" class="com.cannontech.web.bean.WorkOrderBean" scope="session"/>

<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">
	
	<cti:includeCss link="/include/PurpleStyles.css"/>
	<div class="headerbar">
		<%@ include file="include/HeaderBar.jspf" %>
	</div>
 	<br clear="all"> 
 	
	<div class="standardpurplesidebox"> 
		<% String pageName = "WorkOrderResultSet.jsp"; %>
		<%@ include file="include/Nav.jspf" %>
	</div>

	<div class="standardcentralwhitebody">
		<div align="center"> <br>
            <% String header = "RESULTS"; %>
            <%@ include file="include/SearchBar.jspf" %>
            <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
            <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
    		<br clear="all">
    	</div>
    	
		<form name="MForm" method="post" action="<%=request.getContextPath()%>/servlet/WorkOrderManager" onsubmit="prepareSubmit(this)">
            <cti:csrfToken/>
	    	<input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>">
	    	<table width="80%" border="1" align="center" cellspacing="0" cellpadding="3" class="TableCell">
    			<tr> 
        			<td class="HeaderCell" width="100%">The following actions were applied to <c:out value="${woManipulationBean.successes}"/> Work Orders.</td>
      			</tr>
      			<c:forEach var="actionEntry" items="${woManipulationBean.actionsApplied}">
					<tr>		
						<td align="left" width="100%"><c:out value="${actionEntry}"/><td>
					</tr>
				</c:forEach>
    		</table>
        	<br>
	        <c:if test="${woManipulationBean.failures > 0}"> 
	        	<table width="80%" border="1" align="center" cellspacing="0" cellpadding="3" class="TableCell">
	    			<tr> 
	        			<td class="HeaderCell" width="100%"><c:out value="${woManipulationBean.failures}"/> failures detected.</td>
	      			</tr>
	      			<c:forEach var="failedMsg" items="${woManipulationBean.failedManipulateResults}">
					<tr>		
					  <td align="left" width="100%"><c:out value="${failedMsg}"/><td>
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
	                   	<input type="button" name="WOReturn" value="View Work Orders" onclick="returnToWorkOrders(this.form)">
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
		    form.REDIRECT.value += "/operator/WorkOrder/WOFilter.jsp";
		    form.submit();
		}
        
		function returnToWorkOrders(form)
		{
			if (!confirm("Existing filters may no longer return the same Work Orders.  Continue to Work Order view anyway?")) return;
		    form.REDIRECT.value += "/operator/WorkOrder/WorkOrder.jsp";
		    form.submit();
		}
	</script>
</cti:standardPage>          
