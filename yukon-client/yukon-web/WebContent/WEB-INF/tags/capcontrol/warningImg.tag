<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>
<%@ attribute name="alertBox" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<c:if test="${not alertBox}">

    <span class="js-tooltip" id="warningPopup_${paoId}" style="display: none;">
        <cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG_MESSAGE"/> 
    </span>
    <span data-pao-id="${paoId}" class="dn js-has-tooltip js-warning-image">
        <cti:icon classes="row-icon" icon="icon-error"/>
    </span>
</c:if>

<c:if test="${alertBox}">
    <div data-pao-id="${paoId}" class="dn user-message error js-warning-image">
        <cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG_MESSAGE"/> 
    </div>
</c:if>

<cti:dataUpdaterCallback function="yukon.da.updaters.warningIcon" initialize="true" value="${type}/${paoId}/WARNING_FLAG"/>