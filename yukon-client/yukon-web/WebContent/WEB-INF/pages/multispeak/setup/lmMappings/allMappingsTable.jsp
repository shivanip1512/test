<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.adminSetup.lmMappings">
    <table class="compact-results-table dashed with-form-controls">
        <thead>
            <tr>
                <c:forEach var="column" items="${columns}">
                    <tags:sort column="${column}"/>
                </c:forEach>
                <th>&nbsp;</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="mapping" items="${allMappings}">
                <cti:deviceName var="paoName" deviceId="${mapping.paobjectId}"/>
                <tr>
                    <td>${fn:escapeXml(mapping.strategyName)}</td>
                    <td>${fn:escapeXml(mapping.substationName)}</td>
                    <td>${fn:escapeXml(paoName)}</td>
                    <td>
                        <cti:button icon="icon-cross" classes="fr" renderMode="buttonImage"
                            data-ok-event="yukon.substation.mappings.delete"
                            data-mapping-id="${mapping.mspLMInterfaceMappingId}" />
                        <cti:list var="arguments">
                            <cti:item value="${fn:escapeXml(mapping.strategyName)}"/>
                            <cti:item value="${fn:escapeXml(mapping.substationName)}"/>
                        </cti:list>
                        <cti:msg2 var="argument" key=".mapKey" arguments="${arguments}"/>
                        <d:confirm on='[data-mapping-id="${mapping.mspLMInterfaceMappingId}"]' nameKey="confirmDelete"
                            argument="${argument}"/>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</cti:msgScope>