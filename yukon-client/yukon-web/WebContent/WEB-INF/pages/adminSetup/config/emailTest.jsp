<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<div><i:inline key="yukon.web.modules.adminSetup.config.testEmail.message" arguments="${email.getFrom()}"/></div>

<form:form  class="js-no-submit-on-enter" id="adminSetup-testEmail-form" modelAttribute="email" action="emailTest" method="POST">  
    <tags:nameValueContainer2 tableClass="with-form-controls di">    
        <cti:msg key="yukon.web.modules.adminSetup.config.testEmail.tooltip" var="tooltip"/>
        <tags:inputNameValue nameKey="yukon.web.modules.adminSetup.config.testEmail.email" path="to" size="40" maxlength="100" title="${tooltip}"/>
        <tags:hidden path="from"/>
    </tags:nameValueContainer2>
</form:form>

