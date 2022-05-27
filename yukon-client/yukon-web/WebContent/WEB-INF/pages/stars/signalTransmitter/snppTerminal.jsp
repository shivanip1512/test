<%@ taglib prefix="assets" tagdir="/WEB-INF/tags/assets"%>

<tags:nameValueContainer2>
    <tags:nameValue2 nameKey=".login">
        <tags:input path="login" maxlength="20"/>
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".password">
        <tags:password path="password" includeShowHideButton="true" maxlength="64"/>
    </tags:nameValue2>
    <assets:signalTransmitterCommChannel items="${commChannels}"/>
</tags:nameValueContainer2>