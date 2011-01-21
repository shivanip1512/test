<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ tag body-content="empty"%>

<%@ attribute name="nameKey" required="true" rtexprvalue="true"%>
<%@ attribute name="id" rtexprvalue="true"%>
<%@ attribute name="submitName" rtexprvalue="true" description="This will be the name of the OK submit button."%>
<%@ attribute name="arguments" type="java.lang.Object" rtexprvalue="true"%>
<%@ attribute name="on" description="registers click event on the element with this ID"%>

<cti:uniqueIdentifier var="uniqueId"/>
<c:if test="${!empty pageScope.id}">
    <c:set var="uniqueId" value="${pageScope.id}"/>
</c:if>

<cti:msgScope paths="${nameKey},">
    <c:if test="${empty pageScope.arguments}">
        <cti:msg2 var="confirmationMsg" key=".message" />
    </c:if>
    <c:if test="${!empty pageScope.arguments}">
        <cti:msg2 var="confirmationMsg" key=".message" arguments="${pageScope.arguments}" />
    </c:if>

    <i:simplePopup id="${uniqueId}" styleClass="mediumSimplePopup" titleKey=".title" on="${pageScope.on}">
        <h3 class="dialogQuestion">${confirmationMsg}</h3>

        <div class="actionArea">
            <c:if test="${empty pageScope.submitName}">
                <cti:button key="ok" type="submit" />
            </c:if>
            <c:if test="${!empty pageScope.submitName}">
                <cti:button key="ok" type="submit" name="${pageScope.submitName}" />
            </c:if>
            <cti:button key="cancel" onclick="$('${pageScope.on}').hide()" />
        </div>
    </i:simplePopup>
</cti:msgScope>