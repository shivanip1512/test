<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ tag body-content="empty"%>

<%@ attribute name="nameKey" required="true" rtexprvalue="true"%>
<%@ attribute name="id" rtexprvalue="true"%>
<%@ attribute name="cronExpressionId" required="true" rtexprvalue="true"%>
<%@ attribute name="submitName" rtexprvalue="true" description="This will be the name of the OK submit button."%>
<%@ attribute name="on" rtexprvalue="true" description="registers click event on the element with this CSS selector"%>
<%@ attribute name="styleClass" rtexprvalue="true"%>
<%@ attribute name="cronState" required="true" rtexprvalue="true" type="com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState"%>
<%@ attribute name="allowTypeChange" rtexprvalue="true" type="java.lang.Boolean" description="Allow changing between Repeating and One-Time job options."%>
<%@ attribute name="endAction" description="The end action for the confirmDialog. The options for this are 'nothing' (the default--the confirmDialog stays shown), 'hide', or 'block' (the confirmDialog stays shown, but the page is blocked)"%>

<cti:uniqueIdentifier var="uniqueId"/>
<c:if test="${!empty pageScope.id}">
    <c:set var="uniqueId" value="${pageScope.id}"/>
</c:if>

<c:set var="allowChange" value="true"/>
<c:if test="${!empty pageScope.allowTypeChange}">
    <c:set var="allowChange" value="${pageScope.allowTypeChange}"/>
</c:if>

<c:set var="style" value="smallSimplePopup"/>
<c:if test="${!empty pageScope.styleClass}">
    <c:set var="style" value="${pageScope.styleClass}"/>
</c:if>

<cti:msgScope paths="${nameKey},">
    <i:simplePopup id="${uniqueId}" styleClass="${style}" titleKey=".title" on="${pageScope.on}">
        <tags:cronExpressionData state="${pageScope.cronState}" id="${cronExpressionId}"
            allowTypeChange="${allowChange}" />

        <div class="tar">
            <c:choose>
                <c:when test="${empty pageScope.endAction || pageScope.endAction == 'nothing'}">
                    <c:if test="${empty pageScope.submitName}">
                        <cti:button styleClass="${pageScope.styleClass}" key="save" type="submit" />
                    </c:if>
                    <c:if test="${!empty pageScope.submitName}">
                        <cti:button styleClass="${pageScope.styleClass}" key="save" type="submit"
                            name="${pageScope.submitName}" />
                    </c:if>
                </c:when>
                <c:when test="${pageScope.endAction == 'hide'}">
                    <c:if test="${empty pageScope.submitName}">
                        <cti:button styleClass="${pageScope.styleClass}" key="save" type="submit"
                            onclick="$('${uniqueId}').hide()" />
                    </c:if>
                    <c:if test="${!empty pageScope.submitName}">
                        <cti:button styleClass="${pageScope.styleClass}" key="save" type="submit"
                            name="${pageScope.submitName}" onclick="$('${uniqueId}').hide()" />
                    </c:if>
                </c:when>
                <c:when test="${pageScope.endAction == 'block'}">
                    <c:if test="${empty pageScope.submitName}">
                        <cti:button styleClass="${pageScope.styleClass} f_blocker" key="save"
                            type="submit" />
                    </c:if>
                    <c:if test="${!empty pageScope.submitName}">
                        <cti:button styleClass="${pageScope.styleClass} f_blocker" key="save"
                            type="submit" name="${pageScope.submitName}" />
                    </c:if>
                </c:when>
            </c:choose>
            <cti:button key="cancel" onclick="$('${uniqueId}').hide()" />
        </div>
    </i:simplePopup>
</cti:msgScope>
<script type="text/javascript">
callAfterMainWindowLoad(function () {
    adjustDialogSizeAndPosition('${uniqueId}');
});
</script>