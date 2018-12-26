<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="operator" page="infrastructureWarnings" smartNotificationsEvent="INFRASTRUCTURE_WARNING">

<cti:msgScope paths="widgets.infrastructureWarnings">

    <div class="column-14-10">
        <div class="column one">
            <cti:url var="action" value="/stars/infrastructureWarnings/detail" />
            <form action="${action}" method="GET">
            <tags:boxContainer2 nameKey="filters">
                <span class="fr cp"><cti:icon icon="icon-help" data-popup="#results-help"/></span>
                <cti:msg2 var="helpTitle" key=".detail.helpTitle"/>
                <div id="results-help" class="dn" data-width="600" data-height="400" data-title="${helpTitle}"><cti:msg2 key=".detail.helpText"/></div>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".deviceTypes">
                        <div class="button-group stacked">
                            <c:forEach var="type" items="${deviceTypes}">
                                <c:set var="selected" value="${false}"/>
                                <c:if test="${fn:contains(selectedTypes, type)}">
                                    <c:set var="selected" value="${true}"/>
                                </c:if>
                                <cti:msg2 var="deviceType" key=".category.${type}"/>
                                <tags:check name="types" classes="M0" value="${type}" label="${deviceType}" checked="${selected}"></tags:check>
                            </c:forEach>
                        </div>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            
                <div class="fr">
                    <cti:button nameKey="filter" classes="primary action" type="submit"/>
                </div>
            
            </tags:boxContainer2>
            </form>
        </div>
    </div>

    <cti:url var="dataUrl" value="/stars/infrastructureWarnings/detail">
        <c:forEach var="type" items="${selectedTypes}">
            <cti:param name="types" value="${type}"/>
        </c:forEach>
    </cti:url>
    <div data-url="${dataUrl}" data-static>
    <table class="compact-results-table row-highlighting">
        <th class="row-icon" /> 
        <tags:sort column="${name}" />                
        <tags:sort column="${type}" />                
        <tags:sort column="${status}" />
        <tags:sort column="${timestamp}" />

        <c:choose>
            <c:when test="${warnings.hitCount > 0}">
                <c:forEach var="warning" items="${warnings.resultList}">
                    <tr>
                        <td>
                            <c:if test="${notesList.contains(warning.paoIdentifier.paoId)}">
                                <cti:msg2 var="viewAllNotesTitle" key="yukon.web.common.paoNotesSearch.viewAllNotes"/>
                                <cti:icon icon="icon-notes-pin" classes="js-view-all-notes cp" title="${viewAllNotesTitle}" data-pao-id="${warning.paoIdentifier.paoId}"/>
                            </c:if>
                        </td>
                        <td class="wsnw">
                            <cti:paoDetailUrl yukonPao="${warning.paoIdentifier}" newTab="true">
                                <cti:deviceName deviceId="${warning.paoIdentifier.paoId}"/>
                            </cti:paoDetailUrl>
                        </td>
                        <td class="wsnw">${warning.paoIdentifier.paoType.paoTypeName}</td>
                        <td>
                            <c:set var="warningColor" value="warning"/>
                            <c:if test="${warning.severity == 'HIGH'}">
                                <c:set var="warningColor" value="error"/>
                            </c:if>
                            <span class="${warningColor}"><cti:msg2 key="${warning.warningType.formatKey}.${warning.severity}" arguments="${warning.arguments}"/></td>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${warning.timestamp lt epoch1990}">
                                    <i:inline key="yukon.common.dashes"/>
                                </c:when>
                                <c:otherwise>
                                    <cti:formatDate value="${warning.timestamp}" type="FULL"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <tr>
                    <td colspan="3">
                        <span class="empty-list"><i:inline key=".noWarnings" /></span>
                    </td>
                </tr>
            </c:otherwise>
        </c:choose>
    </table>
    <tags:pagingResultsControls result="${warnings}" adjustPageCount="true"/>
    <div class="dn" id="js-pao-notes-popup"></div>
    <cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
    
    </cti:msgScope>
    
</cti:standardPage>