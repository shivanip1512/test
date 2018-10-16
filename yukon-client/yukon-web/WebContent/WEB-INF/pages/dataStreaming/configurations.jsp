<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="dataStreaming">

    <cti:url var="filterUrl" value="/tools/dataStreaming/configurations"/>
    <form:form action="${filterUrl}" method="get" modelAttribute="filter">

    <div id="page-actions" class="dn">
        <cti:url var="summaryUrl" value="/tools/dataStreaming/summary"/>
        <cm:dropdownOption key=".summary.pageName" href="${summaryUrl}" icon="icon-application-view-columns"/>
        <cti:url var="discrepanciesUrl" value="/tools/dataStreaming/discrepancies"/>
        <cm:dropdownOption key=".discrepancies" href="${discrepanciesUrl}" icon="icon-error"/>
        <cti:url var="collectionActionsUrl" value="/collectionActions/home"/>
        <cm:dropdownOption key=".collectionActions" href="${collectionActionsUrl}" icon="icon-cog-go"/>
        <cti:checkRolesAndProperties value="RF_DATA_STREAMING">
            <cti:url var="configureOthersUrl" value="/collectionActions/home?redirectUrl=/bulk/dataStreaming/configureInputs&action=Configure Data Streaming"/>
            <cm:dropdownOption key=".configureOtherDevices" href="${configureOthersUrl}" icon="icon-cog-add"/>
        </cti:checkRolesAndProperties>
    </div>

    <div data-url="${filterUrl}" data-static>
    <table class="compact-results-table has-actions row-highlighting">
        <thead>
            <tr>
                <tags:sort column="${attributes}"/>
                <tags:sort column="${interval}"/>    
                <tags:sort column="${numberOfDevices}"/>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${existingConfigs.hitCount > 0}">
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
                               </td>
                               <td>
                                    <cm:dropdown icon="icon-cog">
                                        <cti:url var="summaryUrl" value="/tools/dataStreaming/summary?selectedConfiguration=${config.id}"/>
                                        <cm:dropdownOption key=".summary.pageName" href="${summaryUrl}" icon="icon-application-view-columns"/>
                                        <cti:checkRolesAndProperties value="RF_DATA_STREAMING">
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
                                        </cti:checkRolesAndProperties>
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
                </c:when>
                <c:otherwise>
                    <tr><td>
                        <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span>
                    </td></tr>                
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${existingConfigs}" hundreds="true" adjustPageCount="true"/>
    </div>
    
    </form:form>

</cti:standardPage>