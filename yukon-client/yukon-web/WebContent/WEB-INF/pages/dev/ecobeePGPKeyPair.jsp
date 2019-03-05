<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="ecobeePGPKeyPair">
    <cti:dataGrid cols="2">
        <form  action="saveEcobeeKeyPair" modelAttribute="pgpKeyPair" method="POST">
            <cti:csrfToken/>
            <div class="column-12-12 clearfix">
                <div class="column one">
                    <p><b><i:inline key=".pgpPublicKey" /></b></p>
                    <textarea path="pgpPublicKey" name="pgpPublicKey" rows="20" cols="60">${pgpKeyPair.pgpPublicKey}</textarea>
                </div>
                <div class="column two nogutter">
                    <p><b><i:inline key=".pgpPrivateKey" /></b></p>
                    <textarea path="pgpPrivateKey" name="pgpPrivateKey" rows="20" cols="60">${pgpKeyPair.pgpPrivateKey}</textarea>
                </div>
            </div>
            <div class="page-action-area">
                <cti:button label="Save" type="submit" />
                <cti:url value="/dev" var="pgpKeyPairbaseURL"/>
                <cti:button nameKey="cancel" name="cancel" href="${pgpKeyPairbaseURL}"/>
            </div>
        </form> 
    </cti:dataGrid>
</cti:standardPage>

