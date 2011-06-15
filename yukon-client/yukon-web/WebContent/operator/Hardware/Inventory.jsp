<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.cannontech.stars.web.bean.InventoryBean" %>

<jsp:useBean id="inventoryBean" class="com.cannontech.stars.web.bean.InventoryBean" scope="session"/>
<jsp:setProperty name="inventoryBean" property="energyCompanyID" value="<%= user.getEnergyCompanyID() %>"/>

<% if (request.getParameter("page") == null) inventoryBean.resetInventoryList(); %>

<jsp:setProperty name="inventoryBean" property="sortBy" param="SortBy"/>
<jsp:setProperty name="inventoryBean" property="sortOrder" param="SortOrder"/>
<jsp:setProperty name="inventoryBean" property="page" param="page"/>

<html>
<head>
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
	form.REDIRECT.value = "<%= request.getContextPath() %>/operator/Hardware/Filter.jsp";
	form.submit();
}

function viewAll(form)
{
	form.action.value = "ViewInventoryResults";
    form.submit();
}

function viewDisabled(form) {
    if (!confirm("Displaying this many results could temporarily decrease system performance.  Do you wish to continue?")) return;
    viewAll(form);    
}
        
function manipulateAll(form)
{
	form.action.value = "ManipulateInventoryResults";
    form.submit();
}

function configureAll(form)
{
	form.action.value = "ConfigureInventoryResults";
    form.submit();
}


function applyOrdering(form)
{
	form.action.value = "ApplyOrdering";
    form.submit();
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<c:set target="${inventoryBean}" property="filterByList" value="${sessionScope.InventoryFilters}" />
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
	            		<% String pageName = "Inventory.jsp"; %>
	            		<%@ include file="include/Nav.jspf" %>
	          		</td>
	          		<td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
	          		<td width="657" valign="top" bgcolor="#FFFFFF"> 
	            	<div align="center"> 
		            	<% String header = "INVENTORY"; %>
		            	<%@ include file="include/SearchBar.jspf" %>
		            	<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
	              		<form name="MForm" method="post" action="<%=request.getContextPath()%>/servlet/InventoryManager">
						    <input type="hidden" name="page" value="1">
						    <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
						    <input type="hidden" name="showAll" value="0">
						    <input type="hidden" name="action" value=""> 
	                		<table width="80%" border="1" cellspacing="0" cellpadding="0" class="TableCell">
	                			<tr> 
	                    			<td class="HeaderCell" width="100%">Current Filters</td>
	                  			</tr>
	                  			<c:forEach var="filterEntry" items="${inventoryBean.filterByList}">
									<tr>		
										<td>
											<div align="left"><c:out value="${filterEntry.filterText}"/></div>
										<td>
									</tr>
								</c:forEach>
	                		</table>
		                
		                	<table width="80%" border="1" cellspacing="0" cellpadding="0">
	                  			<tr>
	                  				<td> 
						               	<div align="left">
											<input type="button" name="Filter" value="Add/Remove Filters" onClick="goFilter(this.form)">
											<!-- <input type="button" name="ShowAll" value="Show All Inventory (Filter Free)" onClick="showAll(this.form)"> -->
										</div>
			          					<br>
			            			</td>
			          			</tr>
			         		</table>
			          		<c:set target="${inventoryBean}" property="internalRequest" value="${pageContext.request}" />
				  			<table width="80%" border="1" cellspacing="0" cellpadding="0" align="center" class="TableCell">
							  	<tr>
									<td width="100%" class="headeremphasis"> INVENTORY RESULTS: 
										<c:out value="${inventoryBean.filterInventoryHTML}"/>
									</td>
								</tr>
				 			</table>
				 			<c:if test="${inventoryBean.numberOfRecords > 0}">
								 <table width="80%" border="0" cellspacing="0" cellpadding="0" align="center" class="TableCell">
								 	<tr>
										<td width="100" align="right"> 
	                                        <c:if test="${inventoryBean.viewResults}">
	                                            <input type="button" name="ViewSet" value="Hide All Results" onclick="viewAll(this.form)">
	                                        </c:if>
	                                        <c:if test="${!inventoryBean.viewResults}">
	                                            <c:choose>
	        					                   	<c:when test="${inventoryBean.overHardwareDisplayLimit}">
	                                                    <input type="button" name="ViewSet" value="View All Results" onclick="viewDisabled(this.form)">
	                                                </c:when>
	                                                <c:otherwise>
	                                                    <input type="button" name="ViewSet" value="View All Results" onclick="viewAll(this.form)">
	                                                </c:otherwise>
	                                            </c:choose>
	                                         </c:if>                    
						               	</td>
						               	<td width="200"> 
						                   	<input type="button" name="Manipulate" value="Manipulate Results" onclick="manipulateAll(this.form)">
						               	</td>
						               	<td width="100"> 
						               	</td>
						               	<td width="200"> 
						                   	<div align="right">
						                   		<input type="button" name="LMConfigure" value="LM Configure" onclick="configureAll(this.form)">
						               		</div>
						               	</td>
					                </tr>
                                    <tr>
                                        <td colspan="4">
                                            <strong>NOTE:  If the results contain MCTs, Digi Gateways or UtilityPRO Zigbee devices, those
                                            devices will be skipped when manipulating results.</strong>
                                        </td>
                                    </tr>
								</table>
							</c:if>
				      		<br>
				      		<c:if test="${inventoryBean.viewResults}">
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
															StarsCustSelectionList sortByList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_INV_SORT_BY );
															for (int i = 0; i < sortByList.getStarsSelectionListEntryCount(); i++) {
																StarsSelectionListEntry entry = sortByList.getStarsSelectionListEntry(i);
																String selected = (entry.getYukonDefID() == inventoryBean.getSortBy())? "selected" : "";
														%>
														                              <option value="<%= entry.getYukonDefID() %>" <%= selected %>><%= entry.getContent() %></option>
														                              <%
															}
														%>
					                          			</select>
					                          		</td>
					                          		<td width="50%"> 
					                            		<select name="SortOrder">
							                            	<option value="<%= InventoryBean.SORT_ORDER_ASCENDING %>" <% if (inventoryBean.getSortOrder() == InventoryBean.SORT_ORDER_ASCENDING) out.print("selected"); %>>Ascending</option>
							                            	<option value="<%= InventoryBean.SORT_ORDER_DESCENDING %>" <% if (inventoryBean.getSortOrder() == InventoryBean.SORT_ORDER_DESCENDING) out.print("selected"); %>>Descending</option>
					                            		</select>
					                          		</td>
					                  			</tr>
			     	              			</table>
			                    		</td>
			                    		<td width="25%"> 
			                      			<input type="button" name="SortOrderButton" value="Apply Ordering" onclick="applyOrdering(this.form)">
			                    		</td>
			                  		</tr>
		                		</table>
					 		</c:if>
	              		</form>
				  		<c:if test="${inventoryBean.viewResults}">
					  		<table width="80%" border="0" cellspacing="0" cellpadding="0" class="MainText">
		                		<tr>
				                	<td>Click on a serial # (device name) to view the hardware details, 
					                    or click on an account # (if available) to view the account 
					                    information.</td>
		                		</tr>
		              		</table>
				  			<br>
                            <%= inventoryBean.getHTML(request) %> 
	              			<p>&nbsp; </p>
	              		</c:if>
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
