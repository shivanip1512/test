<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<div class="energyCompanyInfoFragment">

    <h2 class="standardPageHeading">${energyCompanyInfoFragment.companyName}</h2>
    
    <tags:address address="${energyCompanyInfoFragment.address}"/>
    
</div>