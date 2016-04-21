<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>

<cti:standardPage module="adminSetup" page="maintenance">

<c:url value="/admin/maintenance/toggleJobEnabledAjax" var="toggleJobAjaxUrl"/>

<script>
$(function(){
    $(document).on('change', '.js-toggleJobEnabled .checkbox-input', function() {
        var checkbox = $(this);
        var jobId = checkbox.data('jobId');
        
        $.ajax({
            url: '${toggleJobAjaxUrl}?jobId=' + jobId
        });
    });
});
</script>
		    	<table class="compact-results-table has-alerts">
                <thead>
					<tr>
						<th>&nbsp;</th>
						<th><i:inline key=".tableHeader.scheduleName"/></th>
						<th><i:inline key=".tableHeader.scheduleDescription"/></th>
						<th></th>
					</tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
					<c:forEach var="job" items="${jobs}">
						<cti:msg2 var="jobNameMsg" key=".${job.beanName}.title"/>
						<tr>
							<cti:url var="editScheduleDetailsUrl" value="/admin/maintenance/edit" >
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
							<td class="fr">
                                <tags:switch checked="${not job.disabled}" name="toggle" data-job-id="${job.id}" classes="js-toggleJobEnabled toggle-sm"/>
							</td>
		
						</tr>
					</c:forEach>
                </tbody>
				</table>
</cti:standardPage>