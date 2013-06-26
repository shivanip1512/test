<%@ page import="com.cannontech.database.data.lite.LiteYukonRoleProperty" %>
<%@ page import="com.cannontech.database.data.lite.LiteComparators" %>
<%@ page import="java.util.Arrays" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="adminSetup" page="roles">
<cti:includeScript link="/JavaScript/yukon/ui/fieldHelper.js"/>
    
<script type="text/javascript">

</script>

    <form:form action="/adminSetup/roleEditor/update" id="loginGroupRoleForm" method="post">
        <input type="hidden" name="roleId" value="${roleId}">
        <input type="hidden" name="roleGroupId" value="${roleGroupId}">
        <table class="nameValueTable naturalWidth">
            <c:forEach items="${mappedPropertiesHelper.mappableProperties}" var="prop">
                <tr>
                    <td class="name"><label for="${prop.extra.yukonRoleProperty}"><i:inline key="${prop.extra.key}"/>:</label></td>
                    <td class="value">
                        <span class="focusableFieldHolder">
                            <tags:simpleInputType id="${prop.extra.yukonRoleProperty}" input="${prop.valueType}" path="${prop.path}"/>
                        </span>
                        <input type="hidden" value="${prop.extra.defaultValue}">
                        <span class="focusedFieldDescription"><i:inline key="${prop.extra.description}"/>(${prop.extra.defaultValue})</span>
                        <form:errors path="${prop.path}" cssClass="errorMessage" element="div"/>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <br>

          <cti:button nameKey="save" name="save" type="submit" classes="primary action"/>
          <c:if test="${showDelete}">
              <cti:button nameKey="delete" id="deleteButton" name="delete" type="submit"/>
              <d:confirm on="#deleteButton" nameKey="confirmDelete" argument="${roleGroupName}"/>
          </c:if>
          <cti:button nameKey="cancel" name="cancel" type="submit"/>

    </form:form>
</cti:standardPage>