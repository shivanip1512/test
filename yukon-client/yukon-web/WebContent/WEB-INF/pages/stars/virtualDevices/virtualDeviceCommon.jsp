<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:widgetContainer deviceId="${id}" identify="false">
    <div class="column-12-12 clearfix">
    <div class="one column">
            <tags:widget bean="virtualDeviceInfoWidget" />

            <!-- Including deviceGroupWidget's resources here since this particular
                     widget is being added to the page via ajax  -->
            <cti:includeScript link="JQUERY_TREE" />
            <cti:includeScript link="JQUERY_TREE_HELPERS" />
            <cti:includeCss
                link="/resources/js/lib/dynatree/skin/ui.dynatree.css" />
            <tags:widget bean="deviceGroupWidget" />
        </div>
        <div class="column two nogutter">
            <tags:widget bean="paoNotesWidget" />
        </div>
    </div>
    <div>
        <tags:widget bean="devicePointsWidget" />
    </div>
</tags:widgetContainer>
