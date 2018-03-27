<tags:sectionContainer2 nameKey="dnpConfiguration" styleClass="stacked-lg">
    <tags:nameValueContainer2 tableClass="natural-width">
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
    </tags:nameValueContainer2>
    <tags:nameValueContainer2 tableClass="natural-width js-dnp-fields js-block-this">
        <tags:nameValue2 nameKey=".internalRetries" valueClass="js-dnp-field js-dnp-internalRetries">
            ${dnpConfig.internalRetries}
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".timeOffset" valueClass="js-dnp-field js-dnp-timeOffset">
            <i:inline key="yukon.web.modules.tools.configs.enum.dnpTimeOffset.${dnpConfig.timeOffset}"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".enableTimeSync" valueClass="js-dnp-field js-dnp-enableDnpTimesyncs">
            ${dnpConfig.enableDnpTimesyncs}
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".omitTimeReq" valueClass="js-dnp-field js-dnp-omitTimeRequest">
        ${dnpConfig.omitTimeRequest}
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".unsolicit1" valueClass="js-dnp-field js-dnp-enableUnsolicitedMessagesClass1">
            ${dnpConfig.enableUnsolicitedMessageClass1}
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".unsolicit2" valueClass="js-dnp-field js-dnp-enableUnsolicitedMessagesClass2">
            ${dnpConfig.enableUnsolicitedMessageClass2}
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".unsolicit3" valueClass="js-dnp-field js-dnp-enableUnsolicitedMessagesClass3">
            ${dnpConfig.enableUnsolicitedMessageClass3}
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".nonUpdated" valueClass="js-dnp-field js-dnp-enableNonUpdatedOnFailedScan">
            ${dnpConfig.enableNonUpdatedOnFailedScan}
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>
<cti:includeScript link="/resources/js/common/yukon.dnpconfiguration.js" />
