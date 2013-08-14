<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="user" page="profile.${mode}">

<tags:setFormEditMode mode="${mode}"/>

<dialog:confirm on="#reset_all_preferences" nameKey="preferences.confirmResetAll" argument=""/>

<div class="column_12_12">
    <div class="column one">
    
        <%-- LEFT SIDE COLUMN --%>
        <form:form method="post" action="update" modelAttribute="userProfile">
        
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
            </tags:sectionContainer2>

            <tags:sectionContainer2 nameKey="additionContactInfo" styleClass="no-line">
                <div class="f-display_empty<c:if test="${not empty userProfile.contact.otherNotifications}"> dn</c:if>"><i:inline key="yukon.web.defaults.na"/></div>
                <tags:nameValueContainer2 id="contactNotifs">
                    <c:forEach var="contactNotif" items="${userProfile.contact.otherNotifications}" varStatus="row">
                        <c:set var="notifType" value="${contactNotif.contactNotificationType.contactNotificationMethodType.toString()}" />
                        <c:set var="isPhone" value='${"PHONE".equals(notifType) || "FAX".equals(notifType)}' />
                        <cti:displayForPageEditModes modes="EDIT">
                            <tr class="contactNotif">
                                <td class="name">
                                    <tags:selectWithItems items="${notificationTypes}" path="contact.otherNotifications[${row.index}].contactNotificationType" inputClass="f-contactNotif-type"/>
                                </td>
                                <td class="value">
                                    <tags:input path="contact.otherNotifications[${row.index}].notificationValue" inputClass='f-contactNotif-val${isPhone ? " f-formatPhone" : ""}'></tags:input>
                                </td>
                                <td class="actions">
                                    <a title="Remove" href="javascript:void(0);" class="removeBtn icon icon-cross"> </a>
                                </td>
                            </tr>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <tags:nameValue2 nameKey="${contactNotif.contactNotificationType.formatKey}" rowClass="contactNotif">
                                <c:if test="${isPhone}">
                                    <cti:formatPhoneNumber value="${contactNotif.notificationValue}"/>
                                </c:if>
                                <c:if test="${! isPhone}">${contactNotif.notificationValue}</c:if>
                            </tags:nameValue2>
                        </cti:displayForPageEditModes>
                    </c:forEach>
                </tags:nameValueContainer2>
                <cti:displayForPageEditModes modes="EDIT">
                    <div class="actionArea">
                        <cti:button nameKey="add" icon="icon-add" id="btn_addContactInfo"/>
                    </div>
                </cti:displayForPageEditModes>
            </tags:sectionContainer2>

            <cti:displayForPageEditModes modes="EDIT">
                <div class="pageActionArea">
                    <cti:button nameKey="save" name="update" type="submit" classes="primary action"/>
                    <cti:button nameKey="cancel" name="cancel" href="/user/profile"/>
                </div>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="VIEW">
                <div class="actionArea">
                    <cti:button nameKey="edit" icon="icon-pencil" name="edit" href="/user/profile/edit"/>
                    <c:if test="${canResetPassword}">
                        <cti:button id="btnChangePassword" icon="icon-key" nameKey="changePassword.button"/>
                    </c:if>
                </div>
            </cti:displayForPageEditModes>
        </form:form>

        <cti:displayForPageEditModes modes="VIEW">
            <c:if test="${canResetPassword}">
                <dialog:inline on="#btnChangePassword" id="dlg_change_password" okEvent="evt_ajaxsubmit_confirm_password" nameKey="profile.changePassword" options="{width: 750}">
                    <div class="column_12_12">
                        <div class="column one">
                            <form id="loginBackingBean">
                                <input type="hidden" name="k" value="${changePwdKey}">
                                <input type="hidden" name="userId" value="${userProfile.userId}">
                                <input type="hidden" name="username" value="${fn:escapeXml(userProfile.username)}">
                                <input type="hidden" name="userGroupName" value="${fn:escapeXml(userGroupName)}">
                                <tags:nameValueContainer2>
                                    <tr><td class="name"><i:inline key="yukon.web.changelogin.oldPassword"/></td>
                                        <td class="value"><input maxlength="64" autocomplete="false" type="password" name="oldPassword" value="" class="f-current" placeholder="<cti:msg2 key='yukon.web.changelogin.oldPassword'/>"></input></td>
                                    </tr>
                                    <tr>
                                        <td class="name"><i:inline key="yukon.web.changelogin.newPassword"/></td>
                                        <td class="value"><input maxlength="64" autocomplete="false" type="password" name="password1" value="" class="password_editor_field new f-check_password" placeholder="<cti:msg2 key='yukon.web.changelogin.newPassword'/>"></input></td>
                                    </tr>
                                    <tr>
                                        <td class="name"><i:inline key="yukon.web.changelogin.confirm"/></td>
                                        <td class="value"><input maxlength="64" autocomplete="false" type="password" name="password2" value="" class="password_editor_field confirm" placeholder="<cti:msg2 key='yukon.web.changelogin.confirm'/>"></input></td>
                                    </tr>
                                    <tr>
                                        <td></td>
                                        <td>
                                            <div class="no_match errorMessage"><i:inline key="yukon.web.modules.passwordPolicy.noMatch.description"/></div>
                                            <div class="password_errors dn"></div>
                                        </td>
                                    </tr>
                                </tags:nameValueContainer2>
                            </form>
                        </div>
                        <div class="column two nogutter">
                            <tags:passwordHelper passwordPolicy="${passwordPolicy}"/>
                        </div>
                    </div>
                </dialog:inline>
            </c:if>

            <%-- PREFERENCES SECTION currently at the bottom of the first column --%>
            <tags:sectionContainer2 nameKey="preferences">
                <table id="tbl_preferences"><tbody>
                    <tr data-type="${objPrefName}" class="all-preferences-row">
                        <td class="name"><i:inline key=".preferences.default.all"/></td>
                        </td>
                        <td class="value">
                            <cti:button classes="f-pref-default" id="reset_all_preferences" icon="icon-arrow-swap" nameKey="preferences.default.all.button" />
                        </td>
                    </tr>
        
                    <c:forEach var="objPrefName" items="${allPreferenceNames}" >
                        <tr data-type="${objPrefName}">
                            <td class="name"><i:inline key="${objPrefName.formatKey}"/></td>
                            <c:set var="prefName" value="${objPrefName.toString()}" />
                            <c:set var="isString" value='${"StringType".equals(objPrefName.valueType)}' />
                            <c:set var="isEnum" value='${objPrefName.valueType.toString().startsWith("EnumType")}' />
                            <c:choose>
                                <c:when test="${isEnum}">
                                    <td class="value selection_group">
                                        <c:set var="prefValue" value="${userPreferenceMap.get(prefName) != null ? userPreferenceMap.get(prefName).value : objPrefName.defaultValue}" />
                                        <c:set var="defaultVal" value="${objPrefName.defaultValue}" />
                                        <button class="f-pref-default" data-value="${defaultVal}"><i class="icon icon-arrow-swap"></i></button>
                                        <c:set var="prefOptions" value="${objPrefName.valueType.optionList}" />
                                        <c:set var="prefDisplayOptMap" value="${userPreferencesNameToDisplayOptions.get(prefName)}" />
                                        <c:forEach var="prefOption" items="${prefOptions}" varStatus="stat">
                                            <cti:msg2 key="${prefOption.message}" var="prefText"/>
                                            <c:set var="baseCss" value='${stat.first ? "left" : stat.last ? "right" : "middle"}'/>
                                            <c:set var="iconOnly" value='${prefDisplayOptMap != null && (prefDisplayOptMap.containsKey("iconONLY"))}' />
                                            <button type="button" class="${baseCss}<c:if test="${prefOption.value.equals(prefValue)}"> on</c:if>" data-value="${prefOption.value}"<c:if test="${iconOnly}"> title="${prefText}"</c:if>>
                                                <c:choose>
                                                    <c:when test="${iconOnly}">
                                                        <c:set var="optMap" value='${prefDisplayOptMap.get("iconONLY")}'/><i class="icon ${optMap.get(prefOption.value)}"></i>
                                                    </c:when>
                                                    <c:otherwise><span class="label">${prefText}</span></c:otherwise>
                                                </c:choose>
                                            </button>
                                        </c:forEach>
                                    </td>
                                </c:when>
                                <c:otherwise>
                                    <td class="value">
                                        <c:set var="defaultVal" value="${objPrefName.defaultValue}" />
                                        <c:set var="prefValue" value="${userPreferenceMap.get(prefName) != null ? userPreferenceMap.get(prefName).value : defaultVal}" />
                                        <form class="f-pref-form" action="/this/is/never/called" method="PUT">
                                            <button type="button" class="f-pref-default" data-value="${defaultVal}"><i class="icon icon-arrow-swap"></i></button>
                                            <input type="text" name="${prefName}" value="${fn:escapeXml(prefValue)}" prev-value="${fn:escapeXml(prefValue)}" title="${fn:escapeXml(prefValue)}" style="float:left" maxlength="255">
                                            <button type="button" class="save-preference dn" title="<cti:msg2 key="yukon.common.save" />"><i class="icon icon-disk"></i></button>
                                        </form>
                                    </td>
                                </c:otherwise>
                            </c:choose>
                        </tr>
                    </c:forEach>
                </tbody></table>
            </tags:sectionContainer2>
        </cti:displayForPageEditModes>
    </div>
    
    <%-- RIGHT SIDE COLUMN --%>
    <div class="column two nogutter" id="column_two">
    <cti:displayForPageEditModes modes="VIEW">
    
