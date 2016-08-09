<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<div class="info-fragment">
    <strong class="wbba dib">${fn:escapeXml(energyCompanyInfoFragment.companyName)}</strong>
    <tags:address address="${energyCompanyInfoFragment.address}"/>
</div>