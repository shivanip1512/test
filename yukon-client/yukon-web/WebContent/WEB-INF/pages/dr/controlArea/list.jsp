<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>

<cti:standardPage module="dr" page="controlAreaList">

    <tags:simpleDialog id="drDialog" />
    <cti:includeScript link="/JavaScript/hideReveal.js"/>
    <cti:includeScript link="/JavaScript/drEstimatedLoad.js"/>

    <c:set var="baseUrl" value="/dr/controlArea/list" />
    <cti:url var="submitUrl" value="${baseUrl}" />
    <cti:url var="clearFilterUrl" value="${baseUrl}">
        <c:if test="${!empty param.itemsPerPage}">
            <cti:param name="itemsPerPage" value="${param.itemsPerPage}" />
        </c:if>
        <c:if test="${!empty param.sort}">
            <cti:param name="sort" value="${param.sort}" />
        </c:if>
        <c:if test="${!empty param.descending}">
            <cti:param name="descending" value="${param.descending}" />
        </c:if>
    </cti:url>

    <script type="text/javascript">
            function clearFilter() {
                window.location = '${clearFilterUrl}';
            }
    </script>

    <%-- Control Area filtering popup section --%>

    <cti:msg var="filterLabel"
        key="yukon.web.modules.dr.controlAreaList.filters" />
    <tags:simplePopup id="filterPopup" title="${filterLabel}">
        <cti:flashScopeMessages/>

        <form:form action="${submitUrl}" commandName="backingBean"
            method="get">
            <tags:sortFields backingBean="${backingBean}" />

            <cti:msg var="minStr"
                key="yukon.web.modules.dr.controlAreaList.filter.min" />
            <cti:msg var="maxStr"
                key="yukon.web.modules.dr.controlAreaList.filter.max" />
            <table cellspacing="10">
                <tr>
                    <cti:msg var="fieldName"
                        key="yukon.web.modules.dr.controlAreaList.filter.name" />
                    <td>${fieldName}</td>
                    <td><form:input path="name" size="40" /></td>

                    <cti:checkRolesAndProperties value="CONTROL_AREA_STATE">
                        <cti:msg var="fieldName"
                            key="yukon.web.modules.dr.controlAreaList.filter.state" />
                        <td>${fieldName}</td>
                        <td><form:select path="state">
                            <form:option value="all">
                                <cti:msg
                                    key="yukon.web.modules.dr.controlAreaList.filter.state.all" />
                            </form:option>
                            <form:option value="active">
                                <cti:msg
                                    key="yukon.web.modules.dr.controlAreaList.filter.state.active" />
                            </form:option>
                            <form:option value="inactive">
                                <cti:msg
                                    key="yukon.web.modules.dr.controlAreaList.filter.state.inactive" />
                            </form:option>
                        </form:select></td>
                    </cti:checkRolesAndProperties>
                </tr>

                <cti:checkRolesAndProperties
                    value="CONTROL_AREA_PRIORITY,CONTROL_AREA_LOAD_CAPACITY">
                    <tr>
                        <cti:checkRolesAndProperties value="CONTROL_AREA_PRIORITY">
                            <cti:msg var="fieldName"
                                key="yukon.web.modules.dr.controlAreaList.filter.priority" />
                            <td>${fieldName}</td>
                            <td>
                              <table>
                                <tr>
                                  <td><tags:input path="priority.min" size="5"/>&nbsp;${minStr}</td>
                                  <td><tags:input path="priority.max" size="5"/>&nbsp;${maxStr}</td>
                                </tr>
                              </table>
                            </td>
                        </cti:checkRolesAndProperties>
                    </tr>
                </cti:checkRolesAndProperties>
            </table>

            <div class="action-area">
                <cti:button nameKey="filter" type="submit" classes="action primary"/>
                <cti:button nameKey="showAll" onclick="javascript:clearFilter()"/>
            </div>
        </form:form>
    </tags:simplePopup>

    <%-- Main Control Area list table section --%>

    <cti:msg var="controlAreaTitle"
        key="yukon.web.modules.dr.controlAreaList.controlAreas" />
    <tags:pagedBox title="${controlAreaTitle}"
        searchResult="${searchResult}" filterDialog="filterPopup"
        baseUrl="${baseUrl}" isFiltered="${isFiltered}"
        showAllUrl="${clearFilterUrl}">
        <c:choose>
            <c:when test="${searchResult.hitCount == 0}">
                <cti:msg key="yukon.web.modules.dr.controlAreaList.noResults" />
            </c:when>
            <c:otherwise>
                <table id="controlAreaList" class="compact-results-table row-highlighting has-actions">
                    <thead>
                        <tr>
                            <%-- Table headers - columns are hidden/shown based on role props --%>
                            <th><tags:sortLink nameKey="heading.name"
                                baseUrl="${baseUrl}" fieldName="CA_NAME" isDefault="true"/></th>
                            <cti:checkRolesAndProperties value="CONTROL_AREA_STATE">
                                <th><tags:sortLink nameKey="heading.state"
                                    baseUrl="${baseUrl}" fieldName="CA_STATE" /></th>
                            </cti:checkRolesAndProperties>
                            <cti:checkRolesAndProperties value="CONTROL_AREA_VALUE_THRESHOLD">
                                <th><tags:sortLink nameKey="heading.valueThreshold"
                                    baseUrl="${baseUrl}" fieldName="TR_VALUE_THRESHOLD"/></th>
                            </cti:checkRolesAndProperties>
                            <cti:checkRolesAndProperties value="CONTROL_AREA_PEAK_PROJECTION">
                                <th><tags:sortLink nameKey="heading.peakProjection"
                                    baseUrl="${baseUrl}" fieldName="TR_PEAK_PROJECTION"/></th>
                            </cti:checkRolesAndProperties>
                            <cti:checkRolesAndProperties value="CONTROL_AREA_ATKU">
                                <th><tags:sortLink nameKey="heading.atku"
                                    baseUrl="${baseUrl}" fieldName="TR_ATKU"/></th>
                            </cti:checkRolesAndProperties>
                            <cti:checkRolesAndProperties value="CONTROL_AREA_PRIORITY">
                                <th><tags:sortLink nameKey="heading.priority"
                                    baseUrl="${baseUrl}" fieldName="CA_PRIORITY"/></th>
                            </cti:checkRolesAndProperties>
                            <cti:checkRolesAndProperties value="CONTROL_AREA_TIME_WINDOW">
                                <th><tags:sortLink nameKey="heading.timeWindow"
                                    baseUrl="${baseUrl}" fieldName="CA_START"/></th>
                            </cti:checkRolesAndProperties>
                            <cti:checkRolesAndProperties value="ENABLE_ESTIMATED_LOAD">
                                <th><i:inline key=".heading.kwSavings"/></th>
                            </cti:checkRolesAndProperties>
                            <th class="action-column"></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="controlArea" items="${controlAreas}">
    
                            <%-- Table data section - columns are hidden/shown based on role props --%>
    
                            <c:set var="controlAreaId"
                                value="${controlArea.paoIdentifier.paoId}" />
                            <c:url var="controlAreaUrl" value="/dr/controlArea/detail">
                                <c:param name="controlAreaId" value="${controlAreaId}" />
                            </c:url>
                            <tr>
                                <td><a href="${controlAreaUrl}">${fn:escapeXml(controlArea.name)}</a></td>
                                <cti:checkRolesAndProperties value="CONTROL_AREA_STATE">
                                    <td><dr:controlAreaState controlAreaId="${controlAreaId}" />
                                    </td>
                                </cti:checkRolesAndProperties>
                                <cti:checkRolesAndProperties value="CONTROL_AREA_VALUE_THRESHOLD">
                                    <td><c:if test="${empty controlArea.triggers}">
                                        <cti:msg
                                            key="yukon.web.modules.dr.controlAreaDetail.info.noTriggers" />
                                    </c:if> <c:forEach var="trigger" items="${controlArea.triggers}">
                                        <cti:dataUpdaterValue type="DR_CA_TRIGGER"
                                            identifier="${controlAreaId}/${trigger.triggerNumber}/VALUE_THRESHOLD" />
                                        <br />
                                    </c:forEach></td>
                                </cti:checkRolesAndProperties>
                                <cti:checkRolesAndProperties value="CONTROL_AREA_PEAK_PROJECTION">
                                    <td><c:forEach var="trigger"
                                        items="${controlArea.triggers}">
                                        <c:if test="${trigger.thresholdType}">
                                            <cti:dataUpdaterValue type="DR_CA_TRIGGER"
                                                identifier="${controlAreaId}/${trigger.triggerNumber}/PEAK_PROJECTION" />
                                        </c:if>
                                        <br />
                                    </c:forEach></td>
                                </cti:checkRolesAndProperties>
                                <cti:checkRolesAndProperties value="CONTROL_AREA_ATKU">
                                    <td><c:forEach var="trigger"
                                        items="${controlArea.triggers}">
                                        <c:if test="${trigger.thresholdType}">
                                            <cti:dataUpdaterValue type="DR_CA_TRIGGER"
                                                identifier="${controlAreaId}/${trigger.triggerNumber}/ATKU" />
                                        </c:if>
                                        <br />
                                    </c:forEach></td>
                                </cti:checkRolesAndProperties>
                                <cti:checkRolesAndProperties value="CONTROL_AREA_PRIORITY">
                                    <td><cti:dataUpdaterValue type="DR_CONTROLAREA"
                                        identifier="${controlAreaId}/PRIORITY" /></td>
                                </cti:checkRolesAndProperties>
                                <cti:checkRolesAndProperties value="CONTROL_AREA_TIME_WINDOW">
                                    <td><cti:dataUpdaterValue type="DR_CONTROLAREA"
                                        identifier="${controlAreaId}/START" /> <cti:msg
                                        key="yukon.web.modules.dr.controlAreaDetail.info.separator" />
                                    <cti:dataUpdaterValue type="DR_CONTROLAREA"
                                        identifier="${controlAreaId}/STOP" /></td>
                                </cti:checkRolesAndProperties>
                                <cti:checkRolesAndProperties value="ENABLE_ESTIMATED_LOAD">
                                    <td data-pao="${controlAreaId}">
                                        <cti:icon icon="icon-error" classes="dn"/>
                                        <cti:icon icon="icon-spinner"/>
                                        <span class="f-kw-savings">
                                            <i:inline key="yukon.web.modules.dr.estimatedLoad.calculating"/>
                                        </span>
                                        <cti:dataUpdaterCallback
                                            function="yukon.EstimatedLoad.displaySummaryValue "
                                            value="ESTIMATED_LOAD/${controlAreaId}/CONTROL_AREA"/>
                                    </td>
                                </cti:checkRolesAndProperties>
                                <td><dr:controlAreaListActions pao="${controlArea}"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </tags:pagedBox>

    <c:if test="${hasFilterErrors}">
        <script type="text/javascript">
            jQuery('#filterPopup').show();
        </script>
    </c:if>
    <dt:pickerIncludes/>
</cti:standardPage>
