<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" page="allThermostats">
<cti:standardMenu/>

<cti:msg var="twoTypes" key="yukon.dr.consumer.allThermostats.twoTypes" />
<cti:msg var="mustSelect" key="yukon.dr.consumer.allThermostats.mustSelect" />
<script>

    //INIT
    jQuery(function(){
        jQuery(document).on('change', '.f_thermostatSelectionChanged', thermostatSelectionChanged);
        thermostatSelect(jQuery("input[data-thermostat-type]:checked")[0]);
    });
    
    function thermostatSelectionChanged(event){
        thermostatSelect(event.currentTarget);
    };
    
    function thermostatSelect(elem){
        var target = jQuery(elem);
        var checkedElements = jQuery("input[data-thermostat-type]:checked");
        
        if(checkedElements.length){
            jQuery('input[data-thermostat-type][data-thermostat-type!="'+ target.attr("data-thermostat-type") +'"]').attr("disabled", true);
            jQuery("input:submit").removeAttr("disabled");
        }else{
            jQuery("input[data-thermostat-type]").removeAttr("disabled");
            jQuery("input:submit").attr("disabled", true);
        }
        
        var currentIds = [];
        jQuery(checkedElements).each(function(){
            currentIds.push(this.value);
        });
        jQuery('#thermostatIds').val(currentIds.join());
    };
</script>

    <h3>
        <cti:msg key="yukon.dr.consumer.allThermostats.header" /><br>
    </h3>
    
    <div class="message">
        <form method="post" action="/stars/consumer/thermostat/view/allSelected" onsubmit="return checkSelected()">
            
            <input type="hidden" id="thermostatIds" name="thermostatIds" value="${param.thermostatIds}">
            
            <cti:msg key="yukon.dr.consumer.allThermostats.chooseThermostats" /><br><br>

            <table style="margin-left: auto; margin-right: auto; border: 1px solid gray;">
                <tr>
                    <th>
                        <cti:msg key="yukon.dr.consumer.allThermostats.select" />
                    </th>
                    <th>
                        <cti:msg key="yukon.dr.consumer.allThermostats.name" />
                    </th>
                    <th>
                        <cti:msg key="yukon.dr.consumer.allThermostats.type" />
                    </th>
                </tr>
                <c:forEach var="thermostat" items="${thermostats}">
                    <tr>
                        <td>
                            <input type="checkbox" data-thermostat-type="${thermostat.type}" value="${thermostat.id}" class="f_thermostatSelectionChanged" ${(fn:contains(param.thermostatIds, thermostat.id))? 'checked':''}>
                        </td>
                        <td style="text-align: left; padding-left: 10px;">
                            <spring:escapeBody htmlEscape="true">${thermostat.label}</spring:escapeBody>
                        </td>
                        <td style="text-align: left; padding-left: 10px;">
                            <cti:msg key="${thermostat.type}" />
                        </td>
                    </tr>
                </c:forEach>
            </table>
            <br><br>
            
            <cti:msg var="scheduleText" key="yukon.dr.consumer.allThermostats.schedule" />
            <input type="submit" name="schedule" value="${scheduleText}">
            <cti:msg var="manualText" key="yukon.dr.consumer.allThermostats.manual" />
            <input type="submit" name="manual" value="${manualText}">
        </form>
    </div>

</cti:standardPage>