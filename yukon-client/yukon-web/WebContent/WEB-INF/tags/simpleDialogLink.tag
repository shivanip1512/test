<%@ tag body-content="empty" %>
<%@ attribute name="dialogId" required="true"%>
<%@ attribute name="titleKey" required="true"%>
<%@ attribute name="actionUrl" required="true"%>
<%@ attribute name="logoKey" required="true"%>
<%@ attribute name="labelKey"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<cti:uniqueIdentifier var="dialogTitle" prefix="simpleDialogTitle"/>
<script type="text/javascript">
    var ${dialogTitle} = '<cti:msg key="${pageScope.titleKey}" javaScriptEscape="true" />';
</script>            

<a href="javascript:void(0)" class="simpleLink" 
        onclick="openSimpleDialog('${pageScope.dialogId}', '${pageScope.actionUrl}', ${dialogTitle})">
    <cti:logo key="${pageScope.logoKey}"/>
    <c:if test="${not empty pageScope.labelKey}">
        <cti:msg key="${pageScope.labelKey}" />
    </c:if>
</a>