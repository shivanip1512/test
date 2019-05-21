<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="mapNetwork">
    
    <input type="hidden" class="js-device-id" value="${deviceId}"/>
    <cti:msg2 var="coordinatesDeleted" key=".location.delete.successful"/>
    <input id="coordinatesDeletedMsg" type="hidden" value="${coordinatesDeleted}"/>
    
    <div class="column-10-14 clearfix">
        <div class="column one">
            <c:choose>
                <c:when test="${isGateway}">
                    <tags:widget bean="gatewayInformationWidget" deviceId="${deviceId}" container="section"/>
                </c:when>
                <c:when test="${isRelay}">
                    <tags:widget bean="relayInformationWidget" deviceId="${deviceId}" container="section"/>
                </c:when>
                <c:when test="${isLcr}">
                    <tags:widget bean="lcrInformationWidget" deviceId="${deviceId}" container="section"/>
                </c:when>
                <c:otherwise>
                    <tags:widget bean="meterInformationWidget" deviceId="${deviceId}" container="section"/>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="column two nogutter">
            <%@ include file="/WEB-INF/pages/stars/mapNetwork/locationContainer.jsp" %>
        </div>   
    </div>
    
    <div id="gateway-templates" class="dn"><cti:toJson object="${text}" id="gateway-text"/></div>
    
    <cti:includeScript link="OPEN_LAYERS"/>
    <cti:includeCss link="/resources/js/lib/open-layers/ol.css"/>
    <cti:includeScript link="/resources/js/common/yukon.mapping.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.map.network.js"/>
    <cti:includeScript link="/resources/js/widgets/yukon.widget.gateway.info.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.assets.gateway.shared.js"/>
    <div class="dn" id="js-pao-notes-popup"></div>
    <cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
    
</cti:standardPage>