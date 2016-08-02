<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>


<cti:standardPage module="tools" page="dataStreaming.summary">

    <style>
.inline {
    display: inline-flex;
}
</style>

    <cti:url var="action" value="/tools/dataStreaming/summary" />
    <form:form commandName="searchFilters" action="${action}" method="GET">
        <cti:csrfToken />

        <tags:sectionContainer2 nameKey="filterSection">

            <div class="column-10-10 clearfix">
                <div class="column one">

                    <tags:nameValueContainer2>

                        <tags:nameValue2 nameKey=".filter.gateway">
                            <select multiple name="gatewaysSelect" path="selectedGatewayIds">
                                <option value="-1" selected>Any</option>
                                <c:forEach var="gateway" items="${gateways}">
                                    <option value="${gateway.id}">${gateway.name}</option>
                                </c:forEach>
                            </select>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".filter.gatewayLoading">
                            <tags:input path="minLoadPercent" size="3" />
                            <i:inline key="yukon.common.units.PERCENT" />
                            <i:inline key="yukon.common.to" />
                            <tags:input path="maxLoadPercent" size="3" />
                            <i:inline key="yukon.common.units.PERCENT" />
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
                <div class="column two">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".filter.configuration">
                            <select path="selectedConfiguration" name="selectedConfiguration">
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
                        <tags:nameValue2 nameKey=".filter.attributes" valueClass="inline">
                            <select multiple name="attributesSelect" path="selectedAttributes">
                                <option value="-1" selected>Any</option>
                                <c:forEach var="attribute" items="${searchAttributes}">
                                    <option value="${attribute.key}">${attribute.description}</option>
                                </c:forEach>
                            </select>
                            <span style="margin-left: 40px;"> <i:inline
                                    key=".filter.interval" />:&nbsp;&nbsp;&nbsp;&nbsp; <tags:selectWithItems
                                    path="selectedInterval" items="${searchIntervals}"
                                    defaultItemValue="0" defaultItemLabel="Any" />
                            </span>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
            </div>

            <div class="action-area">
                <cti:button nameKey="search" classes="primary action" type="submit" />
                <cti:button nameKey="showAll" />
            </div>

        </tags:sectionContainer2>

    </form:form>
    
        <h3>
            <i:inline key=".results" arguments="${searchResults.hitCount}"/>&nbsp;
            <c:if test="${searchResults.hitCount > 0}">
                <cm:deviceCollectionMenu deviceCollection="${deviceCollection}"
                    triggerClasses="pull-icon-down"
                    key="yukon.web.modules.common.contextualMenu.actions"/>
            </c:if>
        </h3>
    
                <cti:url var="dataUrl" value="/tools/dataStreaming/summary">
                        <cti:param name="selectedConfiguration" value="${searchFilters.selectedConfiguration}"/>
                </cti:url>
                <div data-url="${dataUrl}" data-static>
                <table class="compact-results-table row-highlighting js-select-all-container">
                    <thead>
                        <tr>
                           <th><input type="checkbox" class="js-select-all" checked></th>
                           <tags:sort column="${deviceName}"/>
                           <tags:sort column="${deviceType}"/>
                           <tags:sort column="${meterNumber}"/>
                           <tags:sort column="${gatewayName}"/>
                           <tags:sort column="${gatewayLoading}"/>
                           <tags:sort column="${attributes}"/>
                           <tags:sort column="${interval}"/>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="result" items="${searchResults.resultList}">
                            <cti:url var="gatewayUrl" value="/stars/gateways/${result.gateway.paoIdentifier.paoId}"/>
                            <tr>
                                <td><input class="js-select-all-item" type="checkbox" name="${result.meter.name}" checked></td>
                                <td><cti:paoDetailUrl yukonPao="${result.meter}">${result.meter.name}</cti:paoDetailUrl></td>
                                <td>${result.meter.paoType.paoTypeName}</td>
                                <td>${result.meter.meterNumber}</td>
                                <td>
                                    <a href="${gatewayUrl}">${fn:escapeXml(result.gateway.name)}</a>
                                </td>
                                <td>${result.gateway.loadingPercent}</td>
                                <td>${result.config.commaDelimitedAttributes}</td>
                                <td>${result.config.selectedInterval}
                                    <c:choose>
                                        <c:when test="${result.config.selectedInterval > 1}">
                                            <i:inline key=".minutes"/>
                                        </c:when>
                                        <c:otherwise>
                                            <i:inline key=".minute"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <tags:pagingResultsControls result="${searchResults}" adjustPageCount="true" hundreds="true"/>
            </div>

</cti:standardPage>