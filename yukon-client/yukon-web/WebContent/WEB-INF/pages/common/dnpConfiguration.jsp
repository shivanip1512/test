<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<tags:sectionContainer2 nameKey="dnpConfiguration" styleClass="stacked-lg">
    <tags:nameValueContainer2 tableClass="natural-width js-dnp-fields js-block-this">
        <tags:nameValue2 nameKey=".dnpConfig">
            <cti:displayForPageEditModes modes="VIEW,EDIT">
                <tags:selectWithItems id="dnp-config" items="${configs}" path="dnpConfigId"
                                      itemLabel="name" itemValue="configurationId" />
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="CREATE">
                <form:hidden path="dnpConfigId" />
                ${fn:escapeXml(dnpConfig.name)}
            </cti:displayForPageEditModes>
        </tags:nameValue2>
        <c:set var="dnpCategoryAlertClass" value="${isDnpConfigCategoryAssigned ? 'dn' : ''}"/>
        <c:set var="showDnpFields" value="${isDnpConfigCategoryAssigned ? '' : 'dn'}"/>
        
        <cti:msg2 var="dnpCategoryErrorMsg" key="yukon.web.modules.tools.configs.category.dnp.unassigned"/>
        <input id="dnpCategoryError" type="hidden" value="${dnpCategoryErrorMsg}"/>
        
        <tags:alertBox key="yukon.web.modules.tools.configs.category.dnp.unassigned" arguments="${dnpConfig.name}"
                       classes="js-dnp-category-alert ${dnpCategoryAlertClass}"/>
        <tags:nameValue2 nameKey=".internalRetries" valueClass="js-dnp-field js-dnp-internalRetries" 
                         rowClass="js-dnp-category ${showDnpFields}">
            ${dnpConfig.internalRetries}
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".timeOffset" valueClass="js-dnp-field js-dnp-timeOffset" 
                         rowClass="js-dnp-category ${showDnpFields}">
            <i:inline key="yukon.web.modules.tools.configs.enum.dnpTimeOffset.${dnpConfig.timeOffset}"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".enableTimeSync" valueClass="js-dnp-field js-dnp-enableDnpTimesyncs"
                         rowClass="js-dnp-category ${showDnpFields}">
            ${dnpConfig.enableDnpTimesyncs}
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".omitTimeReq" valueClass="js-dnp-field js-dnp-omitTimeRequest" 
                         rowClass="js-dnp-category ${showDnpFields}">
            ${dnpConfig.omitTimeRequest}
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".unsolicit1" valueClass="js-dnp-field js-dnp-enableUnsolicitedMessagesClass1"
                         rowClass="js-dnp-category ${showDnpFields}">
            ${dnpConfig.enableUnsolicitedMessageClass1}
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".unsolicit2" valueClass="js-dnp-field js-dnp-enableUnsolicitedMessagesClass2"
                         rowClass="js-dnp-category ${showDnpFields}">
            ${dnpConfig.enableUnsolicitedMessageClass2}
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".unsolicit3" valueClass="js-dnp-field js-dnp-enableUnsolicitedMessagesClass3"
                         rowClass="js-dnp-category ${showDnpFields}">
            ${dnpConfig.enableUnsolicitedMessageClass3}
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".nonUpdated" valueClass="js-dnp-field js-dnp-enableNonUpdatedOnFailedScan"
                         rowClass="js-dnp-category ${showDnpFields}">
            ${dnpConfig.enableNonUpdatedOnFailedScan}
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>
<cti:includeScript link="/resources/js/common/yukon.dnpconfiguration.js" />
