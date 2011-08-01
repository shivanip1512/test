<%@page import="com.cannontech.stars.util.ServletUtils"%>
<%@page import="com.cannontech.util.ServletUtil"%>
<%@page import="com.cannontech.database.data.lite.LiteYukonUser"%>
<%@ page import="com.cannontech.yc.bean.CommandDeviceBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<cti:verifyRolesAndProperties value="ENABLE_WEB_COMMANDER"/>
<%
	final LiteYukonUser lYukonUser = ServletUtil.getYukonUser(session);
	String errorMsg = ServletUtils.removeErrorMessage(session);
%>

<jsp:useBean id="commandDeviceBean" class="com.cannontech.yc.bean.CommandDeviceBean" scope="session"/>

<%-- Grab the search criteria --%>
<jsp:setProperty name="commandDeviceBean" property="page" param="page_"/>
<jsp:setProperty name="commandDeviceBean" property="pageSize" param="pageSize"/>
<jsp:setProperty name="commandDeviceBean" property="filterBy" param="FilterBy"/>
<jsp:setProperty name="commandDeviceBean" property="filterValue" param="FilterValue"/>
<jsp:setProperty name="commandDeviceBean" property="orderBy" param="OrderBy"/>
<jsp:setProperty name="commandDeviceBean" property="orderDir" param="OrderDir"/>
<jsp:setProperty name="commandDeviceBean" property="sortBy" param="SortBy"/>
<jsp:setProperty name="commandDeviceBean" property="searchBy" param="SearchBy"/>
<jsp:setProperty name="commandDeviceBean" property="userID" value="<%=lYukonUser.getUserID()%>"/>
<jsp:setProperty name="commandDeviceBean" property="searchValue" param="SearchValue"/>
<jsp:setProperty name="commandDeviceBean" property="clear" param="Clear"/>

<%@ include file="/apps/CommanderMenu.jspf" %>

