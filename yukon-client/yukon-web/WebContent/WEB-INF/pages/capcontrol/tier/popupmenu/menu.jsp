<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:msgScope paths="yukon.web.modules.capcontrol.menu">

<script type="text/javascript">
<c:if test="${showLocalControl}">
$('localControlsOption').observe('click', function(event) {
    hideMenu();
    getMenuFromURL('/spring/capcontrol/menu/localControl?id=${paoId}', event);
});
</c:if>
<c:if test="${showChangeOpState}">
$('changeOpStateOption').observe('click', function(event) {
    hideMenu();
    getMenuFromURL('/spring/capcontrol/menu/opStateChange?bankId=${paoId}', event);
});
</c:if>
<c:if test="${showResetBankOpCount}">
$('resetBankOpCountOption').observe('click', function(event) {
    hideMenu();
    getMenuFromURL('/spring/capcontrol/menu/resetBankOpCount?bankId=${paoId}', event);
});
</c:if>
<c:if test="${showRecentCommands}">
$('recentEventsOption').observe('click', function(event) {
    hideMenu();
    window.location = '/spring/capcontrol/search/recentEvents?value=${paoId}';
});
</c:if>
<c:if test="${showComments}">
$('viewCommentsOption').observe('click', function(event) {
    hideMenu();
    showDialog($F(event.findElement('li').down('input')), '/spring/capcontrol/comments/paoComments?paoId=${paoId}');
});
</c:if>
</script>

<cti:getProperty var="warnOnCommands" property='CBCSettingsRole.CONTROL_WARNING'/>

<div id="menuPopupBoxContainer" class="thinBorder">
    <div class="titledContainer boxContainer">
    
        <div class="titleBar boxContainer_titleBar">
            <div class="controls" onclick="$('menuPopup').hide()">
                <img class="minMax" alt="close" src="/WebConfig/yukon/Icons/close_x.gif">
            </div>
            <div class="title boxContainer_title">${paoName}</div>
        </div>
        
        <div class="content boxContainer_content">
        
            <ul class="capcontrolMenu">
            
                <%--Commands --%>
                <input type="hidden" name="paoId" value="${paoId}">
                <input type="hidden" name="warnOnCommands" value="${warnOnCommands}">
                <c:forEach var="command" items="${commands}">
                    <cti:msg2 var="commandName" key="${command}"/>
                    <li class="menuOption command" value="${command.commandId}">
                        <span>${commandName}</span>
                        <span class="confirmMessage dn"><i:inline key="yukon.web.modules.capcontrol.command.confirm" arguments="${commandName}"/></span>
                    </li>
                </c:forEach>
                
                <%--States --%>
                <c:forEach var="state" items="${states}">
                    <li class="menuOption stateChange" value="${state.stateRawState}">${state.stateText}</li>
                </c:forEach>
                
                <%--Special Links --%>
                <c:if test="${showResetBankOpCount}">
                    <li class="menuOption" id="resetBankOpCountOption"><i:inline key="${resetBankOpCount}"/></li>
                </c:if>
                <c:if test="${showChangeOpState}">
                    <li class="menuOption" id="changeOpStateOption"><i:inline key="${changeOpState}"/></li>
                </c:if>
                <c:if test="${showComments}">
                    <li class="menuOption" id="viewCommentsOption">
                        <input type="hidden" id="commentsTitle" value="<cti:msg2 key="yukon.web.modules.capcontrol.comments.title" arguments="${paoName}"/>">
                        <i:inline key=".viewComments"/>
                    </li>
                </c:if>
                <c:if test="${showRecentCommands}">
                    <li class="menuOption" id="recentEventsOption"><i:inline key=".viewRecentEvents"/></li>
                </c:if>
                <c:if test="${showLocalControl}">
                    <li class="menuOption" id="localControlsOption"><i:inline key=".moreControls"/></li>
                </c:if>
                
            </ul>
        </div>
        
    </div>
</div>

</cti:msgScope>     