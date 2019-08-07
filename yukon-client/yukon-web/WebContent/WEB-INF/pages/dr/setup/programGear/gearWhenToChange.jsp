<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<tags:nameValue2 nameKey=".whenToChange">
    <cti:displayForPageEditModes modes="EDIT,CREATE">
        <tags:selectWithItems id="whenToChange" path="fields.whenToChangeFields.whenToChange" items="${whenToChangeFields}" />
    </cti:displayForPageEditModes>
</tags:nameValue2>
<tags:nameValue2 id = "js-changePriority-row" nameKey=".changePrority" rowClass="dn">
    <tags:input path="fields.whenToChangeFields.changePriority" size="15" maxlength="60" />
</tags:nameValue2>
<tags:nameValue2 id = "js-changeDurationInMinutes-row" nameKey=".changeDurationInMinutes" rowClass="dn">
    <tags:numeric path="fields.whenToChangeFields.changeDurationInMinutes" size="10" minValue="0" maxValue="99999" stepValue="1" />
    <i:inline key=".min" />
</tags:nameValue2>
<tags:nameValue2 id = "js-triggerNumber-row" nameKey=".triggerNumber" rowClass="dn">
    <tags:input path="fields.whenToChangeFields.triggerNumber" size="15" maxlength="60" />
</tags:nameValue2>
<tags:nameValue2 id = "js-triggerOffset-row" nameKey=".triggerOffset" rowClass="dn">
    <tags:input path="fields.whenToChangeFields.triggerOffset" size="15" maxlength="60" />
</tags:nameValue2>