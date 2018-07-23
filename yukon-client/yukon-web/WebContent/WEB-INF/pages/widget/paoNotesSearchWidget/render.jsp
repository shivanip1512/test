<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<style>
    .name-value-table .name {
         min-width: 90px;
    }
</style>
<cti:uniqueIdentifier var="id" />
<cti:msgScope paths="common.paoNote, common.paoNotesSearch, menu.tools, yukon.common">
    <cti:url var="actionUrl" value="/tools/paoNotes/search"/>
    <form:form action="${actionUrl}" modelAttribute="paoNotesFilter" method="GET">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".noteText" valueClass="full-width" nameClass="namecolumnWidth">
                <form:input path="text" cssClass="full-width"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".devices" valueClass="full-width" nameClass="namecolumnWidth">
                <%@ include file="../../common/paoNotes/selectPaos.jsp" %>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".createDate" nameClass="namecolumnWidth">
                <dt:date path="dateRange.min" wrapperClass="fn vam" forceDisplayPicker="true" id="startDate_${id}"/>
                <i:inline key="yukon.common.to"/>
                <dt:date path="dateRange.max" wrapperClass="fn vam" forceDisplayPicker="true" id="endDate_${id}"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".createdBy" nameClass="namecolumnWidth">
                <cti:yukonUser var="currentUser"/>
                <form:select path="user">
                    <form:option value=""><i:inline key=".anyUser"/></form:option>
                    <form:option value="${currentUser.username}"><i:inline key=".currentUser"/></form:option>
                </form:select>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        <div class="action-area">
            <cti:button type="submit" classes="button" nameKey="search"/>
        </div>
    </form:form>
</cti:msgScope>
