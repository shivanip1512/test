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
<script>
Event.observe(window, 'load', function() {
    $$('.focusableFieldHolder input').each(function(inputField) {
        var blurAndInitial = function() {
            var defaultField = inputField.up('span').next('input');
            if ($F(inputField) == $F(defaultField) || $F(inputField) == "") {
                inputField.addClassName('usingDefaultValue');
                inputField.value = $F(defaultField);
            }
            inputField.up('tr').removeClassName('childHasFocus');
        };
        Event.observe(inputField, "focus", function(event) {
            var defaultField = inputField.up('span').next('input');
            if ($F(inputField) == $F(defaultField)) {
                inputField.value = "";
            }
            inputField.removeClassName('usingDefaultValue');
            inputField.up('tr').addClassName('childHasFocus');
        });
        Event.observe(inputField, "blur", blurAndInitial);
        blurAndInitial();
    });

    <%-- handling select elements is similar, but requires slightly different code --%>
    $$('.focusableFieldHolder select').each(function(inputField) {
        var blurAndInitial = function() {
            var defaultField = inputField.up('span').next('input');
            if ($F(inputField) == $F(defaultField)) {
                inputField.addClassName('usingDefaultValue');
            } else {
                inputField.removeClassName('usingDefaultValue');
            }
        };
        Event.observe(inputField, "change", blurAndInitial);
        Event.observe(inputField, "focus", function(event) {
            inputField.up('tr').addClassName('childHasFocus');
            inputField.removeClassName('usingDefaultValue');
        });
        Event.observe(inputField, "blur", function(event) {
            inputField.up('tr').removeClassName('childHasFocus');
            blurAndInitial();
        });
        blurAndInitial();
    });

});

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
        <tags:csrfTokenRequest>
          <cti:button key="save" name="save" type="submit"/>
          <cti:button key="delete" id="deleteButton" type="button"/>
          <tags:confirmDialog nameKey=".confirmDelete" on="#deleteButton" argument="${groupName}" submitName="delete"/>
          <cti:button key="cancel" name="cancel" type="submit"/>
        </tags:csrfTokenRequest>
    </form:form>
</cti:standardPage>