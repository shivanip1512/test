<%@ taglib prefix="assets" tagdir="/WEB-INF/tags/assets"%>

<tags:nameValueContainer2>
    <assets:signalTransmitterCommChannel items="${commChannels}"/>
    <tags:nameValue2 nameKey=".password">
        <tags:password path="password" includeShowHideButton="true" maxlength="20" showPassword="true"/>
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".sender">
        <tags:input path="sender" maxlength="64"/>
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".securityCode">
        <tags:input path="securityCode" maxlength="64"/>
    </tags:nameValue2>
</tags:nameValueContainer2>