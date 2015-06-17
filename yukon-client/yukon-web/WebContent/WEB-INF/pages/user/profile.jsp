<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="user" page="profile.${mode}">

<tags:setFormEditMode mode="${mode}"/>

<d:confirm on="#restore-to-defaults" nameKey="preferences.confirmResetAll"/>

<div class="column-12-12">
    <div class="column one">
    
        <%-- LEFT SIDE COLUMN --%>
        <form:form method="post" action="update" modelAttribute="userProfile">
            <cti:csrfToken/>
            <form:hidden path="userId" id="user-id"/>
            <tags:sectionContainer2 nameKey="userInfo">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".username">${fn:escapeXml(userProfile.username)}</tags:nameValue2>
                    <tags:inputNameValue nameKey=".firstname" path="contact.firstName" size="35" maxlength="120"/>
                    <tags:inputNameValue nameKey=".lastname" path="contact.lastName" size="35" maxlength="120"/>
                    <tags:inputNameValue nameKey=".email" path="contact.email" size="35" maxlength="130"/>
                    <cti:displayForPageEditModes modes="VIEW">
                        <tags:nameValue2 rowId="password-changed-row" valueClass='${passwordWarning ? "warning" : ""}' 
                                nameKey=".profile.changePassword.lastChanged">
                            <cti:formatDate value="${passwordLastChangeTimestamp}" type="DATE"/>
                        </tags:nameValue2>
                    </cti:displayForPageEditModes>
                </tags:nameValueContainer2>
                <div>
                    <h4><i:inline key="modules.user.additionContactInfo.title"/></h4>
                    <c:set var="classes" value="${not empty userProfile.contact.otherNotifications ? 'dn' : ''}"/>
                    <div class="empty-list js-no-other-notifs ${classes}"><i:inline key="yukon.common.na"/></div>
                    <tags:nameValueContainer2 id="contact-notif-table">
                        <c:forEach var="contactNotif" items="${userProfile.contact.otherNotifications}" varStatus="row">
                            <c:set var="notifType" value="${contactNotif.contactNotificationType}"/>
                            <c:set var="isPhone" value='${notifType.isPhoneType() || notifType.isFaxType()}' />
                            <cti:displayForPageEditModes modes="EDIT">
                                <tr>
                                    <td class="name">
                                        <tags:selectWithItems items="${notificationTypes}" 
                                            path="contact.otherNotifications[${row.index}].contactNotificationType" 
                                            inputClass="js-notif-type"/>
                                    </td>
                                    <td class="value">
                                        <tags:input path="contact.otherNotifications[${row.index}].notificationValue" 
                                            inputClass='js-notif-value${isPhone ? " js-format-phone" : ""}'/>
                                    </td>
                                    <td class="actions">
                                        <cti:button renderMode="buttonImage" icon="icon-cross" classes="js-remove"/>
                                    </td>
                                </tr>
                            </cti:displayForPageEditModes>
                            <cti:displayForPageEditModes modes="VIEW">
                                <tags:nameValue2 nameKey="${contactNotif.contactNotificationType.formatKey}">
                                    <c:if test="${isPhone}">
                                        <cti:formatPhoneNumber value="${contactNotif.notificationValue}"/>
                                    </c:if>
                                    <c:if test="${!isPhone}">${fn:escapeXml(contactNotif.notificationValue)}</c:if>
                                </tags:nameValue2>
                            </cti:displayForPageEditModes>
                        </c:forEach>
                    </tags:nameValueContainer2>
                    <cti:displayForPageEditModes modes="EDIT">
                        <div class="action-area">
                            <cti:button nameKey="add" icon="icon-add" id="add-notif-btn"/>
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
                        <div id="change-password-popup" class="dn"
                            data-dialog
                            data-event="yukon:user:profile:password:save"
                            data-title="<cti:msg2 key=".profile.changePassword"/>"
                            data-ok-class="js-save-pw-btn"
                            data-ok-disabled
                            data-width="750"
                            data-url="<cti:url value="/login/authenticated/change-password"/>"></div>
                    </c:if>
                </div>
            </cti:displayForPageEditModes>
        </form:form>
        
        <cti:displayForPageEditModes modes="VIEW">
            
            <%-- PREFERENCES SECTION --%>
            <tags:sectionContainer2 nameKey="preferences">
                <table id="preferences-table" class="full-width with-form-controls">
                    <tbody>
                        
                        <tr>
                            <td class="name"><i:inline key=".preferences.default.all"/></td>
                            <td class="value">
                                <cti:button classes="js-pref-default" id="restore-to-defaults" icon="icon-arrow-swap" 
                                    nameKey="preferences.default.all.button" 
                                    data-ok-event="yukon:user:profile:pref:defaults"/>
                            </td>
                        </tr>
                        
                        <c:forEach var="preference" items="${allPreferenceNames}" >
                            <tr data-type="${preference}">
                                <td class="name"><i:inline key="${preference}"/></td>
                                
                                <td class="value js-pref-options">
                                    
                                    <c:set var="prefName" value="${preference.toString()}"/>
                                    <c:set var="defaultVal" value="${preference.defaultValue}"/>
                                    <c:set var="prefValue" value="${userPreferenceMap.get(prefName) != null ? userPreferenceMap.get(prefName).value : preference.defaultValue}"/>                                   
                                    <c:set var="prefOptions" value="${preference.valueType.optionList}"/>
                                    <c:set var="prefDisplayOptMap" value="${userPreferencesNameToDisplayOptions.get(prefName)}"/>
                                    
                                    <cti:button classes="js-pref-default" data-value="${defaultVal}" icon="icon-arrow-swap" 
                                        renderMode="buttonImage"/>
                                    
                                    <div class="button-group button-group-toggle fl">
                                        <c:forEach var="prefOption" items="${prefOptions}" varStatus="stat">
                                            <c:set var="cssClass" value="${prefOption.value eq 'OFF' ? 'no' : 'yes'}"/>
                                            <cti:msg2 var="prefText" key="${prefOption.message}"/>
                                            <c:set var="iconOnly" value='${prefDisplayOptMap != null && (prefDisplayOptMap.containsKey("iconONLY"))}'/>
                                            
                                            <c:choose>
                                                <c:when test="${prefOption.value.equals(prefValue)}">
                                                    <c:set var="classes" value="${cssClass} on"/>
                                                </c:when>
                                                <c:otherwise><c:set var="classes" value="${cssClass}"/></c:otherwise>
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
                                            <cti:button classes="${classes}" title="${title}" renderMode="${renderMode}" 
                                                label="${prefText}" icon="${icon}" data-value="${prefOption.value}"/>
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
                <i:inline key="yukon.common.na"/>
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

<cti:displayForPageEditModes modes="EDIT">
<table class="dn">
    <tr id="contact-notif-template">
        <td class="name">
            <cti:msg2 key="yukon.common.selector.selectOne" var="txt_selectOne"/>
            <tags:simpleSelect name="contact.otherNotifications[?].contactNotificationType" 
                items="${notificationTypes}" itemLabelKey="formatKey" 
                defaultItemValue="-1" 
                defaultItemLabel="${txt_selectOne}" 
                cssClass="js-notif-type"/>
        </td>
        <td class="value">
            <input type="text" name="contact.otherNotifications[?].notificationValue" class="js-notif-value">
        </td>
        <td class="actions">
            <cti:button renderMode="buttonImage" icon="icon-cross" classes="js-remove"/>
        </td>
    </tr>
</table>
</cti:displayForPageEditModes>

<cti:includeScript link="/resources/js/pages/yukon.user.profile.js"/>

</cti:standardPage>