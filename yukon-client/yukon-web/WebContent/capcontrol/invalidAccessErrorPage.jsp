<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:standardPage title="Invalid Access Error Page" module="blank">

<cti:standardMenu/>

<cti:breadCrumbs>
    <cti:crumbLink url="/capcontrol/tier/areas" title="Home" />
</cti:breadCrumbs>

<cti:yukonUser var="user"/>

<h2 align="center" style="color: red;">Invalid page access for user ${user.username}!</h2>

</cti:standardPage>