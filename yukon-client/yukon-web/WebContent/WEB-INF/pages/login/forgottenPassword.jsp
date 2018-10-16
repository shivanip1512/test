<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="com.cannontech.common.version.VersionTools" %>


<!DOCTYPE html>
<c:set var="module" value="login" />
<c:set var="page" value="forgottenPassword" />

<tags:noAuthPage module="login" page="forgottenPassword">
<section class="yukon-content" style="min-height: 420px;">
    <div class="column-24 clear">
        <div class="one column nogutter">
            <div class="page-title-bar">
                <h1><i:inline key=".pageName"/></h1>
            </div>
            <cti:flashScopeMessages/>
            <noscript>
                <div class="page-error">
                    <cti:msg2 key="yukon.web.error.noJs"/>
                </div>
            </noscript>
            <form:form action="forgottenPassword" modelAttribute="forgottenPassword" method="POST">
                <cti:csrfToken/>
                <div class="stacked">
                    <i:inline key=".forgottenPasswordMessage"/>
                </div>
                
                <div class="stacked">
                    <h4><i:inline key=".forgottenPasswordFields"/></h4>
                    <form:input autocomplete="off" path="forgottenPasswordField" autofocus="autofocus" size="50"/>
                </div>
                
                <tags:captcha captchaSiteKey="${captchaSiteKey}" captchaTheme="clean" captchaEnabled="${captchaEnabled}" locale="${locale}"/>
                <div class="page-action-area">
                    <cti:button type="submit" nameKey="resetPassword" name="resetPassword" classes="js-disable-after-click primary action"/>
                    <cti:url value="/login.jsp" var="loginPage"/>
                    <cti:button href="${loginPage}" nameKey="cancel"/>
                </div>
            </form:form>
        </div>
    </div>
</section>
</tags:noAuthPage>