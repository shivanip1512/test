<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>


<cti:msgScope paths="changelogin">

    <div class="column-12-12">
        <div class="column one">
            <cti:url var="url" value="/admin/users/${userId}/change-password" />
            <form:form id="change-password-form" commandName="password" action="${url}" method="post">
                <cti:csrfToken />
                <input type="hidden" name="userId" value="${userId}">
                <tags:nameValueContainer2>
                    <c:if test="${!otherUser}">
                        <tags:nameValue2 nameKey=".oldPassword">
                            <tags:password path="oldPassword" autocomplete="off" />
                        </tags:nameValue2>
                    </c:if>
                    <tags:nameValue2 nameKey=".newPassword">
                        <tags:password path="password" cssClass="js-new-password" autocomplete="off" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".confirmPassword">
                        <tags:password path="confirmPassword" cssClass="js-confirm-password" autocomplete="off" />
                    </tags:nameValue2>
                    <tags:nameValue2 excludeColon="true">
                        <div class="js-password-mismatch error">
                            <i:inline key="yukon.web.modules.passwordPolicy.noMatch.description" />
                        </div>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </form:form>
        </div>
        <div class="column two nogutter">
            <tags:passwordHelper passwordPolicy="${passwordPolicy}" userId="${userId}" saveButton=".js-save-pw-btn" />
        </div>
    </div>
</cti:msgScope>
