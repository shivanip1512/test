<%@ attribute name="columnInfo" required="true" type="java.util.ArrayList"%>
<%@ attribute name="url" required="true" type="java.lang.String"%>
<%@ attribute name="title" required="true" type="java.lang.String"%>

<%@ attribute name="height" required="false" type="java.lang.Integer"%>
<%@ attribute name="width" required="false" type="java.lang.String"%>
<%@ attribute name="showLoadMask" required="false" type="java.lang.String" description="Possible values: 'enable', 'disable', 'block'"%>
<%@ attribute name="refreshRate" required="false" type="java.lang.Integer" description="ms between refreshes.  0 Represents NO REFRESH." %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:uniqueIdentifier var="gridId" prefix="gridId_"/>

<%--jqGrid --%>
<cti:includeScript link="JQUERY"/>
<cti:includeScript link="/resources/js/lib/jqgrid/jqgrid.locale.js.jsp"/>
<cti:includeScript link="JQUERY_GRID"/>
<cti:includeScript link="JQUERY_GRID_HELPER"/>
<link rel="stylesheet" type="text/css" media="screen" href="<cti:url value="/resources/js/lib/jqgrid/css/ui.jqgrid.css"/>"/>

<c:if test="${empty pageScope.showLoadMask}">
    <c:set var="showLoadMask" value="block"/>
</c:if>

<c:if test="${not empty pageScope.height}">
	<c:set var="height" value="" />
</c:if>

<c:if test="${not empty pageScope.width}">
	<c:set var="width" value="" />
</c:if>

<script type="text/javascript">
    
    $(document).ready(function(){
        
        jqGridHelper.createGrid({
        	pager: '#${gridId}_pager',
            id: "${gridId}",
            colModel: ${cti:jsonString(columnInfo)},
            loadui: '${showLoadMask}',
            title: '${title}',
            url: "${cti:escapeJavaScript(url)}",
            height: ${height},
            width: '${width}'
        });
    });
</script>

<div class="jqgrid-container">
	<table id="${gridId}"><tr><td/></tr></table>
	<div id="${gridId}_pager"></div>
</div>
