<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="user" page="profile.${mode}">

<tags:setFormEditMode mode="${mode}"/>

<d:confirm on="#reset_all_preferences" nameKey="preferences.confirmResetAll"/>

<div class="column-12-12">
    <div class="column one">
    
        <%-- LEFT SIDE COLUMN --%>
        <form:form method="post" action="update" modelAttribute="userProfile">
            <cti:csrfToken/>
            <form:hidden path="userId"/>
            <tags:sectionContainer2 nameKey="userInfo">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".username">${fn:escapeXml(userProfile.username)}</tags:nameValue2>
                    <tags:inputNameValue nameKey=".firstname" path="contact.firstName" size="35" maxlength="120"/>
                    <tags:inputNameValue nameKey=".lastname" path="contact.lastName" size="35" maxlength="120"/>
                    <tags:inputNameValue nameKey=".email" path="contact.email" size="35" maxlength="130"/>
                    <cti:displayForPageEditModes modes="VIEW">
                        <tags:nameValue2 rowId="password_row" valueClass='${passwordWarning ? "warning" : ""}' nameKey=".profile.changePassword.lastChanged">
                            <cti:formatDate value="${passwordLastChangeTimestamp}" type="DATE"/>
                        </tags:nameValue2>
                    </cti:displayForPageEditModes>
                </tags:nameValueContainer2>
                <div>
                    <h4><i:inline key="yukon.web.modules.user.additionContactInfo.title"/></h4>
                    <div style="margin-left:3px;" class="js-display_empty<c:if test="${not empty userProfile.contact.otherNotifications}"> dn</c:if>"><i:inline key="yukon.web.defaults.na"/></div>
                    <tags:nameValueContainer2 id="contactNotifs">
                        <c:forEach var="contactNotif" items="${userProfile.contact.otherNotifications}" varStatus="row">
                            <c:set var="notifType" value="${contactNotif.contactNotificationType}"/>
                            <c:set var="isPhone" value='${notifType.isPhoneType() || notifType.isFaxType()}' />
                            <cti:displayForPageEditModes modes="EDIT">
                                <tr class="contactNotif">
                                    <td class="name">
                                        <tags:selectWithItems items="${notificationTypes}" path="contact.otherNotifications[${row.index}].contactNotificationType" inputClass="js-contactNotif-type"/>
                                    </td>
                                    <td class="value">
                                        <tags:input path="contact.otherNotifications[${row.index}].notificationValue" inputClass='js-contactNotif-val${isPhone ? " js-format-phone" : ""}'></tags:input>
                                    </td>
                                    <td class="actions">
                                        <cti:button renderMode="buttonImage" icon="icon-cross" classes="js-remove"/>
                                    </td>
                                </tr>
                            </cti:displayForPageEditModes>
                            <cti:displayForPageEditModes modes="VIEW">
                                <tags:nameValue2 nameKey="${contactNotif.contactNotificationType.formatKey}" rowClass="contactNotif">
                                    <c:if test="${isPhone}">
                                        <cti:formatPhoneNumber value="${contactNotif.notificationValue}"/>
                                    </c:if>
                                    <c:if test="${! isPhone}">${fn:escapeXml(contactNotif.notificationValue)}</c:if>
                                </tags:nameValue2>
                            </cti:displayForPageEditModes>
                        </c:forEach>
                    </tags:nameValueContainer2>
                    <cti:displayForPageEditModes modes="EDIT">
                        <div class="action-area">
                            <cti:button nameKey="add" icon="icon-add" id="btn_addContactInfo"/>
                        </div>
                    </cti:displayForPageEditModes>
                </div>
            </tags:sectionContainer2>
            
            <cti:displayForPageEditModes modes="EDIT">
                <div class="page-action-area">
                    <cti:button nameKey="save" name="update" type="submit" classes="primary action"/>
                    <cti:url var="cancelUrl" value="/user/profile"/>
                    <cti:button nameKey="cancel" name="cancel" href="${cancelUrl}"/>
                </div>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="VIEW">
                <div class="action-area">
                    <cti:url var="editUrl" value="/user/profile/edit"/>
                    <cti:button nameKey="edit" icon="icon-pencil" name="edit" href="${editUrl}"/>
                    <c:if test="${canResetPassword}">
                        <cti:button data-popup="#change-password-popup" icon="icon-key" nameKey="changePassword.button"/>
                    </c:if>
                </div>
            </cti:displayForPageEditModes>
        </form:form>
        
        <cti:displayForPageEditModes modes="VIEW">
            <c:if test="${canResetPassword}">
                <div id="change-password-popup" class="dn" 
                        data-dialog 
                        data-event="yukon:user:profile:password:save" 
                        data-title="<cti:msg2 key=".profile.changePassword"/>" 
                        data-ok-class="js-save-pw-btn"
                        data-width="750">
                    <div class="column-12-12">
                        <div class="column one">
                            <form id="change-password-form">
                                <input type="hidden" name="k" value="${changePwdKey}">
                                <input type="hidden" name="userId" value="${userProfile.userId}">
                                <input type="hidden" name="username" value="${fn:escapeXml(userProfile.username)}">
                                <input type="hidden" name="userGroupName" value="${fn:escapeXml(userGroupName)}">
                                <tags:nameValueContainer2>
                                    <tr><td class="name"><i:inline key="yukon.web.changelogin.oldPassword"/></td>
                                        <td class="value">
                                            <input type="password" maxlength="64" autocomplete="false" 
                                                name="oldPassword" class="js-current-password" 
                                                placeholder="<cti:msg2 key='yukon.web.changelogin.oldPassword'/>">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="name"><i:inline key="yukon.web.changelogin.newPassword"/></td>
                                        <td class="value">
                                            <input type="password" maxlength="64" autocomplete="false" 
                                                name="password1" class="js-new-password" 
                                                placeholder="<cti:msg2 key='yukon.web.changelogin.newPassword'/>">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="name"><i:inline key="yukon.web.changelogin.confirm"/></td>
                                        <td class="value">
                                            <input type="password" maxlength="64" autocomplete="false" 
                                                name="password2" class="js-confirm-password" 
                                                placeholder="<cti:msg2 key='yukon.web.changelogin.confirm'/>">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td></td>
                                        <td>
                                            <div class="js-password-mismatch error">
                                                <i:inline key="yukon.web.modules.passwordPolicy.noMatch.description"/>
                                            </div>
                                            <div class="password_errors dn"></div>
                                        </td>
                                    </tr>
                                </tags:nameValueContainer2>
                            </form>
                        </div>
                        <div class="column two nogutter">
                            <tags:passwordHelper passwordPolicy="${passwordPolicy}" userId="${userProfile.userId}"/>
                        </div>
                    </div>
                </div>
            </c:if>
            
            <%-- PREFERENCES SECTION --%>
            <tags:sectionContainer2 nameKey="preferences">
                <table id="tbl_preferences" class="full-width with-form-controls">
                    <tbody>
                        
                        <tr data-type="${objPrefName}" class="all-preferences-row">
                            <td class="name"><i:inline key=".preferences.default.all"/></td>
                            </td>
                            <td class="value">
                                <cti:button classes="js-pref-default" id="reset_all_preferences" icon="icon-arrow-swap" nameKey="preferences.default.all.button"/>
                            </td>
                        </tr>
                        
                        <c:forEach var="objPrefName" items="${allPreferenceNames}" >
                            <tr data-type="${objPrefName}">
                                <td class="name"><i:inline key="${objPrefName.formatKey}"/></td>
                                
                                <td class="value selection_group">
                                    
                                    <c:set var="prefName" value="${objPrefName.toString()}"/>
                                    <c:set var="defaultVal" value="${objPrefName.defaultValue}"/>
                                    <c:set var="prefValue" value="${userPreferenceMap.get(prefName) != null ? userPreferenceMap.get(prefName).value : objPrefName.defaultValue}"/>
                                    <c:set var="prefOptions" value="${objPrefName.valueType.optionList}"/>
                                    <c:set var="prefDisplayOptMap" value="${userPreferencesNameToDisplayOptions.get(prefName)}"/>
                                    
                                    <cti:button classes="js-pref-default" data-value="${defaultVal}" icon="icon-arrow-swap" renderMode="buttonImage"/>
                                    
                                    <div class="button-group fl">
                                        <c:forEach var="prefOption" items="${prefOptions}" varStatus="stat">
                                        
                                            <cti:msg2 var="prefText" key="${prefOption.message}"/>
                                            <c:set var="iconOnly" value='${prefDisplayOptMap != null && (prefDisplayOptMap.containsKey("iconONLY"))}' />
                                            
                                            <c:choose>
                                                <c:when test="${prefOption.value.equals(prefValue)}"><c:set var="classes" value="on"/></c:when>
                                                <c:otherwise><c:set var="classes" value=""/></c:otherwise>
                                            </c:choose>
                                            <c:choose>
                                                <c:when test="${iconOnly}">
                                                    <c:set var="title" value="${prefText}"/>
                                                    <c:set var="renderMode" value="buttonImage"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="title" value=""/>
                                                    <c:set var="renderMode" value="button"/>
                                                </c:otherwise>
                                            </c:choose>
                                            <c:choose>
                                                <c:when test="${iconOnly}">
                                                    <c:set var="optMap" value="${prefDisplayOptMap.get('iconONLY')}"/>
                                                    <c:set var="icon" value="${optMap.get(prefOption.value)}"/>
                                                </c:when>
                                                <c:otherwise><c:set var="icon" value=""/></c:otherwise>
                                            </c:choose>
                                            <cti:button classes="${classes}" data-value="${prefOption.value}" title="${title}" renderMode="${renderMode}" label="${prefText}" icon="${icon}"/>
                                        </c:forEach>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </tags:sectionContainer2>
        </cti:displayForPageEditModes>
    </div>
    
    <%-- RIGHT SIDE COLUMN --%>
    <div class="column two nogutter" id="column_two">
    <cti:displayForPageEditModes modes="VIEW">
    
    <tags:sectionContainer2 nameKey="groups" arguments="${userGroupName}">
        <c:choose>
            <c:when test="${empty categoryRoleMap}">
                <i:inline key="yukon.web.defaults.na"/>
            </c:when>
            <c:otherwise>
                <div class="wsnw">
                    <c:forEach var="category" items="${categoryRoleMap}">
                        <ul class="grouped-list">
                            <li><span class="group"><cti:formatObject value="${category.key}"/></span>
                                <ul class="groupedItem">
                                    <c:forEach var="roleAndGroup" items="${category.value}">
                                        <li><cti:formatObject value="${roleAndGroup.role}"/></li>
                                    </c:forEach>
                                </ul>
                            </li>
                        </ul>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </tags:sectionContainer2>
    
    </cti:displayForPageEditModes>
    </div>
</div>

<cti:includeScript link="/JavaScript/yukon.user.profile.js"/>

<%-- This template row is copied by Javascript when user adds Contact Notification Infos while editing. --%>
<cti:displayForPageEditModes modes="EDIT">
<table class="dn"><tbody>
    <tr id="template_contactNotif" class="contactNotif">
        <td class="name">
            <cti:msg2 key="yukon.web.defaults.selector.selectOne" var="txt_selectOne"/>
            <tags:simpleSelect name="contact.otherNotifications" items="${notificationTypes}" itemLabelKey="formatKey" defaultItemValue="-1" defaultItemLabel="${txt_selectOne}" cssClass="js-contactNotif-type"/>
        </td>
        <td class="value">
            <input type="text" name="contact.otherNotifications" class="js-contactNotif-val">
        </td>
        <td class="actions">
            <cti:button renderMode="buttonImage" icon="icon-cross" classes="js-remove"/>
        </td>
    </tr>
</tbody></table>
</cti:displayForPageEditModes>

</cti:standardPage>