<cti:tabbedContentSelector mode="section">
    <cti:msg2 key=".groups.title" var="groupTitle"/>
    <cti:tabbedContentSelectorContent selectorName="${groupTitle}">
        <h3>${fn:escapeXml(userGroupName)}</h3>
        <c:choose>
            <c:when test="${empty categoryRoleMap}">
                <i:inline key="yukon.web.defaults.na"/>
            </c:when>
            <c:otherwise>
                <div class="rolesContainer wsnw">
                    <c:forEach var="category" items="${categoryRoleMap}">
                        <ul class="groupedList">
                            <li><span class="group"><cti:formatObject value="${category.key}"/></span>
                                <ul class="groupedItem">
                                    <c:forEach var="role" items="${category.value}">
                                        <li class="detail">
                                            <cti:formatObject value="${role.first}"/>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </li>
                        </ul>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </cti:tabbedContentSelectorContent>
</cti:tabbedContentSelector>
    
    </cti:displayForPageEditModes>
    </div>
</div>

<cti:includeScript link="/JavaScript/yukon.user.profile.js" />

<%-- This template row is copied by Javascript when user adds Contact Notification Infos while editing. --%>
<cti:displayForPageEditModes modes="EDIT">
<table class="dn"><tbody>
    <tr id="template_contactNotif" class="contactNotif">
        <td class="name">
            <cti:msg2 key="yukon.web.defaults.selector.selectOne" var="txt_selectOne"/>
            <tags:simpleSelect name="contact.otherNotifications" items="${notificationTypes}" itemLabelKey="formatKey" defaultItemValue="-1" defaultItemLabel="${txt_selectOne}" cssClass="f-contactNotif-type"/>
        </td>
        <td class="value">
            <input name="contact.otherNotifications" class="f-contactNotif-val">
        </td>
        <td class="actions">
            <a title="Remove" href="javascript:void(0);" class="removeBtn icon icon-cross"> </a>
        </td>
    </tr>
</tbody></table>
</cti:displayForPageEditModes>

</cti:standardPage>
