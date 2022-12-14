<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="assetAvailability.detail">
    <cti:breadCrumbs>
        <cti:crumbLink url="/dashboard" title="${home}" />
        <cti:crumbLink url="/dr/home" title="${dr}" />
        <cti:crumbLink url="${paoListUri}" title="${paoTypeName}" />
        <cti:crumbLink url="${paoDetailsUri}" title="${paoName}" />
        <cti:crumbLink title="${assetAvailability}" />
    </cti:breadCrumbs>
    <cti:toJson id="js-asset-availability-summary" object="${summary}"/>
    <c:forEach var="status" items="${statusTypes}">
        <input type="hidden" class="js-asset-${status}" value="<cti:msg2 key=".assetDetails.status.${status}"/>"/>
    </c:forEach>
        
    <cti:url var="formAction" value="/dr/assetAvailability/detail"/>
    <cti:url var="downloadAll" value="/dr/assetAvailability/${paobjectId}/downloadAll"/>
    <form id="js-asset-availability-filter-form" action="${formAction}" method="GET">
        <input type="hidden" name="paobjectId" value="${paobjectId}"/>
        <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".targetLevel" nameClass="mw100">
                        <span>
                            <span class="fl">${fn:escapeXml(paoName)}</span><span class="fl">&nbsp;(${totalDevices}&nbsp;<i:inline key="yukon.common.Devices"/>)</span>
                            <cti:icon icon="icon-csv" nameKey="download" href = "${downloadAll}"/>
                        </span>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <div style="max-height: 200px;" class="js-asset-availability-pie-chart-summary"></div>
            </div>
            <div class="column two nogutter filter-container" style="margin-top:40px;">
                <span class="fr cp"><cti:icon icon="icon-help" data-popup="#filter-results-help"/></span>
                <cti:msg2 var="helpTitle" key=".detail.helpTitle"/>
                <div id="filter-results-help" class="dn" data-title="${helpTitle}" data-width="600" data-height="400">${helpText}</div>
                <br/>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".deviceGroups">
                        <cti:list var="groups">
                            <c:forEach var="subGroup" items="${deviceSubGroups}">
                                <cti:item value="${subGroup}"/>
                            </c:forEach>
                        </cti:list>
                        <tags:deviceGroupPicker inputName="deviceSubGroups" inputValue="${groups}" multi="true"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".range" nameClass="vam">
                        <div class="button-group stacked MB5">
                            <c:forEach var="status" items="${statusTypes}">
                                <c:set var="checked" value="${false}"/>
                                <c:forEach var="statusEnum" items="${statuses}">
                                    <c:if test="${statusEnum eq status}">
                                        <c:set var="checked" value="${true}"/>
                                    </c:if>
                                </c:forEach>
                                <c:set var="buttonTextColor" value="green"/>
                                <c:if test="${status == 'OPTED_OUT'}">
                                    <c:set var="buttonTextColor" value="blue"/>
                                </c:if>
                                <c:if test="${status == 'INACTIVE'}">
                                    <c:set var="buttonTextColor" value="orange"/>
                                </c:if>
                                <c:if test="${status == 'UNAVAILABLE'}">
                                    <c:set var="buttonTextColor" value="grey"/>
                                </c:if>
                                <tags:check name="statuses" key=".assetDetails.status.${status}" classes="M0" 
                                            buttonTextClasses="${buttonTextColor}" checked="${checked}" value="${status}"/>
                            </c:forEach>
                        </div>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".primaryGateway">
                        <cti:msg2 var="gatewayPlaceholder" key=".selectGateways"/>
                        <select id="selectedGatewayIds" name="selectedGatewayIds" class="js-primary-gateway-select w300" multiple="multiple" 
                            data-placeholder="${gatewayPlaceholder}" size="1">
                            <c:forEach var="gateway" items="${gateways}">
                                <c:set var="checked" value="${false}"/>
                                <c:forEach var="selectedGateway" items="${selectedGateways}">
                                    <c:if test="${gateway.id == selectedGateway}">
                                        <c:set var="checked" value="selected='selected'"/>
                                    </c:if>
                                </c:forEach>
                                <option value="${gateway.id}" ${checked}>${fn:escapeXml(gateway.name)}</option>
                            </c:forEach>
                        </select>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <div class="action-area">
                    <cti:button classes="primary action js-filter-results" nameKey="filter" busy="true"/>
                </div>
            </div>
        </div>
    </form>
    
    <div id="js-filtered-results"></div>
    
    <cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
    <cti:includeScript link="/resources/js/common/yukon.assetAvailability.pieChart.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.dr.assetavailability.detail.js"/>
</cti:standardPage>