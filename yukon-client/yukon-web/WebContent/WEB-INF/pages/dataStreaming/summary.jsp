<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>


<cti:standardPage module="tools" page="dataStreaming.summary">

    <cti:url var="action" value="/tools/dataStreaming/summary" />
    <form:form commandName="searchFilters" action="${action}" method="GET">
        <cti:csrfToken />

        <tags:sectionContainer2 nameKey="filterSection">

            <div class="column-10-10 clearfix">
                <div class="column one">

                    <tags:nameValueContainer2>

                        <tags:nameValue2 nameKey=".filter.gateway">
                            <select multiple id="gatewaysSelect" name="gatewaysSelect" size="6" style="min-width:200px;">
                                <option value="-1" <c:if test="${searchFilters.selectedGatewayIds.size() == 0}">selected</c:if>>Any</option>
                                <c:forEach var="gateway" items="${gateways}">
                                    <option value="${gateway.id}" ${(fn:contains(searchFilters.selectedGatewayIds, gateway.id))? 'selected':''}>${gateway.name}</option>
                                </c:forEach>
                            </select>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".filter.gatewayLoading">
                            <tags:input path="minLoadPercent" size="3" />
                            <i:inline key="yukon.common.units.PERCENT" />&nbsp;&nbsp;
                            <i:inline key="yukon.common.to" />&nbsp;&nbsp;
                            <tags:input path="maxLoadPercent" size="3" />
                            <i:inline key="yukon.common.units.PERCENT" />
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
                <div class="column two">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".filter.configuration">
                            <select path="selectedConfiguration" name="selectedConfiguration" class="js-selected-configuration">
                                <option value="-1">Any</option>
                                <c:forEach var="config" items="${existingConfigs}">
                                    <c:set var="selected" value=""/>
                                    <c:if test="${searchFilters.selectedConfiguration == config.id}">
                                        <c:set var="selected" value="selected"/>
                                    </c:if>
                                    <option value="${config.id}" ${selected}>${config.name}</option>
                                </c:forEach>
                            </select>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".filter.attributes" valueClass="dif">
                            <select multiple id="attributesSelect" name="attributesSelect" class="js-selected-attInterval" size="6" style="min-width:200px;">
                                <option value="-1" <c:if test="${searchFilters.selectedAttributes.size() == 0}">selected</c:if>>Any</option>
                                <c:forEach var="attribute" items="${searchAttributes}">
                                    <option value="${attribute.key}" ${(fn:contains(searchFilters.selectedAttributes, attribute.key))? 'selected':''}>${attribute.description}</option>
                                </c:forEach>
                            </select>
                            <span style="margin-left: 40px;"> <i:inline
                                    key=".filter.interval" />:&nbsp;&nbsp;&nbsp;&nbsp; 
                                    <tags:selectWithItems id="intervalSelect" path="selectedInterval" items="${searchIntervals}" inputClass="js-selected-attInterval"
                                    defaultItemValue="-1" defaultItemLabel="Any" />
                            </span>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
            </div>

            <div class="action-area">
                <cti:button nameKey="search" classes="primary action" type="submit" />
                <cti:button nameKey="showAll" classes="js-show-all"/>
            </div>

        </tags:sectionContainer2>

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
                <cti:param name="gatewaysSelect" value="${gateway}"/>
            </c:forEach>
            <c:forEach var="attribute" items="${searchFilters.selectedAttributes}">
                <cti:param name="attributesSelect" value="${attribute}"/>
            </c:forEach>
            <cti:param name="selectedConfiguration" value="${searchFilters.selectedConfiguration}"/>
            <cti:param name="selectedInterval" value="${searchFilters.selectedInterval}"/>
            <cti:param name="minLoadPercent" value="${searchFilters.minLoadPercent}"/>
            <cti:param name="maxLoadPercent" value="${searchFilters.maxLoadPercent}"/>
        </cti:url>
        <div data-url="${dataUrl}" data-load-event="yukon:tools:dataStreaming:results:load">
            <%@ include file="summaryResults.jsp" %>
        </div>
        
            
        <cti:includeScript link="/resources/js/pages/yukon.tools.dataStreaming.js"/>
            

</cti:standardPage>