<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.survey.list">

<h1 class="dialogQuestion">
    <cti:msg2 key=".surveyInUse" arguments="${survey.surveyName}"/>
</h1>

<div class="actionArea">
    <cti:button nameKey="cancel" onclick="parent.$('ajaxDialog').hide()"/>
</div>

</cti:msgScope>
