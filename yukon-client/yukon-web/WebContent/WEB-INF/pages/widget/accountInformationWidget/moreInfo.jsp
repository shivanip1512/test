<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- CUSTOMER INFORMATION --%>
<cti:msg2 var="custInfo" key=".custInfo"/>
<tags:sectionContainer title="${custInfo}">
    <tags:nameValueContainer tableClass="row-highlighting striped two-column-table">
        <c:forEach var="x" items="${custBasicsInfo}">
            <tags:nameValue name="${x.label}">${x.value}</tags:nameValue>
        </c:forEach>
    </tags:nameValueContainer>
</tags:sectionContainer>

<cti:msg2 var="address" key=".address"/>
<%-- CUSTOMER CONTACT INFORMATION --%>
<tags:sectionContainer2 nameKey="contactInfo">
    <tags:nameValueContainer tableClass="row-highlighting striped two-column-table">
        <c:forEach var="x" items="${custContactInfo}">
            <tags:nameValue name="${x.label}">${x.value}</tags:nameValue>
        </c:forEach>
        <tags:nameValue name="${address}"><tags:address address="${custAddress}"/></tags:nameValue>
    </tags:nameValueContainer>
</tags:sectionContainer2>

<%-- SERVICE LOCATION INFORMATION --%>
<cti:msg2 var="serviceLocation" key=".serviceLocation"/>
<tags:sectionContainer title="${serviceLocation}">
    <tags:nameValueContainer tableClass="row-highlighting striped two-column-table">
        <c:forEach var="x" items="${servLocBasicsInfo}">
            <tags:nameValue name="${x.label}">${x.value}</tags:nameValue>
        </c:forEach>
        <tags:nameValue name="${address}"><tags:address address="${servLocAddress}" /></tags:nameValue>
    </tags:nameValueContainer>
</tags:sectionContainer>

<%-- SERVICE LOCATION NETWORK INFORMATION --%>
<tags:sectionContainer2 nameKey="network">
    <tags:nameValueContainer tableClass="row-highlighting striped two-column-table">
        <c:forEach var="x" items="${servLocNetworkInfo}">
            <tags:nameValue name="${x.label}">${x.value}</tags:nameValue>
        </c:forEach>
    </tags:nameValueContainer>
</tags:sectionContainer2>

<%-- METER BASIC INFORMATION --%>
<tags:sectionContainer2 nameKey="meter">
    <tags:nameValueContainer tableClass="row-highlighting striped two-column-table">
        <c:forEach var="x" items="${meterBasicsInfo}">
            <tags:nameValue name="${x.label}">${x.value}</tags:nameValue>
        </c:forEach>
    </tags:nameValueContainer>
</tags:sectionContainer2>

<%-- METER NAMEPLATE INFORMATION --%>
<tags:sectionContainer2 nameKey="nameplate">
    <tags:nameValueContainer tableClass="row-highlighting striped two-column-table">
        <c:forEach var="x" items="${meterNameplateInfo}">
            <tags:nameValue name="${x.label}">${x.value}</tags:nameValue>
        </c:forEach>
    </tags:nameValueContainer>
</tags:sectionContainer2>

<%-- METER UTILITY INFORMATION --%>
<tags:sectionContainer2 nameKey="utilityInfo">
    <tags:nameValueContainer tableClass="row-highlighting striped two-column-table">
        <c:forEach var="x" items="${meterUtilInfoInfo}">
            <tags:nameValue name="${x.label}">${x.value}</tags:nameValue>
        </c:forEach>
    </tags:nameValueContainer>
</tags:sectionContainer2>