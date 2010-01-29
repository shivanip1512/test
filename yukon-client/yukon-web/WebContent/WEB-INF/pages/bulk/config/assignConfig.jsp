<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.assignConfig.pageTitle"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/deviceSelection" title="${deviceSelectionPageTitle}"/>
        
        <%-- collection actions --%>
        <tags:collectionActionsCrumbLink deviceCollection="${deviceCollection}" />
        
        <%-- assign config --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <%-- TITLE --%>
    <h2>${pageTitle}</h2>
    <br>
    
    <%-- BOX --%>
    <cti:msg var="headerTitle" key="yukon.common.device.bulk.assignConfig.header"/>
    <tags:bulkActionContainer   key="yukon.common.device.bulk.assignConfig" deviceCollection="${deviceCollection}">
    
        <form id="assignConfigForm" method="post" action="/spring/bulk/config/doAssignConfig">
        
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            <table>
	            <tr>
	                <td valign="top" class="smallBoldLabel">
	                    <cti:msg key="yukon.common.device.bulk.assignConfig.selectLabel"/>
	                </td>
	                <td>
	                    <select id="configuration" name="configuration">
                            <option value="-1">(none)</option>
	                        <c:forEach var="config" items="${existingConfigs}">
	                            <option value="${config.id}">${config.name}</option>
	                        </c:forEach>
	                    </select>
	                </td>
	            </tr>
            </table>
            <%-- ASSIGN BUTTON --%>
            <c:choose>
                <c:when test="${fn:length(existingConfigs) > 0}">
		            <cti:msg var="assign" key="yukon.common.device.bulk.assignConfig.assign" />
		            <input type="submit" name="assignButton" value="${assign}">
                </c:when>
                <c:otherwise>
                    There are no existing configurations.<br><br>
                    <a href="/spring/bulk/bulkHome">Bulk Operations Home</a>
                </c:otherwise>
            </c:choose>
            
        </form>
            
    </tags:bulkActionContainer>
    
</cti:standardPage>