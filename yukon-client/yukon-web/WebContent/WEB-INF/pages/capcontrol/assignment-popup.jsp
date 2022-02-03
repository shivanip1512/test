<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<script>
$(function() {
    
    assign = function(event, ui, remove) {
        var selectedItem = ui.item;
        selectedItem
        .find('.js-remove, .js-add').toggleClass('js-remove js-add')
        .find('.icon').toggleClass('icon-plus-green icon-cross');
        
        // Show/hide movers.
        selectedItem.find('.select-box-item-movers').toggle(!remove);      
        sort(event, ui);
    },
    
     sort = function(event, ui) {
         ui.item
         .closest('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
     },
    
   $('#unassigned').sortable({
       connectWith: "#assigned",
       remove: function(event, ui) {
           assign(event, ui, false);
       }
   });
   
   $('#assigned').sortable({
       connectWith: "#unassigned",
       stop: function(event, ui) {
           sort(event, ui);
       },
       remove: function(event, ui) {
           assign(event, ui, true);
       }
   }).disableSelection();

});
</script>

<cti:msgScope paths="modules.capcontrol">
    <div class="column-12-12 clearfix select-box">

        <c:if test="${not empty createUrl}">
            <div class="column-12-12 clearfix">
                <span class="fr">
                    <cti:url var="createAssignmentUrl" value="${createUrl}"/>
                    <cti:button nameKey="create" icon="icon-add" href="${createAssignmentUrl}"/>
                </span>
            </div>
        </c:if>

        <div class="column one">
            <h3><i:inline key="yukon.common.available"/></h3>
            <div id="unassigned" class="select-box-available" style="min-height:150px;">
                <c:forEach var="item" items="${unassigned}">
                    <div class="select-box-item clearfix cm"
                         data-id="${item.id}">${fn:escapeXml(item.name)}
                        <cti:button icon="icon-plus-green" renderMode="buttonImage" classes="select-box-item-add js-add"/>
                        <div class="select-box-item-movers" style="display:none;">
                            <cti:button icon="icon-bullet-go-up" renderMode="buttonImage"
                                        classes="left select-box-item-up js-move-up"/>
                            <cti:button icon="icon-bullet-go-down" renderMode="buttonImage"
                                        classes="right select-box-item-down js-move-down"/>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
        
        <div class="column two nogutter">
            <h3><i:inline key="yukon.common.assigned"/></h3>
            <div id="assigned" class="select-box-selected js-with-movables" data-item-selector=".select-box-item" style="min-height:150px;">
                <c:forEach var="item" items="${assigned}" varStatus="status">
                    <div class="select-box-item cm" data-id="${item.id}">${fn:escapeXml(item.name)}
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
        
    </div>
</cti:msgScope>