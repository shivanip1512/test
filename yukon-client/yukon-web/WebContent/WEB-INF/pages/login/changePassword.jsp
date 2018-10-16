<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:noAuthPage module="login" page="changePassword">
    <section class="yukon-content" style="min-height: 420px;">
    <div class="column-24 clear">
        <div class="one column nogutter">
            <div class="page-title-bar">
                <h1><i:inline key=".pageName"/></h1>
            </div>
            <div id="change-password-popup-alerts">
                <c:if test="${ minPasswordAgeNotMet }">
                    <tags:alertBox key="yukon.web.modules.passwordPolicyError.MIN_PASSWORD_AGE_NOT_MET"/>
                </c:if>
            </div>
            <form:form id="change-password-form" modelAttribute="login" action="change-password">
                <cti:csrfToken/>
                <div class="column-12-12">
                    <div class="column one">
                        <input type="hidden" name="k" value="${k}" >
                        <form:hidden path="userId"/>
                        <form:hidden path="username"/>
                        <form:hidden path="userGroupName"/>
                        
                        <tags:nameValueContainer2 id="passwordFields">
                            <tags:nameValue2 nameKey=".username">
                                ${fn:escapeXml(login.username)}
                            </tags:nameValue2>
                                <tags:nameValue2 nameKey=".newPassword">
                                    <tags:password path="password1" cssClass="js-new-password" autocomplete="off" />
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".confirmPassword">
                                    <tags:password path="password2" cssClass="js-confirm-password" autocomplete="off" />
                                </tags:nameValue2>
                                <tr>
                                <td></td>
                                <td>
                                    <span class="js-password-mismatch error"><i:inline key="yukon.web.modules.passwordPolicy.noMatch.description"/></span>
                                </td>
                            </tr>
                        </tags:nameValueContainer2>
                    </div>
                    <div class="column two nogutter">
                        <tags:passwordHelper passwordPolicy="${passwordPolicy}" userId="${login.userId}"/>
                    </div>
                </div>
                
                <div class="page-action-area">
                    <cti:button nameKey="changePassword" type="submit" name="changePassword" classes="primary action js-save-pw-btn"/>
                    <cti:url value="/login.jsp" var="loginPage"/>
                    <cti:button href="${loginPage}" nameKey="cancel"/>
                </div>
            </form:form>
        </div>
    </div>
    </section>
</tags:noAuthPage>