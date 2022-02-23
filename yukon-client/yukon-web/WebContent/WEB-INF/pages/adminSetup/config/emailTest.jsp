<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>



<form:form  class="js-no-submit-on-enter" id="adminSetup-testEmail-form" modelAttribute="email" action="emailTest" method="POST">  
    <tags:nameValueContainer2 tableClass="with-form-controls">
        <tags:inputNameValue nameKey="yukon.web.modules.adminSetup.config.testEmail.email" path="to" size="40" maxlength="100"/>
        <tags:hidden path="from"/>
        <div><i:inline key="yukon.web.modules.adminSetup.config.testEmail.message" arguments="${email.getFrom()}"/></div>
    </tags:nameValueContainer2>
</form:form>

