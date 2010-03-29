<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>

<cti:standardPage module="operator" page="optOut">

<h3><i:inline key="yukon.dr.operator.optoutError.header"/></h3>

    <cti:msg key="${error}"/>

    <br>
    <br>

    <cti:url var="optOutUrl" value="/spring/stars/operator/program/optOut">
        <cti:param name="accountId" value="${customerAccount.accountId}"/>
        <cti:param name="energyCompanyId" value="${energyCompanyId}" />
    </cti:url>
    <input type="button" value='<cti:msg key="yukon.dr.operator.optoutError.ok"/>'
           onclick="location.href='<c:out value="${optOutUrl}"/>';"></input>
</cti:standardPage>