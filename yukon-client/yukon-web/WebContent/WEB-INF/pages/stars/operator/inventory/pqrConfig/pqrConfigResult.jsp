<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="pqrConfigResult">
    <c:set var="newAction" value="/stars/operator/inventory/pqrConfig/newAction"/>
    
    <tags:nameValueContainer2>
        <%-- Progress Bar --%>
        <tags:nameValue2 nameKey=".progress">
            <tags:updateableProgressBar totalCount="${result.counts.total}" containerClasses="vam" 
                countKey="PQR_CONFIG/${resultId}/ITEMS_PROCESSED"/>
        </tags:nameValue2>
        <tags:nameValueGap2/>
        
        <%-- Success count --%>
        <tags:nameValue2 nameKey=".successCount">
            <cti:dataUpdaterValue type="PQR_CONFIG" identifier="${resultId}/SUCCESS_COUNT" styleClass="success fwb"/>
            <cti:classUpdater type="PQR_CONFIG" identifier="${resultId}/NEW_OPERATION_FOR_SUCCESS" initialClassName="dn">
                <cti:url var="successNewAction" value="${newAction}">
                    <cti:param name="action" value="SUCCESSFUL"/>
                    <cti:param name="resultId" value="${resultId}"/>
                </cti:url>
                <a href="${successNewAction}"><i:inline key=".newAction"/></a>
            </cti:classUpdater>
        </tags:nameValue2>
        <tags:nameValueGap2/>
        
        <%-- Unsupported count --%>
        <tags:nameValue2 nameKey=".unsupportedCount">
            <cti:dataUpdaterValue type="PQR_CONFIG" identifier="${resultId}/UNSUPPORTED_COUNT" styleClass="warning fwb"/>
            <cti:classUpdater type="PQR_CONFIG" identifier="${resultId}/NEW_OPERATION_FOR_UNSUPPORTED" initialClassName="dn">
                <cti:url var="unsupportedNewAction" value="${newAction}">
                    <cti:param name="action" value="UNSUPPORTED"/>
                    <cti:param name="resultId" value="${resultId}"/>
                </cti:url>
                <a href="${unsupportedNewAction}"><i:inline key=".newAction"/></a>
            </cti:classUpdater>
        </tags:nameValue2>
        <tags:nameValueGap2/>
        
        <%-- Failed count --%>
        <tags:nameValue2 nameKey=".failedCount">
            <cti:dataUpdaterValue type="PQR_CONFIG" identifier="${resultId}/FAILED_COUNT" styleClass="error fwb"/>
            <cti:classUpdater type="PQR_CONFIG" identifier="${resultId}/NEW_OPERATION_FOR_FAILED" initialClassName="dn">
                <cti:url var="failedNewAction" value="${newAction}">
                    <cti:param name="action" value="FAILED"/>
                    <cti:param name="resultId" value="${resultId}"/>
                </cti:url>
                <a href="${failedNewAction}"><i:inline key=".newAction"/></a>
            </cti:classUpdater>
        </tags:nameValue2>
        
    </tags:nameValueContainer2>
</cti:standardPage>