<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.web.modules.adminSetup.vendor">
    <tags:setFormEditMode mode="${mode}" />
    <cti:url var="action" value="/multispeak/setup/endpointAuthPopup/save" />
    <form:form modelAttribute="multispeakInterface" action="${action}" method="post" id="js-vendor-endpointauth-form">
        <cti:csrfToken/>
        <input type="hidden" id="js-interface-index-value" value="${indexValue}"/>
        <tags:checkbox path="useVendorAuth" descriptionNameKey="yukon.web.modules.adminSetup.vendor.interfaceAuthPopupTitle.useVendorAuth" />
        <tags:sectionContainer2 nameKey="interfaceAuthPopupTitle.incomingAuth">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".userName">
                    <tags:input path="inUserName" maxlength="64" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".password">
                    <tags:password path="inPassword" showPassword="true" includeShowHideButton="true" maxlength="64" />
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
        <tags:sectionContainer2 nameKey="interfaceAuthPopupTitle.outgoingAuth">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".userName">
                    <tags:input path="outUserName" maxlength="64" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".password">
                    <tags:password path="outPassword" showPassword="true" includeShowHideButton="true" maxlength="64" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".validateCertificate">
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <tags:switch path="validateCertificate" classes="toggle-sm"/>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <tags:switch path="validateCertificate" classes="toggle-sm" disabled="true" />
                    </cti:displayForPageEditModes>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
    </form:form>
</cti:msgScope>