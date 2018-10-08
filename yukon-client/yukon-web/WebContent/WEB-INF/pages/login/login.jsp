<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:noAuthPage module="login" page="login">
    <section class="yukon-content" style="min-height: 420px;">
        <div class="login-content">
            <div class="column-12-12 clearfix">
                <div class="one column">
                    <div><h1 class="tagline"><i:inline key="yukon.web.login.tagline"/></h1></div>
                </div>
                <div class="two column nogutter">
                    <div class="faded-round-box login-form">
                        <cti:flashScopeMessages/>
                        <c:if test="${!empty param.failed}">
                            <div class="login-error-msg">
                                <cti:msg2 key="yukon.web.login.invalidLogin"/>
                            </div>
                        </c:if>
                        <c:if test="${!empty param.invalidCsrfToken}">
                            <div class="login-error-msg">
                                <cti:msg2 key="yukon.web.login.invalidCsrfToken"/>
                            </div>
                        </c:if>
                        <c:if test="${!empty param.retrySeconds}">
                            <div class="login-error-msg">
                                <cti:msg2 key="yukon.web.login.retry" argument="${param.retrySeconds}"/>
                            </div>
                        </c:if>
                        <c:if test="${!empty param.invalid}">
                            <div class="login-error-msg">
                                <cti:msg2 key="yukon.web.login.invalidUrlAccess"/>
                            </div>
                        </c:if>
                        <form name="form1" method="post" action="<cti:url value="/servlet/LoginController/login"/>">
                            <cti:csrfToken/>
                            <div class="clearfix stacked"><input type="text" id="login_email" name="USERNAME" class="fl js-focus" placeholder="<cti:msg2 key="yukon.web.login.username"/>"></div>
                            <div class="clearfix">
                                <input type="password" id="login_password" name="PASSWORD" autocomplete="off" placeholder="<cti:msg2 key="yukon.web.login.password"/>" class="fl">
                                <cti:checkGlobalSetting setting="ENABLE_PASSWORD_RECOVERY">
                                    <cti:msg2 key="yukon.web.login.forgot" var="forgot"/>
                                    <cti:button id="forgot-btn" href="login/forgottenPassword" label="${forgot}"/>
                                </cti:checkGlobalSetting>
                            </div>
                            <div class="action-area">
                                <label class="fl remember-me"><input type="checkbox" id="remember_me" name="rememberme"><cti:msg2 key="yukon.web.login.rememberMe"/></label>
                                <cti:msg2 key="yukon.web.login.login" var="login"/>
                                <cti:button type="submit" name="login" classes="action primary" label="${login}"/>
                                <input type="hidden" name="REDIRECTED_FROM" value="${fn:escapeXml(param.REDIRECTED_FROM)}">
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </section>
</tags:noAuthPage>