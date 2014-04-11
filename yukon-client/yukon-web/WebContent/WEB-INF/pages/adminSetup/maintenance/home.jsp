<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>

<cti:standardPage module="adminSetup" page="maintenance">

<c:url value="/adminSetup/maintenance/toggleJobEnabledAjax" var="toggleJobAjaxUrl"/>

<script>
$(function(){
	$("a.icon-disabled").each(function(idx, val){
		$(val).attr('title', $(val).attr('data-truejobNameMsg'));
	});
	
	$("a.icon-enabled").each(function(idx, val){
		$(val).attr('title', $(val).attr('data-falsejobNameMsg'));
	});
	
	$("a.f-toggleJobEnabled").click(function(e){
		$.ajax({
			url: '${toggleJobAjaxUrl}?jobId=' + $(e.currentTarget).attr('data-jobid')
		}).done(function (data, textStatus, jqXHR) {
			var icon = 'icon-disabled';
			if (data == true) icon = 'icon-enabled';
			$(e.currentTarget).removeClass("icon-enabled icon-disabled").addClass(icon).attr('title', $(e.currentTarget).attr('data-'+ !data +'jobNameMsg'));
		});
	});
});
</script>
							
	<cti:dataGrid cols="2">
		<cti:dataGridCell>
		    <tags:boxContainer2 nameKey="maintenanceJobs" hideEnabled="false">
		    	<table class="compact-results-table">
			
					<tr>
						<th>&nbsp;</th>
						<th><i:inline key=".tableHeader.scheduleName"/></th>
						<th><i:inline key=".tableHeader.scheduleDescription"/></th>
						<th><i:inline key=".tableHeader.enabled"/></th>
					</tr>
					<c:forEach var="job" items="${jobs}">
						<cti:msg2 var="jobNameMsg" key=".${job.beanName}.title"/>
						<tr>
							<cti:url var="editScheduleDetailsUrl" value="/adminSetup/maintenance/edit" >
								<cti:param name="jobId" value="${job.id}"/>
							</cti:url>
							<td>
			                	<a href="${editScheduleDetailsUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${jobNameMsg}"/>"><i class="icon icon-script"></i></a>
							</td>
							<td>
								<a href="${editScheduleDetailsUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${jobNameMsg}"/>">
									<i:inline key=".${job.beanName}.title"/>
								</a>
							</td>
							<td>
								<cti:dataUpdaterValue type="JOB" identifier="${job.id}/SCHEDULE_DESCRIPTION"/>
							</td>
							<cti:msg2 key="yukon.web.modules.adminSetup.maintenance.enable.circle.hoverText" argument="${jobNameMsg}" var="enableMsg" />
							<cti:msg2 key="yukon.web.modules.adminSetup.maintenance.disable.circle.hoverText" argument="${jobNameMsg}" var="disableMsg"/>
							<td class="fr">
                                <c:if test="${job.disabled}"><c:set var="iconClass" value="icon-disabled"/></c:if>
                                <c:if test="${not job.disabled}"><c:set var="iconClass" value="icon-enabled"/></c:if>
								<a href="javascript:void(0);" class="icon ${iconClass} f-toggleJobEnabled" data-truejobNameMsg="${enableMsg}" data-falsejobNameMsg="${disableMsg}" data-jobid="${job.id}">Toggle Job</a>
							</td>
		
						</tr>
					</c:forEach>
					
				</table>
		    </tags:boxContainer2>
		</cti:dataGridCell>
	</cti:dataGrid>
  
</cti:standardPage>