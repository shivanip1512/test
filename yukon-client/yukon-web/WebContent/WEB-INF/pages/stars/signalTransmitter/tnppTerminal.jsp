<div class="column-12-12 clearfix">
    <div class="column one">
        <%@ include file="general.jsp" %>
        
        <tags:sectionContainer2 nameKey="communication">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".password">
                    <tags:password path="password" includeShowHideButton="true" maxlength="20"/>
                </tags:nameValue2>
                <assets:signalTransmitterCommChannel items="${commChannels}"/>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
    </div>
    <div class="column two nogutter">
        <tags:sectionContainer2 nameKey="tnppSettings">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".originAddress">
                    <tags:input path="originAddress" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".destinationAddress">
                    <tags:input path="destinationAddress" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".inertia">
                    <tags:input path="inertia" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".protocol">
                    <cti:displayForPageEditModes modes="CREATE,EDIT">
                        <tags:selectWithItems items="${protocols}"
                                              path="protocol"
                                              defaultItemLabel="${selectLbl}" />
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <i:inline key="${signalTransmitter.protocol}"/>
                    </cti:displayForPageEditModes>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".dataFormat">
                    <cti:displayForPageEditModes modes="CREATE,EDIT">
                        <tags:selectWithItems items="${dataFormats}"
                                              path="dataFormat"
                                              defaultItemLabel="${selectLbl}" />
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <i:inline key="${signalTransmitter.dataFormat}"/>
                    </cti:displayForPageEditModes>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".indentifierFormat">
                    <cti:displayForPageEditModes modes="CREATE,EDIT">
                        <tags:selectWithItems items="${identifierFormats}"
                                              path="identifierFormat"
                                              defaultItemLabel="${selectLbl}" />
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <i:inline key="${signalTransmitter.identifierFormat}"/>
                    </cti:displayForPageEditModes>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".pagerId">
                    <tags:input path="pagerId" maxlength="10"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".channel">
                    <tags:input path="channel" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".functionCode">
                    <tags:input path="functionCode" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".zone">
                    <tags:input path="zone" />
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
    </div>
</div>