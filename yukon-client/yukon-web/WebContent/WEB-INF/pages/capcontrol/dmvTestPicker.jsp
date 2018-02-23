<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:msgScope paths="modules.capcontrol">
    <div class="column-6-18 clearfix dmv-test-picker-${nextIndex}">
        <div class="column one">
            <i:inline key=".schedules.dmvTest" />
        </div>
        <div class="column two nogutter">
            <tags:pickerDialog type="dmvTestPicker" 
               id="dmvTestPicker${nextIndex}"
               destinationFieldId="dmvTestId" linkType="selection"
               selectionProperty="dmvTestName"
               endAction="yukon.da.bus.addDMVPrefixInCommand" />
        </div>
    </div>
</cti:msgScope>
