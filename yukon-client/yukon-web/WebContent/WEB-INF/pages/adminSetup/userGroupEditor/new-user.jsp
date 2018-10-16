<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.adminSetup.auth.user,yukon.common">

<form:form id="new-user-form" modelAttribute="user" method="post">
    <cti:csrfToken/>
    
    <tags:nameValueContainer2 tableClass="with-form-controls">
        
        <tags:nameValue2 nameKey=".username">
            <s:bind path="username">
                <c:set var="clazz" value="js-username"/>
                <c:if test="${status.error}">
                    <c:set var="clazz" value="${clazz} error"/>
                </c:if>
                <form:input path="username" cssClass="${clazz}" maxlength="64"/>
                <cti:msg2 var="available" key=".available"/>
                <cti:msg2 var="unavailable" key=".unavailable"/>
                <span class="js-username-available" 
                    data-available="${available}" data-unavailable="${unavailable}"></span>
                <c:if test="${status.error}">
                    <div><form:errors path="username" cssClass="error"/></div>
                 </c:if>
            </s:bind>
        </tags:nameValue2>
        
        <tags:selectNameValue inputClass="js-auth-category" path="authCategory" nameKey=".authentication" 
            items="${categories}"/>
        
        <c:set var="clazz" value="${!showPw ? 'dn' : ''}"/>
        <tags:nameValue2 rowClass="js-pw-row ${clazz}" excludeColon="true">
            <cti:msg2 var="password" key=".password.placeholder"/>
            <tags:input inputClass="js-pw" path="password.password" password="true" 
                placeholder="${password}" maxlength="64" autocomplete="off"/>
        </tags:nameValue2>
        
        <tags:nameValue2 rowClass="js-pw-row ${clazz}" excludeColon="true">
            <cti:msg2 var="confirm" key=".password.confirm.placeholder"/>
            <tags:input inputClass="js-pw-confirm" path="password.confirmPassword" password="true" 
                placeholder="${confirm}" maxlength="64" autocomplete="off"/>
        </tags:nameValue2>
        
        <cti:msg2 var="none" key="yukon.common.none.choice"/>
        <tags:selectNameValue nameKey=".userGroup" items="${userGroups}" itemValue="userGroupId" 
            itemLabel="userGroupName" path="userGroupId"
            defaultItemLabel="${none}" defaultItemValue=""/>
        <tags:nameValue2 nameKey="yukon.common.energyCompany">
            <tags:selectWithItems path="energyCompanyId" items="${companies}"
                itemLabel="name" itemValue="id" defaultItemLabel="${none}" defaultItemValue=""/>
        </tags:nameValue2>
        
        <tags:nameValue2 excludeColon="true">
            <tags:switchButton path="enabled" onNameKey=".enabled" offNameKey=".disabled" offClasses="M0"/>
        </tags:nameValue2>
        
    </tags:nameValueContainer2>
    
</form:form>

</cti:msgScope>