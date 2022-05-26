<%@ taglib prefix="assets" tagdir="/WEB-INF/tags/assets"%>

<tags:nameValueContainer2>
    <cti:displayForPageEditModes modes="CREATE">
        <tags:nameValue2 nameKey=".passwordRequired">
            <tags:switchButton name="passwordRequired"
                               toggleGroup="passwordRequired"
                               toggleAction="hide"
                               onNameKey=".yes.label"
                               offNameKey=".no.label" 
                               checked="true"/>
        </tags:nameValue2>
    </cti:displayForPageEditModes>
    <tags:nameValue2 nameKey=".password" data-toggle-group="passwordRequired">
        <tags:password path="password" includeShowHideButton="true" maxlength="20"/>
    </tags:nameValue2>
    <assets:signalTransmitterCommChannel items="${commChannels}"/>
</tags:nameValueContainer2>