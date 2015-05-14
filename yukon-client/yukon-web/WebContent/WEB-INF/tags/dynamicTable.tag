<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ tag description="Create a table which can have rows added and removed dynamically.  Also
    supports ordering of rows." %>
<%@ attribute name="id" required="true" %>
<%@ attribute name="items" type="java.lang.Object" required="true" %>
<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="addItemParameters" %>
<%@ attribute name="addButtonClass" description="Use this if you need your own behavior for addItem." %>
<%@ attribute name="noBlockOnAdd" description="Set to true to prevent page blocking when the add button is clicked." %>
<%@ attribute name="disableAddButton" description="Add button can be disabled if necessary, as when the table is rendered but not yet ready to have rows added." %>

<cti:includeScript link="/JavaScript/yukon.table.dynamic.js"/>
<c:if test="${empty pageScope.addItemParameters}">
    <c:set var="addItemParameters" value="null"/>
</c:if>
<c:if test="${empty pageScope.addButtonClass}">
    <c:set var="addButtonClass" value="addItem"/>
</c:if>
<c:if test="${empty pageScope.noBlockOnAdd || !pageScope.noBlockOnAdd}">
    <c:set var="blockOnAdd" value="js-blocker"/>
</c:if>
<cti:default var="disableAddButton" value="false" />


<script type="text/javascript">
${id} = new DynamicTable('${id}', ${fn:length(items)}, ${addItemParameters});
</script>


<cti:msgScope paths=".${nameKey},">
    <cti:msg2 var="noItemsMsg" key=".noItems"/>
</cti:msgScope>

<div id="${id}_wrapper" class="dynamicTableWrapper">
    <jsp:doBody/>
    <c:if test="${empty items}">
        <div class="js-no-items-message">${pageScope.noItemsMsg}</div>
    </c:if>
    <div class="action-area">
        <cti:button nameKey="add" classes="${blockOnAdd} ${addButtonClass}" icon="icon-add" disabled="${disableAddButton}"/>
    </div>
    <div style="display: none;" class="tempRequest"></div>
</div>

<script type="text/javascript">
$(function () {
    ${id}.init.bind(${id})();
});
</script>
