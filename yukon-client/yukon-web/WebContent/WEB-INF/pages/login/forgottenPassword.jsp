<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="com.cannontech.common.version.VersionTools" %>

<%@ page import="net.tanesha.recaptcha.ReCaptcha" %>
<%@ page import="net.tanesha.recaptcha.ReCaptchaFactory" %>

<!DOCTYPE html>
<c:set var="module" value="login" />
<c:set var="page" value="forgottenPassword" />

<tags:noAuthPage module="login" page="forgottenPassword">
<section id="content" style="min-height: 420px;">
    <div class="column_24 clear">
        <div class="one column nogutter">
            <div class="page-title-bar">
                <h1><i:inline key=".pageName"/></h1>
            </div>
            <cti:flashScopeMessages/>
            <noscript>
                <div class="page_error">
                    <cti:msg2 key="yukon.web.error.noJs"/>
                </div>
            </noscript>
            <form:form action="forgottenPassword" commandName="forgottenPassword" method="POST">
                <div class="stacked">
                    <i:inline key=".forgottenPasswordMessage"/>
                </div>
                
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".forgottenPasswordFields"></tags:nameValue2>
                </tags:nameValueContainer2>
                <form:input path="forgottenPasswordField" autofocus="autofocus" size="50"/>
                
                <tags:captcha captchaPublicKey="${captchaPublicKey}" captchaTheme="clean" captchaEnabled="${captchaEnabled}" locale="${locale}"/>
                <div class="pageActionArea">
                    <cti:button type="submit" nameKey="resetPassword" name="resetPassword" classes="f-disableAfterClick primary action"/>
                    <cti:url value="/login.jsp" var="loginPage"/>
                    <cti:button href="${loginPage}" nameKey="cancel"/>
                </div>
            </form:form>
        </div>
    </div>
</section>
</tags:noAuthPage>