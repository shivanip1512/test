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
</script>

<cti:getProperty var="warnOnCommands" property='CONTROL_CONFIRMATION_POPUPS'/>

<div id="menuPopupBoxContainer">
	<input type="hidden" id="dialogTitle" value="${fn:escapeXml(paoName)}">
    <div class="js-no-field-message notes stacked dn"><i:inline key=".capBankState.note"/></div>
   
    <ul class="capcontrolMenu detail simple-list">
    
        <input type="hidden" name="paoId" value="${paoId}">
        <input type="hidden" name="warnOnCommands" value="${warnOnCommands}">
    
        <%--Yukon Actions--%>
        <c:if test="${!empty yukonActions}">
            <div class="PB10">
                <i:inline key=".yukonActions" arguments="${capControlType}"/>
                <c:forEach var="command" items="${yukonActions}">
                    <%@ include file="commandLineItem.jspf" %>
                </c:forEach> 
            </div>
        </c:if>
        
        <%--Non-Operation Commands--%>
        <c:if test="${!empty nonOperationCommands}">
            <div class="PB10">
                <i:inline key=".nonOperationCommands"/>
                <c:forEach var="command" items="${nonOperationCommands}">
                    <%@ include file="commandLineItem.jspf" %>
                </c:forEach> 
            </div>
        </c:if>

        <%--Field Operation Commands--%>
        <c:if test="${!empty fieldOperationCommands}">
            <i:inline key=".fieldOperationCommands"/>
            <c:forEach var="command" items="${fieldOperationCommands}">
                <%@ include file="commandLineItem.jspf" %>
            </c:forEach> 

            <c:if test="${showLocalControl}">
                <li class="menuOption" id="localControlsOption"><a href="javascript:void(0);"><i:inline key=".moreControls"/></a></li>
            </c:if>
        </c:if>
        
        <!-- Local Commands -->
        <c:if test="${!empty commands}">
            <c:forEach var="command" items="${commands}">
                <%@ include file="commandLineItem.jspf" %>
            </c:forEach> 
        </c:if>
                    
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
    </ul>
</div>

</cti:msgScope>     