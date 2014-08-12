<%@ tag  dynamic-attributes="linkParameters" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="arguments" %>
<%@ attribute name="hide" type="java.lang.Boolean" %>
<%@ attribute name="method" required="true" %>
<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="showConfirm" %>
<%@ attribute name="type" description="The type of this element. Either 'button' or 'link'. Defaults to 'button'" %>
<%@ attribute name="icon" description="Icon for the button" %>
<%@ attribute name="renderMode" description="Render mode for the button, see ButtonTag" %>
<%@ attribute name="classes" description="CSS class names to apply to the link or button" %>

<cti:default var="type" value="button"/>
<cti:default var="renderMode" value="button"/>

<c:if test="${!pageScope.hide}">

    <cti:uniqueIdentifier var="buttonId" prefix="widgetRefreshButton_"/>
    
    <script type="text/javascript">
        ${widgetParameters.jsWidget}.setupLink('${buttonId}', ${cti:jsonString(pageScope.linkParameters)});
    </script>
    
    <c:if test="${showConfirm}">
        <cti:msg2 var="confirmText" key=".${nameKey}.confirmText" arguments="${arguments}"/>
    </c:if>
    
    <c:choose>
        <c:when test="${type == 'button'}">
            <c:if test="${not empty pageScope.icon}">
                <cti:button nameKey="${nameKey}" id="${buttonId}" icon="${icon }" renderMode="${renderMode}" classes="${pageScope.classes}" data-ok-event="yukon_action_confirm"/>
            </c:if>
            <c:if test="${empty icon}">
                <cti:button nameKey="${nameKey}" id="${buttonId}" renderMode="${renderMode}" classes="${pageScope.classes}" data-ok-event="yukon_action_confirm"/>
            </c:if>
        </c:when>
        <c:otherwise>
            <a id="${buttonId}" href="javascript:void(0);" classes="${pageScope.classes}" data-ok-event="yukon_action_confirm">
             <i:inline key=".${nameKey}.label"/>
            </a>
        </c:otherwise>
    </c:choose>
    <d:confirm on="#${buttonId}" nameKey="confirmAction" />
    <script type="text/javascript">
        $('#' + '${buttonId}').on('yukon_action_confirm', function () {
            yukon.ui.busy('#' + '${buttonId}');
            ${widgetParameters.jsWidget}.doActionRefresh({command: '${method}', key: '${buttonId}'});
        });
    </script>
</c:if>