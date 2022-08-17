<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<cti:msgScope paths="modules.operator.gateways.detail,modules.operator.gateways,modules.operator">

    <c:if test="${not empty errorMsg}"><tags:alertBox>${errorMsg}</tags:alertBox></c:if>
    
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".wifiCapableCount">
            <c:choose>
                <c:when test="${wifiCount > 0}">
                    <cti:msg2 var="viewMeters" key=".viewWiFiMeters"/>
                    <cti:url var="wifiUrl" value="/stars/wifiConnection/connectedDevices/${gateway.id}"/>
                    <a href="${wifiUrl}" title="${viewMeters}">${wifiCount}</a>
                </c:when>
                <c:otherwise>
                    ${wifiCount}
                </c:otherwise>
            </c:choose>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".readyNodes" valueClass="js-gw-data-completeness">
                            
            <c:set var="successWidth" value="${gateway.data.percentForReadyNodes}" />
            <c:set var="failedWidth" value="${100 - gateway.data.percentForReadyNodes}" />
            <cti:list var="msgArgs">
                <cti:item value="${gateway.data.gwTotalReadyNodes}"/>
                <cti:item value="${gateway.data.gwTotalNotReadyNodes}"/>
            </cti:list>
            <div class="progress" style="width: 200px; float: left;"
                 title="<i:inline key=".tooltip.readyNodes" arguments="${msgArgs}"/>">
                <div class="progress-bar progress-bar-success" role="progressbar"
                    aria-valuenow="${gateway.data.gwTotalReadyNodes}%"
                    aria-valuemin="0" aria-valuemax="100" style="width: ${successWidth}%">
                </div>
            </div>&nbsp;
            <span>
                <fmt:formatNumber pattern="###.##%" value="${gateway.data.percentForReadyNodes / 100}"/>
            </span>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".nodesWithInfo" valueClass="js-gw-data-completeness">
            <c:set var="successWidth" value="${gateway.data.percentForNodesWithInfo}" />
            <c:set var="failedWidth" value="${100 - gateway.data.percentForNodesWithInfo}" />
            <cti:list var="msgArgs">
                <cti:item value="${gateway.data.gwTotalNodesWithInfo}"/>
                <cti:item value="${gateway.data.gwTotalNodesNoInfo}"/>
            </cti:list>
            <div class="progress" style="width: 200px; float: left;"
                 title="<i:inline key=".tooltip.nodesWithInfo" arguments="${msgArgs}"/>">
                <div class="progress-bar progress-bar-success" role="progressbar"
                    aria-valuenow="${gateway.data.gwTotalNodesWithInfo}%"
                    aria-valuemin="0" aria-valuemax="100" style="width: ${successWidth}%">
                </div>
            </div>&nbsp;
            <span>
                <fmt:formatNumber pattern="###.##%" value="${gateway.data.percentForNodesWithInfo / 100}"/>
            </span>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".nodesWithSNs" valueClass="js-gw-data-completeness">
            <c:set var="successWidth" value="${gateway.data.percentForNodesWithSN}" />
            <c:set var="failedWidth" value="${100 - gateway.data.percentForNodesWithSN}" />
            <cti:list var="msgArgs">
                <cti:item value="${gateway.data.gwTotalNodesWithSN}"/>
                <cti:item value="${gateway.data.gwTotalNodes}"/>
            </cti:list>
            <div class="progress" style="width: 200px; float: left;"
                 title="<i:inline key=".tooltip.nodesWithSNs" arguments="${msgArgs}"/>">
                <div class="progress-bar progress-bar-success" role="progressbar"
                    aria-valuenow="${gateway.data.gwTotalNodesWithSN}%"
                    aria-valuemin="0" aria-valuemax="100" style="width: ${successWidth}%">
                </div>
            </div>&nbsp;
            <span>
                <fmt:formatNumber pattern="###.##%" value="${gateway.data.percentForNodesWithSN / 100}"/>
            </span>
        </tags:nameValue2>
    </tags:nameValueContainer2>

</cti:msgScope>
