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
var YG = {
        PHONE: {
            FORMATS: <cti:msg2 key="yukon.common.phoneNumberFormatting.formats"/>
        },
        
        APP_NAME: '${appName}',
        
        TEXT: {
            ok: '<cti:msg2 key="yukon.web.components.button.ok.label"/>',
            cancel: '<cti:msg2 key="yukon.web.components.button.cancel.label"/>'
        },
        DEV_MODE: '<cti:getBooleanConfigParam param="DEVELOPMENT_MODE"/>' === 'true'
};
</script>