<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:choose>
<c:when test="${!hasVendorId}">
	<i:inline key=".noVendor"/>
</c:when>
<c:otherwise>

<%-- MORE INFO LINK --%>
<div style="text-align:right;padding-right:20px;font-size:12px;">
    <a href="javascript:void(0);" onclick="$('moreInfo').toggle();"><i:inline key=".viewDetails"/></a>
</div>

<div id="moreInfoDiv">
<tags:simplePopup id="moreInfo" title="CIS Details" onClose="$('moreInfo').toggle();">
    <div style="height:300px;overflow:auto;">
        <jsp:include page="/WEB-INF/pages/widget/accountInformationWidget/moreInfo.jsp" />
    </div>
</tags:simplePopup>
</div>

<%@ include file="accountInfoPartial.jspf" %>

</c:otherwise>
</c:choose>
