<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<table class="compact-results-table row-highlighting has-actions">
    <thead>
        <tr>
            <tags:sort column="${POINTNAME}" />
            <tags:sort column="${ATTRIBUTE}" />
            <th></th>
            <th><i:inline key="yukon.common.value"/></th>
            <th><i:inline key="yukon.common.dateTime"/></th>
            <tags:sort column="${POINTTYPE}" />
            <tags:sort column="${POINTOFFSET}" />
            <cti:checkRolesAndProperties value="MANAGE_POINT_DATA" level="UPDATE">
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0" /></th>
            </cti:checkRolesAndProperties>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="point" items="${points.resultList}">
            <tr>
                <td>
                    <c:set var="isPointLinkVisible" value= "false" />
                    <cti:checkRolesAndProperties value="MANAGE_POINTS" level="VIEW">
                        <c:set var="isPointLinkVisible" value= "true" />
                    </cti:checkRolesAndProperties>
                    <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                        <c:set var="isPointLinkVisible" value= "true" />
                    </cti:checkRolesAndProperties>
                    <c:choose>
                        <c:when test="${!isPointLinkVisible}">${fn:escapeXml(point.pointName)}</c:when>
                        <c:otherwise>
                            <cti:url var="pointUrl" value="/tools/points/${point.pointId}" />
                            <a href="${pointUrl}">${fn:escapeXml(point.pointName)}</a>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${empty point.attribute}">
                            <i:inline key="yukon.common.na"/>
                        </c:when>
                        <c:otherwise>
                            <i:inline key="${point.attribute}" htmlEscape="true"/>
                            <c:if test="${point.displayPopup}">
                                <cti:msg2 var="moreAttributes" key="yukon.common.plusMore" argument="${point.allAttributes.size() - 1}"/>
                                <a href="javascript:void(0);" data-popup="#attributes-popup-${point.pointId}">
                                    ${moreAttributes}
                                </a>
                                <div id="attributes-popup-${point.pointId}" class="dn" data-width="300"
                                    data-title="<cti:msg2 key="yukon.common.attributes"/>">
                                    <%@ include file="/WEB-INF/pages/common/pao/attributesTable.jsp" %>
                                </div>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td class="state-indicator">
                    <cti:pointStatus pointId="${point.pointId}" statusPointOnly="${true}" />
                </td>
                <td class="wsnw">
                    <cti:pointValue pointId="${point.pointId}" format="SHORT" />
                </td>
                <td class="wsnw">
                    <tags:historicalValue pao="${device}" pointId="${point.pointId}" />
                </td>
                <td>
                    <i:inline key="${point.paoPointIdentifier.pointIdentifier.pointType}"/>
                </td>
                <td>
                    ${point.paoPointIdentifier.pointIdentifier.offset}
                </td>
                <cti:checkRolesAndProperties value="MANAGE_POINT_DATA" level="UPDATE">
                    <td>
                        <cm:dropdown icon="icon-cog">
                            <cti:msg2 key="yukon.common.point.manualEntry.title" var="title"/>
                            <cti:list var="arguments">
                                <cti:item value="${title}"/>
                                <cti:item value="${device.paoName}"/>
                                <cti:item value="${point.pointName}"/>
                            </cti:list>
                            <cti:msg2 key="yukon.common.point.popupTitle" arguments="${arguments}" var="popupTitle"/>
                            <cm:dropdownOption key="yukon.common.point.manualEntry.title" icon="icon-pencil"
                                               data-point-id="${point.pointId}" data-popup-title="${popupTitle}" 
                                               classes="js-manual-entry" id="manualEntry-${point.pointId}"/>
                            <tags:dynamicChoose updaterString="TDC/MAN_CONTROL/${point.pointId}" suffix="${point.pointId}">
                                <cti:msg2 key="yukon.web.modules.tools.tdc.manualControl.title" var="title"/>
                                <cti:list var="arguments">
                                    <cti:item value="${title}"/>
                                    <cti:item value="${device.paoName}"/>
                                    <cti:item value="${point.pointName}"/>
                                </cti:list>
                                <cti:msg2 key="yukon.web.modules.tools.tdc.popupTitle" arguments="${arguments}" var="popupTitle"/>
                                <tags:dynamicChooseOption optionId="TRUE">
                                    <cm:dropdownOption key="yukon.web.modules.tools.tdc.manualControl.title" icon="icon-wrench" 
                                        data-point-id="${point.pointId}" data-popup-title="${popupTitle}" 
                                        data-device-id="${device.liteID}" classes="js-manual-control" 
                                        id="manualControl-${point.pointId}"/>
                                </tags:dynamicChooseOption>
                            </tags:dynamicChoose>
                        </cm:dropdown>
                    </td>
                </cti:checkRolesAndProperties>
            </tr>
        </c:forEach>
    </tbody>
</table>
<c:if test="${empty points.resultList}">
    <span class="empty-list compact-results-table"><i:inline key="yukon.common.search.noResultsFound" /></span>
</c:if>
<tags:pagingResultsControls result="${points}" adjustPageCount="true" thousands="true"/>
