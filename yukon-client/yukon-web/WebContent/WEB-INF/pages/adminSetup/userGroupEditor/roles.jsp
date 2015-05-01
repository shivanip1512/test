<%@ page import="com.cannontech.database.data.lite.LiteYukonRoleProperty" %>
<%@ page import="com.cannontech.database.data.lite.LiteComparators" %>
<%@ page import="java.util.Arrays" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="adminSetup" page="auth.role">

<c:set var="legend"><cti:icon icon="icon-asterisk-orange"/>&nbsp;<i:inline key=".legend"/></c:set>

<cti:url var="url" value="/admin/role-groups/${roleGroupId}/roles/${roleId}"/>
<form:form action="${url}" method="post">
    <cti:csrfToken/>
    <input type="hidden" name="roleId" value="${roleId}">
    <input type="hidden" name="roleGroupId" value="${roleGroupId}">
    
    <tags:sectionContainer2 nameKey="properties" controls="${legend}">
        <c:forEach items="${mappedPropertiesHelper.mappableProperties}" var="prop">
            <div class="setting">
                <div class="column-6-18 clearfix">
                    <div class="column one">
                        <label for="${prop.extra.yukonRoleProperty}"><i:inline key="${prop.extra.key}"/></label>
                    </div>
                    <div class="column two nogutter">
                        <div class="default-indicator">
                            <c:if test="${!empty prop.extra.defaultValue}">
                                <c:if test="${command.values[prop.extra.yukonRoleProperty] != prop.extra.defaultValue}">
                                    <cti:icon icon="icon-asterisk-orange"/>
                                </c:if>
                            </c:if>
                        </div>
                        <div class="value-description">
                            <div class="value">
                                <tags:simpleInputType id="${prop.extra.yukonRoleProperty}" 
                                    input="${prop.valueType}" path="${prop.path}"/>
                            </div>
                            <div class="description">
                                <p class="wrbw">
                                    <i:inline key="${prop.extra.description}"/>
                                    <c:if test="${!empty prop.extra.defaultValue}">
                                        <span class="default">
                                            <i:inline key=".default"/>:&nbsp;(${fn:escapeXml(prop.extra.defaultValue)})
                                        </span>
                                    </c:if>
                                    <form:errors path="${prop.path}" cssClass="error" element="div"/>
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
    </tags:sectionContainer2>
    
    <cti:button nameKey="save" name="save" type="submit" classes="primary action"/>
    <c:if test="${showDelete}">
        <cti:button nameKey="delete" id="deleteButton" name="delete" type="submit" classes="delete"/>
        <d:confirm on="#deleteButton" nameKey="confirmDelete" argument="${roleGroupName}"/>
    </c:if>
    <cti:url var="url" value="/admin/role-groups/${roleGroupId}"/>
    <cti:button nameKey="cancel" href="${url}"/>

</form:form>
</cti:standardPage>