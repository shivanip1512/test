<%@ tag trimDirectiveWhitespaces="true" dynamic-attributes="linkParameters" %>

<%@ attribute name="method" required="true" %>
<%@ attribute name="icon" description="Icon to use for the button." %>
<%@ attribute name="container" required="true" %>
<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="showConfirm" %>
<%@ attribute name="hide" type="java.lang.Boolean" %>
<%@ attribute name="type" description="The type of this element. Either 'button' or 'link'. Defaults to 'button'" %>
<%@ attribute name="renderMode" description="Render mode for the button, see ButtonTag" %>
<%@ attribute name="classes" description="CSS class names to apply to the link or button" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:default var="type" value="button"/>
<cti:default var="renderMode" value="button"/>

<c:if test="${!pageScope.hide}">
    <cti:uniqueIdentifier var="buttonId" prefix="widgetUpdateButton_"/>
        
    <script type="text/javascript">
        ${widgetParameters.jsWidget}.setupLink('${buttonId}', ${cti:jsonString(pageScope.linkParameters)});
    </script>

    <c:if test="${showConfirm}">
        <cti:msg2 var="confirmText" key=".${nameKey}.confirmText"/>
    </c:if>
    
    <c:choose>
        <c:when test="${type == 'button'}">
            <c:if test="${not empty pageScope.icon}">
                <cti:button nameKey="${nameKey}" id="${buttonId}" icon="${icon }" renderMode="${renderMode}" classes="${pageScope.classes}" busy="true"/>
            </c:if>
            <c:if test="${empty icon}">
                <cti:button nameKey="${nameKey}" id="${buttonId}" renderMode="${renderMode}" classes="${pageScope.classes}" busy="true"/>
            </c:if>
        </c:when>
        <c:otherwise>
            <a id="${buttonId}" href="javascript:void(0);"><i:inline key=".${nameKey}.label"/></a>
        </c:otherwise>
    </c:choose>
    <script>
        $(document.getElementById("${buttonId}")).click(function() {
            var confirmText = '${cti:escapeJavaScript(pageScope.confirmText)}';
            var confirmed = true;
            if (confirmText.trim() !== '') {
                confirmed = confirm(confirmText);   
            }
            if (confirmed) {
                ${widgetParameters.jsWidget}.doActionUpdate({command: '${method}', containerID: '${container}', key: '${buttonId}'});
            }
        });
    </script>
</c:if>