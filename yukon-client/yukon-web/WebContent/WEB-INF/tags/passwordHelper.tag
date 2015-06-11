<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="passwordPolicy" required="true" type="com.cannontech.core.authentication.model.PasswordPolicy" %>
<%@ attribute name="userId" required="true" description="The user id for this password change attempt." %>

<%@ attribute name="confirmPassword" description="CSS selector for the confirm password input. Default: '.js-confirm-password'." %>
<%@ attribute name="newPassword" description="CSS selector for the new password input. Default: '.js-new-password'." %>
<%@ attribute name="saveButton" description="CSS selector for the save button. Default: 'button.unlock'." %>

<cti:default var="newPassword" value=".js-new-password"/>
<cti:default var="confirmPassword" value=".js-confirm-password"/>
<cti:default var="saveButton" value=".js-save-pw-btn"/>

<c:set var="keyBase" value="yukon.web.modules.passwordPolicy."/>

<style type="text/css">
.password-manager .rule-list, 
.password-manager .rule-list .no-fail {
    list-style: none;
    padding-left: 16px;
}
.password-manager .rule-list { padding-left: 0; } 
</style>

<div class="password-manager" 
    data-min-length="${passwordPolicy.minPasswordLength}"
    data-new-password="${newPassword}"
    data-confirm-password="${confirmPassword}"
    data-save-button="${saveButton}"
    data-user-id="${userId}">
    
    <h4><i:inline key="${keyBase}requirements"/></h4>
    <ul class="rule-list">
        <li class="INVALID_PASSWORD_LENGTH">
            <cti:icon icon="icon-tick vh"/>
            <span><i:inline key="${keyBase}length.description" 
                arguments="${passwordPolicy.minPasswordLength}"/>
            </span>
        </li>
        <c:if test="${passwordPolicy.passwordHistory > 0}">
            <li class="PASSWORD_USED_TOO_RECENTLY">
                <cti:icon icon="icon-tick vh"/>
                <span><i:inline key="${keyBase}history.description" 
                    arguments="${passwordPolicy.passwordHistory}"/>
                </span>
            </li>
        </c:if>
        <li class="PASSWORD_DOES_NOT_MEET_POLICY_QUALITY">
            <cti:icon icon="icon-tick vh"/>
            <span><i:inline key="${keyBase}quality.description" 
                arguments="${passwordPolicy.passwordQualityCheck}"/>
            </span>
        </li>
        <li>
            <ul class="no-fail">
                <c:forEach items="${passwordPolicy.policyRules}" var="rule">
                    <li class="${rule}">
                        <cti:icon icon="icon-tick vh"/>
                        <span><i:inline key="${rule.formatKey}.description"/></span>
                    </li>
                </c:forEach>
            </ul>
        </li>
    </ul>
</div>

<cti:includeScript link="/resources/js/tags/yukon.ui.passwords.js" force="true"/>