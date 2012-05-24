<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:msgScope paths="yukon.web.modules.capcontrol.menu">

<script type="text/javascript">
<c:if test="${showLocalControl}">
jQuery('#localControlsOption').click(function(event) {
    hideMenu();
    getMenuFromURL('/spring/capcontrol/menu/localControl?id=${paoId}', event, {position: "center", modal: true});
});
</c:if>
<c:if test="${showChangeOpState}">
jQuery('#changeOpStateOption').click(function(event) {
    hideMenu();
    getMenuFromURL('/spring/capcontrol/menu/opStateChange?bankId=${paoId}', event, {position: "center", modal: true});
});
</c:if>
<c:if test="${showResetBankOpCount}">
jQuery('#resetBankOpCountOption').click(function(event) {
    hideMenu();
    getMenuFromURL('/spring/capcontrol/menu/resetBankOpCount?bankId=${paoId}', event, {position: "center", modal: true});
});
</c:if>
<c:if test="${showRecentCommands}">
jQuery('#recentEventsOption').click(function(event) {
    hideMenu();
    window.location = '/spring/capcontrol/search/recentEvents?value=${paoId}';
});
</c:if>
<c:if test="${showComments}">
jQuery('#viewCommentsOption').click(function(event) {
    hideMenu();
    showDialog(jQuery(event.currentTarget).find('input').val(), '/spring/capcontrol/comments/paoComments?paoId=${paoId}');
});
</c:if>
</script>

<cti:getProperty var="warnOnCommands" property='CBCSettingsRole.CONTROL_WARNING'/>

<div id="menuPopupBoxContainer">
	<input type="hidden" id="dialogTitle" value="${paoName}">
   
	<div class="content boxContainer_content">
	
	    <ul class="capcontrolMenu">
	    
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
	                <input type="hidden" id="commentsTitle" value="<cti:msg2 key="yukon.web.modules.capcontrol.comments.title" arguments="${paoName}"/>">
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
</div>

</cti:msgScope>     