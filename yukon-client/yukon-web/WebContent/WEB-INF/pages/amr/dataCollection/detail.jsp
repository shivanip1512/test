<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="amr" page="dataCollection.detail">

    <cti:toJson id="summaryData" object="${summary}"/>
    
    <c:forEach var="range" items="${rangeTypes}">
        <input type="hidden" class="js-${range}" value="<i:inline key=".rangeType.${range}"/>"></input>
    </c:forEach>
    
    <cti:url var="action" value="/amr/dataCollection/detail" />
    <form action="${action}" method="GET">
        <input type="hidden" name="deviceGroup" value="${deviceGroup}"/>
        <input type="hidden" name="includeDisabled" value="${includeDisabled}"/>
        <div class="column-12-12 clearfix">
            <div class="column one">
                <div style="max-height: 200px;" class="js-pie-chart-summary"></div>
            </div>
            <div class="column two nogutter" style="border:1px solid #ccc;padding:5px;margin-top:20px;">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".deviceGroup">
                        ${deviceGroup}
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".deviceGroups">
                        <cti:list var="group"><cti:item value="${deviceSubGroup}"/></cti:list>
                        <tags:deviceGroupPicker inputName="deviceSubGroup" inputValue="${group}"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".range">
                        <div class="button-group stacked">
                            <c:forEach var="range" items="${rangeTypes}">
                                <c:set var="checked" value="${false}"/>
                                <c:forEach var="rangeEnum" items="${ranges}">
                                    <c:if test="${rangeEnum eq range}">
                                        <c:set var="checked" value="${true}"/>
                                    </c:if>
                                </c:forEach>
                                <tags:check name="ranges" key=".rangeType.${range}" checked="${checked}" value="${range}"></tags:check>
                            </c:forEach>
                        </div>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <div class="action-area">
                    <cti:button classes="primary action" label="Filter" type="submit" busy="true"/>
                </div>
            </div>
        </div>
    
    </form>


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
                <cm:dropdownOption key=".collectionActions" href="${collectionActionsUrl}" icon="icon-cog-go"/>    
                <cti:url var="mapUrl" value="/tools/map">
                    <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                </cti:url>
                <cm:dropdownOption icon="icon-map-sat" key=".mapDevices" href="${mapUrl}"/>     
                <cti:url var="readUrl" value="/group/groupMeterRead/homeCollection">
                    <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
                        <cti:param name="${cp.key}" value="${cp.value}"/>
                    </c:forEach>                
                </cti:url>
                <cm:dropdownOption icon="icon-read" key=".readAttribute" href="${readUrl}"/>          
                <cti:url var="commandUrl" value="/group/commander/collectionProcessing">
                    <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
                        <cti:param name="${cp.key}" value="${cp.value}"/>
                    </c:forEach>                
                </cti:url>
                <cm:dropdownOption icon="icon-ping" key=".sendCommand" href="${commandUrl}"/>   
                <cti:url var="locateRouteUrl" value="/bulk/routeLocate/home">
                    <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
                        <cti:param name="${cp.key}" value="${cp.value}"/>
                    </c:forEach>                
                </cti:url>
                <cm:dropdownOption icon="icon-connect" key=".locateRoute" href="${locateRouteUrl}"/>   
            </cm:dropdown>
        </span>
    </c:if>

    <cti:url var="dataUrl" value="/amr/dataCollection/deviceResults">
        <cti:param name="deviceGroup" value="${deviceGroup}"/>
        <cti:param name="deviceSubGroup" value="${deviceSubGroup}"/>
        <cti:param name="includeDisabled" value="${includeDisabled}"/>
        <c:forEach var="range" items="${ranges}">
            <cti:param name="ranges" value="${range}"/>
        </c:forEach>
    </cti:url>
    <div data-url="${dataUrl}">
        <%@ include file="deviceTable.jsp" %>
    </div>
    
    <cti:includeScript link="HIGH_STOCK"/>
    <cti:includeScript link="/resources/js/widgets/yukon.widget.dataCollection.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.ami.dataCollection.detail.js"/>

</cti:standardPage>