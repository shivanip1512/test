<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="commChannelDetail">
    <tags:widgetContainer deviceId="${id}" identify="false">
        <div class="column-12-12">
            <div class="one column">
                <tags:widget bean="commChannelInfoWidget"/>
            </div>
        </div>
    </tags:widgetContainer>
</cti:standardPage>