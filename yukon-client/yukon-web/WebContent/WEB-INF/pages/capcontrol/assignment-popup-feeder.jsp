<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<script>
$(function() {
    $('#tripOrder, #closeOrder').sortable({
       stop: function(event, ui) {
           updateArrows(event, ui);
       }
   }); 
   
   $('#unassigned').sortable({
       connectWith: "#assigned",
       remove: function(event, ui) {
           assignCapBank(event, ui);
       }
   });
   
   $('#assigned').sortable({
       connectWith: "#unassigned",
       stop: function(event, ui) {
           updateArrows(event, ui);  
       },
       remove: function(event, ui) {
           unAssignCapBank(event, ui);
       }
   }).disableSelection();

});
</script>

<cti:msgScope paths="yukon.web.modules.capcontrol">

<div class="column-12-12 clearfix select-box">
    <div id="unassigned" class="column one select-box-available" style="min-height:150px;">
        <h3><i:inline key="yukon.common.available"/></h3>
        <c:forEach var="item" items="${unassigned}">
            <div class="select-box-item clearfix cm"
                data-id="${item.id}">${fn:escapeXml(item.name)}
                <cti:button icon="icon-plus-green" renderMode="buttonImage" classes="select-box-item-add js-add-capbank"/>
                <div class="select-box-item-movers" style="display:none;">
                    <cti:button icon="icon-bullet-go-up" renderMode="buttonImage"
                            classes="left select-box-item-up js-move-up"/>
                    <cti:button icon="icon-bullet-go-down" renderMode="buttonImage"
                            classes="right select-box-item-down js-move-down"/>
                </div>
            </div>
        </c:forEach>
    </div>
    <div id="assigned" class="column two nogutter select-box-selected js-with-movables" style="min-height:150px;" data-item-selector=".select-box-item">
        <h3><i:inline key="yukon.common.assigned"/></h3>
        <c:forEach var="item" items="${assigned}" varStatus="status">
            <div class="select-box-item cm"
                data-id="${item.id}">${fn:escapeXml(item.name)}
                <cti:button icon="icon-cross" renderMode="buttonImage" classes="select-box-item-remove js-remove-capbank"/>

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
    
     <div id="tripOrder" class="column one select-box-trip-order js-with-movables" data-item-selector=".select-box-item" style="margin-top:15px;">
        <h3><i:inline key=".bankMove.tripOrder"/></h3>
        <c:forEach var="item" items="${tripOrders}" varStatus="status">
            <div class="select-box-item cm" style="padding-left:10px;"
                data-id="${item.id}">${fn:escapeXml(item.name)}

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
        <div id="closeOrder" class="column two nogutter select-box-close-order js-with-movables" data-item-selector=".select-box-item" style="margin-top:15px;">
        <h3><i:inline key=".bankMove.closeOrder"/></h3>
        <c:forEach var="item" items="${closeOrders}" varStatus="status">
            <div class="select-box-item cm" style="padding-left:10px;"
                data-id="${item.id}">${fn:escapeXml(item.name)}

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