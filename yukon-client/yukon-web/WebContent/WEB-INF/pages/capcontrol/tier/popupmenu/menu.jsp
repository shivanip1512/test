<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="yukon.web.modules.capcontrol.menu">

<style>
.menuOption {margin-top: 3px;}
.menuOption a {
    display: block;
    padding: 0 15px;
    clear: both;
    font-weight: normal;
    line-height: 18px;
    color: #555;
    white-space: nowrap;
    text-decoration: none;
    cursor: pointer;
}
.menuOption a:hover {
    text-decoration: none;
    background-color: whitesmoke;
}
</style>

<script type="text/javascript">
<c:if test="${showLocalControl}">
$('#localControlsOption').click(function(event) {
    yukon.da.common.hideMenu();
    yukon.da.common.getMenuFromURL(yukon.url('/capcontrol/menu/localControl?id=${paoId}'), event, {});
});
</c:if>
<c:if test="${showChangeOpState}">
$('#changeOpStateOption').click(function(event) {
    yukon.da.common.hideMenu();
    yukon.da.common.getMenuFromURL(yukon.url('/capcontrol/menu/opStateChange?bankId=${paoId}'), event, {});
});
</c:if>
<c:if test="${showResetBankOpCount}">
$('#resetBankOpCountOption').click(function(event) {
    yukon.da.common.hideMenu();
    yukon.da.common.getMenuFromURL(yukon.url('/capcontrol/menu/resetBankOpCount?bankId=${paoId}'), event, {});
});
</c:if>
<c:if test="${showRecentCommands}">
$('#recentEventsOption').click(function(event) {
    yukon.da.common.hideMenu();
    window.location.href = yukon.url('/capcontrol/search/recentEvents?value=${paoId}');
});
</c:if>
<c:if test="${showComments}">
$('#viewCommentsOption').click(function(event) {
    yukon.da.common.hideMenu();
    yukon.da.common.showDialog($(event.currentTarget).find('input').val(), yukon.url('/capcontrol/comments/paoComments?paoId=${paoId}'), {}, "#contentPopup" );
});
</c:if>
</script>

<cti:getProperty var="warnOnCommands" property='CONTROL_WARNING'/>

<div id="menuPopupBoxContainer">
	<input type="hidden" id="dialogTitle" value="${fn:escapeXml(paoName)}">
    <div class="js-no-field-message notes stacked dn"><i:inline key=".capBankState.note"/></div>
   
    <ul class="capcontrolMenu detail simple-list">
    
        <%--Commands --%>
        <input type="hidden" name="paoId" value="${paoId}">
        <input type="hidden" name="warnOnCommands" value="${warnOnCommands}">
        <c:forEach var="command" items="${commands}">
            <cti:msg2 var="commandName" key="${command}"/>
            <li class="menuOption command" value="${command.commandId}">
                <a href="javascript:void(0);">${commandName}</a>
                <span class="confirmMessage dn"><i:inline key="yukon.web.modules.capcontrol.command.confirm" arguments="${commandName}"/></span>
            </li>
        </c:forEach>
        
        <%--States --%>
        <c:forEach var="state" items="${states}">
            <li class="menuOption stateChange" value="${state.stateRawState}"><a href="javascript:void(0);">${state.stateText}</a></li>
        </c:forEach>
        
        <%--Special Links --%>
        <c:if test="${showResetBankOpCount}">
            <li class="menuOption" id="resetBankOpCountOption"><a href="javascript:void(0);"><i:inline key="${resetBankOpCount}"/></a></li>
        </c:if>
        <c:if test="${showChangeOpState}">
            <li class="menuOption" id="changeOpStateOption"><a href="javascript:void(0);"><i:inline key="${changeOpState}"/></a></li>
        </c:if>
        <c:if test="${showComments}">
            <li class="menuOption" id="viewCommentsOption">
                <input type="hidden" value="<cti:msg2 key="yukon.web.modules.capcontrol.comments.title" arguments="${paoName}"/>">
                <a href="javascript:void(0);"><i:inline key=".viewComments"/></a>
            </li>
        </c:if>
        <c:if test="${showRecentCommands}">
            <li class="menuOption" id="recentEventsOption"><a href="javascript:void(0);"><i:inline key=".viewRecentEvents"/></a></li>
        </c:if>
        <c:if test="${showLocalControl}">
            <li class="menuOption" id="localControlsOption"><a href="javascript:void(0);"><i:inline key=".moreControls"/></a></li>
        </c:if>
        
    </ul>
</div>

</cti:msgScope>     