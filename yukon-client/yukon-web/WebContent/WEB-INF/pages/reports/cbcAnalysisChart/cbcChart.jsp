<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="highChart" tagdir="/WEB-INF/tags/highChart" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<div style="position: absolute;">
    <c:forEach var="targetId" items="${targetIds}">
        <%-- TARGET NAME --%>
        <div style="margin-left:0px;">
            <h3>${targetNames[targetId]}</h3>
            <%-- REPORT LINKS --%>
            <i:inline key="yukon.web.modules.capcontrol.tabularData"/> 
            <cti:simpleReportLinkFromNameTag parameterAttributes="${targetReportInfo[targetId]}"
                                             definitionName="${targetReportInfo[targetId].definitionName}"
                                             viewType="extView">
                <i:inline key="yukon.common.html"/>
            </cti:simpleReportLinkFromNameTag>
            |
            <cti:simpleReportLinkFromNameTag parameterAttributes="${targetReportInfo[targetId]}"
                                             definitionName="${targetReportInfo[targetId].definitionName}"
                                             viewType="csvView">
                <i:inline key="yukon.common.csv"/>
            </cti:simpleReportLinkFromNameTag>
            |
            <cti:simpleReportLinkFromNameTag parameterAttributes="${targetReportInfo[targetId]}"
                                             definitionName="${targetReportInfo[targetId].definitionName}"
                                             viewType="pdfView"><i:inline key="yukon.common.pdf"/>
            </cti:simpleReportLinkFromNameTag>
        </div>

        <%-- LOOP PER GRAPH --%>
        <c:forEach var="graph" items="${targetGraphs[targetId]}">
            <div style="margin-left:20px;height: 250px;" class="js-trend-analysis">
                <highChart:trend converterType="${graph.converterType}"
                                 interval="${graph.interval}"
                                 startDate="${graph.startDateMillis}"
                                 endDate="${graph.endDateMillis}"
                                 chartHeight="250"
                                chartWidth="500"
                                pointIds="${graph.pointIds}"
                                title="${graph.pointName}"/>
            </div>
        </c:forEach>
    </c:forEach>
</div>