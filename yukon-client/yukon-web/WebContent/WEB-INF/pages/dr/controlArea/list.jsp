<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="dr" page="controlAreaList">
    <cti:standardMenu menuSelection="details|controlareas"/>

    <tags:simpleDialog id="drDialog"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>
    <cti:includeScript link="/JavaScript/demandResponseAction.js"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
            <cti:msg key="yukon.web.modules.dr.controlAreaList.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.modules.dr.controlAreaList.breadcrumb.controlAreas"/></cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.controlAreaList.controlAreas"/></h2>
    <br>

    <c:set var="baseUrl" value="/spring/dr/controlArea/list"/>
    <cti:url var="submitUrl" value="${baseUrl}"/>
    <cti:url var="clearFilterUrl" value="${baseUrl}">
        <c:if test="${!empty param.itemsPerPage}">
            <cti:param name="itemsPerPage" value="${param.itemsPerPage}"/>
        </c:if>
        <c:if test="${!empty param.sort}">
            <cti:param name="sort" value="${param.sort}"/>
        </c:if>
        <c:if test="${!empty param.descending}">
            <cti:param name="descending" value="${param.descending}"/>
        </c:if>
    </cti:url>

    <script type="text/javascript">
    function clearFilter() {
        window.location = '${clearFilterUrl}';
    }
    </script>

    <cti:msg var="filterLabel" key="yukon.web.modules.dr.controlAreaList.filters"/>
    <tags:abstractContainer type="triangle" title="${filterLabel}" showInitially="false">
    <form:form action="${submitUrl}" commandName="backingBean" method="get">
        <c:if test="${!empty param.sort}">
            <input type="hidden" name="sort" value="${param.sort}"/>
        </c:if>
        <c:if test="${!empty param.descending}">
            <input type="hidden" name="descending" value="${param.descending}"/>
        </c:if>
        <c:if test="${!empty param.itemsPerPage}">
            <input type="hidden" name="itemsPerPage" value="${param.itemsPerPage}"/>
        </c:if>

        <table cellspacing="10">
            <tr>
                <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaList.filter.name"/>
                <td>${fieldName}</td>
                <td><form:input path="name"/></td>

                <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaList.filter.state"/>
                <td>${fieldName}:</td>
                <td>
                    <form:select path="state">
                        <form:option value="all">
                            <cti:msg key="yukon.web.modules.dr.controlAreaList.filter.state.all"/>
                        </form:option>
                        <form:option value="active">
                            <cti:msg key="yukon.web.modules.dr.controlAreaList.filter.state.active"/>
                        </form:option>
                        <form:option value="inactive">
                            <cti:msg key="yukon.web.modules.dr.controlAreaList.filter.state.inactive"/>
                        </form:option>
                    </form:select>
                </td>
            </tr>

            <tr>
                <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaList.filter.priority"/>
                <td>${fieldName}:</td>
                <td>
                    <form:input path="priority.min"/>${minStr}
                    <form:input path="priority.max"/>${maxStr}
                </td>

                <!--
                <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaList.filter.loadCapacity"/>
                <td>${fieldName}:</td>
                <td>
                    <form:input path="loadCapacity.min"/>${minStr}
                    <form:input path="loadCapacity.max"/>${maxStr}
                </td>
                -->
            </tr>
        </table>

        <br>
        <input type="submit" value="<cti:msg key="yukon.web.modules.dr.controlAreaList.filter.submit"/>"/>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.controlAreaList.filter.clear"/>"
            onclick="javascript:clearFilter()"/>
    </form:form>
    </tags:abstractContainer><br>

    <c:if test="${searchResult.hitCount == 0}">
        <cti:msg key="yukon.web.modules.dr.controlAreaList.noResults"/>
    </c:if>
    <c:if test="${searchResult.hitCount > 0}">
        <dr:searchNavigation searchResult="${searchResult}" baseUrl="${baseUrl}"/>
        <table id="controlAreaList" class="resultsTable activeResultsTable">
            <tr>
                <th>
                    <dr:sortByLink key="yukon.web.modules.dr.controlAreaList.heading.name"
                        baseUrl="${baseUrl}" fieldName="NAME"/>
                </th>
                <th>
                    <dr:sortByLink key="yukon.web.modules.dr.controlAreaList.heading.state"
                        baseUrl="${baseUrl}" fieldName="STATE"/>
                </th>
                <th><cti:msg key="yukon.web.modules.dr.controlAreaList.heading.actions"/></th>
                <th><cti:msg key="yukon.web.modules.dr.controlAreaList.heading.valueThreshold"/></th>
                <th><cti:msg key="yukon.web.modules.dr.controlAreaList.heading.peakProjection"/></th>
                <th><cti:msg key="yukon.web.modules.dr.controlAreaList.heading.atku"/></th>
                <th>
                    <dr:sortByLink key="yukon.web.modules.dr.controlAreaList.heading.priority"
                        baseUrl="${baseUrl}" fieldName="PRIORITY"/>
                </th>
                <th>
                    <dr:sortByLink key="yukon.web.modules.dr.controlAreaList.heading.timeWindow"
                        baseUrl="${baseUrl}" fieldName="START"/>
                </th>
                <th>
                    <dr:sortByLink key="yukon.web.modules.dr.controlAreaList.heading.loadCapacity"
                        baseUrl="${baseUrl}" fieldName="LOAD_CAPACITY"/>
                </th>
            </tr>
            <c:forEach var="controlArea" items="${controlAreas}">
                <c:set var="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
                <c:url var="controlAreaURL" value="/spring/dr/controlArea/detail">
                    <c:param name="controlAreaId" value="${controlAreaId}"/>
                </c:url>
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td>
                        <a href="${controlAreaURL}"><spring:escapeBody htmlEscape="true">${controlArea.name}</spring:escapeBody></a>
                    </td>
                    <td>
                        <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/STATE"/>
                    </td>
                    <td>
                        <span id="actionSpan_${loadGroupId}">
                            <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${controlArea}">
                                <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.start"/><br>
                                <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.stop"/><br>
        
                                <cti:url var="sendEnableUrl" value="/spring/dr/controlArea/sendEnableConfirm">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                    <cti:param name="isEnabled" value="true"/>
                                </cti:url>
                                <a id="enableLink_${controlAreaId}" href="javascript:void(0)"
                                    onclick="openSimpleDialog('drDialog', '${sendEnableUrl}', '<cti:msg key="yukon.web.modules.dr.controlArea.sendEnableConfirm.title"/>')">
                                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.enable"/>
                                </a>
                                <cti:url var="sendDisableUrl" value="/spring/dr/controlArea/sendEnableConfirm">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                    <cti:param name="isEnabled" value="false"/>
                                </cti:url>
                                <a id="disableLink_${controlAreaId}" href="javascript:void(0)"
                                    onclick="openSimpleDialog('drDialog', '${sendDisableUrl}', '<cti:msg key="yukon.web.modules.dr.controlArea.sendDisableConfirm.title"/>')">
                                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.disable"/>
                                </a><br>
                                <cti:dataUpdaterCallback function="updateEnabled('${controlAreaId}')" initialize="true" state="DR_CONTROLAREA/${controlAreaId}/ENABLED" />
                            </cti:checkPaoAuthorization>
                            <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${controlArea}" invert="true">
                                <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.start"/><br>
                                <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.stop"/><br>
                                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange"/><br>
                                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChange"/><br>
                                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.enable"/> / 
                                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.disable"/><br>
                                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak"/>
                            </cti:checkPaoAuthorization>
                        </span>
                    </td>

                    <td>
                        <c:if test="${empty controlArea.triggers}">
                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.info.noTriggers"/>
                        </c:if>
                        <c:forEach var="trigger" items="${controlArea.triggers}">
                               <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${trigger.triggerNumber}/VALUE_THRESHOLD"/>
                               <br/>
                        </c:forEach>
                    </td>
                    <td>
                        <c:forEach var="trigger" items="${controlArea.triggers}">
                            <c:if test="${trigger.thresholdType}">
                                <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${trigger.triggerNumber}/PEAK_PROJECTION"/>
                            </c:if>
                            <br/>
                        </c:forEach>

                    </td>
                    <td>
                        <c:forEach var="trigger" items="${controlArea.triggers}">
                           <c:if test="${trigger.thresholdType}">
                               <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${trigger.triggerNumber}/ATKU"/>
                            </c:if>
                            <br/>
                        </c:forEach>
                    </td>

                    <td>
                        <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/PRIORITY"/>
                    </td>
                    <td>
                        <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/START"/>
                        <cti:msg key="yukon.web.modules.dr.controlAreaDetail.info.separator"/>
                        <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/STOP"/>
                    </td>
                    <td>
                        <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/LOAD_CAPACITY"/>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <dr:searchNavigation searchResult="${searchResult}" baseUrl="${baseUrl}"/>
    </c:if>

</cti:standardPage>
