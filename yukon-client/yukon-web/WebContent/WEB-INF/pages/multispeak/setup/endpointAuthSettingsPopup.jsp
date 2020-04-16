<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.web.modules.adminSetup.vendor">
    <tags:setFormEditMode mode="${mode}" />
    <form:form modelAttribute="multispeakInterface" id="js-vendor-endpointauth-form">
        <cti:csrfToken/>
        <input type="hidden" id="js-interface-index-value" value="${indexValue}"/>
        <tags:checkbox id="js-dialog-usevendorauth" path="useVendorAuth" descriptionNameKey="yukon.web.modules.adminSetup.vendor.interfaceAuthPopupTitle.useVendorAuth" />
        <tags:sectionContainer2 nameKey="interfaceAuthPopupTitle.incomingAuth">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".userName">
                    <tags:input id="js-dialog-inusername" path="inUserName" maxlength="64" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".password">
                    <tags:password cssClass="js-dialog-inpassword" path="inPassword" showPassword="true" includeShowHideButton="true" maxlength="64" />
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
        <tags:sectionContainer2 nameKey="interfaceAuthPopupTitle.outgoingAuth">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".userName">
                    <tags:input id="js-dialog-outusername" path="outUserName" maxlength="64" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".password">
                    <tags:password cssClass="js-dialog-outpassword" path="outPassword" showPassword="true" includeShowHideButton="true" maxlength="64" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".validateCertificate">
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <tags:switch id="js-dialog-validatecertificate" path="validateCertificate" classes="toggle-sm"/>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <tags:switch id="js-dialog-validatecertificate" path="validateCertificate" classes="toggle-sm" disabled="true" />
                    </cti:displayForPageEditModes>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
    </form:form>
</cti:msgScope>