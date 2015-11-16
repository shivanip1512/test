<%@ tag body-content="empty"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<%@ attribute name="nameKey" required="true" rtexprvalue="true"%>

<%@ attribute name="argument" type="java.lang.Object" rtexprvalue="true"%>
<%@ attribute name="endAction" description="The end action for the confirmDialog. The options for this are 'nothing' (the default--the confirmDialog stays shown), 'hide', or 'block' (the confirmDialog stays shown, but the page is blocked)"%>
<%@ attribute name="href" rtexprvalue="true"%>
<%@ attribute name="id" rtexprvalue="true"%>
<%@ attribute name="on" rtexprvalue="true" description="registers click event on the element with this CSS selector"%>
<%@ attribute name="styleClass" rtexprvalue="true"%>
<%@ attribute name="submitName" rtexprvalue="true" description="This will be the name of the OK submit button."%>
<%@ attribute name="formId" rtexprvalue="true" description="The id of the form to submit, required when not using the 'href' attribute."%>

<cti:uniqueIdentifier var="uniqueId"/>
<c:if test="${!empty pageScope.id}">
    <c:set var="uniqueId" value="${pageScope.id}"/>
</c:if>

<c:set var="style" value=""/>
<c:if test="${!empty pageScope.styleClass}">
    <c:set var="style" value="${pageScope.styleClass}"/>
</c:if>

<cti:msgScope paths="${nameKey},">
    <cti:msg2 var="confirmationMsg" key=".message" argument="${pageScope.argument}" />

    <i:simplePopup id="${uniqueId}" styleClass="${style}" titleKey=".title" on="${pageScope.on}" arguments="${pageScope.argument}">
        <p>${confirmationMsg}<p>

        <div class="action-area">
            <c:choose>
                <c:when test="${empty pageScope.endAction || pageScope.endAction == 'nothing'}">
                    <c:if test="${empty pageScope.submitName}">
                        <cti:button classes="primary action ${pageScope.styleClass}" nameKey="ok" type="submit" />
                    </c:if>
                    <c:if test="${!empty pageScope.submitName}">
                        <c:if test="${empty pageScope.href}">
                            <cti:button classes="primary action ${pageScope.styleClass}" nameKey="ok"
                                name="${pageScope.submitName}" type="submit"/>
                        </c:if>
                        <c:if test="${!empty pageScope.href}">
                            <cti:button classes="primary action ${pageScope.styleClass}" nameKey="ok"
                                name="${pageScope.submitName}" href="${pageScope.href}"/>
                        </c:if>
                    </c:if>
                </c:when>
                <c:when test="${pageScope.endAction == 'hide'}">
                    <c:if test="${empty pageScope.submitName}">
                        <cti:button classes="primary action ${pageScope.styleClass}" nameKey="ok" type="submit"
                            onclick="$('#${uniqueId}').dialog('close')" />
                    </c:if>
                    <c:if test="${!empty pageScope.submitName}">
                        <c:if test="${empty pageScope.href}">
                            <cti:button classes="primary action ${pageScope.styleClass}" nameKey="ok"
                                name="${pageScope.submitName}" type="submit" 
                                onclick="$('#${uniqueId}').dialog('close')"/>
                        </c:if>
                        <c:if test="${!empty pageScope.href}">
                            <cti:button classes="primary action ${pageScope.styleClass}" nameKey="ok"
                                name="${pageScope.submitName}" href="${pageScope.href}"
                                onclick="$('#${uniqueId}').dialog('close')"/>
                        </c:if>
                    </c:if>
                </c:when>
                <c:when test="${pageScope.endAction == 'block'}">
                    <c:if test="${empty pageScope.submitName}">
                        <cti:button classes="primary action ${pageScope.styleClass} js-blocker" nameKey="ok"
                            type="submit" />
                    </c:if>
                    <c:if test="${!empty pageScope.submitName}">
                        <c:if test="${empty pageScope.href}">
                            <cti:button classes="primary action ${pageScope.styleClass}" nameKey="ok"
                                name="${pageScope.submitName}" type="submit"/>
                        </c:if>
                        <c:if test="${!empty pageScope.href}">
                            <cti:button classes="primary action ${pageScope.styleClass} js-blocker" nameKey="ok"
                                name="${pageScope.submitName}" href="${pageScope.href}"/>
                        </c:if>
                    </c:if>
                </c:when>
            </c:choose>
            <cti:button nameKey="cancel" onclick="$('#${uniqueId}').dialog('close')" />
        </div>
    </i:simplePopup>
</cti:msgScope>