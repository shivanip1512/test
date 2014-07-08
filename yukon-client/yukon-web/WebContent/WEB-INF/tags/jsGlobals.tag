<%@ tag trimDirectiveWhitespaces="true" body-content="empty" 
        description="Sets some global javascript values for Yukon on the global variable 'YG'" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:url var="appName" value="/"/>
<c:if test="${appName == '/'}">
    <%-- When we're running as the root web application, we need an empty string for a prefix. --%>
    <c:set var="appName" value=""/>
</c:if>
<script type="text/javascript">
var yg = {
    phone: {
        formats: <cti:msg2 key="yukon.common.phoneNumberFormatting.formats"/>
    },
    
    app_name: '${appName}',
    
    text: {
        cancel: '<cti:msg2 key="yukon.web.components.button.cancel.label"/>',
        'delete': '<cti:msg2 key="yukon.web.components.button.delete.label"/>',
        edit: '<cti:msg2 key="yukon.web.components.button.edit.label"/>',
        ok: '<cti:msg2 key="yukon.web.components.button.ok.label"/>',
        save: '<cti:msg2 key="yukon.web.components.button.save.label"/>',
        view: '<cti:msg2 key="yukon.web.components.button.view.label"/>'
    },
    dev_mode: '<cti:getBooleanConfigParam param="DEVELOPMENT_MODE"/>' === 'true'
};
</script>