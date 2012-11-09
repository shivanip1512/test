<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.cannontech.web.bean.WorkOrderBean" %>

<jsp:useBean id="workOrderBean" class="com.cannontech.web.bean.WorkOrderBean" scope="session"/>
<jsp:setProperty name="workOrderBean" property="energyCompanyID" value="<%= user.getEnergyCompanyID() %>"/>
<%-- request.getParameter("page") == null) workOrderBean.resetInventoryList();--%>

<%--<jsp:setProperty name="workOrderBean" property="sortBy" param="SortBy"/>
<jsp:setProperty name="workOrderBean" property="sortOrder" param="SortOrder"/>--%>
<jsp:setProperty name="workOrderBean" property="page" param="page_"/>

<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../include/PurpleStyles.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function showAll(form) 
{
	
	form.submit();
}

function goFilter(form) 
{
	form.action.value = "";
	form.REDIRECT.value = "<%= request.getContextPath() %>/operator/WorkOrder/WOFilter.jsp";
	form.submit();
}

function viewAll(form, viewAll)
{
	if( viewAll)
    {
		form.action.value = "ViewAllResults";
    }
	else
		form.action.value = "HideAllResults";
		
    form.submit();
}
        
function manipulateAll(form)
{
	form.action.value = "ManipulateResults";
    form.submit();
}

