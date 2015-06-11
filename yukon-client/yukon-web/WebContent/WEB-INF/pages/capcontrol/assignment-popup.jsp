<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.capcontrol">

<div class="column-12-12 clearfix select-box">
    <div class="column one select-box-available">
        <h3><i:inline key="yukon.common.available"/></h3>
        <c:forEach var="item" items="${unassigned}">
            <div class="select-box-item clearfix"
                data-id="${item.id}">${fn:escapeXml(item.name)}
                <cti:button icon="icon-plus-green" renderMode="buttonImage" classes="select-box-item-add js-add"/>
                <div class="select-box-item-movers" style="display: none;">
                    <cti:button icon="icon-bullet-go-up" renderMode="buttonImage"
                            classes="left select-box-item-up js-move-up"/>
                    <cti:button icon="icon-bullet-go-down" renderMode="buttonImage"
                            classes="right select-box-item-down js-move-down"/>
                </div>
            </div>
        </c:forEach>
    </div>
    <div class="column two nogutter select-box-selected js-with-movables" data-item-selector=".select-box-item">
        <h3><i:inline key="yukon.common.assigned"/></h3>
        <c:forEach var="item" items="${assigned}" varStatus="status">
            <div class="select-box-item"
                data-id="${item.id}">${fn:escapeXml(item.name)}
                <cti:button icon="icon-cross" renderMode="buttonImage" classes="select-box-item-remove js-remove"/>
                <div class="select-box-item-movers">
                    <c:set var="disabled" value="${status.first}"/>
                    <cti:button icon="icon-bullet-go-up" renderMode="buttonImage"
                            classes="left select-box-item-up js-move-up" disabled="${disabled}"/>
                    <c:set var="disabled" value="${status.last}"/>
                    <cti:button icon="icon-bullet-go-down" renderMode="buttonImage"
                            classes="right select-box-item-down js-move-down" disabled="${disabled}"/>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

</cti:msgScope>