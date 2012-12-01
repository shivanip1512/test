<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="jqGrid" tagdir="/WEB-INF/tags/jqGrid" %>

<cti:url var="dataUrl" value="/widget/rfnOutagesWidget/outageData"><cti:param name="deviceId" value="${deviceId}"/></cti:url>
<script type="text/javascript">
var loadOutageTable = function() {
    var extraParameters = {'deviceId': '${deviceId}'};
    ${widgetParameters.jsWidget}.doActionUpdate({
        'command' : 'outageData',
        'extraParameters' : extraParameters,
        'containerID' : 'outageLog'
    });
}
jQuery(document).ready(loadOutageTable());

jQuery(document).on('click', '#refreshLogs', loadOutageTable);
</script>

<tags:nameValueContainer2>
    <c:forEach var="attribute" items="${attributes}">
        <tags:nameValue2 label="${attribute}">
            <tags:attributeValue device="${meter}" attribute="${attribute}" />
        </tags:nameValue2>
    </c:forEach>
</tags:nameValueContainer2>

<div id="${widgetParameters.widgetId}_results"></div>

<div class="actionArea stacked">
    <tags:widgetActionUpdate container="${widgetParameters.widgetId}_results" method="read" nameKey="read"/>
</div>

<div id="outageLog"></div>