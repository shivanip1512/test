<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="assets" tagdir="/WEB-INF/tags/assets"%>

<tags:nameValueContainer2>
    <assets:signalTransmitterCommChannel items="${commChannels}"/>
    <tags:nameValue2 nameKey=".password">
        <tags:password path="password" includeShowHideButton="true"/>
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".sender">
        <tags:input path="sender" />
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".securityCode">
        <tags:input path="securityCode" />
    </tags:nameValue2>
</tags:nameValueContainer2>