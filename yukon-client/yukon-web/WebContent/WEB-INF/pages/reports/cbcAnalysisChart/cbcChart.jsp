<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:forEach var="targetId" items="${targetIds}">

	<%-- TARGET NAME --%>
	<div style="margin-left:0px;">
		<h3>${targetNames[targetId]}</h3>
		
		<%-- REPORT LINKS --%>
		<i:inline key="yukon.web.modules.capcontrol.tabularData"/> 
		<cti:simpleReportLinkFromNameTag 
			parameterAttributes="${targetReportInfo[targetId]}"
			definitionName="${targetReportInfo[targetId].definitionName}"
			viewType="extView"><i:inline key="yukon.common.html"/>
        </cti:simpleReportLinkFromNameTag>
		|
		<cti:simpleReportLinkFromNameTag 
			parameterAttributes="${targetReportInfo[targetId]}"
			definitionName="${targetReportInfo[targetId].definitionName}"
			viewType="csvView"><i:inline key="yukon.common.csv"/>
        </cti:simpleReportLinkFromNameTag>
		|
		<cti:simpleReportLinkFromNameTag 
			parameterAttributes="${targetReportInfo[targetId]}"
			definitionName="${targetReportInfo[targetId].definitionName}"
			viewType="pdfView"><i:inline key="yukon.common.pdf"/>
        </cti:simpleReportLinkFromNameTag>
										
	</div>
	
    <%-- LOOP PER GRAPH --%>
	<c:forEach var="graph" items="${targetGraphs[targetId]}">
		<div style="margin-left:20px;height: 250px">
			<flot:trend title="${graph.pointName}" 
						pointIds="${graph.pointIds}" 
						startDate="${graph.startDateMillis}" 
						endDate="${graph.endDateMillis}" 
						interval="${graph.interval}" 
						converterType="${graph.converterType}" 
						graphType="${graph.graphType}"/>
		</div>
	</c:forEach>

</c:forEach>
