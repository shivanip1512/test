<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>

<cti:standardPage module="user" page="profile.${mode}">
<script type="text/javascript">
$(document).ready(function(){
    expirePassword();
    });
    
    function expirePassword(){
        var passwordExpired = <%=session.getAttribute("passwordExpired")%>;
        var url = "../login/expirePassword";
        if(passwordExpired){
            $.ajax({
                    type: "POST",
                    url: url
               });
        }
    }
</script>

<tags:setFormEditMode mode="${mode}"/>

<d:confirm on="#restore-to-defaults" nameKey="preferences.confirmResetAll"/>

<div class="column-12-12 clearfix">
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
                        <tags:nameValue2 nameKey=".userGroup">
                            <cti:url var="url" value="/admin/user-groups/${userGroupId}"/>
                            <a href="${url}">${fn:escapeXml(userGroupName)}</a>
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
    </div>
    
    <%-- RIGHT SIDE COLUMN --%>
    <div class="column two nogutter" id="column_two">
        <cti:displayForPageEditModes modes="VIEW">
            
            <%-- PREFERENCES SECTION --%>
            <tags:sectionContainer2 nameKey="preferences">
                <tags:nameValueContainer2 id="preferences-table">
                        <tags:nameValue2 nameKey=".preferences.default.all">
                            <cti:button classes="js-pref-default" id="restore-to-defaults" icon="icon-arrow-swap" nameKey="preferences.default.all.button" data-ok-event="yukon:user:profile:pref:defaults" />
                        </tags:nameValue2>
                        <c:forEach var="preference" items="${allPreferenceNames}">
                            <c:set var="prefName" value="${preference.toString()}" />
                            <c:set var="prefInputType" value="${preference.valueType}" />
                            <c:set var="defaultVal" value="${preference.defaultValue}" />
                            <tags:nameValue2 nameKey=".preferences.${preference}" data-type="${preference}" valueClass="js-pref-options">
                                <c:choose>
                                    <c:when test="${fn:contains(prefInputType, 'Enum')}">
                                        <c:set var="prefValue" value="${userPreferenceMap.get(prefName) != null ? userPreferenceMap.get(prefName).value : preference.defaultValue}" />
                                        <c:set var="prefOptions" value="${preference.valueType.optionList}" />
                                        <c:set var="prefDisplayOptMap" value="${userPreferencesNameToDisplayOptions.get(prefName)}" />

                                        <cti:button classes="js-pref-default" data-value="${defaultVal}" icon="icon-arrow-swap" renderMode="buttonImage" />

                                        <div class="button-group button-group-toggle fl">
                                            <c:forEach var="prefOption" items="${prefOptions}" varStatus="stat">
                                                <cti:msg2 var="prefText" key="${prefOption.message}" />
                                                <c:set var="iconOnly" value='${prefDisplayOptMap != null && (prefDisplayOptMap.containsKey("iconONLY"))}' />
                                                <c:set var="classes" value="${prefOption.value eq 'OFF' ? 'no' : 'yes'}" />
                                                <c:if test="${prefOption.value.equals(prefValue)}">
                                                    <c:set var="classes" value="${classes} on" />
                                                </c:if>
                                                <c:choose>
                                                    <c:when test="${iconOnly}">
                                                        <c:set var="title" value="${prefText}" />
                                                        <c:set var="renderMode" value="buttonImage" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:set var="title" value="" />
                                                        <c:set var="renderMode" value="button" />
                                                    </c:otherwise>
                                                </c:choose>
                                                <c:choose>
                                                    <c:when test="${iconOnly}">
                                                        <c:set var="optMap" value="${prefDisplayOptMap.get('iconONLY')}" />
                                                        <c:set var="icon" value="${optMap.get(prefOption.value)}" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:set var="icon" value="" />
                                                    </c:otherwise>
                                                </c:choose>
                                                <cti:button classes="${classes}" title="${title}" renderMode="${renderMode}" label="${prefText}" icon="${icon}" data-value="${prefOption.value}" />
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:when test="${prefName == 'COMMANDER_PRIORITY'}">
                                        <cti:msg2 key="yukon.web.modules.tools.commander.commandPriority.title" var="priorityRange" arguments="${minCmdPriority}-${maxCmdPriority}"/>
                                        <cti:button id="resetCommandPriorityBtn" data-value="${defaultVal}" icon="icon-arrow-swap" renderMode="buttonImage" />
                                        &nbsp;<input type="number" id="commandPriority" name="commandPriority" style='width:75px;' title="${priorityRange}"
                                            min="${minCmdPriority}" max="${maxCmdPriority}" value="${priority}" />
                                    </c:when>
                                    <c:when test="${prefName == 'COMMANDER_QUEUE_COMMAND'}">
                                        <cti:button classes="js-pref-default" data-value="${defaultVal}" icon="icon-arrow-swap" renderMode="buttonImage" />
                                        <c:set var="trueClass" value="${queueCommand ? 'on' : ''}" />
                                        <c:set var="falseClass" value="${queueCommand ? '' : 'on'}" />
                                        <div id="queueCommand" class="button-group button-group-toggle">
                                            <cti:button nameKey="yes" classes="yes ${trueClass}" data-value="true" />
                                            <cti:button nameKey="no" classes="no ${falseClass}" data-value="false" />
                                        </div>
                                    </c:when>
                                </c:choose>
                            </tags:nameValue2>
                        </c:forEach>
                    </tags:nameValueContainer2>
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

