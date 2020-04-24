<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="wifiConnection">

    <tags:alertBox type="success" key=".queryMsg" classes="js-refresh-msg dn"></tags:alertBox>
    <input type="hidden" id="gatewayId" value="${gateway.id}"/>
    <input type="hidden" id="deviceIds" value="${deviceIds}"/>
    <input type="hidden" id="connectedStatusValue" value="${connectedStatusValue}"/>

    <div>
        <hr/>
        <i:inline key="yukon.common.filterBy"/>&nbsp;
        <cti:msg2 var="allStatuses" key=".allStatuses"/>
        <select id="commStatusFilter" multiple="multiple" data-placeholder="${allStatuses}">
            <c:forEach var="status" items="${commStatusValues}">
                <option value="${status.liteID}">${status.stateText}</option>
            </c:forEach>
        </select>
        <hr/>
    </div>

    <span class="fwn"><i:inline key="yukon.common.filteredResults"/>
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
                <cm:dropdownOption key="yukon.common.mapDevices" icon="icon-map-sat" classes="js-map"/>
                <cm:dropdownOption key=".queryAll" icon="icon-read" classes="js-refresh-all"/>
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
                    <i:inline key=".statusLastUpdated"/>
                    <i class="icon icon=blank"/>
                </th>
                <th data-sorted="false">
                    <i:inline key="yukon.common.attribute.builtInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR"/>
                    <i class="icon icon=blank"/>
                </th>
                <th data-sorted="false">
                    <i:inline key=".rssiLastUpdated"/>
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
                        <tags:historicalValue pao="${data.device}" pointId="${data.commStatusPoint.pointID}"/>
                    </td>
                    <td>
                        <cti:pointValue pointId="${data.rssiPoint.pointID}" format="VALUE"/>
                    </td>
                    <td>
                        <tags:historicalValue pao="${data.device}" pointId="${data.rssiPoint.pointID}"/>
                    </td>
                    <td class="PL0">
                        <cti:msg2 var="queryTitle" key=".queryStatus"/>
                        <cti:button renderMode="image" icon="icon-read" classes="js-refresh-wifi show-on-hover" 
                            data-device-id="${data.device.paoIdentifier.paoId}" title="${queryTitle}"/>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <cti:includeScript link="/resources/js/lib/sortable/sortable.js"/>
    <cti:includeCss link="/resources/js/lib/sortable/sortable.css"/>
    <cti:includeScript link="/resources/js/pages/yukon.assets.wifi.connection.js"/>

</cti:standardPage>