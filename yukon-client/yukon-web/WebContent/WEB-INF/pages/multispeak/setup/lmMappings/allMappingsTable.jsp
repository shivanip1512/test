<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<table class="compact-results-table">
    <tr>
        <th>
            <tags:sortByLink ascending="${ascending}" 
                            columnName="STRATEGY" 
                            orderedColumnName="${orderedColumnName}" 
                            callback="reloadAllMappingsTable('STRATEGY', true);">Strategy Name</tags:sortByLink>
        </th>
        <th>
            <tags:sortByLink ascending="${ascending}" 
                            columnName="SUBSTATION" 
                            orderedColumnName="${orderedColumnName}" 
                            callback="reloadAllMappingsTable('SUBSTATION', true);">Substation Name</tags:sortByLink>
        </th>
        <th>
            <tags:sortByLink ascending="${ascending}" 
                            columnName="PAO" 
                            orderedColumnName="${orderedColumnName}" 
                            callback="reloadAllMappingsTable('PAO', true);">Program/Scenario Name</tags:sortByLink>
        </th>
        <th>&nbsp;</th>
    </tr>
    <c:forEach var="mapping" items="${allMappings}">

        <cti:deviceName var="paoName" deviceId="${mapping.paobjectId}"/>

        <tr>
            <td>${fn:escapeXml(mapping.strategyName)}</td>
            <td>${fn:escapeXml(mapping.substationName)}</td>
            <td>${fn:escapeXml(paoName)}</td>
            <td>
                <cti:icon icon="icon-cross" onclick="removeLmMapping(${mapping.mspLMInterfaceMappingId});"/>
            </td>
        </tr>
    </c:forEach>
</table>