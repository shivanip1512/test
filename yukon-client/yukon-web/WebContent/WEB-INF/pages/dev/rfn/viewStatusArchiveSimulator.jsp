<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="rfnTest">

<tags:sectionContainer title="RFN Status Archive Simulator">
    <form action="sendStatusArchiveRequest" method="post">
        <cti:csrfToken/>
        <tags:nameValueContainer>
            
            <tags:nameValue name="Demand Reset Status Code">
                <select name="statusCode">
                    <option value="0">SUCCESS</option>
                    <option value="1">SCHEDULED</option>
                    <option value="2">FAILED</option>
                    <option value="3">NOT_SUPPORTED</option>
                </select>
            </tags:nameValue>
            <tags:nameValue name="Number of Messages to send"><input name="messageCount" type="text" value="10"></tags:nameValue>
        </tags:nameValueContainer>
        <br>
        <div>Status Archive helper info:</div>
        <ul>
            <li>Demand Reset Status Code - this is the Status Code that Yukon will reply with</li>
            <li>Number of Messages to send - Yukon will find this number of devices and send a message with the value entered above</li>
        </ul>
        <div class="page-action-area">
            <cti:button nameKey="send" type="submit" classes="js-blocker"/>
        </div>
    </form>
</tags:sectionContainer>

</cti:standardPage>