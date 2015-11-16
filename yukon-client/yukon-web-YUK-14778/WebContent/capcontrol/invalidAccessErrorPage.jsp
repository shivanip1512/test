<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage title="Invalid Access Error Page" module="blank">

<cti:breadCrumbs>
    <cti:crumbLink url="/capcontrol/tier/areas" title="Home" />
</cti:breadCrumbs>

<cti:yukonUser var="user"/>

<h2 align="center" style="color: red;">Invalid page access for user ${fn:escapeXml(user.username)}!</h2>

</cti:standardPage>