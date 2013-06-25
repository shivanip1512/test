<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
        <title><cti:msg2 key="yukon.web.login.pageTitle"/></title>
        
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/normalize.css"/>">
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/layout.css"/>">
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/yukon.css"/>">
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/functional-overrides.css"/>">
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/buttons/css/buttons.css"/>" >
            
        <cti:includeScript link="JQUERY" force="true"/>
        <cti:includeScript link="JQUERY_PLACEHOLDER" force="true"/>
        <script type="text/javascript">
        jQuery(function(){
            jQuery.placeholder();
        });
        </script>
    </head>

    <body onLoad="document.forms.form1.USERNAME.focus()">
        <div id="page">
            <header class="yukon-header">
                <div class="outer" role="banner">
                    <div class="inner"></div>
                </div>
                <div class="nav">
                    <div class="navOut">
                        <nav role="navigation">
                            <ul>
                                <li class="logo" style="background: transparent;">
                                    <a href="/dashboard">
                                        <img src="/WebConfig/yukon/layout/YukonBW2.png" alt="Yukon Home">
                                    </a>
                                </li>
                            </ul>
                        </nav>
                    </div>
                </div>
            </header>
            <section id="content" style="min-height: 420px;">
                <div class="login-content">
                    <div class="column_12_12 clear">
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
                                    <div class="clearfix stacked"><input type="text" id="login_email" name="USERNAME" class="fl" placeholder="<cti:msg2 key="yukon.web.login.username"/>"></div>
                                    <div class="clearfix">
                                        <input type="password" id="login_password" name="PASSWORD" autocomplete="off" placeholder="<cti:msg2 key="yukon.web.login.password"/>" class="fl">
                                        <a href="/login/forgottenPassword">
                                            <button id="forgot-btn" class="fl"><span class="label"><cti:msg2 key="yukon.web.login.forgot"/></span></button>
                                        </a>
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
			<footer id="yukon-footer" class="yukon-footer">
				<div class="utility">
					<div class="footerNav">
						<nav>
							<ul>
								<li><a href="/billing">Help</a></li>
								<li><a href="/billing">Contact Us</a></li>
							</ul>
						</nav>
					</div>
				</div>
				<div class="footer">
					<div class="footerNav clearfix">
						<div class="wrapper">
							<div class="legal">
								<p class="copyright">
									<cti:msg2 key="yukon.web.layout.standard.copyrightFull" />
								</p>
							</div>
						</div>
					</div>
				</div>
			</footer>
        </div>
    </body>
</html>