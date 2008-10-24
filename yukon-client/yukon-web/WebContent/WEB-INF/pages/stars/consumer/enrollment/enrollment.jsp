<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<c:url var="actionUrl" value="/spring/stars/consumer/enrollmentDetail"/>

<cti:standardPage module="consumer" page="enrollmentDetail">
    <cti:standardMenu />
    
<script type="text/javascript">
var hash = $H();

function enrollmentAction(inventoryIds, programId, applianceCategoryId, enroll, clearProgramIds) {

    if(enroll && $('check_' + applianceCategoryId) != null) {
        $('check_' + applianceCategoryId).checked = true;
    }

    // Remove any existing programs in the clearProgramIds list
    var clearIds = clearProgramIds.split(',');
    for(var i=0; i<clearIds.length;i++){
        var clearId = clearIds[i];
        hash.remove(clearId);
    }

    var value = hash[programId];
    if (!value) {
        value = new $H();
        hash[programId] = value;
    }

    var ids = inventoryIds.split(',');

    for(var i=0; i<ids.length;i++){
	
        var inventoryId = ids[i];
	    var value2 = value[inventoryId];
	    if (!value2) {
	        value2 = new $H();
	        value[inventoryId] = value2;
	    }
	    
	    value2['programId'] = programId;
	    value2['inventoryId'] = inventoryId;
	    value2['applianceCategoryId'] = applianceCategoryId;
	    value2['enroll'] = enroll;
    }
}

function categoryAction(categoryId, checked, programIds) {
    
    if(checked) {
        // Category checkbox checked, select the first program's radio button
        var ids = programIds.split(',');
        var programId = ids[0];
        $('program_' + programId).checked = true;
        
        $('program_' + programId).onclick();
        
    } else {
        // Category checkbox unchecked, unselect each program's radio button
        var ids = programIds.split(',');
        for(var i=0; i<ids.length;i++) {
	        var programId = ids[i];
	        $('program_' + programId).checked = false;
	        // Remove from hash so we're unenrolled
	        hash.remove(programId);
        }
    }
    
}

