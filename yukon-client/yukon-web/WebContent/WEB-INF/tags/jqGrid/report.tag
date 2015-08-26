<%@ attribute name="columnInfo" required="true" type="java.util.ArrayList" %>
<%@ attribute name="dataUrl" required="true" type="java.lang.String" description="Assumed to have had ServletUtil.buildSafeQueryStringFromMap applied" %>
<%@ attribute name="height" required="true" type="java.lang.Integer" %>
<%@ attribute name="width" required="true" type="java.lang.String" %>
<%@ attribute name="showLoadMask" required="false" type="java.lang.String" description="Possible values: 'enable', 'disable', 'block'" %>
<%@ attribute name="refreshRate" required="false" type="java.lang.Integer" description="ms between refreshes.  0 Represents NO REFRESH." %>

<%@ attribute name="title" required="true" type="java.lang.String" %>
<%@ attribute name="csvUrl" required="true" type="java.lang.String" description="Assumed to have had cti:url applied" %>
<%@ attribute name="pdfUrl" required="true" type="java.lang.String" description="Assumed to have had cti:url applied" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:uniqueIdentifier var="gridId" prefix="gridId_"/>

<%--jqGrid --%>
<cti:includeScript link="/resources/js/lib/jqgrid/jqgrid.locale.js.jsp"/>
<cti:includeScript link="JQUERY_GRID"/>
<cti:includeScript link="JQUERY_GRID_HELPER"/>
<link rel="stylesheet" type="text/css" media="screen" href="<cti:url value="/resources/js/lib/jqgrid/css/ui.jqgrid.css"/>" />

<c:if test="${empty pageScope.showLoadMask}">
    <c:set var="showLoadMask" value="block"/>
</c:if>

<c:if test="${empty pageScope.refreshRate}">
    <c:set var="refreshRate" value="0"/>
</c:if>

<script type="text/javascript">
    
    $(document).ready(function(){
        
        jqGridHelper.createGrid({
        	pager: '#${gridId}_pager',
            id: "${gridId}",
            colModel:  ${cti:jsonString(columnInfo)},
            height: ${pageScope.height},
            loadui: '${showLoadMask}',
            refreshRate: ${pageScope.refreshRate},
            title: '${title}',
            toolbar: [true, "top"],
            url: "${dataUrl}",
            width: '${pageScope.width}'
        });
    });
</script>

<div class="jqgrid-container">
	<table id="${gridId}"><tr><td/></tr></table>
	<div id="${gridId}_pager"></div>
</div>

<div class="dn">
    <div id="${gridId}_toolbar_buttons" class="toolbar_buttons">
        <div class="fr fwn">
            <a href="#" class="refresh" data-grid-id="${gridId}"><i:inline key="components.refresh" /></a>
            <i:inline key="components.export"/>
            <a class="excel" href="${csvUrl}"><i:inline key="components.csv"/></a>
            <a class="pdf" href="${pdfUrl}"><i:inline key="components.pdf"/></a>
        </div>
    </div>
</div>
