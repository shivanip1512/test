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
            <cti:msg2 var="propertyDisplayName" key="${prop.extra.key}"/>
            <div class="setting">
                <div class="column-6-18 clearfix">
                    <div class="column one">
                        <label for="${prop.extra.yukonRoleProperty}">${propertyDisplayName}</label>
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
                                <cti:msg2 var="helpText" key="${prop.extra.yukonRoleProperty.helpTextKey}" blankIfMissing="true"/>
                                <c:if test="${!empty helpText}">
                                    <cti:icon icon="icon-help" classes="ML0 cp fn" 
                                        data-popup="#helpText-${prop.extra.yukonRoleProperty}" data-popup-toggle=""/>
                                        <div id="helpText-${prop.extra.yukonRoleProperty}" class="dn" 
                                            data-title="${propertyDisplayName}" data-width="600">${helpText}</div>
                                </c:if>
                            </div>
                            <div class="description">
                                <p class="wrbw">
                                    <i:inline key="${prop.extra.description}"/>
                                    <c:if test="${!empty prop.extra.defaultValue}">
                                        <cti:msg2 var="formattedDefault" key="${prop.extra.defaultValue}" blankIfMissing="true"/>
                                        <span class="default">
                                            <i:inline key=".default"/>:&nbsp;(${fn:escapeXml(!empty formattedDefault ? formattedDefault : prop.extra.defaultValue)})
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