<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="dataStreaming">

    <cti:url var="filterUrl" value="/tools/dataStreaming/configurations"/>
    <form:form action="${filterUrl}" method="get" commandName="filter">

    <div id="page-actions" class="dn">
        <cti:url var="discrepanciesUrl" value="/tools/dataStreaming/discrepancies"/>
        <cm:dropdownOption key=".discrepancies" href="${discrepanciesUrl}" icon="icon-error"/>
        <cti:url var="collectionActionsUrl" value="/bulk/deviceSelection"/>
        <cm:dropdownOption key=".collectionActions" href="${collectionActionsUrl}" icon="icon-cog-go"/>
        <cti:url var="configureOthersUrl" value="/bulk/deviceSelection?redirectUrl=/bulk/dataStreaming/configure"/>
        <cm:dropdownOption key=".configureOtherDevices" href="${configureOthersUrl}" icon="icon-cog-add"/>
    </div>

    <div data-url="${filterUrl}" data-static>
    <table class="compact-results-table has-actions row-highlighting">
        <thead>
            <tr>
                <tags:sort column="${attributes}"/>
                <tags:sort column="${interval}"/>    
                <tags:sort column="${numberOfDevices}"/>            
            </tr>
        </thead>
        <c:forEach var="config" items="${existingConfigs.resultList}">
            <c:set var="deviceCollection" value="${configsAndDevices[config]}"/>
            <tr>
                <td>${config.commaDelimitedAttributes}</td>
                <td>${config.selectedInterval}
                    <c:choose>
                        <c:when test="${config.selectedInterval > 1}">
                            <i:inline key=".minutes"/>
                        </c:when>
                        <c:otherwise>
                            <i:inline key=".minute"/>
                        </c:otherwise>
                    </c:choose>
                   </td>
                   <td>
                        ${deviceCollection.deviceCount}
                        <cm:dropdown icon="icon-cog" triggerClasses="fr">
                            <cti:url var="summaryUrl" value="/tools/dataStreaming/summary?selectedConfiguration=${config.id}"/>
                            <cm:dropdownOption key=".summary.pageName" href="${summaryUrl}" icon="icon-application-view-columns"/>
                            <cti:url var="configureUrl" value="/bulk/dataStreaming/configure">
                                <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
                                    <cti:param name="${cp.key}" value="${cp.value}"/>
                                </c:forEach>
                            </cti:url>
                            <cm:dropdownOption key=".configure" href="${configureUrl}" icon="icon-cog-edit"/>
                            <cti:url var="removeUrl" value="/bulk/dataStreaming/remove">
                                <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
                                    <cti:param name="${cp.key}" value="${cp.value}"/>
                                </c:forEach>
                            </cti:url>                            
                            <cm:dropdownOption key=".remove" href="${removeUrl}" icon="icon-cross"/>
                            <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                                <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
                                    <cti:param name="${cp.key}" value="${cp.value}"/>
                                </c:forEach>
                            </cti:url>
                            <cm:dropdownOption key=".collectionActions" href="${collectionActionsUrl}" icon="icon-cog-go"/>
                        </cm:dropdown>
                   </td>
                </tr>
            </c:forEach>
    </table>
    <tags:pagingResultsControls result="${existingConfigs}" hundreds="true" adjustPageCount="true"/>
    </div>
    
    </form:form>

</cti:standardPage>