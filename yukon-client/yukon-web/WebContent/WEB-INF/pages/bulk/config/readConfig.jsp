<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.readConfig.pageTitle"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink url="/bulk/deviceSelection" title="${deviceSelectionPageTitle}"/>
        
        <%-- collection actions --%>
        <tags:collectionActionsCrumbLink deviceCollection="${deviceCollection}" />
        
        <%-- read config --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <%-- TITLE --%>
    <h2>${pageTitle}</h2>
    <br>
    
    <%-- BOX --%>
    <cti:msg var="headerTitle" key="yukon.common.device.bulk.readConfig.header"/>
    <cti:msg var="read" key="yukon.common.device.bulk.readConfig.read" />
    <tags:bulkActionContainer   key="yukon.common.device.bulk.readConfig" deviceCollection="${deviceCollection}">
        <form id="readConfigForm" method="post" action="/bulk/config/doReadConfig">
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            <table>
                <tr>
                    <td><input type="submit" name="readButton" value="${read}"></td>
                </tr>
            </table>
        </form>
    </tags:bulkActionContainer>
</cti:standardPage>