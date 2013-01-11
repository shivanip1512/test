<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>

<cti:standardPage title="${title}" module="capcontrol">

	<cti:includeScript link="/JavaScript/swfobject.js"/>
	
	<%-- NO DATA --%>
	<c:if test="${empty targetIds}">
		<div style="text-align:center;">
			<h3>No target selected.</h3>
			<h4>Select one or more Substation Buses or Feeders.</h4>
		</div>
	</c:if>

	<c:forEach var="targetId" items="${targetIds}">
	
		<%-- TARGET NAME --%>
		<div style="margin-left:0px;">
			<h3>${targetNames[targetId]}</h3>
			
			<%-- REPORT LINKS --%>
			Tabular Data: 
			<cti:simpleReportLinkFromNameTag 
				parameterAttributes="${targetReportInfo[targetId]}"
				definitionName="${targetReportInfo[targetId].definitionName}"
				viewType="extView">HTML</cti:simpleReportLinkFromNameTag>
			|
			<cti:simpleReportLinkFromNameTag 
				parameterAttributes="${targetReportInfo[targetId]}"
				definitionName="${targetReportInfo[targetId].definitionName}"
				viewType="csvView">CSV</cti:simpleReportLinkFromNameTag>
			|
			<cti:simpleReportLinkFromNameTag 
				parameterAttributes="${targetReportInfo[targetId]}"
				definitionName="${targetReportInfo[targetId].definitionName}"
				viewType="pdfView">PDF</cti:simpleReportLinkFromNameTag>
											
		</div>
		
		<c:choose>
			
			<%-- NO GRAPHS --%>
			<c:when test="${empty targetGraphs[targetId]}">
				No Points To Chart
			</c:when>
			
			<%-- LOOP PER GRAPH --%>
			<c:otherwise>
		
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
				
			</c:otherwise>
			
		</c:choose>
	
	</c:forEach>

</cti:standardPage>
