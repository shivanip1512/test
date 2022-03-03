<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.comprehensiveMap">
    <div>
        <tags:alertBox classes="dn js-no-location-message" type="warning" includeCloseButton="true">
            <i:inline key=".missingLocations" />
        </tags:alertBox>
        <tags:alertBox classes="dn js-no-descendants-message" type="warning" includeCloseButton="true">
            <i:inline key=".descendants.noDevicesReturned" />
        </tags:alertBox>
        <tags:alertBox classes="dn js-descendants-missing-locations-message" type="warning" includeCloseButton="true">
            <i:inline key=".descendants.missingLocations" />
        </tags:alertBox>
    </div>

</cti:msgScope>