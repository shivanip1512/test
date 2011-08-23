<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="rfnTest">

    <tags:sectionContainer title="RFN Meter Archive Request Test">
        <form action="sendMeterArchiveRequest" method="post">
            <tags:nameValueContainer>
                <tags:nameValue name="Serial Number"><input name="serial" type="text" value="1234"></tags:nameValue>
                <tags:nameValue name="Manufacturer"><input name="manufacturer" type="text" value="LGYR"></tags:nameValue>
                <tags:nameValue name="Model"><input name="model" type="text" value="FocusAXD"></tags:nameValue>
                <tags:nameValue name="Wh Value"><input name="value" type="text" value="34543.4"></tags:nameValue>
            </tags:nameValueContainer>
            <div clas="actionArea"><cti:button nameKey="send" type="submit"/></div>
        </form>
    </tags:sectionContainer>

</cti:standardPage>