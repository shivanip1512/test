<%@ page import="com.cannontech.core.dao.DaoFactory" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonRoleProperty" %>
<%@ page import="com.cannontech.roles.YukonRoleDefs" %>
<%@ page import="com.cannontech.database.data.lite.LiteComparators" %>
<%@ page import="java.util.Arrays" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="adminSetup" page="roles">
<cti:includeScript link="/JavaScript/cooper/ui/general.js"/>
    
<script type="text/javascript">
function showPointingPopup(event) {
    
    var target = Event.element(event);
    var offsets = target.cumulativeOffset();
    var popupLeft = offsets.left + target.getDimensions().width + 2;
    var left = popupLeft + 'px';
    var top = (offsets.top -20) + 'px';
    
    $('descriptionPopup').setStyle({left:left, top:top});
    $('descriptionPopup_content').innerHTML = target.up().next('span.focusedFieldDescription').innerHTML;
    $('descriptionPopup').show();
    
}
</script>

    <form:form action="/spring/adminSetup/roleEditor/update" id="loginGroupRoleForm" method="post">
        <input type="hidden" name="roleId" value="${roleId}">
        <input type="hidden" name="groupId" value="${groupId}">
        <table class="nameValueTable naturalWidth">
            <c:forEach items="${mappedPropertiesHelper.mappableProperties}" var="prop">
                <tr>
                    <td class="name"><label for="${prop.extra.yukonRoleProperty}"><i:inline key="${prop.extra.key}"/>:</label></td>
                    <td class="value">
                        <span class="focusableFieldHolder">
                            <tags:simpleInputType id="${prop.extra.yukonRoleProperty}" input="${prop.valueType}" path="${prop.path}"/>
                        </span>
                        <input type="hidden" value="${prop.extra.defaultValue}">
                        <span class="focusedFieldDescription"><i:inline key="${prop.extra.description}"/> (${prop.extra.defaultValue})</span>
                        <form:errors path="${prop.path}" cssClass="errorMessage" element="div"/>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <br>
<%--        <tags:csrfTokenRequest> --%>
          <cti:button nameKey="save" name="save" type="submit"/>
          <c:if test="${showDelete}">
              <cti:button nameKey="delete" id="deleteButton" type="button"/>
              <tags:confirmDialog nameKey=".confirmDelete" on="#deleteButton" argument="${groupName}" submitName="delete"/>
          </c:if>
          <cti:button nameKey="cancel" name="cancel" type="submit"/>
<%--        </tags:csrfTokenRequest> --%>
    </form:form>
    
    <div class="pointingPopup_container" id="descriptionPopup" style="display:none;">
        <div class="pointingPopup_chevron ov pr">
        </div>
        <div class="pointingPopup_content" id="descriptionPopup_content">
        </div>
    </div>
    
</cti:standardPage>