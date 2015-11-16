<%@ tag body-content="empty"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<c:set var="includeUserFeedback" value="false"/>
<cti:checkGlobalRolesAndProperties value="USER_FEEDBACK_ENABLED">
    <c:set var="includeUserFeedback" value="true"/>
</cti:checkGlobalRolesAndProperties>

<c:if test="${includeUserFeedback}">
    <script type="text/javascript">
        var uvOptions = {};
        (function() {
            var uv = document.createElement('script'); uv.type = 'text/javascript'; uv.async = true;
            uv.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'widget.uservoice.com/0eUWGbrITxgivbE4RZksTQ.js';
            var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(uv, s);
        })();
    </script>
</c:if>