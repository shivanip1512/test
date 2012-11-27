<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>

<cti:standardPage module="adminSetup" page="maintenance">

<c:url value="/adminSetup/maintenance/toggleJobEnabledAjax" var="toggleJobAjaxUrl"/>

<script>
jQuery(function(){
	jQuery("a.enabled_false").each(function(idx, val){
		jQuery(val).attr('title', jQuery(val).attr('data-truejobNameMsg'));
	});
	
	jQuery("a.enabled_true").each(function(idx, val){
		jQuery(val).attr('title', jQuery(val).attr('data-falsejobNameMsg'));
	});
	
	jQuery("a.f_toggleJobEnabled").click(function(e){
		jQuery.ajax({
			url: '${toggleJobAjaxUrl}?jobId=' + jQuery(e.currentTarget).attr('data-jobid'),
			success: function(data){
				jQuery(e.currentTarget).removeClass("enabled_false, enabled_true").addClass("enabled_"+data).attr('title', jQuery(e.currentTarget).attr('data-'+ !data +'jobNameMsg'));
			}
		});
	});
});
</script>
							
	<cti:dataGrid cols="2">
		<cti:dataGridCell>
		    <tags:boxContainer2 nameKey="maintenanceJobs" hideEnabled="false">
		    	<table class="compactResultsTable">
			
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
			                	<a href="${editScheduleDetailsUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${jobNameMsg}"/>" class="icon icon_script"></a>
							</td>
							<td class="nonwrapping">
								<a href="${editScheduleDetailsUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${jobNameMsg}"/>">
									<i:inline key=".${job.beanName}.title"/>
								</a>
							</td>
							<td class="nonwrapping">
								<cti:dataUpdaterValue type="JOB" identifier="${job.id}/SCHEDULE_DESCRIPTION"/>
							</td>
							<cti:msg2 key="yukon.web.modules.adminSetup.maintenance.enable.circle.hoverText" argument="${jobNameMsg}" var="enableMsg" />
							<cti:msg2 key="yukon.web.modules.adminSetup.maintenance.disable.circle.hoverText" argument="${jobNameMsg}" var="disableMsg"/>
							<td class="fr">
								<a href="javascript:void(0)" class="icon enabled_${not job.disabled} f_toggleJobEnabled" data-truejobNameMsg="${enableMsg}" data-falsejobNameMsg="${disableMsg}" data-jobid="${job.id}">Toggle Job</a>
							</td>
		
						</tr>
					</c:forEach>
					
				</table>
		    </tags:boxContainer2>
		</cti:dataGridCell>
	</cti:dataGrid>
  
</cti:standardPage>