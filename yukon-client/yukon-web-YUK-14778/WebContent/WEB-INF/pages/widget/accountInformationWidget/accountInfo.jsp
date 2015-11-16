<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<script type="text/javascript">
$(function() {
    $("#cisDetailsLink").click(function(event){
        $("#cisDetails").dialog({width: "auto", minWidth: 400});
    });
});
</script>

<c:choose>
    <c:when test="${!hasVendorId}">
    	<i:inline key=".noVendor"/>
    </c:when>
    <c:otherwise>
        <%@ include file="accountInfoPartial.jspf" %>
        <div class="action-area">
            <a href="javascript:void(0);" id="cisDetailsLink" class="fl"><i:inline key=".viewDetails"/></a>
        </div>
        <div id="cisDetails" class="dn scroll-lg dialog-no-buttons" title="<cti:msg2 key=".cisDetails.title"/>" style="min-width: 400px;">
            <jsp:include page="/WEB-INF/pages/widget/accountInformationWidget/moreInfo.jsp" />
        </div>
    </c:otherwise>
</c:choose>