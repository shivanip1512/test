<%@ attribute name="type" required="true" description="Spring bean name of the Picker class"%>
<%@ attribute name="id" required="true" description="Unique id for this picker"%>
<%@ attribute name="destinationFieldId" description="Id of field to place selected items on picker close"%>
<%@ attribute name="destinationFieldName"  description="Name of field to place selected items on picker close"%>
<%@ attribute name="multiSelectMode" type="java.lang.Boolean" description="True if this picker allows selection of multiple items"%>
<%@ attribute name="immediateSelectMode" type="java.lang.Boolean" description="True if picker should select and close when an item is clicked"%>
<%@ attribute name="endAction" description="Javascript function to call on picker close"%>
<%@ attribute name="memoryGroup" description="Adds the picker to the memory group - picker will open up with previous search text populated (as long as no page refresh between)"%>
<%@ attribute name="asButton" type="java.lang.Boolean" description="If true, picker creates a button instead of a link"%>
<%@ attribute name="styleClass" description="If provided, puts the styleClass provided on the picker link's span"%>
<%@ attribute name="extraArgs" description="Dynamic inputs to picker search"%>
<%@ attribute name="extraDestinationFields" description="used when a selection has been made and the picker is closed.  It's a semicolon separated list of: [property]:[fieldId]"%>
<%@ attribute name="buttonStyleClass" description="Class to style the button with"%>
<%@ attribute name="anchorStyleClass" description="Class to style the anchor with"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/picker.js"/>
<cti:includeScript link="/JavaScript/simpleDialog.js"/>
<cti:includeScript link="/JavaScript/tableCreation.js"/>

<script type="text/javascript">

    var ${pageScope.id} = new Picker('${pageScope.type}', '${pageScope.destinationFieldName}', '${pageScope.id}', '${pageScope.extraDestinationFields}');

    <c:if test="${pageScope.multiSelectMode}">
        ${pageScope.id}.multiSelectMode = true;
    </c:if>
    <c:if test="${pageScope.immediateSelectMode}">
        ${pageScope.id}.immediateSelectMode = true;
    </c:if>
    <c:if test="${!empty pageScope.endAction}">
        ${pageScope.id}.endAction = ${pageScope.endAction};
    </c:if>
    <c:if test="${!empty pageScope.destinationFieldId}">
        ${pageScope.id}.destinationFieldId = '${pageScope.destinationFieldId}';
    </c:if>
    <c:if test="${!empty pageScope.memoryGroup}">
        ${pageScope.id}.memoryGroup = '${pageScope.memoryGroup}';
    </c:if>
    <c:if test="${!empty pageScope.extraArgs}">
        ${pageScope.id}.extraArgs = '<spring:escapeBody javaScriptEscape="true">${pageScope.extraArgs}</spring:escapeBody>';
    </c:if>
    
</script>
<span id="picker_${pageScope.id}_inputArea"></span>

<span <c:if test="${not empty pageScope.styleClass}">class="${pageScope.styleClass}"</c:if>>
    <c:if test="${!pageScope.asButton}">
        <c:set var="anchorAttributes" value=""/>
        <c:if test="${!empty pageScope.anchorStyleClass}">
            <c:set var="anchorAttributes" value=" class=\"${pageScope.anchorStyleClass}\""/>
        </c:if>
        <a href="javascript:${pageScope.id}.show()"${anchorAttributes}><jsp:doBody/></a>
    </c:if>
    <c:if test="${pageScope.asButton}">
        <input type="button" value="<jsp:doBody/>" 
            onclick="javascript:${pageScope.id}.show()" 
            class="formSubmit <c:if test="${not empty pageScope.buttonStyleClass}">${pageScope.buttonStyleClass}</c:if>">
    </c:if>
</span>
