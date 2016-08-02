<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="dataStreaming">

    <cti:url var="filterUrl" value="/tools/dataStreaming/configurations"/>
    <form:form action="${filterUrl}" method="get" commandName="filter">

    <div id="page-actions" class="dn">
        <cti:url var="collectionActionsUrl" value="/bulk/deviceSelection"/>
        <cm:dropdownOption key=".collectionActions" href="${collectionActionsUrl}" icon="icon-cog-go"/>
        <cti:url var="configureOthersUrl" value="/bulk/deviceSelection"/>
        <cm:dropdownOption key=".configureOtherDevices" href="${configureOthersUrl}" icon="icon-cog-add"/>
    </div>

    <div data-url="${filterUrl}" data-static>
    <table class="compact-results-table has-actions row-highlighting">
        <thead>
            <tr>
                <tags:sort column="${attributes}"/>
                <tags:sort column="${interval}"/>                
                <th><i:inline key=".numberOfDevices"/></th>
            </tr>
        </thead>
        <c:forEach var="config" items="${existingConfigs.resultList}">
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
                        <cm:dropdown icon="icon-cog" triggerClasses="fr">
                            <!-- TODO: Change these to pre-populate with config or devices-->
                            <cti:url var="summaryUrl" value="/tools/dataStreaming/summary?selectedConfiguration=${config.id}"/>
                            <cm:dropdownOption key=".summary.pageName" href="${summaryUrl}" icon="icon-application-view-columns"/>
                            <cti:url var="configureUrl" value="/bulk/deviceSelection"/>
                            <cm:dropdownOption key=".configure" href="${configureUrl}" icon="icon-cog-edit"/>
                            <cti:url var="removeUrl" value="/bulk/deviceSelection"/>
                            <cm:dropdownOption key=".remove" href="${removeUrl}" icon="icon-cross"/>
                            <cti:url var="collectionActionsUrl" value="/bulk/deviceSelection"/>
                            <cm:dropdownOption key=".collectionActions" href="${collectionActionsUrl}" icon="icon-cog-go"/>
                            <cti:url var="deviceGroupUrl" value="/group/editor/home"/>
                            <cm:dropdownOption key=".deviceGroup" href="${deviceGroupUrl}" icon="icon-folder-magnify"/>
                        </cm:dropdown>
                   </td>
                </tr>
            </c:forEach>
    </table>
    <tags:pagingResultsControls result="${existingConfigs}" hundreds="true" adjustPageCount="true"/>
    </div>
    
    </form:form>

</cti:standardPage>