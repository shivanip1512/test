<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="dr" page="controlStatus">

    <table class="compact-results-table row-highlighting has-actions">
        <thead>
            <th>Device</th>
            <th>Control Status</th>
            <th>Last Updated</th>
            <th class="action-column"><cti:icon icon="icon-cog" classes="M0" /></th>
        </thead>
        <tbody>
            <c:forEach var="controlStatus" items="${statuses}">
                <tr>
                    <td>
                        <cti:url var="inventoryViewUrl" value="/stars/operator/inventory/view">
                            <cti:param name="inventoryId" value="${controlStatus.inventoryId}"/>
                        </cti:url>
                        <a href="${inventoryViewUrl}" target="_blank">${fn:escapeXml(controlStatus.meterDisplayName)}</a>
                    </td>
                    <td><i:inline key="${controlStatus.controlStatus.formatKey}"/></td>
                    <td><cti:formatDate type="BOTH" value="${controlStatus.controlStatusTime}"/></td>
                    <td>
                        <cm:dropdown icon="icon-cog">
                            <cti:paoDetailUrl var="meterDetailLink" paoId="${controlStatus.deviceId}"/>
                            <cm:dropdownOption key=".meterDetail" href="${meterDetailLink}" newTab="true" icon="icon-control-equalizer-blue"/>
                        </cm:dropdown>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    
</cti:standardPage>