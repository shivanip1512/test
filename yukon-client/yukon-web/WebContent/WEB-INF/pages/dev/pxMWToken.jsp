<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<cti:standardPage module="dev" page="pxMWToken">
    <c:if test="${false}">
        <tags:alertBox>Decryption was not successful with provided key.</tags:alertBox>
    </c:if>


            <cti:csrfToken/>
            <div class="column-12-12 clearfix">
                <div class="column one">
                    <p><b><i:inline key=".pxMWAuthToken" /></b></p>
                    <textarea path="itronPublicKey" name="publicKey" rows="20" cols="60">${authToken}</textarea>
                </div>
            </div>
            <div class="page-action-area">

                <cti:url value="/dev" var="pxMWTokenPairbaseURL"/>
                <cti:button nameKey="cancel" name="cancel" href="${pxMWTokenPairbaseURL}"/>
            </div>


</cti:standardPage>