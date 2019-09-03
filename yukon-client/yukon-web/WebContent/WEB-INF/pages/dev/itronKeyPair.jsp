<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<cti:standardPage module="dev" page="itronKeyPair">
    <c:if test="${decryptFailed}">
        <tags:alertBox>Decryption was not successful with provided key.</tags:alertBox>
    </c:if>
    <cti:dataGrid cols="2">
        <form  action="saveItronKeyPair" modelAttribute="keyPair" method="POST">
            <cti:csrfToken/>
            <div class="column-12-12 clearfix">
                <div class="column one">
                    <p><b><i:inline key=".itronPublicKey" /></b></p>
                    <textarea path="itronPublicKey" name="publicKey" rows="20" cols="60">${keyPair.publicKey}</textarea>
                </div>
                <div class="column two nogutter">
                    <p><b><i:inline key=".itronPrivateKey" /></b></p>
                    <textarea path="itonrPrivateKey" name="privateKey" rows="20" cols="80">${keyPair.privateKey}</textarea>
                </div>
            </div>
            <div class="page-action-area">
                <cti:button label="Save" type="submit" />
                <cti:url value="/dev" var="itronKeyPairbaseURL"/>
                <cti:button nameKey="cancel" name="cancel" href="${itronKeyPairbaseURL}"/>
            </div>
        </form> 
    </cti:dataGrid>
</cti:standardPage>