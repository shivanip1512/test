<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dev">
<c:set var="method" value="${create ? 'post' : 'put'}"/>
<form:form modelAttribute="person" action="person" method="${method}" cssClass="js-new-person-form">
    <form:hidden path="id"/>
    <tags:nameValueContainer2>
        <c:if test="${!create}">
            <tags:setFormEditMode mode="VIEW"/>
            <form:hidden path="name"/>
        </c:if>
        <tags:inputNameValue nameKey=".name" path="name"/>
        <tags:setFormEditMode mode="EDIT"/>
        <tags:inputNameValue nameKey=".age" path="age"/>
        <tags:inputNameValue nameKey=".email" path="email"/>
        <tags:nameValue2 excludeColon="true">
            <label><tags:switch path="spam"/>Send me Spam?</label>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</form:form>
</cti:msgScope>