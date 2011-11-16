<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>


<dialog:ajaxPage nameKey="details" module="survey" page="list" okAction="submit">

<script type="text/javascript">
jQuery(document).ready(function() {
    jQuery('#inputForm').ajaxForm({'target' : '#ajaxDialog'});
});
</script>

<cti:url var="submitUrl" value="saveDetails"/>
<form:form id="inputForm" commandName="survey" action="${submitUrl}">
    <form:hidden path="surveyId"/>
    <form:hidden path="energyCompanyId"/>
    <tags:nameValueContainer>
        <cti:msg2 var="fieldName" key=".name"/>
        <tags:nameValue name="${fieldName}">
            <tags:input id="surveyName" path="surveyName" size="30" maxlength="64"/>
        </tags:nameValue>

        <cti:msg2 var="fieldName" key=".key"/>
        <tags:nameValue name="${fieldName}">
            <tags:input path="surveyKey" size="30" maxlength="64"/>
        </tags:nameValue>
    </tags:nameValueContainer>
</form:form>

</dialog:ajaxPage>
