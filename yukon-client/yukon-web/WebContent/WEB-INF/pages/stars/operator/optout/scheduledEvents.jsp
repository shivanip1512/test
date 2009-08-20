<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>

<cti:verifyRolesAndProperties value="CONSUMER_INFO"/>
    
<cti:standardPage module="dr" page="optOutAdmin">
    <cti:standardMenu menuSelection="optout|scheduledevents"/>

    <table style="width: 100%">
        <tr>
            <td width="50%">
                <h3><cti:msg key="yukon.web.modules.dr.scheduledEvents.title" /></h3>
            </td>
            <td align="right">
	            <form name="custSearchForm" method="POST" action="<cti:url value="/servlet/SOAPClient"/>">
		            <input type="hidden" name="action" value="SearchCustAccount" />
		            <cti:msg key="yukon.web.modules.dr.optOut.search" />
		            <div>
		                <select name="SearchBy" onchange="document.custSearchForm.SearchValue.value=''">
		                    <c:forEach items="${customerSearchList}" var="entry">
		                        <option value="${entry.entryID}" >${entry.entryText}</option>
		                    </c:forEach>
		                </select>
		    
		                <input type="text" name="SearchValue" size="15" value=''>
		                <img class="cssicon" src="<cti:url value="/WebConfig/yukon/Icons/clearbits/search.gif"/>" alt="search" onClick="Javascript:document.custSearchForm.submit();">
		            </div> 
	           </form>
            </td>
        </tr>
    </table>
    
    <br><br>
    
    <c:choose>
        <c:when test="${fn:length(scheduledEvents) > 0}">
		    <table class="resultsTable">
		        <tr>
		            <th><cti:msg key="yukon.web.modules.dr.scheduledEvents.startDateTime" /></th>
		            <th><cti:msg key="yukon.web.modules.dr.scheduledEvents.duration" /></th>
		            <th><cti:msg key="yukon.web.modules.dr.scheduledEvents.accountNumber" /></th>
		            <th><cti:msg key="yukon.web.modules.dr.scheduledEvents.serialNumber" /></th>
		        </tr>
		        <c:forEach var="event" items="${scheduledEvents}">
		            <tr>
		                <td><cti:formatDate value="${event.startDate}" type="DATEHM"/></td>
		                <td><cti:formatTimePeriod startDate="${event.startDate}" endDate="${event.stopDate}" type="DH"/></td>
		                <td>${event.accountNumber}</td>
		                <td>${event.serialNumber}</td>
		            </tr>
		        </c:forEach>
		    </table>
	    </c:when>
	    <c:otherwise>
	       <cti:msg key="yukon.web.modules.dr.scheduledEvents.noScheduledEvents" />
	    </c:otherwise>
    </c:choose>
</cti:standardPage>