<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>
<%@ attribute name="alertBox" type="java.lang.Boolean" %>

<c:if test="${not alertBox}">
    <span id="cc-warning-msg-${paoId}" class="dn">
        <cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG_MESSAGE" initialize="false"/> 
    </span>
    <cti:icon classes="dn js-warning-image" icon="icon-error" 
        data-pao-id="${paoId}"
        data-tooltip="#cc-warning-msg-${paoId}"/>
</c:if>

<c:if test="${alertBox}">
    <div data-pao-id="${paoId}" class="dn user-message error js-warning-image">
        <cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG_MESSAGE" initialize="false"/> 
    </div>
</c:if>

<cti:dataUpdaterCallback function="yukon.da.updaters.warningIcon" initialize="false" value="${type}/${paoId}/WARNING_FLAG"/>