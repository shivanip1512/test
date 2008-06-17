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

function enrollmentAction(inventoryId, programId, applianceCategoryId, enroll) {
    var value = hash[programId];
    if (!value) {
        value = new $H();
        hash[programId] = value;
    }

    var value2 = value[inventoryId];
    if (!value2) {
        value2 = new $H();
        value[inventoryId] = value2;
    }
    
    value2['programId'] = programId;
    value2['inventoryId'] = inventoryId;
    value2['applianceCategoryId'] = applianceCategoryId;
    value2['enroll'] = enroll;
    
    //alert(hash.toJSON());
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
        <input type="hidden" name="enrollPage" value="enrollmentDetail" />
    
        <ct:boxContainer title="${enrollmentTitle}" hideEnabled="false">
            <div id="programEnrollmentDiv">
            
                <c:set var="disableCheckBox" value="${disableCheckBox ? 'disabled' : ''}"/>
                <c:set var="count" value="0"/>
                <table class="enrolledPrograms" width="99%" align="center">
                
                    <tr>
                        <th></th>
                        <th><cti:msg key="yukon.dr.consumer.enrollment.category"/></th>
                        <th><cti:msg key="yukon.dr.consumer.enrollment.programName"/></th>
                        <th><cti:msg key="yukon.dr.consumer.enrollment.description"/></th>
                        <th><cti:msg key="yukon.dr.consumer.enrollment.enrolledHardware"/></th>
                    </tr>
                
                    <c:forEach var="enrollment" items="${enrollments}">
                        <c:set var="count" value="${count + 1}"/>
                        
                        <c:set var="isBordered" value="${fn:length(enrollments) != count}"/>
                        <c:set var="tableDataClass" value="${isBordered ? 'bordered' : ''}"/>
                        
                        <c:set var="enrollmentPrograms" value="${enrollment.enrollmentPrograms}"/>
                        <c:set var="enrollmentProgramsSize" value="${fn:length(enrollmentPrograms)}"/>
                        <c:set var="rowspan" value="${enrollmentProgramsSize > 0 ? enrollmentProgramsSize : 1}"/>
                    
                        <c:set var="applianceCategoryId" value="${enrollment.applianceCategoryId}"/>
                    
                        <tr>
                            <td valign="top" class="${tableDataClass}" rowspan="${rowspan + 1}" width="5%">
                                <img src="../../../WebConfig/${enrollment.applianceLogo}">
                            </td>
                            <td valign="top" class="${tableDataClass}" rowspan="${rowspan + 1}" width="10%">
                                <b><cti:msg key="${enrollment.applianceType}"/></b>
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
                                <td class="${programClass}">
                                    <cti:msg key="${enrollmentProgram.program.displayName}"/>
                                </td>
                                <td class="${programClass}" rowspan="${inventoryRowspan}">
                                    <spring:escapeBody htmlEscape="true">${enrollmentProgram.program.description}</spring:escapeBody>
                                </td>
                                <td class="${programClass}" style="padding-bottom: 0.5em;">
                                    <c:forEach var="enrollmentInventory" items="${inventories}">
                                        <c:set var="inventoryId" value="${enrollmentInventory.inventoryId}"/>
                                        <label>
                                            <input type="checkbox" 
                                                   value="${inventoryId}"
                                                   <c:if test="${enrollmentInventory.enrolled}">checked</c:if>
                                                   onclick="javascript:enrollmentAction(${inventoryId},${programId}, ${applianceCategoryId}, this.checked);"
                                                   ${disableCheckBox}>
                                                   
                                            <script type="text/javascript">
                                                enrollmentAction(${inventoryId},${programId}, ${applianceCategoryId}, ${enrollmentInventory.enrolled});        
                                            </script>
                                                                                                           
                                            <c:if test="${inventorySize > 1}">
                                                <spring:escapeBody htmlEscape="true">${enrollmentInventory.displayName}</spring:escapeBody>
                                            </c:if>    
                                        </label>
                                        <br>
                                    </c:forEach>
                                </td>
                            </tr>    
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
            <input type="reset" value="<cti:msg key='yukon.dr.consumer.enrollment.reset'/>"></input>
        </div>
    </form>
    
</cti:standardPage>  
