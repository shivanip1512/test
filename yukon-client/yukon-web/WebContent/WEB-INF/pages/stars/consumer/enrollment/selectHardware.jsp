<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" page="enrollmentSelectHardware">
    <cti:includeCss link="/WebConfig/yukon/styles/consumer/enrollment.css"/>
    <cti:standardMenu/>

    <h3><cti:msg key="yukon.dr.consumer.enrollment.header" /></h3>
    <br>
    <c:if test="${!empty errorMessage}">
        <div class="userMessage ERROR"><cti:msg key="${errorMessage}"/></div>
    </c:if>

    <cti:msg var="programName" key="${displayableEnrollmentProgram.program.displayName}"/>
    <p><cti:msg key="yukon.dr.consumer.selectHardware.query" argument="${programName}"/></p>

    <cti:url var="submitUrl" value="/stars/consumer/enrollment/enrollSelectedHardware"/>
    <form method="POST" action="${submitUrl}">
        <input type="hidden" name="assignedProgramId" value="${displayableEnrollmentProgram.program.programId}"/>
		<c:forEach var="inventory" items="${displayableEnrollmentProgram.inventory}">
		    <c:set var="inventoryId" value="${inventory.inventoryId}"/>
	        <input type="checkbox" name="inventoryIds" value="${inventoryId}"
	           id="inventoryIdCB${inventoryId}">
	        <label for="inventoryIdCB${inventoryId}"><spring:escapeBody htmlEscape="true">${inventory.displayName}</spring:escapeBody></label>
	        <br>
		</c:forEach>

        <div align="center">
            <input type="submit"
                value="<cti:msg key="yukon.dr.consumer.selectHardware.ok"/>">
            <cti:url var="cancelUrl" value="/stars/consumer/enrollment"/>
            <input type="button"
                value="<cti:msg key="yukon.dr.consumer.selectHardware.cancel"/>"
                onclick="location.href='${cancelUrl}';">
        </div>
    </form>

</cti:standardPage>
