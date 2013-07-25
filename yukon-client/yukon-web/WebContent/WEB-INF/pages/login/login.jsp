<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:noAuthPage module="login" page="login">
    <section id="content" style="min-height: 420px;">
        <div class="login-content">
            <div class="column_12_12">
                <div class="one column">
                    <div class="faded-round-box" style="visibility: hidden;"></div>
                    <div><h1 class="tagline">Optimizing Energy Delivery</h1></div>
                </div>
                <div class="two column nogutter">
                    <div class="faded-round-box login-form">
                        <cti:flashScopeMessages/>
                        <c:if test="${!empty param.failed}">
                            <div class="loginErrorMsg">
                                <cti:msg2 key="yukon.web.login.invalidLogin"/>
                            </div>
                        </c:if>
                        <c:if test="${!empty param.retrySeconds}">
                            <div class="loginErrorMsg">
                                <cti:msg2 key="yukon.web.login.retry" argument="${param.retrySeconds}"/>
                            </div>
                        </c:if>
                        <c:if test="${!empty param.invalid}">
                            <div class="loginErrorMsg">
                                <cti:msg2 key="yukon.web.login.invalidUrlAccess"/>
                            </div>
                        </c:if>
                        <form name="form1" method="post" action="<cti:url value="/servlet/LoginController"/>">
                            <div class="clearfix stacked"><input type="text" id="login_email" name="USERNAME" class="fl f-focus" placeholder="<cti:msg2 key="yukon.web.login.username"/>"></div>
                            <div class="clearfix">
                                <input type="password" id="login_password" name="PASSWORD" autocomplete="off" placeholder="<cti:msg2 key="yukon.web.login.password"/>" class="fl">
                                <button type="button" id="forgot-btn" class="fl" data-href="/login/forgottenPassword"><span class="label"><cti:msg2 key="yukon.web.login.forgot"/></span></button>
                            </div>
                            <div class="actionArea">
                                <label class="fl remember-me"><input type="checkbox" id="remember_me"><cti:msg2 key="yukon.web.login.rememberMe"/></label>
                                <button type="submit" name="login" class="action primary">
                                    <span class="label"><cti:msg2 key="yukon.web.login.login"/></span>
                                </button>
                                <input type="hidden" name="REDIRECTED_FROM" value="${fn:escapeXml(param.REDIRECTED_FROM)}">
                                <input type="hidden" name="ACTION" value="LOGIN">
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </section>
</tags:noAuthPage>
            