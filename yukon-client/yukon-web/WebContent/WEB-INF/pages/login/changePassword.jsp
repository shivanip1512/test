<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<tags:noAuthPage module="login" page="changePassword">
    <section id="content" style="min-height: 420px;">
    <div class="column-24 clear">
        <div class="one column nogutter">
            <div class="page-title-bar">
                <h1><i:inline key=".pageName"/></h1>
            </div>    
            <form:form commandName="loginBackingBean" action="changePassword">
                <cti:csrfToken/>
                <div class="column-12-12">
                    <div class="column one">
                        <input type="hidden" name="k" value="${k}" >
                        <form:hidden path="userId" />
                        <form:hidden path="username" />
                        <form:hidden path="userGroupName" />
        
                        <tags:nameValueContainer2 id="passwordFields">
                            <tags:nameValue2 nameKey=".username">
                                ${fn:escapeXml(loginBackingBean.username)}
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".newPassword">
                                <tags:password path="password1" cssClass="password_editor_field new js-check_password" autocomplete="false" maxlength="64" />
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".confirmPassword">
                                <tags:password path="password2" cssClass="password_editor_field confirm" autocomplete="false" maxlength="64" />
                            </tags:nameValue2>
                            <tr>
                                <td>
                                </td>
                                <td>
                                <span class="no_match error"><i:inline key="yukon.web.modules.passwordPolicy.noMatch.description"/></span>
                                </td>
                            </tr>
                            <tags:nameValue2 nameKey="defaults.blank" excludeColon="true">
                                <br>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </div>
                    <div class="column two nogutter">
                        <tags:passwordHelper passwordPolicy="${passwordPolicy}" />
                    </div>
                </div>
                
                <div class="page-action-area">
                    <cti:button nameKey="changePassword" type="submit" name="changePassword" classes="primary action unlock" />
                    <cti:url value="/login.jsp" var="loginPage"/>
                    <cti:button href="${loginPage}" nameKey="cancel"/>
                </div>
            </form:form>
        </div>
    </div>
    </section>
</tags:noAuthPage>
                    