<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<cti:url var="actionUrl" value="/spring/stars/consumer/enrollmentDetail"/>

<cti:standardPage module="consumer" page="enrollment">
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
    <c:set var="savingsDescriptionIconDisplayed" value="false"/>
    <c:set var="controlPercentDescriptionIconDisplayed" value="false"/>
    <c:set var="environmentDescriptionIconDisplayed" value="false"/>

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
                        <th colspan="2"><cti:msg key="yukon.dr.consumer.enrollment.programNameAndDescriptionLabel"/></th>
                        <th>&nbsp;</th>
                    </tr>
                    <cti:getProperty var="perProgram" property="ResidentialCustomerRole.ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY" />

                    <c:forEach var="enrollment" items="${enrollments}">
                        <c:set var="count" value="${count + 1}"/>
                        
                        <c:set var="isBordered" value="${fn:length(enrollments) != count}"/>
                        <c:set var="tableDataClass" value="${isBordered ? 'bordered' : ''}"/>
                        
                        <c:set var="enrollmentPrograms" value="${enrollment.enrollmentPrograms}"/>
                        <c:set var="enrollmentProgramsSize" value="${fn:length(enrollmentPrograms)}"/>

                        <c:set var="enrollmentProgramIds" value=""/>
                        <c:if test="${!perProgram}">
                            <c:set var="enrollmentProgramIds" value="${fn:join(enrollment.programIds, ',')}"/>
                        </c:if>
                        <c:set var="rowspan" value="${enrollmentProgramsSize > 0 ? enrollmentProgramsSize * 2 : 1}"/>
                    
                        <c:set var="applianceCategoryId" value="${enrollment.applianceCategory.applianceCategoryId}"/>
                    
                    
                        <tr>
                            <td valign="top" class="${tableDataClass}" rowspan="${rowspan + 1}" width="5%">
                                <img src="../../../WebConfig/${enrollment.applianceLogo}">
                            </td>
                            <td valign="top" class="${tableDataClass}" rowspan="${rowspan + 1}" width="20%">
                                <c:if test="{!perProgram}">
                                    <input type="checkbox" 
                                        id="check_${applianceCategoryId}" 
                                        <c:if test="${enrollment.enrolled}">checked</c:if>
                                        onclick="categoryAction(${applianceCategoryId}, this.checked, '${enrollmentProgramIds}')">
                                </c:if>
                                <b><c:out value="${enrollment.applianceCategory.categoryLabel}"/></b>
	                        <c:if test="${!perProgram}">
	                            <c:set var="inventoryIds" value="${fn:join(enrollment.enrolledInventoryIds, ',')}"/>
	                        </c:if>
                            </td>
                            <td colspan="3"></td>
                        </tr>

                        <c:set var="programCount" value="0"/>
                        <c:forEach var="enrollmentProgram" items="${enrollmentPrograms}">
                            <c:set var="programCount" value="${programCount + 1}"/>
                            <c:set var="programId" value="${enrollmentProgram.program.programId}"/>
                            <c:set var="isBorderedProgram" value="${isBordered || fn:length(enrollmentPrograms) != programCount}"/>
                            <c:set var="programClass" value="${isBorderedProgram ? 'bordered' : ''}"/>
                            <c:set var="inventories" value="${enrollmentProgram.inventory}"/>
                            <c:set var="inventorySize" value="${fn:length(inventories)}"/>

                            
                            <tr valign="top" class="">
                            	<c:set var="inputType" value="radio"/>
                            	<c:if test="${perProgram}">
		                            <c:set var="inventoryIds" value="${fn:join(enrollmentProgram.programInventoryIds, ',')}"/>
                            	    <c:set var="inputType" value="checkbox"/>
                            	</c:if>
                                <td class="${programClass}">
                                    <input type="${inputType}"
                                        id="program_${programId}"
                                        <c:if test="${enrollmentProgram.enrolled}">checked</c:if>
                                        name="radio_${applianceCategoryId}"
                                        onclick="enrollmentAction('${inventoryIds}',${programId}, ${applianceCategoryId}, this.checked, '${enrollmentProgramIds}');">
                                    <cti:msg key="${enrollmentProgram.program.displayName}"/>
                                </td>
                                <td class="images">
                                    <c:if test="${!empty enrollmentProgram.program.savingsDescriptionIcon}">
                                    	<img src="../../../WebConfig/yukon/Icons/${enrollmentProgram.program.savingsDescriptionIcon}">
										<c:set var="savingsDescriptionIconDisplayed" value="true"/>
                                    </c:if>
                                    <c:if test="${!empty enrollmentProgram.program.controlPercentDescriptionIcon}">
                                    	<img src="../../../WebConfig/yukon/Icons/${enrollmentProgram.program.controlPercentDescriptionIcon}">
										<c:set var="controlPercentDescriptionIconDisplayed" value="true"/>
                                    </c:if>
                                    <c:if test="${!empty enrollmentProgram.program.environmentDescriptionIcon}">
                                    	<img src="../../../WebConfig/yukon/Icons/${enrollmentProgram.program.environmentDescriptionIcon}">
										<c:set var="environmentDescriptionIconDisplayed" value="true"/>
                                    </c:if>
                                </td>
                                <td class="${programClass}">
                                    <a href="/spring/stars/consumer/enrollment/details?categoryId=${applianceCategoryId}&programId=${programId}">
                                        <cti:msg key="yukon.dr.consumer.enrollment.details"/>
                                    </a>
                                    <script type="text/javascript">
                                        enrollmentAction('${inventoryIds}',${programId}, ${applianceCategoryId}, ${enrollmentProgram.enrolled}, '');        
                                    </script>
                                </td>
                            </tr> 
                            <tr valign="top" class="">
                                <td class="${programClass}" colspan="2">
                                    <spring:escapeBody htmlEscape="true">${enrollmentProgram.program.description}</spring:escapeBody>
                                </td>
                                <td class="${programClass}">&nbsp;</td>
                            </tr>
                        </c:forEach>
                    </c:forEach>    
                </table>
            </div>                            
        </ct:boxContainer>
        <c:if test="${savingsDescriptionIconDisplayed || controlPercentDescriptionIconDisplayed || environmentDescriptionIconDisplayed}">
            <cti:msg key="yukon.dr.consumer.enrollment.iconKeyTitle" var="iconKeyTitle"/>
            <br>
            <div class="legend">
            	<h1>${iconKeyTitle}</h1>
            	<table>
                <c:if test="${savingsDescriptionIconDisplayed}">
                    <tr><td><img src="../../../WebConfig/yukon/Icons/$$Sm.gif"></td>
                    <td><cti:msg key="yukon.dr.consumer.enrollment.savingsIconDescription"/></td></tr>
                </c:if>
                <c:if test="${controlPercentDescriptionIconDisplayed}">
                    <tr><td><img src="../../../WebConfig/yukon/Icons/ThirdSm.gif"></td>
                    <td><cti:msg key="yukon.dr.consumer.enrollment.controlPercentIconDescription"/></td></tr>
                </c:if>
                <c:if test="${environmentDescriptionIconDisplayed}">
                    <tr><td><img src="../../../WebConfig/yukon/Icons/Tree2Sm.gif"></td>
                    <td><cti:msg key="yukon.dr.consumer.enrollment.environmentIconDescription"/></td></tr>
                </c:if>
                </table>
            </div>
        </c:if>
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