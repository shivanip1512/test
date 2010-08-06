<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:msgScope paths="modules.survey.list">

<h1 class="dialogQuestion">
    <cti:msg2 key=".surveyInUse" arguments="${survey.surveyName}"/>
</h1>

<div class="actionArea">
    <input type="button" value="<cti:msg2 key=".cancel"/>"
        onclick="$('ajaxDialog').hide()"/>
</div>

</cti:msgScope>
