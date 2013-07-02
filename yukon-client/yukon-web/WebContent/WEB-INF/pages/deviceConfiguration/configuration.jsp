<%@page import="com.cannontech.web.deviceConfiguration.model.DeviceConfigurationBackingBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog" %>

<cti:standardPage module="tools" page="configs.config.${mode}">
    <tags:setFormEditMode mode="${mode}"/>
    
    <form:form commandName="deviceConfigurationBackingBean" action="save">
        <form:hidden path="configId"/>
        <c:forEach var="item" items="${deviceConfigurationBackingBean.supportedTypes}">
            <form:hidden path="supportedTypes[${item.key}]"/>
        </c:forEach>

        <!-- CONFIGURATION NAME -->
        <span style="line-height: 26px;">
            <tags:nameValueContainer2 tableClass="stacked">
                <tags:inputNameValue nameKey=".configName" path="configName" size="50" maxlength="60"/>
            </tags:nameValueContainer2>
        </span>
        
        <!-- DEVICE CONFIGURATION CATEGORIES HEADER AND HELP BUTTON -->
        <div class="stacked">
            <h3 class="dib f-has-tooltip">
                <i:inline key=".configCategories"/>
            </h3>
            <cti:displayForPageEditModes modes="EDIT">
                <span class="f-tooltip dn">
                    <i:inline key=".configCategories.description"/>
                </span>
            </cti:displayForPageEditModes>
        </div>
        
        <!-- CATEGORIES -->
        <div class="column_12_12">
            <div class="column one">
                <c:forEach var="category" items="${deviceConfigurationBackingBean.categorySelections}" begin="0" step="2" end="${fn:length(deviceConfigurationBackingBean.categorySelections)}" varStatus="loopStatus">
                    <%@ include file="categories.jspf" %>
                </c:forEach>
            </div>
            <div class="column two nogutter">
                <c:forEach var="category" items="${deviceConfigurationBackingBean.categorySelections}" begin="1" step="2" end="${fn:length(deviceConfigurationBackingBean.categorySelections)}" varStatus="loopStatus">
                    <%@ include file="categories.jspf" %>
                </c:forEach>
            </div>
        </div>

        <!-- PAGE ACTION BUTTONS -->
        <div class="pageActionArea clear">
            <cti:displayForPageEditModes modes="VIEW">
                <cti:checkRolesAndProperties value="${editingRoleProperty}">
                    <cti:url value="edit" var="editUrl">
                        <cti:param name="configId" value="${deviceConfigurationBackingBean.configId}"/>
                    </cti:url>
                    <cti:button nameKey="edit" href="${editUrl}" icon="icon-pencil"/>
                </cti:checkRolesAndProperties>
                <cti:button nameKey="back" name="back" href="/deviceConfiguration/home"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT">
                <cti:checkRolesAndProperties value="${editingRoleProperty}">
                    <dialog:confirm on="#save" nameKey="confirmSave"/>
                    <cti:button nameKey="save" id="save" type="submit" classes="primary action"/>
                    <c:if test="${isDeletable}">
                        <dialog:confirm on="#remove" nameKey="confirmRemove"/>
                        <cti:url var="deleteUrl" value="delete">
                            <cti:param name="configId" value="${deviceConfigurationBackingBean.configId}"/>
                        </cti:url>
                        <cti:button nameKey="remove" id="remove" href="${deleteUrl}"/>
                    </c:if>
                    <cti:url var="viewUrl" value="view">
                        <cti:param name="configId" value="${deviceConfigurationBackingBean.configId}"/>
                    </cti:url>
                    <cti:button nameKey="cancel" href="${viewUrl}"/>
                </cti:checkRolesAndProperties>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="CREATE">
                <cti:checkRolesAndProperties value="${editingRoleProperty}">
                    <cti:button nameKey="create" type="submit" classes="primary action"/>
                    <cti:button nameKey="cancel" href="/deviceConfiguration/home"/>
                </cti:checkRolesAndProperties>
            </cti:displayForPageEditModes>
        </div>
    </form:form>
</cti:standardPage>