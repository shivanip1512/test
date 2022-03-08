<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>


<cti:standardPage module="tools" page="dataStreaming.summary">
    <cti:url var="action" value="/tools/dataStreaming/exportSearch" />
    <form:form id="searchForm" modelAttribute="searchFilters" action="${action}" method="GET">
        <cti:csrfToken />

        <span class="fr cp"><cti:icon icon="icon-help" data-popup="#page-help"/></span>
        <cti:msg2 var="helpTitle" key=".detail.helpTitle"/>
        <cti:msg2 var="helpText" key=".detail.helpText"/>
        <div id="page-help" class="dn" data-title="${helpTitle}" data-width="500" data-height="270">${helpText}</div>

        <div class="filter-section" id="searchSection">
            <hr>

            <span class="vat"><i:inline key="yukon.common.filterBy"/></span>
            <cti:msg2 var="gatewayPlaceholder" key=".filter.gateways"/>
            <tags:selectWithItems items="${gateways}" path="selectedGatewayIds" itemLabel="name" itemValue="id"
                                  inputClass="js-selected-gateways" dataPlaceholder="${gatewayPlaceholder}"/>
                                  
            <span class="ML15"><i:inline key=".filter.gatewayLoading"/>:</span>
            <cti:msg2 var="percent" key="yukon.common.units.PERCENT"/>
            <cti:msg2 var="min" key="yukon.common.min"/>
            <cti:msg2 var="max" key="yukon.common.max"/>
            <tags:input path="minLoadPercent" size="3" units="${percent}" inputClass="MR0 vam" placeholder="${min}"/>
            &nbsp;&nbsp;<i:inline key="yukon.common.to" />&nbsp;&nbsp;
            <tags:input path="maxLoadPercent" size="3" units="${percent}" inputClass="MR0 vam" placeholder="${max}"/>
            
            <div style="padding-top:5px;padding-left:60px;">
                <form:select path="selectedConfiguration" name="selectedConfiguration" class="js-selected-configuration">
                    <form:option value="-1">
                        <cti:msg2 key=".filter.configuration"/>
                    </form:option>
                    <c:forEach var="config" items="${existingConfigs}">
                        <c:set var="selected" value=""/>
                        <c:if test="${searchFilters.selectedConfiguration == config.id}">
                            <c:set var="selected" value="selected"/>
                        </c:if>
                        <option value="${config.id}" ${selected}>${fn:escapeXml(config.name)}</option>
                    </c:forEach>
                </form:select>
                
                <span class="ML15">
                    <cti:msg2 var="attributePlaceholder" key=".filter.attributes"/>
                    <tags:selectWithItems items="${searchAttributes}" path="selectedAttributes" itemLabel="description" itemValue="key"
                                          inputClass="js-selected-attInterval js-selected-attribute ML15" dataPlaceholder="${attributePlaceholder}"/>
                </span>

                <span class="ML15">
                    <cti:msg2 var="intervalPlaceholder" key=".filter.interval"/>
                    <tags:selectWithItems id="intervalSelect" path="selectedInterval" items="${searchIntervals}" inputClass="js-selected-attInterval vam"
                                          defaultItemValue="-1" defaultItemLabel="${intervalPlaceholder}"/>
                </span>

                <span class="fr MT5 MB5">
                    <cti:url var="showAllUrl" value="/tools/dataStreaming/summary" />
                    <cti:button nameKey="showAll" href="${showAllUrl}"/>
                    <cti:button nameKey="export" classes="primary action" type="submit" />
                </span>
            </div>

        <hr>
    </div>

    </form:form>
    
    <cti:url var="action" value="/tools/dataStreaming/createTemporaryGroup" />
    <form:form id="createTempGroupForm" action="${action}" method="POST">
        <cti:csrfToken/>
        <input class="js-selected-ids" type="hidden" id="deviceIds" name="idList.ids" value="${deviceIds}"/>
        <input type="hidden" id="redirectUrl" name="redirectUrl"/>
     </form:form>
        
        <h3>
            <i:inline key=".results"/>&nbsp;
            <span class="dn js-results-count">${searchResults.hitCount}</span>
            <span class="dn js-results-ids">${deviceIds}</span>
            <span class="badge">${searchResults.hitCount}</span>
            <span class="fwn"><i:inline key=".devices"/></span>
            <c:if test="${searchResults.hitCount > 0}">
                <span class="js-cog-menu">
                    <cm:dropdown icon="icon-cog">
                        <cti:checkRolesAndProperties value="RF_DATA_STREAMING">
                            <cm:dropdownOption key=".configure" icon="icon-cog-edit" classes="js-selected-configure"/>
                            <cm:dropdownOption key=".remove" href="${removeUrl}" icon="icon-cross" classes="js-selected-remove"/>
                        </cti:checkRolesAndProperties>
                        <cm:dropdownOption key=".collectionActions" href="${collectionActionsUrl}" icon="icon-cog-go" classes="js-selected-actions"/>
                    </cm:dropdown>
                </span>
            </c:if>
        </h3>

        <cti:url var="dataUrl" value="/tools/dataStreaming/summaryResults">
            <c:forEach var="gateway" items="${searchFilters.selectedGatewayIds}">
                <cti:param name="selectedGatewayIds" value="${gateway}"/>
            </c:forEach>
            <c:forEach var="attribute" items="${searchFilters.selectedAttributes}">
                <cti:param name="selectedAttributes" value="${attribute}"/>
            </c:forEach>
            <cti:param name="selectedConfiguration" value="${searchFilters.selectedConfiguration}"/>
            <cti:param name="selectedInterval" value="${searchFilters.selectedInterval}"/>
            <cti:param name="minLoadPercent" value="${searchFilters.minLoadPercent}"/>
            <cti:param name="maxLoadPercent" value="${searchFilters.maxLoadPercent}"/>
        </cti:url>
        <div data-url="${dataUrl}" data-load-event="yukon:tools:dataStreaming:results:load">
            <%@ include file="summaryResults.jsp" %>
        </div>

    <cti:includeScript link="OPEN_LAYERS"/>
    <cti:includeCss link="/resources/js/lib/open-layers/ol.css"/>
    <cti:includeScript link="/resources/js/pages/yukon.tools.dataStreaming.js"/>

</cti:standardPage>