function applyOrdering(form)
{
	form.action.value = "SortWorkOrders";
	form.submit();
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<c:set target="${workOrderBean}" property="filters" value="${sessionScope.WorkOrderFilters}" />
<table width="760" border="0" cellspacing="0" cellpadding="0">
	<tr>
    	<td>
      		<%@ include file="include/HeaderBar.jspf" %>
    	</td>
  	</tr>
  	<tr>
    	<td>
      		<table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
	        	<tr> 
		            <td width="101" bgcolor="#000000" height="1"></td>
		            <td width="1" bgcolor="#000000" height="1"></td>
		            <td width="657" bgcolor="#000000" height="1"></td>
				    <td width="1" bgcolor="#000000" height="1"></td>
	        	</tr>
	        	<tr> 
	          		<td  valign="top" width="101"> 
	            		<% String pageName = "WorkOrder.jsp"; %>
	            		<%@ include file="include/Nav.jspf" %>
	          		</td>
	          		<td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
	          		<td width="657" valign="top" bgcolor="#FFFFFF"> 
	            	<div align="center"> 
		            	<% String header = "WORK ORDERS"; %>
		            	<%@ include file="include/SearchBar.jspf" %>
		            	<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
                        <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>		            	
	              		<form name="MForm" method="post" action="<%=request.getContextPath()%>/servlet/WorkOrderManager">
						    <input type="hidden" name="page_" value="1">
						    <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
						    <input type="hidden" name="showAll" value="0">
						    <input type="hidden" name="action" value=""> 
	                		<table width="80%" border="1" cellspacing="0" cellpadding="3" class="TableCell">
	                			<tr> 
	                    			<td class="HeaderCell" width="100%">Current Filters</td>
	                  			</tr>
	                  			<c:forEach var="filterEntry" items="${workOrderBean.filters}">
									<tr>		
										<td>
											<div align="left"><c:out value="${filterEntry.filterText}"/></div>
										<td>
									</tr>
								</c:forEach>
	                		</table>
		                
		                	<table width="80%" border="0" cellspacing="0" cellpadding="0">
	                  			<tr>
	                  				<td> 
						               	<div align="left">
											<input type="button" name="Filter" value="Add/Remove Filters" onClick="goFilter(this.form)">
											<!-- <input type="button" name="ShowAll" value="Show All Work Orders(Filter Free)" onClick="showAll(this.form)"> -->
										</div>
			          					<br>
			            			</td>
			          			</tr>
			         		</table>
<%--			          		<c:set target="${workOrderBean}" property="internalRequest" value="${pageContext.request}" />--%>
				  			<table width="80%" border="0" cellspacing="0" cellpadding="0" align="center" class="TableCell">
							  	<tr>
									<td width="100%" class="headeremphasis"> WORK ORDER RESULTS: 
										<c:out value="${workOrderBean.numberOfRecords}"/>
 entries returned.
									</td>
								</tr>
				 			</table>
							 <table width="80%" border="0" cellspacing="0" cellpadding="0" align="center" class="TableCell">
							 	<tr>
									<td width="100" align="right"> 
					                   	<c:if test="${workOrderBean.viewAllResults}">
					                   		<input type="button" name="ViewSet" value="Hide All Results" onclick="viewAll(this.form, false)">
					               		</c:if>
					               		<c:if test="${!workOrderBean.viewAllResults}">
					                   		<input type="button" name="ViewSet" value="View All Results" onclick="viewAll(this.form, true)">
					               		</c:if>
					               	</td>
					               	<td width="500"> 
					                   	<input type="button" name="Manipulate" value="Manipulate Results" onclick="manipulateAll(this.form)">
					               	</td>
				                </tr>
							</table>
							<BR>
				      		<c:if test="${workOrderBean.viewAllResults}">
					      		<table width="80%" border="0" cellspacing="0" cellpadding="0">
						      		<tr>
			                    		<td width="75%"> 
			                      			<table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
												<tr> 
					                          		<td width="15%"> 
					                            		<div align="right">Order by:</div>
					                          		</td>
					                          		<td width="35%"> 
					                            		<select name="SortBy">
														                              <%
															StarsCustSelectionList sortByList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SO_SORT_BY );
															for (int i = 0; i < sortByList.getStarsSelectionListEntryCount(); i++) {
																StarsSelectionListEntry entry = sortByList.getStarsSelectionListEntry(i);
																String selected = (entry.getYukonDefID() == workOrderBean.getSortBy())? "selected" : "";
														%>
														                              <option value="<%= entry.getYukonDefID() %>" <%= selected %>><%= entry.getContent() %></option>
														                              <%
															}
														%>
					                          			</select>
					                          		</td>
					                          		<td width="50%"> 
					                            		<select name="SortOrder">
							                            	<option value="<%= workOrderBean.SORT_ORDER_ASCENDING %>" <% if (workOrderBean.getSortOrder() == workOrderBean.SORT_ORDER_ASCENDING) out.print("selected"); %>>Ascending</option>
							                            	<option value="<%= workOrderBean.SORT_ORDER_DESCENDING %>" <% if (workOrderBean.getSortOrder() == workOrderBean.SORT_ORDER_DESCENDING) out.print("selected"); %>>Descending</option>
					                            		</select>
					                          		</td>
					                  			</tr>
			     	              			</table>
			                    		</td>
			                    		<td width="25%"> 
			                      			<input type="submit" name="Submit" value="Apply Ordering" onclick="applyOrdering(this.form)">
			                    		</td>
			                  		</tr>
		                		</table>
					 		</c:if>
				  		<c:if test="${workOrderBean.viewAllResults}">
					  		<table width="80%" border="0" cellspacing="0" cellpadding="0" class="MainText">
		                		<tr>
				                	<td>Click on a serial # (device name) to view the hardware details.</td>
		                		</tr>
		              		</table>
				  			<br>
	              			<%= workOrderBean.getHTML(request) %> 
	              			<p>&nbsp; </p>
	              		</c:if>
	              		</form>
					<div id="browserWarning" style="display:none; font-weight: bold; color: red; font-size: 14px; text-align: center; margin: 12px 0">This page only works with Internet Explorer.</div>
	            	</div>
	        		<td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
	    		</tr>
			</table>
    	</td>
	</tr>
</table>
<br>
</body>
</html>
<script>
if (!Prototype.Browser.IE) {
	$('browserWarning').show();
}
</script>