<cti:standardPage title="Energy Services Operations Center" module="commanderSelect">
	<cti:standardMenu menuSelection="<%= menuSelection %>" />
	
	<script language="JavaScript">
	
		<!-- onLoad, call init() --> 
		Event.observe(window, 'load', function(){init();});
		
		function changeFilter(filterBy) {
			document.getElementById("DivRoute").style.display = (filterBy == <%= CommandDeviceBean.ROUTE_FILTER %>)? "" : "none";
			document.getElementById("DivCommChannel").style.display = (filterBy == <%= CommandDeviceBean.COMM_CHANNEL_FILTER %>)? "" : "none";
		    document.getElementById("DivCBCType").style.display = (filterBy == <%= CommandDeviceBean.CBC_TYPE_FILTER%>)? "" : "none";
		}
		
		function init() {
			var form = document.MForm;
			changeFilter(form.FilterBy.value);
		}
		
		function showAll(form) {
			form.Clear.value = "true";
			form.submit();
		}
		
		function setFilterValue(form){
			if(document.getElementById("DivRoute").style.display == "")
				form.FilterValue.value = form.RouteFilterValue.value;
			else if(document.getElementById("DivCommChannel").style.display == "")
				form.FilterValue.value = form.CommChannelFilterValue.value;
		    else if( document.getElementById("DivCBCType").style.display == "")
		        form.FilterValue.value = form.CBCTypeFilterValue.value;        
			form.submit();
		}
	</script>
	
	<div class="mainFull">
		
		<h2>Device Selection</h2>
	
		<!-- Order / Filter by form -->
		<form name="MForm" method="post" action="">
			<input type="hidden" name="page" value="1">
			<input type="hidden" name="Clear" value="false">
	   		<input type="hidden" name="FilterValue" value="">
	
			<!-- Order by section -->
			<div class="formSection">
		        Order by:
		        <span class="selection">
					<select name="OrderBy">
						<c:set var="count" scope="page" value="0" />
						<c:forEach items="${commandDeviceBean.orderByStrings}" var="entry">
							<option value="${count}" ${commandDeviceBean.orderBy == count ? 'selected' : ''}>${entry}</option>
							<c:set var="count" scope="page" value="${count + 1}" />
						</c:forEach>
					</select>
				</span>
		        <span class="selection">
					<select name="OrderDir">
						<option value="${cti:constantValue('com.cannontech.yc.bean.CommandDeviceBean.ORDER_DIR_ASCENDING')}" ${commandDeviceBean.orderDir == cti:constantValue('com.cannontech.yc.bean.CommandDeviceBean.ORDER_DIR_ASCENDING') ? 'selected' : ''}>Ascending</option>
			            <option value="${cti:constantValue('com.cannontech.yc.bean.CommandDeviceBean.ORDER_DIR_DESCENDING')}" ${commandDeviceBean.orderDir == cti:constantValue('com.cannontech.yc.bean.CommandDeviceBean.ORDER_DIR_DESCENDING') ? 'selected' : ''}>Descending</option>
			        </select>
				</span>
			</div>
				
			<!-- Filter by section -->
			<div class="formSection">
				<c:set var="currentSortBy" scope="page" value="<%=currentSortBy%>" />
				<c:if test="${currentSortBy == cti:constantValue('com.cannontech.database.data.pao.DeviceClasses.IED') ||
							  currentSortBy == cti:constantValue('com.cannontech.database.data.pao.DeviceClasses.RTU') ||
							  currentSortBy == cti:constantValue('com.cannontech.database.data.pao.DeviceClasses.CARRIER') ||
							  currentSortBy == cti:constantValue('com.cannontech.database.data.pao.DeviceClasses.TRANSMITTER') ||
							  currentSortBy == cti:constantValue('com.cannontech.database.data.pao.PAOGroups.CAT_CAPCONTROL')}">
						Filter by:
				</c:if>
			
				<!-- Filter by drop down -->
			
				<!-- See if any filters are selected -->
				<c:set var="noSelected" scope="page" value="${commandDeviceBean.filterBy == cti:constantValue('com.cannontech.yc.bean.CommandDeviceBean.NO_FILTER')}" />
				<c:set var="routeSelected" scope="page" value="${commandDeviceBean.filterBy == cti:constantValue('com.cannontech.yc.bean.CommandDeviceBean.ROUTE_FILTER')}" />
				<c:set var="commChannelSelected" scope="page" value="${commandDeviceBean.filterBy == cti:constantValue('com.cannontech.yc.bean.CommandDeviceBean.COMM_CHANNEL_FILTER')}" />
				<c:set var="cbcSelected" scope="page" value="${commandDeviceBean.filterBy == cti:constantValue('com.cannontech.yc.bean.CommandDeviceBean.CBC_TYPE_FILTER')}" />
				
				<c:choose>
					<c:when test="${currentSortBy == cti:constantValue('com.cannontech.database.data.pao.DeviceClasses.CARRIER')}">
						<!-- Carrier options -->
					    <select name="FilterBy" onChange="changeFilter(this.value)">
					    	<option value="${cti:constantValue('com.cannontech.yc.bean.CommandDeviceBean.NO_FILTER')}" ${noSelected ? 'selected' : ''}>(none)</option>
					      	<option value="${cti:constantValue('com.cannontech.yc.bean.CommandDeviceBean.ROUTE_FILTER')}" ${routeSelected ? 'selected' : ''}>Route</option>
				        </select>
				    </c:when>
					<c:when test="${currentSortBy == cti:constantValue('com.cannontech.database.data.pao.DeviceClasses.IED') ||
									  currentSortBy == cti:constantValue('com.cannontech.database.data.pao.DeviceClasses.RTU')}">
						<!-- IED/RTU options -->
				        <select name="FilterBy" onChange="changeFilter(this.value)">
				        	<option value="${cti:constantValue('com.cannontech.yc.bean.CommandDeviceBean.NO_FILTER')}" ${noSelected ? 'selected' : ''}>(none)</option>
				          	<option value="${cti:constantValue('com.cannontech.yc.bean.CommandDeviceBean.COMM_CHANNEL_FILTER')}" ${commChannelSelected ? 'selected' : ''}>Comm Channel</option>
				        </select>
				    </c:when>
					<c:when test="${currentSortBy == cti:constantValue('com.cannontech.database.data.pao.DeviceClasses.TRANSMITTER')}">
						<!-- Transmitter options -->
					    <select name="FilterBy" onChange="changeFilter(this.value)">
				        	<option value="${cti:constantValue('com.cannontech.yc.bean.CommandDeviceBean.NO_FILTER')}" ${noSelected ? 'selected' : ''}>(none)</option>
				          	<option value="${cti:constantValue('com.cannontech.yc.bean.CommandDeviceBean.COMM_CHANNEL_FILTER')}" ${commChannelSelected ? 'selected' : ''}>Comm Channel</option>
					    </select>
				    </c:when>
					<c:when test="${currentSortBy == cti:constantValue('com.cannontech.database.data.pao.PAOGroups.CAT_CAPCONTROL')}">
						<!-- Cap Control options -->
					    <select name="FilterBy" onChange="changeFilter(this.value)">
				        	<option value="${cti:constantValue('com.cannontech.yc.bean.CommandDeviceBean.NO_FILTER')}" ${noSelected ? 'selected' : ''}>(none)</option>
					      	<option value="${cti:constantValue('com.cannontech.yc.bean.CommandDeviceBean.CBC_TYPE_FILTER')}" ${cbcSelected ?  'selected' : ''}>CBC Type</option>
					    </select> 
				    </c:when>
				    <c:otherwise>
						<!-- No Filter by -->
						<input type="hidden" name="FilterBy" value="${cti:constantValue('com.cannontech.yc.bean.CommandDeviceBean.NO_FILTER')}">
				    </c:otherwise>
			    </c:choose>
			                    
			    <!-- Route filter by drop down -->      
			    <span id="DivRoute" style="display:none"> 
			    	<select name="RouteFilterValue">
			      		<c:forEach items="${commandDeviceBean.validRoutes}" var="route">
			      			<option value="${route.yukonID}" ${commandDeviceBean.filterValue == route ? 'selected' : ''}>${route.paoName}</option>
			      		</c:forEach>
			      	</select>
			    </span>
			    <!-- Comm Channel filter by drop down -->      
			    <span id="DivCommChannel" style="display:none">
			    	<select name="CommChannelFilterValue">
			      		<c:forEach items="${commandDeviceBean.validCommChannels}" var="channel">
			      			<option value="${channel.yukonID}" ${commandDeviceBean.filterValue == channel ? 'selected' : ''}>${channel.paoName}</option>
			      		</c:forEach>
			      </select>
			    </span>
			    <!-- CBC filter by drop down -->      
			    <span id="DivCBCType" style="display:none"> 
			   		<select name="CBCTypeFilterValue">
			      		<c:forEach items="${commandDeviceBean.validCBCTypes}" var="type">
			      			<option value="${type}" ${commandDeviceBean.filterValue == type ? 'selected' : ''}>${type}</option>
			      		</c:forEach>
			      	</select>
			    </span>   
			    <span class="filterButtons">
					<input type="button" name="Submit" value="Show" onClick="setFilterValue(this.form);">
					<input type="button" name="ShowAll" value="Show All" onClick="showAll(this.form)">
				</span>
				
				<!-- Search form -->
				<div class="searchForm">
					Find specific device:
					<form name="SearchForm" method="POST" action="">
						<select name="SearchBy">
							<c:forEach items="${commandDeviceBean.searchByStrings}" var="entry" varStatus="status">
								<option value="${entry}" ${(commandDeviceBean.searchBy eq entry) ? 'selected' : ''}>${entry}</option>
							</c:forEach>
						</select>
						<input type="text" name="SearchValue" size="14" value="${commandDeviceBean.searchValue}">
						<input type="submit" name="Submit" value="Search" >
					</form>			
				
				</div>
			</div>
		</form>
		
		<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
	  	<br>
		    ${commandDeviceBean.deviceTableHTML}
		
			<div class="filterCancel">
				<input type='button' name='Cancel' value='Cancel' onclick='location.href="../operator/Operations.jsp"'>
			</div>
	</div>

</cti:standardPage>