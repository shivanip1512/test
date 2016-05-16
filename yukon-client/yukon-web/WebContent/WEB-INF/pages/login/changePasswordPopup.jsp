<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="changelogin">
<div class="column-12-12">
    <div class="column one">
        <cti:url var="url" value="/login/authenticated/change-password"/>
        <form:form id="change-password-form" action="${url}" method="post" commandName="login">
            <cti:csrfToken />
            <form:hidden path="userId" cssClass="js-user-id"/>
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".oldPassword">
                    <tags:input path="oldPassword" password="true" inputClass="js-old-password" maxlength="64" 
                            autocomplete="off"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".newPassword">
                    <tags:input path="password1" password="true" inputClass="js-new-password" maxlength="64" 
                            autocomplete="off"/>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey="yukon.web.modules.adminSetup.auth.user.confirmPassword">
                    <tags:input path="password2" password="true" inputClass="js-confirm-password" maxlength="64" 
                            autocomplete="off" placeholder="${placeholder}"/>
                </tags:nameValue2>
                <tr>
                    <td></td>
                    <td>
                        <div class="js-password-mismatch error dn">
                            <i:inline key="modules.passwordPolicy.noMatch.description"/>
                        </div>
                    </td>
                </tr>
            </tags:nameValueContainer2>
        </form:form>
    </div>
    <div class="column two nogutter">
        <tags:passwordHelper passwordPolicy="${passwordPolicy}" userId="${login.userId}"/>
    </div>
</div>
</cti:msgScope>