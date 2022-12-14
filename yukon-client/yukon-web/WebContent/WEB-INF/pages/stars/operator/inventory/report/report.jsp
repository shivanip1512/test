<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="inventory.report">
<cti:includeScript link="/resources/js/pages/yukon.assets.report.js"/>
<style>
#device-report { min-height: 300px; }
</style>

<div id="page-buttons">
<cti:url var="url" value="/stars/operator/inventory/report/download">
    <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
        <cti:param name="${parm.key}" value="${parm.value}"/>
    </c:forEach>
</cti:url>

<script type="text/javascript">
function disableDownload(){
    var url = "${url}";
    window.location = url;
    $('#downloadBtn').attr("disabled","disabled");
    //disable download button for a while
    setTimeout(function() {
        $("#downloadBtn").removeAttr("disabled");      
    }, 5000); 
}
</script>

<cti:button nameKey="download" id="downloadBtn" icon="icon-page-white-excel" href="${url}" onclick="disableDownload();"/>
</div>

<div class="stacked-md">
    <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="device-report-collection"/>
</div>

<cti:url var="url" value="report/data">
    <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
        <cti:param name="${parm.key}" value="${parm.value}"/>
    </c:forEach>
</cti:url>
<div id="device-report" data-url="${url}"></div>

</cti:standardPage>