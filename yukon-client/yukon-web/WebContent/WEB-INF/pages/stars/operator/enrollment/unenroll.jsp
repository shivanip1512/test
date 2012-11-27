<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>


<cti:msgScope paths="modules.operator.enrollmentList">

<cti:url var="submitUrl" value="/stars/operator/enrollment/unenroll"/>
<form id="inputForm" action="${submitUrl}" onsubmit="return submitFormViaAjax('peDialog', 'inputForm')">
    <input type="hidden" name="accountId" value="${param.accountId}"/>
    <input type="hidden" name="assignedProgramId" value="${param.assignedProgramId}"/>

    <p>
        <i:inline key=".confirmUnenroll" arguments="${assignedProgram.name.displayName}"/>
    </p>

    <div class="actionArea">
        <input type="submit" value="<cti:msg2 key=".ok"/>"/>
        <input type="button" value="<cti:msg2 key=".cancel"/>"
            onclick="parent.$('peDialog').hide()"/>
    </div>
</form>

</cti:msgScope>
