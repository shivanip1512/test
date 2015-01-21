<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dev.dialogs">
<form:form commandName="person" action="person" method="post" cssClass="js-new-person-form">
    <tags:nameValueContainer2 tableClass="with-form-controls">
        <tags:inputNameValue nameKey=".name" path="name"/>
        <tags:inputNameValue nameKey=".age" path="age"/>
        <tags:inputNameValue nameKey=".email" path="email"/>
        <tags:nameValue2 excludeColon="true">
            <label><tags:switch path="spam"/>Send me Spam?</label>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</form:form>
</cti:msgScope>