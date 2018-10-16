<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.adminSetup.auth.role.group,yukon.common">

<form:form id="new-role-group-form" modelAttribute="group" method="post">
    <cti:csrfToken/>
    
    <tags:nameValueContainer2 tableClass="with-form-controls">
        <tags:inputNameValue nameKey=".name" path="groupName" size="40"/>
        <tags:textareaNameValue nameKey=".description" rows="3" cols="35" path="groupDescription"/>
    </tags:nameValueContainer2>
    
</form:form>

</cti:msgScope>