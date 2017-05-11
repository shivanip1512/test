<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="dataCollection.detail">

    <cti:toJson id="summaryData" object="${summary}"/>
    
    <div class="column-12-12 clearfix">
        <div class="column one">
            <div style="max-height: 200px;" class="js-pie-chart-summary"></div>
        </div>
        <div class="column two nogutter">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".deviceGroup">
                    ${deviceGroup}
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".deviceGroups">
                    <tags:deviceGroupPicker inputName="deviceSubGroup" />
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </div>
    </div>

    <span class="fwn"><i:inline key=".devices"/>:</span>
    <span class="badge">${detail.hitCount}</span>
    
    <c:if test="${detail.hitCount > 0}">
        <span class="js-cog-menu">
            <cm:dropdown icon="icon-cog">
                <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                    <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
                        <cti:param name="${cp.key}" value="${cp.value}"/>
                    </c:forEach>
                </cti:url>
                <cm:dropdownOption key=".collectionActions" href="${collectionActionsUrl}" icon="icon-cog-go" newTab="true"/>    
                <cti:url var="mapUrl" value="/tools/map">
                    <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                </cti:url>
                <cm:dropdownOption icon="icon-map-sat" key=".mapDevices" href="${mapUrl}" newTab="true"/>                
            </cm:dropdown>
        </span>
    </c:if>

    <cti:url var="dataUrl" value="/amr/dataCollection/detail">
        <cti:param name="deviceGroup" value="${deviceGroup}"/>
        <cti:param name="includeDisabled" value="${includeDisabled}"/>
    </cti:url>
    <div data-url="${dataUrl}" data-static>
        <table class="compact-results-table row-highlighting has-actions">
            <tags:sort column="${deviceName}" />                
            <tags:sort column="${meterSerialNumber}" />                
            <tags:sort column="${deviceType}" />                
            <tags:sort column="${address}" />                
            <tags:sort column="${recentReading}" />                                
            <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            <c:forEach var="device" items="${detail.resultList}">
                <tr>
                    <td><cti:paoDetailUrl yukonPao="${device.paoIdentifier}">${device.deviceName}</cti:paoDetailUrl></td>
                    <td>${device.meterSerialNumber}</td>
                    <td>${device.paoIdentifier.paoType.paoTypeName}</td>
                    <td>${device.address}</td>
                    <td>
                        <cti:uniqueIdentifier var="popupId" prefix="historical-readings-"/>
                        <cti:url var="historyUrl" value="/meter/historicalReadings/view">
                            <cti:param name="pointId" value="${device.value.id}"/>
                            <cti:param name="deviceId" value="${device.paoIdentifier.paoId}"/>
                        </cti:url>
                        <a href="javascript:void(0);" data-popup="#${popupId}" class="${pageScope.classes}">
                            <cti:pointValueFormatter format="VALUE_UNIT" value="${device.value}" />
                            &nbsp;<cti:formatDate type="BOTH" value="${device.value.pointDataTimeStamp}"/>
                        </a>
                        <div id="${popupId}" data-width="500" data-height="400" data-url="${historyUrl}" class="dn"></div>
                    </td>
                    <td></td>
                </tr>
            </c:forEach>
        </table>
        <tags:pagingResultsControls result="${detail}" adjustPageCount="true" thousands="true"/>
    </div>
    
    <cti:includeScript link="HIGH_STOCK"/>
    <cti:includeScript link="/resources/js/widgets/yukon.widget.dataCollection.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.ami.dataCollection.detail.js"/>

</cti:standardPage>