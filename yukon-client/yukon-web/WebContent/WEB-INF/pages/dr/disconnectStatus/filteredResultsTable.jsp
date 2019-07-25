<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.disconnectStatus">

    <span class="fwn"><i:inline key="yukon.common.filteredResults"/></span>
    <span class="badge">${disconnectStatusList.hitCount}</span>&nbsp;<i:inline key="yukon.common.devices"/>
    
    <c:if test="${disconnectStatusList.hitCount > 0}">
        <span class="js-cog-menu">
            <cm:dropdown icon="icon-cog">
                <cti:url var="collectionActionUrl" value="/bulk/collectionActions" htmlEscape="true">
                    <cti:param name="collectionType" value="group"/>
                    <cti:param name="group.name" value="${group.fullName}"/>
                </cti:url>
                <cm:dropdownOption key="yukon.common.collectionActions" icon="icon-cog-go" classes="js-collection-action" href="${collectionActionUrl}" newTab="true"/> 
                <cm:dropdownOption icon="icon-csv" key="yukon.common.download" classes="js-download"/>  
            </cm:dropdown>
        </span>
    </c:if>

    <table class="compact-results-table row-highlighting has-actions">
        <thead>
            <tags:sort column="${device}" />
            <tags:sort column="${status}" />
            <tags:sort column="${timestamp}" />
            <cti:checkRolesAndProperties value="ALLOW_DISCONNECT_CONTROL">
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0" /></th>
            </cti:checkRolesAndProperties>
        </thead>
        <tbody>
            <c:forEach var="disconnectStatus" items="${disconnectStatusList.resultList}">
                <c:set var="pao" value="${disconnectStatus.key}"/>
                <c:set var="pointData" value="${disconnectStatus.value}"/>
                <c:set var="paoId" value="${pao.paoIdentifier.paoId}"/>
                <tr>
                    <td>
                        <cti:url var="inventoryViewUrl" value="/stars/operator/inventory/view">
                            <cti:param name="inventoryId" value="${disconnectStatus.key.inventoryId}"/>
                        </cti:url>
                        <a href="${inventoryViewUrl}" target="_blank">${fn:escapeXml(pao.paoName)}</a>
                    </td>
                    <td>                    
                        <span class="js-status-${paoId}"><cti:pointValueFormatter format="VALUE" value="${pointData}" /></span>&nbsp;
                    </td>
                    <td>
                        <span class="js-time-${paoId}"><cti:formatDate type="BOTH" value="${pointData.pointDataTimeStamp}"/></span>
                    </td>
                    <cti:checkRolesAndProperties value="ALLOW_DISCONNECT_CONTROL">
                        <td>
                            <cm:dropdown icon="icon-cog">
                                <cm:dropdownOption key=".connect" classes="js-connect" icon="icon-connect" 
                                    data-device-id="${paoId}"/>
                                <cm:dropdownOption key=".disconnect" classes="js-disconnect" icon="icon-disconnect" 
                                    data-device-id="${paoId}"/>
                                <cti:paoDetailUrl var="meterDetailLink" yukonPao="${pao}"/>
                                <cm:dropdownOption key=".meterDetail" href="${meterDetailLink}" newTab="true" icon="icon-control-equalizer-blue"/>
                            </cm:dropdown>
                        </td>
                    </cti:checkRolesAndProperties>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${disconnectStatusList}" adjustPageCount="true" thousands="true"/>
</cti:msgScope>