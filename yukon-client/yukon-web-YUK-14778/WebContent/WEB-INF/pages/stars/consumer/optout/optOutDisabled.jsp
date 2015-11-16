<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:url var="generalUrl" value="/stars/consumer/general"/>

<cti:standardPage module="consumer" page="optoutdisabled">
    <h3><i:inline key=".header"/></h3>
    <div align="center">
        <i:inline key=".msg"/>
        <br>
        <br>
        <cti:button nameKey="home" href="/stars/consumer/general"/>
    </div>
</cti:standardPage>