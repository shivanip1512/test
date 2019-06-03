<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<i:inline key = "yukon.web.modules.adminSetup.config.emailTestBody"/>

<form:form  class="js-no-submit-on-enter" id= "adminSetup-testEmail-form" modelAttribute="email" action="emailTest" method="POST">  
    <tags:nameValueContainer2 tableClass="with-form-controls">
        <tags:inputNameValue nameKey="yukon.web.modules.adminSetup.config.email" path="to" size="40" maxlength="100"/>
    </tags:nameValueContainer2>
</form:form>

