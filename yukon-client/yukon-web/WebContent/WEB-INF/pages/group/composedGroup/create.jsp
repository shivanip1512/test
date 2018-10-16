<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="deviceGroups.create.composed">
<c:if test="${not empty errorMsg}">
    <div class="stacked error">${errorMsg}</div>
</c:if>

<cti:url var="action" value="/group/composedGroup/save"/>
<form:form modelAttribute="rules" action="${action}" method="post">

    <cti:csrfToken/>
    <form:hidden path="groupName"/>
            
    <%-- MATCH --%>
    <h3>
        <i:inline key=".matchSentence.prefix"/>
        <form:select path="compositionType">
            <c:forEach var="compositionType" items="${availableCompositionTypes}">
                <form:option value="${compositionType}"><cti:msg key="${compositionType.formatKey}"/></form:option>
            </c:forEach>
        </form:select>
        <i:inline key=".matchSentence.suffix"/>
        <cti:icon icon="icon-help"  classes="cp fn" data-popup="#instructions" data-popup-toggle="true"/>
        <cti:msg2 var="title" key=".instructions.title"/>
        <div id="instructions" class="dn" data-title="${title}" data-width="600">
            <i:inline key=".instructions"/>
        </div>
    </h3>
    <hr>
    <%-- RULES TABLE --%>
    <div class="separated-sections js-rules-list">
        <c:forEach var="group" items="${rules.groups}" varStatus="status">
            <div class="clearfix section js-rule">
                <span><i:inline key=".ruleSentence.deviceGroup.prefix"/></span>

                <form:select path="groups[${status.index}].negate">
                    <form:option value="false"><i:inline key=".contained"/></form:option>
                    <form:option value="true"><i:inline key=".notContained"/></form:option>
                </form:select>

                <span><i:inline key=".ruleSentence.deviceGroup.suffix"/></span>

	                <c:if test="${not empty group.groupFullName}">
	                    <cti:list var="values">
	                        <cti:item value="${group.groupFullName}" />
	                    </cti:list>
	                </c:if>
                    <div class="dib"><tags:deviceGroupPicker inputName="groups[${status.index}].groupFullName" inputValue="${values}" callbacks="${callbacks}"/></div>
                <cti:button classes="fr js-remove-rule" nameKey="remove"  renderMode="buttonImage" icon="icon-cross"/>
            </div>
        </c:forEach>
    </div>
    <div class="action-area">
        <cti:button nameKey="addAnotherDeviceGroup" icon="icon-add" classes="js-add-rule"/>
    </div>

    <%-- SAVE --%>
    <div class="page-action-area">
        <cti:button nameKey="save" type="submit" classes="primary action"/>
        <cti:url var="cancel" value="/group/editor/home" />
        <cti:button nameKey="cancel" href="${cancel}"/>
    </div>
</form:form>

<div class="dn js-template js-rule clearfix section">
    <span><i:inline key=".ruleSentence.deviceGroup.prefix"/></span>
    <select name="groups[?].negate">
        <option value="false"><i:inline key=".contained"/></option>
        <option value="true"><i:inline key=".notContained"/></option>
    </select>
    <span><i:inline key=".ruleSentence.deviceGroup.suffix"/></span>
    <div class="dib"><tags:deviceGroupPicker inputName="groups[?].groupFullName" callbacks="${callbacks}"/></div>
    <cti:button classes="fr js-remove-rule" nameKey="remove"  renderMode="buttonImage" icon="icon-cross"/>
</div>
<cti:includeScript link="/resources/js/pages/yukon.tools.composed.group.js"/>
</cti:standardPage>