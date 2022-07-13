<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.wifiConnection">

    <span class="fwn"><i:inline key="yukon.common.filteredResults"/></span>
    <span class="badge js-count">${wifiData.size()}</span>&nbsp;<i:inline key="yukon.common.devices"/>
    
    <c:if test="${wifiData.size() > 0}">
        <span class="js-cog-menu">
            <cm:dropdown icon="icon-cog">
                <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                    <cti:param name="collectionType" value="idList"/>
                    <cti:param name="idList.ids" value="${deviceIds}"/>
                </cti:url>
                <cm:dropdownOption key="yukon.common.collectionActions" icon="icon-cog-go" href="${collectionActionsUrl}" newTab="true" classes="js-collection-actions"/>
                <cm:dropdownOption key="yukon.common.download" icon="icon-csv" classes="js-download"/>
                <cm:dropdownOption key="yukon.common.mapDevices" icon="icon-map" classes="js-map"/>
                <cm:dropdownOption key="yukon.web.modules.operator.connectedDevices.queryAll" icon="icon-read" classes="js-refresh-all"/>
            </cm:dropdown>
        </span>
    </c:if>

    <table class="compact-results-table row-highlighting" data-sortable>
        <thead>
            <tr>
                <th data-sorted="true" data-sorted-direction="ascending">
                    <i:inline key="yukon.common.name"/>
                    <i class="icon icon=blank"/>
                </th>
                <th data-sorted="false">
                    <i:inline key="yukon.common.attribute.builtInAttribute.COMM_STATUS"/>
                    <i class="icon icon=blank"/>
                </th>
                <th data-sorted="false">
                    <i:inline key="yukon.web.modules.operator.connectedDevices.statusLastUpdated"/>
                    <i class="icon icon=blank"/>
                </th>
                <th data-sorted="false">
                    <i:inline key="yukon.common.attribute.builtInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR"/>
                    <i class="icon icon=blank"/>
                </th>
                <th data-sorted="false">
                    <i:inline key="yukon.web.modules.operator.connectedDevices.rssiLastUpdated"/>
                    <i class="icon icon=blank"/>
                </th>
                <th class="action-column"><cti:icon icon="icon-read" classes="M0"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="data" items="${wifiData}">
                <tr class="js-table-row" data-device-id="${data.device.paoIdentifier.paoId}">
                    <td>
                        <cti:paoDetailUrl yukonPao="${data.device}" newTab="true">${fn:escapeXml(data.device.name)}</cti:paoDetailUrl>
                    </td>
                    <td>
                        <cti:pointStatus pointId="${data.commStatusPoint.pointID}"/>
                        <span class="dn js-comm-status-value"><cti:pointValue pointId="${data.commStatusPoint.pointID}" format="RAWVALUE"/></span>
                        <span class="js-comm-status"><cti:pointValue pointId="${data.commStatusPoint.pointID}" format="VALUE"/></span>
                    </td>
                    <td>
                        <tags:historicalValue pao="${data.device}" pointId="${data.commStatusPoint.pointID}" format="DATE_QUALITY"/>
                    </td>
                    <td>
                        <cti:pointValue pointId="${data.rssiPoint.pointID}" format="VALUE"/>
                    </td>
                    <td>
                        <tags:historicalValue pao="${data.device}" pointId="${data.rssiPoint.pointID}" format="DATE_QUALITY"/>
                    </td>
                    <td class="PL0">
                        <cti:msg2 var="queryTitle" key="yukon.web.modules.operator.connectedDevices.queryStatus"/>
                        <cti:button renderMode="image" icon="icon-read" classes="js-refresh-status show-on-hover" 
                            data-device-id="${data.device.paoIdentifier.paoId}" title="${queryTitle}"/>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    
</cti:msgScope>