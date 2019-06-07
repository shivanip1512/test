<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<cti:standardPage module="tools" page="tdc.display.${mode}">

    <tags:setFormEditMode mode="${mode}" />

    <cti:url var="action" value="/tools/data-viewer/save" />
    <form:form id="custom-form" modelAttribute="display" action="${action}" method="POST">
            <cti:csrfToken />
            <form:hidden path="displayId" />
            <input type="hidden" id="selectedPoints" name="pointIds"/>
            
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".name">
                    <tags:input path="name" maxlength="40" size="30" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".description">
                    <tags:input path="description" maxlength="200" size="50" />
                </tags:nameValue2>
            </tags:nameValueContainer2>

        <div class="column-12-12 clearfix select-box bordered-div" style="margin-top: 30px;">
            <div class="column one">
                <h3>
                    <i:inline key=".available" />
                </h3>

                <div class="bordered-div" style="height: 700px;">
                    <div id="unassigned">
                        <tags:pickerDialog id="displayPointPicker" type="pointPicker"
                            container="unassigned" multiSelectMode="${true}" disabledIds="${selectedIds}"/>
                    </div>
                    <div>
                        <cti:button nameKey="add" classes="fr js-add-points" icon="icon-add"/>
                    </div>
                </div>
            </div>
            <div class="column two nogutter">
                <h3>
                    <i:inline key=".selected" />
                </h3>

                <div class="bordered-div" style="height: 700px;font-size:12px;">

                    <div id="assigned" class="select-box-selected js-with-movables"
                        data-item-selector=".select-box-item" style="min-height: 150px;">
                        <c:forEach var="item" items="${displayData}" varStatus="status">
                            <div class="select-box-item cm" data-id="${item.pointId}"
                                style="min-height: 35px;">
                                <c:if test="${item.pointId != 0}">
                                    ${fn:escapeXml(item.deviceName)} - ${fn:escapeXml(item.pointName)}
                                </c:if>
                                <cti:button icon="icon-cross" renderMode="buttonImage"
                                    classes="select-box-item-remove js-remove" />
                                <div class="select-box-item-movers">
                                    <c:set var="disabled" value="${status.first}" />
                                    <cti:button icon="icon-bullet-go-up" renderMode="buttonImage"
                                        classes="left select-box-item-up js-move-up"
                                        disabled="${disabled}" />
                                    <c:set var="disabled" value="${status.last}" />
                                    <cti:button icon="icon-bullet-go-down" renderMode="buttonImage"
                                        classes="right select-box-item-down js-move-down"
                                        disabled="${disabled}" />
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <div class="dn template-row select-box-item cm" data-id="0"
                            style="min-height: 35px;">
                            <cti:button icon="icon-cross" renderMode="buttonImage"
                                classes="select-box-item-remove js-remove" />
                            <div class="select-box-item-movers">
                                <cti:button icon="icon-bullet-go-up" renderMode="buttonImage"
                                    classes="left select-box-item-up js-move-up" />
                                <cti:button icon="icon-bullet-go-down" renderMode="buttonImage"
                                    classes="right select-box-item-down js-move-down"
                                    disabled="${true}" />
                            </div>
                        </div>
                </div>
            </div>
        </div>

        <div class="page-action-area">

            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" classes="primary action js-save-display" busy="true" />
                
                <cti:url var="viewUrl" value="/tools/data-viewer" />
                <c:if test="${display.displayId != 0}">
                    <cti:url var="viewUrl" value="/tools/data-viewer/${display.displayId}" />
                </c:if>
                <cti:button nameKey="cancel" href="${viewUrl}" />
            </cti:displayForPageEditModes>
        </div>

    </form:form>
    
    <script>
$(function() {             
    yukon.pickers['displayPointPicker'].show(); 
    $('#assigned').sortable({
        stop: function(event, ui) {
            ui.item.closest('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
        }
    });
});
</script>

 <cti:includeScript link="/resources/js/pages/yukon.tools.tdc.js"/>
    
</cti:standardPage>