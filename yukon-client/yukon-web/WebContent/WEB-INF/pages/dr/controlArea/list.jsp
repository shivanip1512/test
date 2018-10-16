<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="controlAreaList">
    
    <tags:simpleDialog id="drDialog"/>
    
    <cti:msg2 var="controlAreaTitle" key=".controlAreas"/>
    <tags:sectionContainer title="${controlAreaTitle}">
    
        <div class="filter-section">
            <cti:url var="baseUrlWithContextPath" value="/dr/controlArea/list"/>
            <form:form action="${baseUrlWithContextPath}" modelAttribute="filter" method="get">
                <i:inline key="yukon.common.filterBy"/>&nbsp;
                <cti:msg2 var="namePlaceholder" key=".filter.name"/>
                <tags:input path="name" size="20" placeholder="${namePlaceholder}"/>
                <form:select path="state">
                    <form:option value="all"><cti:msg2 key=".filter.state.all"/></form:option>
                    <form:option value="active"><cti:msg2 key=".filter.state.active"/></form:option>
                    <form:option value="inactive"><cti:msg2 key=".filter.state.inactive"/></form:option>
                </form:select>
                <cti:checkRolesAndProperties value="DR_VIEW_PRIORITY">
                    <i:inline key=".filter.priority"/>
                    <cti:msg2 var="minPlaceholder" key=".filter.min"/>
                    <tags:input path="priority.min" size="5" placeholder="${minPlaceholder}" displayValidationToRight="true"/>
                    <cti:msg2 var="maxPlaceholder" key=".filter.max"/>
                    <tags:input path="priority.max" size="5" placeholder="${maxPlaceholder}" displayValidationToRight="true"/>
                </cti:checkRolesAndProperties>
                <cti:button nameKey="filter" type="submit" classes="action primary fn vab"/>
            </form:form>
            <hr/>
        </div>
        
        <c:choose>
            <c:when test="${areas.hitCount == 0}">
                <span class="empty-list"><i:inline key=".noResults"/></span>
            </c:when>
            <c:otherwise>
                <cti:url var="url" value="list">
                    <cti:param name="name" value="${filter.name}"></cti:param>
                    <cti:param name="state" value="${filter.state}"></cti:param>
                    <cti:param name="priority.min" value="${filter.priority.min}"></cti:param>
                    <cti:param name="priority.max" value="${filter.priority.max}"></cti:param>
                </cti:url>
                <div data-url="${url}" data-static>
                    <table class="compact-results-table with-form-controls has-actions">
                        <thead>
                            <tr>
                                <tags:sort column="${CA_NAME}"/>
                                <tags:sort column="${CA_STATE}"/>
                                <cti:checkRolesAndProperties value="DR_VIEW_CONTROL_AREA_TRIGGER_INFO">
                                    <tags:sort column="${TR_VALUE_THRESHOLD}"/>
                                    <tags:sort column="${TR_PEAK_PROJECTION}"/>
                                    <tags:sort column="${TR_ATKU}"/>
                                </cti:checkRolesAndProperties>
                                <cti:checkRolesAndProperties value="DR_VIEW_PRIORITY">
                                    <tags:sort column="${CA_PRIORITY}"/>
                                </cti:checkRolesAndProperties>
                                <tags:sort column="${CA_START}"/>
                                <cti:checkRolesAndProperties value="ENABLE_ESTIMATED_LOAD">
                                    <th><i:inline key=".heading.kwSavings"/></th>
                                </cti:checkRolesAndProperties>
                                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                            </tr>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                            <c:forEach var="controlArea" items="${areas.resultList}">
                                <c:set var="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
                                <c:url var="controlAreaUrl" value="/dr/controlArea/detail">
                                    <c:param name="controlAreaId" value="${controlAreaId}"/>
                                </c:url>
                                <tr>
                                    <td><a href="${controlAreaUrl}">${fn:escapeXml(controlArea.name)}</a></td>
                                    <td><dr:controlAreaState controlAreaId="${controlAreaId}"/></td>
                                    <cti:checkRolesAndProperties value="DR_VIEW_CONTROL_AREA_TRIGGER_INFO">
                                        <td>
                                            <c:if test="${empty controlArea.triggers}">
                                                <span><cti:msg2 key="modules.dr.controlAreaDetail.info.noTriggers"/></span>
                                             </c:if>
                                            <c:forEach var="trigger" items="${controlArea.triggers}">
                                                <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${trigger.triggerNumber}/VALUE_THRESHOLD"/>
                                                <br>
                                            </c:forEach>
                                        </td>
                                        <td>
                                            <c:forEach var="trigger" items="${controlArea.triggers}">
                                                <c:if test="${trigger.thresholdType}">
                                                    <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${trigger.triggerNumber}/PEAK_PROJECTION"/>
                                                </c:if>
                                                <br>
                                            </c:forEach>
                                        </td>
                                        <td>
                                            <c:forEach var="trigger" items="${controlArea.triggers}">
                                                <c:if test="${trigger.thresholdType}">
                                                    <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${trigger.triggerNumber}/ATKU"/>
                                                </c:if>
                                                <br>
                                            </c:forEach>
                                        </td>
                                    </cti:checkRolesAndProperties>
                                    <cti:checkRolesAndProperties value="DR_VIEW_PRIORITY">
                                        <td>
                                            <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/PRIORITY"/>
                                        </td>
                                    </cti:checkRolesAndProperties>
                                    
                                    <td>
                                        <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/START"/>
                                        <span>&nbsp;<cti:msg2 key="modules.dr.controlAreaDetail.info.separator"/></span>
                                        <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/STOP"/>
                                    </td>
                                    
                                    <cti:checkRolesAndProperties value="ENABLE_ESTIMATED_LOAD">
                                        <td data-pao="${controlAreaId}" class="wsnw">
                                            <cti:dataUpdaterCallback
                                                function="yukon.dr.estimatedLoad.displayValue"
                                                value="ESTIMATED_LOAD/${controlAreaId}/CONTROL_AREA"/>
                                            <cti:icon icon="icon-loading-bars" 
                                                classes="js-est-load-calculating push-down-4"/>
                                            <cti:button classes="js-est-load-error-btn dn fn vat M0" 
                                                renderMode="buttonImage" icon="icon-error" 
                                                data-popup="[data-control-area-id=${controlAreaId}]"/>
                                            <span class="js-kw-savings"></span>
                                            <cti:url var="url" value="/dr/estimatedLoad/summary-error">
                                                <cti:param name="paoId" value="${controlAreaId}"/>
                                            </cti:url>
                                            <div data-url="${url}" 
                                                data-control-area-id="${controlAreaId}" data-height="235" data-width="575" 
                                                class="dn"/>
                                        </td>
                                    </cti:checkRolesAndProperties>
                                    <td><dr:controlAreaListActions pao="${controlArea}"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <tags:pagingResultsControls result="${areas}" adjustPageCount="true"/>
                </div>
            </c:otherwise>
        </c:choose>
    </tags:sectionContainer>
    
    <dt:pickerIncludes/>
    
    <cti:includeScript link="/resources/js/pages/yukon.dr.estimated.load.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.dr.dataUpdater.showAction.js"/>
    
</cti:standardPage>