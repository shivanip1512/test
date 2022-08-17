<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:includeScript link="/resources/js/pages/yukon.dialog.ajax.js"/>
<cti:msgScope paths="modules.tools.tdc">
    <cti:msg2 key="yukon.web.modules.tools.tdc.tags.title" var="tagsPopupTitle" />
    <cti:flashScopeMessages/>


    <form:form id="tdc-tags-form" cssClass="js-no-submit-on-enter" modelAttribute="backingBean">
        <cti:csrfToken/> 
        <form:hidden path="deviceId" /> 
        <form:hidden path="pointId" /> 
        <form:hidden path="rowIndex" />
        <tags:nameValueContainer2>
        <tags:nameValue2 nameKey="yukon.common.device">${fn:escapeXml(deviceName)}</tags:nameValue2>
        <tags:nameValue2 nameKey="yukon.common.point">${fn:escapeXml(pointName)}</tags:nameValue2>
        </tags:nameValueContainer2>
        <div class="scroll-lg">
            <table id="tagList" class="compact-results-table row-highlighting with-form-controls clearfix">
                <thead>
                    <tr>
                        <th><i:inline key=".tags.group"/></th>
                        <th><i:inline key=".tags.description"/></th>
                        <th><i:inline key=".tags.timestamp"/></th>
                        <th><i:inline key=".tags.taggedBy"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="tags" items="${backingBean.tags}" varStatus="row">
                        <tr class="tagItems">
                            <td class="clearfix">
                                <div class="clearfix">
                                    <form:hidden path="tags[${row.index}].instanceID" />
                                    <form:hidden path="tags[${row.index}].pointID" />
                                    <form:hidden path="tags[${row.index}].tagTime" />
                                    <form:hidden path="tags[${row.index}].username" />
                                    <form:select path="tags[${row.index}].tagID" cssClass="fl">
                                        <form:options items="${allTags}" itemValue="liteID"/>
                                    </form:select>
                                </div>
                            </td>
                            <td><form:input path="tags[${row.index}].descriptionStr" maxlength="120" cssClass="fl" /></td>
                            <td><cti:formatDate value="${tags.tagTime}" type="BOTH" /></td>
                            <td>${fn:escapeXml(tags.username)}&nbsp;
                                <cti:button nameKey="tags.deleteTagButton" 
                                    icon="icon-cross" 
                                    data-row="${row.index}" 
                                    renderMode="image" 
                                    classes="fr show-on-hover js-tdc-tags-remove" />
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="action-area">
            <cti:button id="newTag" nameKey="tags.newTagButton" icon="icon-add" classes="js-tdc-tags-add" var="tagsPopupTitle"/>
        </div>
        <div class="action-area">
            <form:checkbox path="deviceControlInhibited" />&nbsp;<i:inline key=".tags.cntrDisabledOnDevice"/>
        </div>
        <div class="action-area">
            <cti:button id="save"  nameKey="save" classes="primary action js-tdc-tags-save"/>
            <cti:button nameKey="close" onclick="$('#tdc-popup').dialog('close');" />
        </div>
          </form:form>
</cti:msgScope>