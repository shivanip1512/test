<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.gateways.cert.update">

<c:if test="${not empty errorMsg}"><tags:alertBox>${errorMsg}</tags:alertBox></c:if>

<cti:url var="url" value="/stars/gateways/cert-update"/>
<form id="gateway-cert-form" method="POST" enctype="multipart/form-data" action="${url}">

<cti:csrfToken/>

<tags:nameValueContainer2 tableClass="with-form-controls">
    <tags:nameValue2 nameKey=".file">
        <tags:file name="file"/>
    </tags:nameValue2>
</tags:nameValueContainer2>

<h3><i:inline key=".gateways"/></h3>
<div class="empty-list stacked">
    <i:inline key=".note"/>
</div>

<div class="column-12-12 clearfix js-select-all-container scroll-md js-resize-with-dialog">
<c:forEach var="gateway" items="${gateways}" varStatus="status">
    <c:set var="checked" value="${checkedGateways[gateway.paoIdentifier.paoId]}"/>
    
    <c:choose>
        <c:when test="${status.index % 2 == 0}">
            <div class="column one stacked">
                <label>
                    <input class="js-select-all-item" type="checkbox" 
                        name="gateways" value="${gateway.paoIdentifier.paoId}" ${checked}>${fn:escapeXml(gateway.name)}
                </label>
            </div>
        </c:when>
        <c:otherwise>
            <div class="column two nogutter stacked">
                <label>
                    <input class="js-select-all-item" type="checkbox" 
                        name="gateways" value="${gateway.paoIdentifier.paoId}" ${checked}>${fn:escapeXml(gateway.name)}
                </label>
            </div>
        </c:otherwise>
    </c:choose>
</c:forEach>
<div>
    <label><input class="js-select-all" type="checkbox" ${selectAll}><i:inline key="yukon.common.selectall"/></label>
</div>
</div>

</form>

</cti:msgScope>