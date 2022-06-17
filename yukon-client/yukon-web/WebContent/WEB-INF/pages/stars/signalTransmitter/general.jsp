<tags:sectionContainer2 nameKey="general">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".name">
            <tags:input path="name" maxlength="60" autofocus="autofocus" inputClass="w300 js-name"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".type">
            <cti:displayForPageEditModes modes="CREATE">
                <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl"/>
                <tags:selectWithItems items="${types}"
                                      path="type"
                                      defaultItemLabel="${selectLbl}"
                                      defaultItemValue=""
                                      inputClass="js-type"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT,VIEW">
                <i:inline key="${signalTransmitter.type}"/>
                <!-- TODO: Check if we need this hidden field -->
                <form:hidden path="type" value="${signalTransmitter.type}"/>
            </cti:displayForPageEditModes>
        </tags:nameValue2>
        <c:if test="${signalTransmitter.type != null}">
            <c:if test="${signalTransmitter.type != 'TNPP_TERMINAL'}">
                <tags:nameValue2 nameKey=".pagerNumber" rowClass="noswitchtype">
                    <tags:input path="pagerNumber" maxlength="20"/>
                </tags:nameValue2>
            </c:if>
            <tags:nameValue2 nameKey=".status" rowClass="noswitchtype">
                <tags:switchButton path="enabled" offNameKey="yukon.common.disabled" onNameKey="yukon.common.enabled" />
            </tags:nameValue2>
        </c:if>
    </tags:nameValueContainer2>
</tags:sectionContainer2>