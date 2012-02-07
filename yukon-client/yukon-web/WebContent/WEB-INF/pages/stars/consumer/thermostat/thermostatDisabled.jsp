<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:url var="generalUrl" value="/spring/stars/consumer/general"/>

<cti:standardPage module="consumer" page="thermostatDisabled">
    <cti:standardMenu />
    <h3><i:inline key=".header"/></h3>
    <div align="center">
        <i:inline key=".msg"/>
        <br>
        <br>
        <cti:button key=".home" href="/spring/stars/consumer/general"/>
    </div>
</cti:standardPage>