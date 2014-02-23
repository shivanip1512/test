<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="porterResponseMonitor.${mode}">
    <cti:includeScript link="/JavaScript/yukon.monitor.porter.response.js"/>

    <cti:url var="fullErrorCodesURL" value="/support/errorCodes/view"/>

    <i:simplePopup titleKey=".errorCodesPopup" id="errorCodesHelpPopup" on="#codes-help" options="{'width': 600,'height': 500}">
        <table id="errorCodes" class="compact-results-table stacked">
            <thead>
            <tr>
                <th><i:inline key=".errorCodesPopup.header.code" /></th>
                <th><i:inline key=".errorCodesPopup.header.porter" /></th>
            </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
            <c:forEach items="${allErrors}" var="error">
                <tr>
                    <td>${error.errorCode}</td>
                    <td>${error.description}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <span class="footerLink">
            <i:inline key=".errorCodesVerboseMsg"/>
            <a href="${fullErrorCodesURL}"><i:inline key=".errorCodesSupportLink"/></a>
        </span>
    </i:simplePopup>

    <table class="dn">
        <tr class="f-template-row" data-row="0">
            <td>
                <input type="hidden" name="rules[?].ruleId" class="f-row-id"/>
                <input type="hidden" name="rules[?].ruleOrder" class="f-row-order"/>
                <span class="f-row-order-text"></span>
            </td>
            <td>
                <label>
                    <input type="checkbox" name="rules[?].success" class="f-row-success"/>
                    <i:inline key=".rule.success"/>
                </label>
            </td>
            <td>
                <input type="text" name="rules[?].errorCodes" size="40" class="f-row-error-codes"/>
            </td>
            <td>
                <select name="rules[?].matchStyle" class="f-row-match-style">
                    <c:forEach var="style" items="${matchStyleChoices}">
                        <option value="${style}" ><cti:msg2 key="${style}"/></option>
                    </c:forEach>
                </select>
            </td>
            <td>
                <select name="rules[?].state" class="f-row-state">
                    <c:forEach var="state" items="${monitorDto.stateGroup.statesList}">
                        <option value="${state.liteID}">${fn:escapeXml(state.stateText)}</option>
                    </c:forEach>
                </select>
                <cti:button classes="f-remove fr" icon="icon-cross" renderMode="buttonImage"/>
                <cti:button classes="f-up right fr" icon="icon-bullet-go-up" renderMode="buttonImage"/>
                <cti:button classes="f-down left fr" icon="icon-bullet-go-down" renderMode="buttonImage" disabled="true"/>
            </td>
        </tr>
    </table>

    <form:form commandName="monitorDto" action="update" method="post">

        <form:hidden path="monitorId"/>
        <form:hidden path="stateGroup"/>
        <form:hidden path="evaluatorStatus"/>

        <tags:sectionContainer2 nameKey="info" styleClass="stacked with-form-controls">
            <tags:nameValueContainer2>

                <%-- name --%>
                <tags:inputNameValue nameKey=".name" path="name" maxlength="50" size="50"/>

                <cti:msg2 var="deviceGroupTitle" key=".popupInfo.deviceGroup.title"/>

                <%-- device group --%>
                <tags:nameValue2 nameKey=".deviceGroup">
                    <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
                    <tags:deviceGroupNameSelector fieldName="groupName"
                        fieldValue="${monitorDto.groupName}" 
                        dataJson="${groupDataJson}"
                        linkGroupName="true" />
                </tags:nameValue2>

                <%-- enable/disable monitoring --%>
                <c:if test="${monitorDto.enabled}"><c:set var="clazz" value="success"/></c:if>
                <c:if test="${!monitorDto.enabled}"><c:set var="clazz" value="error"/></c:if>
                <tags:nameValue2 nameKey=".monitoring" valueClass="${clazz}">
                    <i:inline key="${monitorDto.evaluatorStatus}" />
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
        
        <tags:sectionContainer2 nameKey="rulesTable" hideEnabled="false">
            <div class="scroll-large">
            <table id="rules-table" class="compact-results-table with-form-controls">
                <thead>
                    <tr>
                        <th><i:inline key=".rulesTable.header.ruleOrder"/></th>
                        <th><i:inline key=".rulesTable.header.outcome"/></th>
                        <th>
                            <i:inline key=".rulesTable.header.errors"/>
                            <cti:icon id="codes-help" nameKey="help" icon="icon-help" classes="fn cp vatt"/>
                        </th>
                        <th><i:inline key=".rulesTable.header.matchStyle"/></th>
                        <th><i:inline key=".rulesTable.header.state"/></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="ruleEntry" items="${monitorDto.rules}" varStatus="status">
                        <tr data-row="${status.index}">
                            <td>
                                <form:hidden path="rules[${status.index}].ruleId" cssClass="f-row-id"/>
                                <form:hidden path="rules[${status.index}].ruleOrder" cssClass="f-row-order"/>
                                <span class="f-row-order-text">${status.index + 1}</span>
                            </td>
                            <td>
                                <label>
                                    <form:checkbox path="rules[${status.index}].success" cssClass="f-row-success"/>
                                    <i:inline key=".rule.success"/>
                                </label>
                            </td>
                            <td>
                                <tags:input path="rules[${status.index}].errorCodes" size="40" inputClass="f-row-error-codes"/>
                            </td>
                            <td>
                                <form:select path="rules[${status.index}].matchStyle" cssClass="f-row-match-style">
                                    <c:forEach var="style" items="${matchStyleChoices}">
                                        <form:option value="${style}">
                                            <i:inline key="${style.formatKey}" />
                                        </form:option>
                                    </c:forEach>
                                </form:select>
                            </td>
                            <td>
                                <form:select path="rules[${status.index}].state" cssClass="f-row-state">
                                    <c:forEach var="state" items="${monitorDto.stateGroup.statesList}">
                                        <form:option value="${state.liteID}">${fn:escapeXml(state.stateText)}</form:option>
                                    </c:forEach>
                                </form:select>
                                <cti:button classes="f-remove fr" icon="icon-cross" renderMode="buttonImage"/>
                                <cti:button classes="f-up right fr" icon="icon-bullet-go-up" renderMode="buttonImage"/>
                                <cti:button classes="f-down left fr" icon="icon-bullet-go-down" renderMode="buttonImage"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            </div>
            <div class="action-area">
                <cti:button nameKey="rulesTable.add" classes="f-add-rule" icon="icon-add"/>
            </div>
        </tags:sectionContainer2>

        <%-- update / enable_disable / delete / cancel --%>
        <div class="page-action-area">
            <cti:button nameKey="update" type="submit" busy="true" classes="primary action" data-disable-group="actionButtons"/>

            <c:set var="monitoringKey" value="enable"/>
            <c:if test="${monitorDto.enabled}">
                <c:set var="monitoringKey" value="disable"/>
            </c:if>
            <cti:button type="submit" name="toggleEnabled" nameKey="${monitoringKey}" busy="true" data-disable-group="actionButtons"/>
            <cti:button id="deleteButton" nameKey="delete" name="delete" type="submit" classes="delete"/>
            <d:confirm on="#deleteButton" nameKey="confirmDelete" argument="${monitorDto.name}"/>

            <cti:url value="/amr/porterResponseMonitor/viewPage" var="viewUrl">
                <cti:param name="monitorId" value="${monitorDto.monitorId}"/>
            </cti:url>
            <cti:button nameKey="cancel" href="${viewUrl}" busy="true" data-disable-group="actionButtons" />
        </div>
    </form:form>
    
</cti:standardPage>