<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>

<cti:standardPage module="amr" page="analysis.list">

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <%-- metering --%>
        <cti:msg var="metersPageTitle" key="yukon.web.modules.amr.meteringStart.pageName" />
        <cti:crumbLink url="/spring/meter/start" title="${metersPageTitle}" />
        <%-- ADA List --%>
        <cti:crumbLink><i:inline key="yukon.web.modules.amr.analysis.list.pageName"/></cti:crumbLink>
    </cti:breadCrumbs>
    
    <cti:includeScript link="/JavaScript/yukon/ui/general.js"/>
    
    <script>
    var deleteConfirmAnalysisId;
    
    function confirmDelete(analysisId) {
        deleteConfirmAnalysisId = analysisId;
        $('deleteConfirmationPopup').show();
    }
    
    function deleteAnalysis() {
        $('deleteConfirmationPopup').hide();
        var url = "/spring/bulk/archiveDataAnalysis/list/delete?analysisId=" + deleteConfirmAnalysisId;
        window.location = url;
    }
    
    YEvent.observeSelectorClick('#deleteButton', function(event){
        var analysisId = event.findElement('tr').down('input[type=hidden]');
        deleteConfirmAnalysisId = analysisId.value;
        $('deleteConfirmationPopup').show();
    });
    </script>
    
    <tags:boxContainer2 nameKey="title">
        <table class="compactResultsTable">
            <tr>
                <th><i:inline key="yukon.web.modules.amr.analysis.list.runDate"/></th>
                <th><i:inline key="yukon.web.modules.amr.analysis.attribute"/></th>
                <th><i:inline key="yukon.web.modules.amr.analysis.list.numberOfDevices"/></th>
                <th><i:inline key="yukon.web.modules.amr.analysis.list.range"/></th>
                <th><i:inline key="yukon.web.modules.amr.analysis.interval"/></th>
                <th><i:inline key="yukon.web.modules.amr.analysis.pointQuality"/></th>
                <th><i:inline key="yukon.web.modules.amr.analysis.list.status"/></th>
                <th><i:inline key="yukon.web.modules.amr.analysis.list.actions"/></th>
            </tr>
            
            <c:forEach items="${analysisMap}" var="analysisEntry">
                <c:if test="${analysisEntry.key.status != 'DELETED'}">
                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                        <input type="hidden" value="${analysisEntry.key.analysisId}"/>
                        <td>
                            <cti:formatDate value="${analysisEntry.key.runDate}" type="DATEHM"/>
                        </td>
                        <td>
                            <cti:formatObject value="${analysisEntry.key.attribute.description}"/>
                        </td>
                        <td>
                            ${analysisEntry.value}
                        </td>
                        <td>
                            <cti:formatInterval type="DATEHM" value="${analysisEntry.key.dateTimeRange}"/>
                        </td>
                        <td>
                            <cti:formatPeriod value="${analysisEntry.key.intervalPeriod}" type="DHMS_REDUCED"/>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${analysisEntry.key.excludeBadPointQualities}">
                                    <i:inline key="yukon.web.modules.amr.analysis.list.normalOnly"/>
                                </c:when>
                                <c:otherwise>
                                    <i:inline key="yukon.web.modules.amr.analysis.list.allQualities"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <c:choose>
                            <%--if analyzing, disable view button, enable delete, status links to progress page--%>
                            <c:when test="${analysisEntry.key.status == 'RUNNING'}">
                                <td>
                                    <cti:url var="analysisProgressUrl" value="/spring/bulk/archiveDataAnalysis/home/processing">
                                        <cti:param name="resultsId" value="${analysisEntry.key.statusId}"/>
                                        <cti:param name="analysisId" value="${analysisEntry.key.analysisId}"/>
                                    </cti:url>
                                    <cti:link href="${analysisProgressUrl}" key="${analysisEntry.key.status.formatKey}"/>
                                </td>
                                <td>
                                    <cti:button nameKey="viewButtonAnalyzing" renderMode="image" disabled="true"/>
                                    <cti:button id="deleteButton" nameKey="remove" renderMode="image"/>
                                </td>
                            </c:when>
                            <%-- if complete with some devices successfully analyzed, enable view, enable delete, status doesn't link--%>
                            <%-- if complete with 0 devices successfully analyzed, disable view, enable delete, status doesn't link--%>
                            <c:when test="${analysisEntry.key.status == 'COMPLETE'}">
                                <td>
                                    <i:inline key="${analysisEntry.key.status}"/>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${analysisEntry.value == 0}">
                                            <cti:button nameKey="viewButtonNoDevices" renderMode="image" disabled="true"/>
                                            <cti:button id="deleteButton" nameKey="remove" renderMode="image"/>
                                        </c:when>
                                        <c:otherwise>
                                            <cti:url var="viewUrl" value="/spring/bulk/archiveDataAnalysis/results/view">
                                                <cti:param name="analysisId" value="${analysisEntry.key.analysisId}"/>
                                            </cti:url>
                                            <cti:button nameKey="viewButton" renderMode="image" href="${viewUrl}"/>
                                            <cti:button id="deleteButton" nameKey="remove" renderMode="image"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </c:when>
                            <%--if reading, enable view, enable delete, status links to read progress--%>
                            <c:when test="${analysisEntry.key.status == 'READING'}">
                                <td>
                                    <cti:url var="readProgressUrl" value="/spring/bulk/archiveDataAnalysis/read/readResults">
                                        <cti:param name="resultId" value="${analysisEntry.key.statusId}"/>
                                        <cti:param name="analysisId" value="${analysisEntry.key.analysisId}"/>
                                    </cti:url>
                                    <cti:link href="${readProgressUrl}" key="${analysisEntry.key.status.formatKey}"/>
                                </td>
                                <td>
                                    <cti:url var="viewUrl" value="/spring/bulk/archiveDataAnalysis/results/view">
                                        <cti:param name="analysisId" value="${analysisEntry.key.analysisId}"/>
                                    </cti:url>
                                    <cti:button nameKey="viewButton" renderMode="image" href="${viewUrl}"/>
                                    <cti:button id="deleteButton" nameKey="remove" renderMode="image"/>
                                </td>
                            </c:when>
                        </c:choose>
                    </tr>
                </c:if>
            </c:forEach>
        </table>
    </tags:boxContainer2>
    
    <i:simplePopup id="deleteConfirmationPopup" styleClass="mediumSimplePopup" titleKey="yukon.web.modules.amr.analysis.list.deleteConfirmation.title">
        <h3 class="dialogQuestion"><cti:msg key="yukon.web.modules.amr.analysis.list.deleteConfirmation.message"/></h3>
        <div class="actionArea">
            <cti:button nameKey="ok" onclick="deleteAnalysis()"/>
            <cti:button nameKey="cancel" onclick="$('deleteConfirmationPopup').hide();" />
        </div>
    </i:simplePopup>
    
</cti:standardPage>