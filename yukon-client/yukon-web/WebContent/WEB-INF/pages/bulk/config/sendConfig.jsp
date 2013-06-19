<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:standardPage module="tools" page="bulk.sendConfig">

    <tags:bulkActionContainer key="yukon.common.device.bulk.sendConfig" deviceCollection="${deviceCollection}">
        <div class="pageActionArea">
            <form id="sendConfigForm" method="post" action="/bulk/config/doSendConfig">
            
                <%-- DEVICE COLLECTION --%>
                <cti:deviceCollection deviceCollection="${deviceCollection}" />
                <label>
                    <span class="fl"><i:inline key="yukon.common.device.bulk.sendConfig.selectLabel"/></span>
                    <select id="method" name="method" class="fl" style="margin-left:5px;">
                        <option value="Standard"><cti:msg2 key=".standard"/></option>
                        <option value="Force"><cti:msg2 key=".force"/></option>
                    </select>
                </label>
                <cti:button nameKey="send" name="sendButton" type="submit" classes="f_disableAfterClick primary action" busy="true"/>
            </form>
        </div>
    </tags:bulkActionContainer>
</cti:standardPage>