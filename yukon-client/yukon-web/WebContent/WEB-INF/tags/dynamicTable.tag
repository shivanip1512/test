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

<cti:includeScript link="/JavaScript/dynamicTable.js"/>
<c:if test="${empty pageScope.addItemParameters}">
    <c:set var="addItemParameters" value="null"/>
</c:if>
<c:if test="${empty pageScope.addButtonClass}">
    <c:set var="addButtonClass" value="addItem"/>
</c:if>

<script type="text/javascript">
${id} = new DynamicTable('${id}', ${fn:length(items)}, ${addItemParameters});
</script>


<cti:msgScope paths=".${nameKey},">
    <cti:msg2 var="noItemsMsg" key=".noItems"/>
</cti:msgScope>

<div id="${id}_wrapper" class="dynamicTableWrapper">
    <jsp:doBody/>
    <c:if test="${empty items}">
        <div class="noItemsMessage">${pageScope.noItemsMsg}</div>
    </c:if>
    <div class="actionArea">
        <cti:button nameKey="add" styleClass="f_blocker ${addButtonClass}"/>
    </div>
    <div style="display: none;" class="tempRequest"></div>
</div>

<script type="text/javascript">
callAfterMainWindowLoad(${id}.init.bind(${id}));
</script>