<!--NOTIFICATIONS SECTION-->
<cti:displayForPageEditModes modes="VIEW">

    <cti:msgScope paths="modules.smartNotifications">
    
        <tags:setFormEditMode mode="EDIT"/>
    
        <div id="notifications-section" style="margin-top:50px;">
            <cti:button icon="icon-email-add" nameKey="subscribe" data-popup="#create-popup" classes="fr"/>
            <tags:sectionContainer2 nameKey="notifications">
                
                <div>
                    <cti:url var="singleNotificationUrl" value="/notifications/singleNotification"/>
                    <form action="${singleNotificationUrl}" method="POST">
                        <cti:csrfToken/>
                        <span><span class="fl" style="padding-right:10px;"><i:inline key=".singleNotification"/>: </span>
                            <tags:switchButton name="singleNotification" classes="fn vam" onNameKey=".yes.label" offNameKey=".no.label" 
                                checked="${!empty sendTime}" toggleGroup="singleNotificationToggle" toggleAction="hide"/>
                             <tags:timeSlider startName="sendTime" startValue="${fn:escapeXml(sendTime)}" dataToggleGroup="singleNotificationToggle" stepValue="60" 
                                displayTimeToLeft="true" timeFormat="HHMM" maxValue="1380"/>
                            <cti:button nameKey="save" type="submit" classes="fn" style="margin-left:20px;"/>
                        </span>
                    </form>
                </div>
                <hr/>
                
               <cti:url var="action" value="/notifications/subscriptions"/>
                <form:form id="filter-form" action="${action}" modelAttribute="filter" method="get">
                    <i:inline key="yukon.common.filterBy"/>
                    <cti:msg2 var="allTypes" key=".allTypes"/>
                    <tags:selectWithItems path="eventType" items="${eventTypes}" defaultItemLabel="${allTypes}" />
                    <cti:button nameKey="filter" classes="action primary js-filter fn vab"/>
                </form:form>
        
                <hr/>
    
                <!--NOTIFICATIONS TABLE-->
                <cti:url var="dataUrl" value="/notifications/subscriptions"/>
                <div id="smart-notifications-container" data-url="${dataUrl}">
                    <%@ include file="../smartNotifications/subscriptions.jsp" %>
                </div>
                
            </tags:sectionContainer2>
        </div>
    
        <cti:url var="smartNotificationsUrl" value="/notifications/subscription/create"/>
        <div id="create-popup" data-dialog
                class="dn js-smart-notifications-popup" data-event="yukon:notifications:save"
                data-title="<cti:msg2 key=".createPopup.title"/>"
                data-url="${smartNotificationsUrl}" 
                data-load-event="yukon:notifications:load"
                data-width="600"></div>
                        
    </cti:msgScope>
    
</cti:displayForPageEditModes>
         
<cti:includeScript link="/resources/js/pages/yukon.user.profile.js"/>
<cti:includeScript link="/resources/js/pages/yukon.smart.notifications.js"/>
    
</cti:standardPage>
