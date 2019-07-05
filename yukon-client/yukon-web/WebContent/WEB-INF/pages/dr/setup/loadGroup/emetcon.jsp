<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<tags:sectionContainer2 nameKey="address">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".goldAddress">
            <tags:input path="goldAddress"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".silverAddress">
            <tags:input path="silverAddress"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".addressToUse">
            <tags:radio path="addressUsage" value="GOLD" classes="left yes M0" key=".goldAddress"/>
            <tags:radio path="addressUsage" value="SILVER" classes="right yes M0" key=".silverAddress"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".relayToUse">
            <tags:radio path="relayUsage" value="A" classes="left yes M0" key=".relay.A"/>
            <tags:radio path="relayUsage" value="B" classes="middle yes M0" key=".relay.B"/>
            <tags:radio path="relayUsage" value="C" classes="middle yes M0" key=".relay.C"/>
            <tags:radio path="relayUsage" value="S" classes="right yes M0" key=".relay.All"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>