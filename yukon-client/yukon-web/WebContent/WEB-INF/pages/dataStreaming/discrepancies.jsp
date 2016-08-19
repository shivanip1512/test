<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="tools" page="dataStreaming.discrepancies">

<p class="notes"><i:inline key=".instructions"/></p>

    <cti:url var="dataUrl" value="/tools/dataStreaming/discrepancies"/>
    <div data-url="${dataUrl}" data-static>
        <table class="compact-results-table has-actions row-highlighting">
            <thead>
                <tr>
                    <tags:sort column="${device}"/>
                    <tags:sort column="${expectedAttributes}"/>
                    <tags:sort column="${actualAttributes}"/>
                    <tags:sort column="${expectedInterval}"/>
                    <tags:sort column="${actualInterval}"/>
                    <tags:sort column="${status}"/>
                    <tags:sort column="${lastCommunicated}"/>
                </tr>
            </thead>
            <c:forEach var="discrepancy" items="${discrepancies.resultList}">
                <cti:url var="detailUrl" value="/meter/home">
                    <cti:param name="deviceId" value="${discrepancy.deviceId}"/>
                </cti:url>
                <tr>
                    <td><a href="${detailUrl}">${fn:escapeXml(discrepancy.paoName)}</a></td>
                    <td class="wrbw">${discrepancy.expected.commaDelimitedAttributesOnOff}</td>
                    <td class="wrbw">${discrepancy.actual.commaDelimitedAttributesOnOff}</td>
                    <td class="wsnw">${discrepancy.expected.selectedInterval}
                        <c:choose>
                            <c:when test="${discrepancy.expected.selectedInterval == 1}">
                                <i:inline key=".minute"/>
                            </c:when>
                            <c:when test="${discrepancy.expected.selectedInterval > 1}">
                                <i:inline key=".minutes"/>
                            </c:when>
                            <c:otherwise>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="wsnw"><c:if test="${discrepancy.actual.selectedInterval > 0}">${discrepancy.actual.selectedInterval}</c:if>
                        <c:choose>
                            <c:when
                                test="${discrepancy.actual.selectedInterval == 1}">
                                <i:inline key=".minute" />
                            </c:when>
                            <c:when
                                test="${discrepancy.actual.selectedInterval > 1}">
                                <i:inline key=".minutes" />
                            </c:when>
                            <c:otherwise>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="wsnw"><i:inline key=".${discrepancy.status}"/></td>
                    <td class="wsnw dif"><cti:formatDate value="${discrepancy.lastCommunicated}" type="DATEHM_12" />&nbsp;&nbsp;
                        <cm:dropdown icon="icon-cog" triggerClasses="fr">
                            <cti:url var="resendUrl" value="/tools/dataStreaming/discrepancies/${discrepancy.deviceId}/resend"/>
                            <cm:dropdownOption key=".resend" href="${resendUrl}" icon="icon-control-repeat-blue"/>
                            <cti:url var="acceptUrl" value="/tools/dataStreaming/discrepancies/${discrepancy.deviceId}/accept"/>
                            <cm:dropdownOption key=".accept" href="${acceptUrl}" icon="icon-accept"/>
                            <cti:url var="removeUrl" value="/tools/dataStreaming/discrepancies/${discrepancy.deviceId}/remove"/>
                            <cm:dropdownOption key=".remove" href="${removeUrl}" icon="icon-cross"/>
                        </cm:dropdown>
                   </td>
                </tr>
            </c:forEach>
        </table>
        <tags:pagingResultsControls result="${discrepancies}" adjustPageCount="true" hundreds="true"/>
    </div>
    
    <div class="page-action-area">

        <cti:url var="resendAllUrl" value="/tools/dataStreaming/discrepancies/resendAll"/>
        <form:form id="resendAll" action="${resendAllUrl}" method="POST">
            <cti:csrfToken/>
            <input type="hidden" id="deviceIds" name="deviceIds" value="${deviceIds}"/>
            <cti:button type="submit" nameKey="resendAll" />
        </form:form>
        
        <cti:url var="acceptAllUrl" value="/tools/dataStreaming/discrepancies/acceptAll"/>
        <form:form id="acceptAll" action="${acceptAllUrl}" method="POST">
            <cti:csrfToken/>
            <input type="hidden" id="deviceIds" name="deviceIds" value="${deviceIds}"/>
            <cti:button type="submit" nameKey="acceptAll" />
        </form:form>
      
    </div>

</cti:standardPage>