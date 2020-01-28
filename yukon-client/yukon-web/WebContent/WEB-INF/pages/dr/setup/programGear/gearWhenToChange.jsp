<tags:nameValue2 nameKey=".whenToChangeFields.whenToChange">
    <cti:displayForPageEditModes modes="EDIT,CREATE">
        <tags:selectWithItems id="whenToChange" path="fields.whenToChangeFields.whenToChange" items="${whenToChangeFields}" inputClass="js-when-to-change"/>
    </cti:displayForPageEditModes>
    <cti:displayForPageEditModes modes="VIEW">
        <input type=hidden id="whenToChange" value="${programGear.fields.whenToChangeFields.whenToChange}">
        <cti:msg2 key=".${programGear.fields.whenToChangeFields.whenToChange}"/>
    </cti:displayForPageEditModes>
</tags:nameValue2>
<tags:nameValue2 id="js-changePriority-row" nameKey=".whenToChangeFields.changePriority" rowClass="dn">
    <tags:numeric path="fields.whenToChangeFields.changePriority" size="10" minValue="0" maxValue="9999" stepValue="1" />
</tags:nameValue2>
<tags:nameValue2 id="js-changeDurationInMinutes-row" nameKey=".whenToChangeFields.changeDurationInMinutes" rowClass="dn">
    <cti:msg2 var="durationUnit" key="yukon.common.units.MINUTES"/>
    <tags:numeric path="fields.whenToChangeFields.changeDurationInMinutes" units="${durationUnit}" size="10" minValue="0" maxValue="99999" stepValue="1" />
</tags:nameValue2>
<tags:nameValue2 id="js-triggerNumber-row" nameKey=".whenToChangeFields.triggerNumber" rowClass="dn">
    <tags:numeric path="fields.whenToChangeFields.triggerNumber" size="10" minValue="0" maxValue="99999" stepValue="1" />
</tags:nameValue2>
<tags:nameValue2 id="js-triggerOffset-row" nameKey=".whenToChangeFields.triggerOffset" rowClass="dn">
    <tags:input path="fields.whenToChangeFields.triggerOffset" size="10" maxlength="60" />
</tags:nameValue2>
