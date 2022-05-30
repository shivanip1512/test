<%@ taglib prefix="assets" tagdir="/WEB-INF/tags/assets"%>

<tags:nameValueContainer2>
    <tags:nameValue2 nameKey=".password" data-toggle-group="passwordRequired">
        <tags:password path="password" includeShowHideButton="true" maxlength="20" />
    </tags:nameValue2>
    <assets:signalTransmitterCommChannel items="${commChannels}" />
</tags:nameValueContainer2>