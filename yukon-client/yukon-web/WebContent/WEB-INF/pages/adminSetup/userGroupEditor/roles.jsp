<%@ page import="com.cannontech.database.data.lite.LiteYukonRoleProperty" %>
<%@ page import="com.cannontech.database.data.lite.LiteComparators" %>
<%@ page import="java.util.Arrays" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="adminSetup" page="roles">
    
    <c:set var="legend"><cti:icon icon="icon-asterisk-orange"/>&nbsp;<i:inline key=".legend"/></c:set>
    
    <form:form action="/adminSetup/roleEditor/update" id="loginGroupRoleForm" method="post">
        <input type="hidden" name="roleId" value="${roleId}">
        <input type="hidden" name="roleGroupId" value="${roleGroupId}">
        
        <cti:msg2 key=".roleGroupEditor.groupSettings.title" var="title"/>
        <tags:sectionContainer title="${title}" controls="${legend}">
            <c:forEach items="${mappedPropertiesHelper.mappableProperties}" var="prop">
                <div class="setting">
                    <div class="column_6_12_6 clearfix">
                        <div class="column one">
                            <label for="${prop.extra.yukonRoleProperty}"><i:inline key="${prop.extra.key}"/></label>
                        </div>
                        <div class="column two">
                            <div class="default-indicator">
                                <c:if test="${!empty prop.extra.defaultValue}">
                                    <c:if test="${command.values[prop.extra.yukonRoleProperty] != prop.extra.defaultValue}">
                                        <cti:icon icon="icon-asterisk-orange"/>
                                    </c:if>
                                </c:if>
                            </div>
                            <div class="value-description">
                                <div class="value">
                                    <tags:simpleInputType id="${prop.extra.yukonRoleProperty}" input="${prop.valueType}" path="${prop.path}"/>
                                </div>
                                <div class="description">
                                    <p>
                                        <i:inline key="${prop.extra.description}"/>
                                        <c:if test="${!empty prop.extra.defaultValue}">
                                            <span class="default"><i:inline key=".default"/>:&nbsp;(${fn:escapeXml(prop.extra.defaultValue)})</span>
                                        </c:if>
                                        <form:errors path="${prop.path}" cssClass="errorMessage" element="div"/>
                                    </p>
                                </div>
                            </div>
                        </div>
                        <div class="column three nogutter comments">
                            <!-- Empty -->
                        </div>
                    </div>
                </div>
            </c:forEach>
        </tags:sectionContainer>

        <cti:button nameKey="save" name="save" type="submit" classes="primary action"/>
        <c:if test="${showDelete}">
            <cti:button nameKey="delete" id="deleteButton" name="delete" type="submit"/>
            <d:confirm on="#deleteButton" nameKey="confirmDelete" argument="${roleGroupName}"/>
        </c:if>
        <cti:button nameKey="cancel" name="cancel" type="submit"/>

    </form:form>
</cti:standardPage>