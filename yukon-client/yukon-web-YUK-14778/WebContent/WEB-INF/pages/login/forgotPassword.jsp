<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:verifyGlobalRolesAndProperties value="ENABLE_PASSWORD_RECOVERY" />

<tags:noAuthPage module="login" page="forgotPassword">
    <section class="yukon-content" style="min-height: 420px;">
    <div class="column-24 clear">
        <div class="one column nogutter">
            <div class="page-title-bar">
                <h1><i:inline key=".pageName"/></h1>
            </div>
            <cti:msg2 var="boxTitle" key=".boxTitle" />
            <tags:boxContainer title="${boxTitle}">
                <div class="loginIntroText stacked">
                    <i:inline key=".intro" />
                </div>
                <c:if test="${!empty param.failedMsg}">
                    <div class="stacked error">
                        <i:inline key=".${param.failedMsg}" />
                    </div>
                </c:if>
                <form name="form1" method="post" action="<cti:url value="/servlet/StarsPWordRequest"/>">
                    <cti:csrfToken/>
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".username">
                            <input autocomplete="off" id="USERNAME" name="USERNAME">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".email">
                            <input name="EMAIL">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".firstName">
                            <input name="FIRST_NAME">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".lastName">
                            <input name="LAST_NAME">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".account">
                            <input name="ACCOUNT_NUM">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".provider">
                            <input name="ENERGY_COMPANY">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".notes">
                            <textarea name="NOTES" cols="30" rows="3"></textarea>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                    <div class="stacked page-action-area">
                        <cti:button nameKey="submit" type="submit" classes="primary action"/>
                        <cti:url value="/login.jsp" var="loginPage"/>
                        <cti:button href="${loginPage}" nameKey="cancel"/>
                    </div>
                    
                    <c:if test="${!empty param.success}">
                        <div class="success">
                            <i:inline key=".success" />
                        </div>
                    </c:if>
                </form>
            </tags:boxContainer>
        </div>
    </div>
    </section>
</tags:noAuthPage>