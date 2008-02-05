<%@ page import="com.cannontech.database.cache.DefaultDatabaseCache"%>
<%@ page import="com.cannontech.yukon.IDatabaseCache"%>
<%@ page import="com.cannontech.billing.FileFormatTypes"%>

<%@ include file="include/billing_header.jsp" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" title="Billing">
	<cti:standardMenu menuSelection="billing|generation"/>
	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
	    &gt; Billing
	</cti:breadCrumbs>
	
	<cti:includeScript link="/JavaScript/calendarControl.js"/>
	<cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
	
	<h2>Billing</h2>
	<br><br>
	
	<c:set var="formatMap" value="<%=FileFormatTypes.getValidFormats()%>"></c:set>
	<c:set var="origEndDate" value="<%= datePart.format(billingBean.getEndDate()) %>"></c:set>

	<form name = "MForm" action="<c:url value="/servlet/BillingServlet" />" method="post">
	
		<div style="width: 700px">
			<tags:nameValueContainer>
					
				<tags:nameValue name="File Format">
		            <select name="fileFormat">
		            	<c:forEach var="format" items="${formatMap}">
		            		<option value="${format.value}" ${(format.value == BILLING_BEAN.fileFormat)?'selected':''}>${format.key}</option>
		            	</c:forEach>
		            </select>
				</tags:nameValue>
				<tags:nameValue name="Billing End Date">
		        	<tags:dateInputCalendar fieldName="endDate" fieldValue="${origEndDate}"></tags:dateInputCalendar>
				</tags:nameValue>
				<tags:nameValue name="Demand Days Previous">
		        	<input type="text" name="demandDays" value="${BILLING_BEAN.demandDaysPrev}" size = "8">
				</tags:nameValue>
				<tags:nameValue name="Energy Days Previous">
		        	<input type="text" name="energyDays" value="${BILLING_BEAN.energyDaysPrev}" size = "8">
				</tags:nameValue>
				<tags:nameValue name="Remove Multiplier">
		        	<input type="checkbox" name="removeMultiplier" ${(BILLING_BEAN.removeMult)? 'checked':''} >
				</tags:nameValue>
				<tags:nameValue name="Billing Group">
		        	<c:set value="${true}" var="isFirst"/>
			        <select name="billGroup" size="10" multiple>
			        	<c:forEach items="${BILLING_BEAN.availableGroups}" var="item">
			           		<c:choose>
				           		<c:when test="${isFirst}">
				            		<option selected>${item}</option>
				           		</c:when>
				           		<c:otherwise>
				            		<option>${item}</option>
				           		</c:otherwise>
			           		</c:choose>
			           		<c:set value="${false}" var="isFirst"/>
			         	</c:forEach>
			        </select>
				</tags:nameValue>
				
			</tags:nameValueContainer>
			<input type="submit" name="generate" value="Generate">
		</div>

	</form>
</cti:standardPage>