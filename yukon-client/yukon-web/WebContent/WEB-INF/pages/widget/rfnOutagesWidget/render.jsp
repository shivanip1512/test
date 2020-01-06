<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:url var="dataUrl" value="/widget/rfnOutagesWidget/outageData"><cti:param name="deviceId" value="${deviceId}"/></cti:url>
<script type="text/javascript">
var loadOutageTable = function() {
    var extraParameters = {'deviceId': '${deviceId}', 'com.cannontech.yukon.request.csrf.token' : $("#ajax-csrf-token").val()};
    ${widgetParameters.jsWidget}.doActionUpdate({
        'command' : 'outageData',
        'extraParameters' : extraParameters,
        'containerID' : 'outageLog'
    });
}
$(document).ready(loadOutageTable());

$(document).on('click', '#refreshLogs', loadOutageTable);
</script>

<tags:nameValueContainer2>
    <c:forEach var="attribute" items="${attributes}">
        <tags:nameValue2 label="${attribute}">
            <tags:attributeValue pao="${device}" attribute="${attribute}" />
        </tags:nameValue2>
    </c:forEach>
</tags:nameValueContainer2>

<div id="${widgetParameters.widgetId}_results" class="stacked"></div>

<div id="outageLog"></div>