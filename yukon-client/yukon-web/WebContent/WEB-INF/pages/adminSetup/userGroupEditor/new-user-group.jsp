<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.adminSetup.auth.user.group,yukon.common">

<form:form id="new-user-group-form" modelAttribute="group" method="post">
    <cti:csrfToken/>
    
    <tags:nameValueContainer2 tableClass="with-form-controls">
        <tags:inputNameValue nameKey="yukon.common.name" path="userGroupName"/>
        <tags:textareaNameValue nameKey="yukon.common.description" rows="3" cols="35" path="userGroupDescription"/>
    </tags:nameValueContainer2>
    
</form:form>

</cti:msgScope>