function submitEnrollment() {
    var hiddenInput = document.createElement('INPUT');
    hiddenInput.type = 'hidden';
    hiddenInput.name = 'json';
    hiddenInput.value = hash.toJSON();
    
    var form = $('mainForm'); 
    form.appendChild(hiddenInput);
    form.submit();
}
</script>    
    
    <h3><cti:msg key="yukon.dr.consumer.enrollment.header" /></h3>
    <br>
    <cti:msg key="yukon.dr.consumer.enrollment.enrollmentTitle" var="enrollmentTitle"/>
    <form id="mainForm" action="/spring/stars/consumer/enrollmentUpdate" method="POST">
        
        <input type="hidden" name="enrollPage" value="enrollment" />
        <ct:boxContainer title="${enrollmentTitle}" hideEnabled="false">
            <div id="programEnrollmentDiv">
            
                <table class="enrolledPrograms" width="99%" align="center">
                
                    <tr>
                        <th></th>
                        <th><cti:msg key="yukon.dr.consumer.enrollment.category"/></th>
                        <th><cti:msg key="yukon.dr.consumer.enrollment.programName"/></th>
                        <th><cti:msg key="yukon.dr.consumer.enrollment.description"/></th>
                        <th>&nbsp;</th>
                    </tr>
                    <cti:getProperty var="perProgram" property="ResidentialCustomerRole.ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY" />

                    <c:forEach var="enrollment" items="${enrollments}">
                        <c:set var="count" value="${count + 1}"/>
                        
                        <c:set var="isBordered" value="${fn:length(enrollments) != count}"/>
                        <c:set var="tableDataClass" value="${isBordered ? 'bordered' : ''}"/>
                        
                        <c:set var="enrollmentPrograms" value="${enrollment.enrollmentPrograms}"/>
                        <c:set var="enrollmentProgramsSize" value="${fn:length(enrollmentPrograms)}"/>

                        <c:set var="enrollmentProgramIds" value="${fn:join(enrollment.programIds, ',')}"/>
                        <c:set var="rowspan" value="${enrollmentProgramsSize > 0 ? enrollmentProgramsSize : 1}"/>
                    
                        <c:set var="applianceCategoryId" value="${enrollment.applianceCategory.applianceCategoryId}"/>
                    
                    
                        <c:choose>
	                        <c:when test="${!perProgram}">
		                        <tr>
		                            <td valign="top" class="${tableDataClass}" rowspan="${rowspan + 1}" width="5%">
		                                <img src="../../../WebConfig/${enrollment.applianceLogo}">
		                            </td>
		                            <td valign="top" class="${tableDataClass}" rowspan="${rowspan + 1}" width="20%">
		                                <input 
		                                    type="checkbox" 
		                                    id="check_${applianceCategoryId}" 
		                                    <c:if test="${enrollment.enrolled}">checked</c:if>
		                                    onclick="categoryAction(${applianceCategoryId}, this.checked, '${enrollmentProgramIds}')"
		                                >
		                                <b><c:out value="${enrollment.applianceCategory.categoryLabel}"/></b>
		                            </td>
		                            <td colspan="2"></td>
		                        </tr>

	                            <c:set var="inventoryIds" value="${fn:join(enrollment.enrolledInventoryIds, ',')}"/>
	                        </c:when>
	                        <c:otherwise>
		                        <tr>
		                            <td valign="top" class="${tableDataClass}" rowspan="${rowspan + 1}" width="5%">
		                                <img src="../../../WebConfig/${enrollment.applianceLogo}">
		                            </td>
		                            <td valign="top" class="${tableDataClass}" rowspan="${rowspan + 1}" width="20%">
		                                <b><cti:msg key="${enrollment.applianceType}"/></b>
		                            </td>
		                            <td colspan="2"></td>
		                        </tr>
	                        </c:otherwise>
                        </c:choose>

                        <c:set var="programCount" value="0"/>
                        <c:forEach var="enrollmentProgram" items="${enrollmentPrograms}">
                            <c:set var="programCount" value="${programCount + 1}"/>
                            <c:set var="programId" value="${enrollmentProgram.program.programId}"/>
                            <c:set var="isBorderedProgram" value="${isBordered || fn:length(enrollmentPrograms) != programCount}"/>
                            <c:set var="programClass" value="${isBorderedProgram ? 'bordered' : ''}"/>
                            <c:set var="inventories" value="${enrollmentProgram.inventory}"/>
                            <c:set var="inventorySize" value="${fn:length(inventories)}"/>

                            
                            <c:choose>
                                <c:when test="${!perProgram}">
		                            <tr valign="top" class="">
		                                <td class="${programClass}">
		                                    <input 
		                                        type="radio" 
		                                        id="program_${programId}"
		                                        <c:if test="${enrollmentProgram.enrolled}">checked</c:if>
		                                        name="radio_${applianceCategoryId}"
		                                        onclick="enrollmentAction('${inventoryIds}',${programId}, ${applianceCategoryId}, this.checked, '${enrollmentProgramIds}');"
		                                    >
		                                    <cti:msg key="${enrollmentProgram.program.displayName}"/>
		                                </td>
		                                <td class="${programClass}" rowspan="${inventoryRowspan}">
		                                    <spring:escapeBody htmlEscape="true">${enrollmentProgram.program.description}</spring:escapeBody>
		                                </td>
		                                <td class="${programClass}" rowspan="${inventoryRowspan}">
                                            <a href="/spring/stars/consumer/enrollment/details?categoryId=${applianceCategoryId}&programId=${programId}">
                                                <cti:msg key="yukon.dr.consumer.enrollment.details"/>
                                            </a>
		                                </td>
		                                
		                                <script type="text/javascript">
		                                    enrollmentAction('${inventoryIds}',${programId}, ${applianceCategoryId}, ${enrollmentProgram.enrolled}, '');        
		                                </script>
		                            </tr>
		                        </c:when>
		                        <c:otherwise>    
		                            <c:set var="inventoryIds" value="${fn:join(enrollmentProgram.programInventoryIds, ',')}"/>
		                            <tr valign="top" class="">
		                                <td class="${programClass}">
		                                    <input 
		                                        type="checkbox" 
		                                        id="program_${programId}"
		                                        <c:if test="${enrollmentProgram.enrolled}">checked</c:if>
		                                        onclick="enrollmentAction('${inventoryIds}',${programId}, ${applianceCategoryId}, this.checked, '');"
		                                    >
		                                    <cti:msg key="${enrollmentProgram.program.displayName}"/>
		                                </td>
		                                <td class="${programClass}" rowspan="${inventoryRowspan}">
		                                    <spring:escapeBody htmlEscape="true">${enrollmentProgram.program.description}</spring:escapeBody>
		                                </td>
		                                <td class="${programClass}" rowspan="${inventoryRowspan}">
                                            <a href="/spring/stars/consumer/enrollment/details?categoryId=${applianceCategoryId}&programId=${programId}">
                                                <cti:msg key="yukon.dr.consumer.enrollment.details"/>
                                            </a>
                                        </td>
		                                
		                                <script type="text/javascript">
		                                    enrollmentAction('${inventoryIds}',${programId}, ${applianceCategoryId}, ${enrollmentProgram.enrolled}, '');        
		                                </script>
		                            </tr> 
		                        </c:otherwise>
		                    </c:choose>   
                        </c:forEach>
                    </c:forEach>    
                </table>
            </div>                            
        </ct:boxContainer>
        <br>
        <div align="center">
            <span style="padding-right: 0.5em;">
                <input type="button" 
                       value="<cti:msg key='yukon.dr.consumer.enrollment.save'/>"
                       onclick="javascript:submitEnrollment();">
                </input>
            </span>
            <span style="padding-right: 0.5em;">
                <input type="reset" value="<cti:msg key='yukon.dr.consumer.enrollment.reset'/>"></input>
            </span><br><br>
            <cti:checkProperty property="ResidentialCustomerRole.ENROLLMENT_PER_DEVICE">
                <a href="/spring/stars/consumer/enrollmentDetail"><cti:msg key='yukon.dr.consumer.enrollment.enrollmentDetail'/></a>
            </cti:checkProperty>
        </div>
    </form>
    
</cti:standardPage>