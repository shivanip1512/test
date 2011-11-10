<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:msgScope paths="yukon.web.modules.capcontrol.bankMove">

<tags:boxContainer2 nameKey="selectedFeeder">
    <div style="max-height: 112px;overflow: auto;overflow-x: hidden;">
    <table class="compactResultsTable">
    	<tr>
    		<th><i:inline key=".bankName"/></th>
    		<th><i:inline key=".displayOrder"/></th>
    		<th><i:inline key=".closeOrder"/></th>
    		<th><i:inline key=".tripOrder"/></th>
    	</tr>
    	<c:forEach var="cap" items="${capBankList}">
    		<tr class="<tags:alternateRow odd="" even="altRow"/>">
    			<td><spring:escapeBody htmlEscape="true">${cap.ccName}</spring:escapeBody></td>
    			<td>${cap.controlOrder}</td>
    			<td>${cap.closeOrder}</td>
    			<td>${cap.tripOrder}</td>
    		</tr>
    	</c:forEach>
    </table>
    </div>
</tags:boxContainer2>
</cti:msgScope>