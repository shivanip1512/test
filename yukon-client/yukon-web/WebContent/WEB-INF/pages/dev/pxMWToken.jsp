<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:standardPage module="dev" page="pxMWToken">
    <div class="column-12-12 clearfix">
        <div class="column one">
            <p><b><i:inline key=".pxMWAuthToken" /></b></p>
            <textarea name="publicKey" rows="20" cols="60">${authToken}</textarea>
        </div>
    </div>
    <div class="page-action-area">
        <cti:url value="/dev" var="pxMWTokenPairbaseURL"/>
        <cti:button nameKey="cancel" name="cancel" href="${pxMWTokenPairbaseURL}"/>
    </div>
</cti:standardPage>