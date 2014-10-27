<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.gateways.cert.update">

<cti:url var="url" value="/stars/gateways/cert-update"/>
<form id="gateway-cert-form" method="POST" enctype="multipart/form-data" action="${url}">
        
<tags:nameValueContainer2 tableClass="with-form-controls">
    <tags:nameValue2 nameKey=".file">
        <input type="file" name="file">
    </tags:nameValue2>
</tags:nameValueContainer2>

<h3><i:inline key=".gateways"/></h3>
<div class="column-12-12 clearfix js-select-all-container scroll-md">
<c:forEach var="gateway" items="${gateways}" varStatus="status">
    <c:choose>
        <c:when test="${status.index % 2 == 0}">
            <div class="column one stacked">
                <label>
                    <input class="js-select-all-item" type="checkbox" 
                        name="gateways" value="${gateway.paoIdentifier.paoId}">${fn:escapeXml(gateway.name)}
                </label>
            </div>
        </c:when>
        <c:otherwise>
            <div class="column two nogutter stacked">
                <label>
                    <input class="js-select-all-item" type="checkbox" 
                        name="gateways" value="${gateway.paoIdentifier.paoId}">${fn:escapeXml(gateway.name)}
                </label>
            </div>
        </c:otherwise>
    </c:choose>
</c:forEach>
<div>
    <label><input class="js-select-all" type="checkbox"><i:inline key="yukon.common.selectall"/></label>
</div>
</div>

</form>

</cti:msgScope>