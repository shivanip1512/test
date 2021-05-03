<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.operator.mapNetwork">

    <div id="marker-info" class="dn">
        <div id="device-info" class="dn"></div>
        <tags:hideReveal2 styleClass="mw300 dn js-nm-error" titleClass="error" titleKey="yukon.web.modules.tools.map.network.error" showInitially="false">
            <span class="js-nm-error-text"></span>
        </tags:hideReveal2>
    </div>

</cti:msgScope>