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
    <dr:favoriteIconSetup/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
            <cti:msg key="yukon.web.modules.dr.controlAreaList.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/home">
            <cti:msg key="yukon.web.modules.dr.controlAreaList.breadcrumb.drHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.modules.dr.controlAreaList.breadcrumb.controlAreas"/></cti:crumbLink>
    </cti:breadCrumbs>

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

    <%-- Control Area filtering popup section --%>
    
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
        <div class="actionArea">
            <input type="submit" value="<cti:msg key="yukon.web.modules.dr.controlAreaList.filter.submit"/>"/>
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.controlAreaList.filter.clear"/>"
                onclick="javascript:clearFilter()"/>
        </div>
    </form:form>
    </tags:simplePopup><br>

    <%-- Main Control Area list table section --%>

    <cti:msg var="controlAreaTitle" key="yukon.web.modules.dr.controlAreaList.controlAreas"/>
    <tags:pagedBox title="${controlAreaTitle}" searchResult="${searchResult}"
        filterDialog="filterPopup" baseUrl="${baseUrl}" isFiltered="${isFiltered}"
        showAllUrl="${clearFilterUrl}">
        <table id="controlAreaList" class="compactResultsTable rowHighlighting">
            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                
                <%-- Table headers - columns are hidden/shown based on role props --%>
                
                <th></th>
                <c:set var="numColumns" value="1"/>
                <th>
                    <dr:sortByLink key="yukon.web.modules.dr.controlAreaList.heading.name"
                        baseUrl="${baseUrl}" fieldName="CA_NAME"/>
                </th>
                <cti:checkRolesAndProperties value="CONTROL_AREA_STATE">
                    <c:set var="numColumns" value="${numColumns + 1}"/>
                    <th>
                        <dr:sortByLink key="yukon.web.modules.dr.controlAreaList.heading.state"
                            baseUrl="${baseUrl}" fieldName="CA_STATE"/>
                    </th>
                </cti:checkRolesAndProperties>
                <c:set var="numColumns" value="${numColumns + 1}"/>
                <th><cti:msg key="yukon.web.modules.dr.controlAreaList.heading.actions"/></th>
                <cti:checkRolesAndProperties value="CONTROL_AREA_VALUE_THRESHOLD">
                    <c:set var="numColumns" value="${numColumns + 1}"/>
                    <th>
                        <dr:sortByLink key="yukon.web.modules.dr.controlAreaList.heading.valueThreshold"
                            baseUrl="${baseUrl}" fieldName="TR_VALUE_THRESHOLD"/>
                    </th>
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="CONTROL_AREA_PEAK_PROJECTION">
                    <c:set var="numColumns" value="${numColumns + 1}"/>
                    <th>
                        <dr:sortByLink key="yukon.web.modules.dr.controlAreaList.heading.peakProjection"
                            baseUrl="${baseUrl}" fieldName="TR_PEAK_PROJECTION"/>
                    </th>
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="CONTROL_AREA_ATKU">
                    <c:set var="numColumns" value="${numColumns + 1}"/>
                    <th>
                        <dr:sortByLink key="yukon.web.modules.dr.controlAreaList.heading.atku"
                            baseUrl="${baseUrl}" fieldName="TR_ATKU"/>
                    </th>
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="CONTROL_AREA_PRIORITY">
                    <c:set var="numColumns" value="${numColumns + 1}"/>
                    <th>
                        <dr:sortByLink key="yukon.web.modules.dr.controlAreaList.heading.priority"
                            baseUrl="${baseUrl}" fieldName="CA_PRIORITY"/>
                    </th>
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="CONTROL_AREA_TIME_WINDOW">
                    <c:set var="numColumns" value="${numColumns + 1}"/>
                    <th>
                        <dr:sortByLink key="yukon.web.modules.dr.controlAreaList.heading.timeWindow"
                            baseUrl="${baseUrl}" fieldName="CA_START"/>
                    </th>
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="CONTROL_AREA_LOAD_CAPACITY">
                    <c:set var="numColumns" value="${numColumns + 1}"/>
                    <th>
                        <dr:sortByLink key="yukon.web.modules.dr.controlAreaList.heading.loadCapacity"
                            baseUrl="${baseUrl}" fieldName="CA_LOAD_CAPACITY"/>
                    </th>
                </cti:checkRolesAndProperties>
            </tr>
            <c:forEach var="controlArea" items="${controlAreas}">
                
                <%-- Table data section - columns are hidden/shown based on role props --%>
                
                <c:set var="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
                <c:url var="controlAreaUrl" value="/spring/dr/controlArea/detail">
                    <c:param name="controlAreaId" value="${controlAreaId}"/>
                </c:url>
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td><dr:favoriteIcon paoId="${controlAreaId}" isFavorite="${favoritesByPaoId[controlAreaId]}"/></td>
                    <td>
                        <a href="${controlAreaUrl}"><spring:escapeBody htmlEscape="true">${controlArea.name}</spring:escapeBody></a>
                    </td>
                    <cti:checkRolesAndProperties value="CONTROL_AREA_STATE">
                        <td>
                            <dr:controlAreaState controlAreaId="${controlAreaId}"/>
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
            <c:if test="${searchResult.hitCount == 0}">
                <tr><td></td><td colspan="${numColumns}"><cti:msg key="yukon.web.modules.dr.controlAreaList.noResults"/></td></tr>
            </c:if>

        </table>
    </tags:pagedBox>

</cti:standardPage>
