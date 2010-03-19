<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="energyCompanyAdmin" page="createApplianceCategory">

    <cti:includeScript link="/JavaScript/iconChooser.js"/>

    <tags:simpleDialog id="acDialog"
        onClose="javascript:window.location='/operator/Admin/ConfigEnergyCompany.jsp'"/>

    <cti:url var="detailUrl"
        value="/spring/stars/dr/admin/applianceCategory/createDetails"/>

    <cti:msg2 var="dialogTitleText" key=".detailsDialogTitle" javaScriptEscape="true"/>

    <script type="text/javascript">
        openSimpleDialog('acDialog', '${detailUrl}', '${dialogTitleText}')
    </script>

</cti:standardPage>
