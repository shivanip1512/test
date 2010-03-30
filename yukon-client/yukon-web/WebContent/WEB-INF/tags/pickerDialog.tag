<%@ attribute name="type" required="true" %>
<%@ attribute name="id" required="true" %>
<%@ attribute name="destinationFieldId" %>
<%@ attribute name="destinationFieldName" %>
<%@ attribute name="multiSelectMode" type="java.lang.Boolean" %>
<%@ attribute name="immediateSelectMode" type="java.lang.Boolean" %>
<%@ attribute name="endAction" %>
<%@ attribute name="memoryGroup" %>
<%@ attribute name="asButton" type="java.lang.Boolean" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="extraArgs" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/picker.js"/>
<cti:includeScript link="/JavaScript/simpleDialog.js"/>
<cti:includeScript link="/JavaScript/tableCreation.js"/>

<script type="text/javascript">
    var ${pageScope.id} = new Picker('${pageScope.type}', '${pageScope.destinationFieldName}', '${pageScope.id}');
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
        ${pageScope.id}.extraArgs = '${pageScope.extraArgs}';
    </c:if>
</script>
<span id="picker_${pageScope.id}_inputArea"></span>

<span class="pickerLink ${styleClass}">
    <c:if test="${!pageScope.asButton}">
    <a href="javascript:${pageScope.id}.show()"><jsp:doBody/></a>
    </c:if>
    <c:if test="${pageScope.asButton}">
        <input type="button" value="<jsp:doBody/>"
            onclick="javascript:${pageScope.id}.show()"/>
    </c:if>
</span>
