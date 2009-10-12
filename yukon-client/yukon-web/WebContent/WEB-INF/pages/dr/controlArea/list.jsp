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
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
            <cti:msg key="yukon.web.modules.dr.controlAreaList.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.modules.dr.controlAreaList.breadcrumb.controlAreas"/></cti:crumbLink>
    </cti:breadCrumbs>
    <dr:favoriteIconJS/>

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
    <tags:simplePopup id="filterPopup" title="${filterLabel}">
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

        <cti:msg var="minStr" key="yukon.web.modules.dr.controlAreaList.filter.min"/>
        <cti:msg var="maxStr" key="yukon.web.modules.dr.controlAreaList.filter.max"/>
        <table cellspacing="10">
            <tr>
                <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaList.filter.name"/>
                <td>${fieldName}</td>
                <td><form:input path="name" size="40"/></td>
                
                <cti:checkRolesAndProperties value="CONTROL_AREA_STATE">
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
                </cti:checkRolesAndProperties>
            </tr>

            <cti:checkRolesAndProperties value="CONTROL_AREA_PRIORITY,CONTROL_AREA_LOAD_CAPACITY">
                <tr>
                    <cti:checkRolesAndProperties value="CONTROL_AREA_PRIORITY">
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaList.filter.priority"/>
                        <td>${fieldName}:</td>
                        <td>
                            <form:input path="priority.min" size="5"/>&nbsp;${minStr}&nbsp;&nbsp;
                            <form:input path="priority.max" size="5"/>&nbsp;${maxStr}
                        </td>
                    </cti:checkRolesAndProperties>
    
                    <cti:checkRolesAndProperties value="CONTROL_AREA_LOAD_CAPACITY">
                        <!--
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaList.filter.loadCapacity"/>
                        <td>${fieldName}:</td>
                        <td>
                            <form:input path="loadCapacity.min"/>${minStr}
                            <form:input path="loadCapacity.max"/>${maxStr}
                        </td>
                        -->
                    </cti:checkRolesAndProperties>
                </tr>
                </cti:checkRolesAndProperties>
        </table>

        <br>
        <input type="submit" value="<cti:msg key="yukon.web.modules.dr.controlAreaList.filter.submit"/>"/>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.controlAreaList.filter.clear"/>"
            onclick="javascript:clearFilter()"/>
    </form:form>
    </tags:simplePopup><br>

    <c:if test="${searchResult.hitCount == 0}">
        <cti:msg key="yukon.web.modules.dr.controlAreaList.noResults"/><br>
        <a href="javascript:void(0)" onclick="$('filterPopup').show()"><cti:msg key="yukon.web.modules.dr.paging.filter"/></a>
        <a href="javascript:void(0)" onclick="javascript:clearFilter()"><cti:msg key="yukon.web.modules.dr.loadGroupList.filter.clear"/></a>
    </c:if>
    <c:if test="${searchResult.hitCount > 0}">
        <dr:searchNavigation searchResult="${searchResult}" baseUrl="${baseUrl}"
            filter="$('filterPopup').show()"/>
        <cti:msg var="controlAreaTitle" key="yukon.web.modules.dr.controlAreaList.controlAreas"/>
        <tags:abstractContainer type="box" title="${controlAreaTitle}">
            <table id="controlAreaList" class="compactResultsTable rowHighlighting">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <th></th>
                    <th>
                        <dr:sortByLink key="yukon.web.modules.dr.controlAreaList.heading.name"
                            baseUrl="${baseUrl}" fieldName="NAME"/>
                    </th>
                    <cti:checkRolesAndProperties value="CONTROL_AREA_STATE">
                        <th>
                            <dr:sortByLink key="yukon.web.modules.dr.controlAreaList.heading.state"
                                baseUrl="${baseUrl}" fieldName="STATE"/>
                        </th>
                    </cti:checkRolesAndProperties>
                    <th><cti:msg key="yukon.web.modules.dr.controlAreaList.heading.actions"/></th>
                    <cti:checkRolesAndProperties value="CONTROL_AREA_VALUE_THRESHOLD">
                        <th><cti:msg key="yukon.web.modules.dr.controlAreaList.heading.valueThreshold"/></th>
                    </cti:checkRolesAndProperties>
                    <cti:checkRolesAndProperties value="CONTROL_AREA_PEAK_PROJECTION">
                        <th><cti:msg key="yukon.web.modules.dr.controlAreaList.heading.peakProjection"/></th>
                    </cti:checkRolesAndProperties>
                    <cti:checkRolesAndProperties value="CONTROL_AREA_ATKU">
                        <th><cti:msg key="yukon.web.modules.dr.controlAreaList.heading.atku"/></th>
                    </cti:checkRolesAndProperties>
                    <cti:checkRolesAndProperties value="CONTROL_AREA_PRIORITY">
                        <th>
                            <dr:sortByLink key="yukon.web.modules.dr.controlAreaList.heading.priority"
                                baseUrl="${baseUrl}" fieldName="PRIORITY"/>
                        </th>
                    </cti:checkRolesAndProperties>
                    <cti:checkRolesAndProperties value="CONTROL_AREA_TIME_WINDOW">
                        <th>
                            <dr:sortByLink key="yukon.web.modules.dr.controlAreaList.heading.timeWindow"
                                baseUrl="${baseUrl}" fieldName="START"/>
                        </th>
                    </cti:checkRolesAndProperties>
                    <cti:checkRolesAndProperties value="CONTROL_AREA_LOAD_CAPACITY">
                        <th>
                            <dr:sortByLink key="yukon.web.modules.dr.controlAreaList.heading.loadCapacity"
                                baseUrl="${baseUrl}" fieldName="LOAD_CAPACITY"/>
                        </th>
                    </cti:checkRolesAndProperties>
                </tr>
                <c:forEach var="controlArea" items="${controlAreas}">
                    <c:set var="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
                    <c:url var="controlAreaUrl" value="/spring/dr/controlArea/detail">
                        <c:param name="controlAreaId" value="${controlAreaId}"/>
                    </c:url>
                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                        <td><dr:favoriteIcon paoId="${controlAreaId}"/></td>
                        <td>
                            <a href="${controlAreaUrl}"><spring:escapeBody htmlEscape="true">${controlArea.name}</spring:escapeBody></a>
                        </td>
                        <cti:checkRolesAndProperties value="CONTROL_AREA_STATE">
                            <td>
                                <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/STATE"/>
                            </td>
                        </cti:checkRolesAndProperties>
                        <td style="white-space: nowrap;">
                            <dr:controlAreaListActions pao="${controlArea}"/>
                        </td>
                        <cti:checkRolesAndProperties value="CONTROL_AREA_VALUE_THRESHOLD">
                            <td>
                                <c:if test="${empty controlArea.triggers}">
                                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.info.noTriggers"/>
                                </c:if>
                                <c:forEach var="trigger" items="${controlArea.triggers}">
                                       <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${trigger.triggerNumber}/VALUE_THRESHOLD"/>
                                       <br/>
                                </c:forEach>
                            </td>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="CONTROL_AREA_PEAK_PROJECTION">
                            <td>
                                <c:forEach var="trigger" items="${controlArea.triggers}">
                                    <c:if test="${trigger.thresholdType}">
                                        <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${trigger.triggerNumber}/PEAK_PROJECTION"/>
                                    </c:if>
                                    <br/>
                                </c:forEach>
        
                            </td>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="CONTROL_AREA_ATKU">
                            <td>
                                <c:forEach var="trigger" items="${controlArea.triggers}">
                                   <c:if test="${trigger.thresholdType}">
                                       <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${trigger.triggerNumber}/ATKU"/>
                                    </c:if>
                                    <br/>
                                </c:forEach>
                            </td>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="CONTROL_AREA_PRIORITY">
                            <td>
                                <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/PRIORITY"/>
                            </td>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="CONTROL_AREA_TIME_WINDOW">
                            <td>
                                <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/START"/>
                                <cti:msg key="yukon.web.modules.dr.controlAreaDetail.info.separator"/>
                                <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/STOP"/>
                            </td>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="CONTROL_AREA_LOAD_CAPACITY">
                            <td>
                                <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/LOAD_CAPACITY"/>
                            </td>
                        </cti:checkRolesAndProperties>
                    </tr>
                </c:forEach>
            </table>
        </tags:abstractContainer>
        <dr:searchNavigation searchResult="${searchResult}" baseUrl="${baseUrl}"
            filter="$('filterPopup').show()"/>
    </c:if>

</cti:standardPage>
