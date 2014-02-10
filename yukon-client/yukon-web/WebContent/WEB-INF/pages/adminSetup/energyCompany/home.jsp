<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="home">

<script type="text/javascript">

jQuery(document).on('click', 'button.loginButton',function() {

        var parentLoginId = jQuery(this).attr('name');
        jQuery('#loginAsUserId').val(parentLoginId); 
        jQuery('#parentLoginForm').submit();

});

</script>
    <form action="parentLogin" method="post" id="parentLoginForm">
        <cti:csrfToken/>
        <input type="hidden" name="loginAsUserId" id="loginAsUserId">
    </form>
    <div class="column-8-16">
        <div class="column one nogutter">
            <tags:boxContainer2 nameKey="companiesContainer">
                
                <div class="membersContainer">
                    <table class="energyCompaniesTable row-highlighting">
                        <c:forEach items="${companies}" var="company" varStatus="status">
                            <tr>
                                <td>
                                    <a href="/adminSetup/energyCompany/general/view?ecId=${company.energyCompanyId}">
                                        <spring:escapeBody htmlEscape="true">${company.name}</spring:escapeBody>
                                    </a>
                                </td>
                                <td class="buttonColumn">
                                    <c:if test="${canManageMembers && company.parent != null}">
                                        <c:set var="parentLoginId" value="${parentLogins[company.energyCompanyId]}"/>
                                        <c:if test="${not empty parentLoginId && parentLoginId != loggedInUserId && previousUserId != parentLoginId}">
                                            <cti:button nameKey="login" classes="loginButton" name="${parentLoginId}"/>
                                        </c:if>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
                
                <cti:checkRolesAndProperties value="ADMIN_SUPER_USER">
                    <div class="action-area">
                        <form action="/adminSetup/energyCompany/new">
                            <cti:button nameKey="create" icon="icon-plus-green" type="submit" name="create"/>
                        </form>
                    </div>
                </cti:checkRolesAndProperties>
            
            </tags:boxContainer2>
        </div>
    </div>

</cti:standardPage>