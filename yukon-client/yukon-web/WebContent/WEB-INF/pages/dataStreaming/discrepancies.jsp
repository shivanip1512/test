<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
                    <td>${discrepancy.expected.commaDelimitedAttributesOnOff}</td>
                    <td>${discrepancy.actual.commaDelimitedAttributesOnOff}</td>
                    <td class="wsnw">${discrepancy.expected.selectedInterval}
                        <c:choose>
                            <c:when test="${discrepancy.expected.selectedInterval > 1}">
                                <i:inline key=".minutes"/>
                            </c:when>
                            <c:otherwise>
                                <i:inline key=".minute"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="wsnw">${discrepancy.actual.selectedInterval}
                        <c:choose>
                            <c:when test="${discrepancy.actual.selectedInterval > 1}">
                                <i:inline key=".minutes"/>
                            </c:when>
                            <c:otherwise>
                                <i:inline key=".minute"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="wsnw">${discrepancy.status}</td>
                    <td class="wsnw"><cti:formatDate value="${discrepancy.lastCommunicated}" type="DATEHM_12" /></td>
                        <cm:dropdown icon="icon-cog" triggerClasses="fr">
    <%--                         <cti:url var="summaryUrl" value="/tools/dataStreaming/summary?selectedConfiguration=${config.id}"/>
                            <cm:dropdownOption key=".summary.pageName" href="${summaryUrl}" icon="icon-application-view-columns"/>
                            <cti:url var="configureUrl" value="/bulk/dataStreaming/configure">
                                <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
                                    <cti:param name="${cp.key}" value="${cp.value}"/>
                                </c:forEach>
                            </cti:url>
                            <cm:dropdownOption key=".configure" href="${configureUrl}" icon="icon-cog-edit"/>
                            <cti:url var="removeUrl" value="/bulk/dataStreaming/remove">
                                <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
                                    <cti:param name="${cp.key}" value="${cp.value}"/>
                                </c:forEach>
                            </cti:url>                            
                            <cm:dropdownOption key=".remove" href="${removeUrl}" icon="icon-cross"/>
                            <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                                <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
                                    <cti:param name="${cp.key}" value="${cp.value}"/>
                                </c:forEach>
                            </cti:url>
                            <cm:dropdownOption key=".collectionActions" href="${collectionActionsUrl}" icon="icon-cog-go"/> --%>
                        </cm:dropdown>
                   </td>
                </tr>
            </c:forEach>
        </table>
        <tags:pagingResultsControls result="${discrepancies}" adjustPageCount="true" hundreds="true"/>
    </div>
    
    <div class="page-action-area">

        <cti:url var="resendAllUrl" value="/tools/dataStreaming/discrepancies/resendAll"/>
        <cti:button nameKey="resendAll" href="${resendAllUrl}"/>
        
        <cti:url var="acceptAllUrl" value="/tools/dataStreaming/discrepancies/acceptAll"/>
        <cti:button nameKey="acceptAll" href="${acceptAllUrl}"/>
      
    </div>

</cti:standardPage>