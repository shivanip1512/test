<%@ tag trimDirectiveWhitespaces="true" dynamic-attributes="linkParameters" %>

<%@ attribute name="method" required="true" %>
<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="icon" required="true" %>

<%@ attribute name="arguments" %>
<%@ attribute name="btnClass" %>
<%@ attribute name="showConfirm" %>
<%@ attribute name="disableInputs" type="java.lang.Boolean" description="Should the refresh action disable all inputs while waiting for the asynchronous response. Defaults to false." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:uniqueIdentifier var="buttonId" prefix="widgetLinkId_"/>

<c:if test="${showConfirm}">
    <cti:msg2 var="confirmText" key=".${nameKey}.confirmText" arguments="${arguments}"/>
</c:if>

<c:if test="${empty pageScope.disableInputs}">
    <c:set var="disableInputs" value="false" />
</c:if>

<script type="text/javascript">
    ${widgetParameters.jsWidget}.setupLink('${buttonId}', ${cti:jsonString(pageScope.linkParameters)});

    widgetActionRefreshImageConfirm_${buttonId} = function () {

        var confirmText = '${cti:escapeJavaScript(pageScope.confirmText)}',
            confirmed = true;
        if (confirmText !== null && $.trim(confirmText) !== '') {
            confirmed = window.confirm(confirmText);
        }
        // generate mouseleave event so tipsy tooltip lib knows to close tooltip
        $('#' + 'linkImg_' + '${buttonId}').trigger('mouseleave');
        if (confirmed) {
            ${widgetParameters.jsWidget}.doActionRefresh({
                command:     '${method}', 
                key:         '${buttonId}',
                disableInputs: ${disableInputs}});
        }
    }
</script>

<cti:button nameKey="${nameKey}" id="${buttonId}" renderMode="image" arguments="${arguments}" onclick="widgetActionRefreshImageConfirm_${buttonId}();" classes="${btnClass}" icon="${icon}"/>
