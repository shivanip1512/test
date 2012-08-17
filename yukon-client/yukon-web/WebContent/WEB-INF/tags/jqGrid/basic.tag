<%@ attribute name="columnInfo" required="true" type="java.util.ArrayList"%>
<%@ attribute name="rows" required="true" type="java.util.ArrayList"%>
<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="total" required="true" type="java.lang.Integer"%>

<%@ attribute name="height" required="false" type="java.lang.Integer"%>
<%@ attribute name="width" required="false" type="java.lang.String"%>
<%@ attribute name="showLoadMask" required="false" type="java.lang.String" description="Possible values: 'enable', 'disable', 'block'"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:uniqueIdentifier var="gridId" prefix="gridId_"/>

<%--jqGrid --%>
<cti:includeScript link="JQUERY"/>
<cti:includeScript link="JQUERY_GRID"/>
<cti:includeScript link="JQUERY_GRID_HELPER"/>
<link rel="stylesheet" type="text/css" media="screen" href="/JavaScript/lib/jQuery/plugins/jqGrid/4.2.0/css/ui.jqgrid.css" />


<c:if test="${empty pageScope.showLoadMask}">
    <c:set var="showLoadMask" value="block"/>
</c:if>

<c:if test="${empty pageScope.height}">
	<c:set var="height" value="100" />
</c:if>

<c:if test="${empty pageScope.width}">
	<c:set var="width" value="" />
</c:if>

<script type="text/javascript">
    jQuery(document).ready(function(){
        jqGridHelper.createGrid({
			pager: '#${gridId}_pager',
            id: "${gridId}",
            colModel: ${cti:jsonString(columnInfo)},
            data: {page:1, total:${total}, rows:${cti:jsonString(rows)}},
            height: ${height},
            width: '${width}',
            toolbar: [true, "top"],
            loadui: '${showLoadMask}',
            title: '${title}',
        });
    });
</script>

<div class="jqgrid-container">
	<table id="${gridId}"><tr><td/></tr></table>
	<div id="${gridId}_pager"></div>
</div>