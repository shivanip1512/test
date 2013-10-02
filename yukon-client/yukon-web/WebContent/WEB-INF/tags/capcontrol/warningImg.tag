<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>
<%@ attribute name="alertBox" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<input type="hidden" id="warning_${paoId}" value='<cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG"/>'>

<c:if test="${not alertBox}">
    <span id="warning_${paoId}_ok" style="display: none;">
        <cti:icon classes="row-icon" icon="icon-blank"/>
    </span>

    <span class="f-tooltip" id="warningPopup_${paoId}" style="display: none;">
        <cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG_MESSAGE"/> 
    </span>
    <span id="warning_${paoId}_alert" style="display: none;" class="f-has-tooltip">
        <cti:icon classes="row-icon" icon="icon-error"/>
    </span>
</c:if>

<c:if test="${alertBox}">
    <div id="warning_${paoId}_ok" style="display: none;">
    </div>
    <div id="warning_${paoId}_alert" class="userMessage ERROR" style="display: none;">
        <cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG_MESSAGE"/> 
    </div>
</c:if>

<cti:dataUpdaterCallback function="updateWarningImage('warning_${paoId}')" initialize="true" value="${type}/${paoId}/WARNING_FLAG"/>