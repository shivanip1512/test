<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="widgets.infrastructureWarnings">

    <table class="compact-results-table row-highlighting">
        <th class="row-icon" /> 
        <tags:sort column="${name}" />                
        <tags:sort column="${type}" />                
        <tags:sort column="${status}" />
        <tags:sort column="${timestamp}" />

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
                    <span class="${warningColor}"><i:inline key="${warning.warningType.formatKey}.${warning.severity}" arguments="${warning.arguments}"/>
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
    </table>
    <tags:pagingResultsControls result="${warnings}" adjustPageCount="true"/>

</cti:msgScope>
