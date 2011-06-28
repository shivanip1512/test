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
        $('deleteConfirmationPopup').show()
    }
    
    function deleteAnalysis() {
        $('deleteConfirmationPopup').hide()
        Yukon.ui.blockPage();
        var url = "delete?analysisId=" + deleteConfirmAnalysisId;
        window.location = url;
    }
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
                <th><i:inline key="yukon.web.modules.amr.analysis.list.actions"/></th>
            </tr>
            
            <c:forEach items="${analysisMap}" var="analysisEntry">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
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
                        <cti:formatPeriod value="${analysisEntry.key.intervalLength}" type="DHMS_REDUCED"/>
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
                    <td>
                        <%-- TODO: Only display action buttons if analysis is complete? --%>
                        <c:choose>
                            <c:when test="${analysisEntry.value == 0}">
                                <cti:button key="viewButtonNoDevices" renderMode="image" disabled="true"/>
                            </c:when>
                            <c:otherwise>
                                <cti:button key="viewButton" renderMode="image" href="results?analysisId=${analysisEntry.key.analysisId}"/>
                            </c:otherwise>
                        </c:choose>
                        <cti:button id="deleteButton" key="deleteButton" renderMode="image" onclick="confirmDelete(${analysisEntry.key.analysisId})"/>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </tags:boxContainer2>
    
    <i:simplePopup id="deleteConfirmationPopup" styleClass="mediumSimplePopup" titleKey="yukon.web.modules.amr.analysis.list.deleteConfirmation.title">
        <h3 class="dialogQuestion"><cti:msg key="yukon.web.modules.amr.analysis.list.deleteConfirmation.message"/></h3>
        <div class="actionArea">
            <cti:button key="ok" onclick="deleteAnalysis()"/>
            <cti:button key="cancel" onclick="$('deleteConfirmationPopup').hide();" />
        </div>
    </i:simplePopup>
    
</cti:standardPage>