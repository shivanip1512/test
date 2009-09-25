<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<script type="text/javascript">
</script>

<p>
    <cti:msg key="yukon.web.modules.dr.program.startProgramDetails.confirmQuestion"
        argument="${program.name}"/>
</p><br>

<cti:url value="/spring/dr/program/startProgramConstraints"/>
<form:form id="startProgramForm" commandName="backingBean" action="${submitUrl}">
    <form:hidden path="programId"/>
    <form:hidden path="gearNumber"/>
    <form:hidden path="gearAdjustments"/>
    <form:hidden path="startDate"/>
    <form:hidden path="autoObserverConstraints"/>

<!-- TODO:  internationalize -->
    TODO:  need to ask for gear adjustments here; default to 100%
    <br>
    <br>

    <input id="autoObserveConstraints" name="autoObserveConstraints" type="checkbox"/>
    <label for="autoObserveConstraints">Automatically Observe Constraints</label><br>

    <div class="actionArea">
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.startProgramDetails.okButton"/>"
            onclick="submitForm()"/>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.startProgramDetails.cancelButton"/>"
            onclick="parent.$('drDialog').hide()"/>
    </div>
</form:form>
