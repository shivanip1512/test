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
    $(function(){
        $(document).on('change', '.js-thermostatSelectionChanged', thermostatSelectionChanged);
        thermostatSelect($("input[data-thermostat-type]:checked")[0]);
    });
    
    function thermostatSelectionChanged(event){
        thermostatSelect(event.currentTarget);
    };
    
    function thermostatSelect(elem){
        var target = $(elem);
        var checkedElements = $("input[data-thermostat-type]:checked");
        
        if(checkedElements.length){
            $('input[data-thermostat-type][data-thermostat-type!="'+ target.attr("data-thermostat-type") +'"]').attr("disabled", true);
            $("input:submit").removeAttr("disabled");
        }else{
            $("input[data-thermostat-type]").removeAttr("disabled");
            $("input:submit").attr("disabled", true);
        }
        
        var currentIds = [];
        $(checkedElements).each(function(){
            currentIds.push(this.value);
        });
        $('#thermostatIds').val(currentIds.join());
    };
</script>

    <h3>
        <cti:msg key="yukon.dr.consumer.allThermostats.header" /><br>
    </h3>
    
    <div>
        <cti:url var="allSelectedUrl" value="/stars/consumer/thermostat/view/allSelected"/>
        <form method="post" action="${allSelectedUrl}" onsubmit="return checkSelected()">
            <cti:csrfToken/>
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
                            <input type="checkbox" data-thermostat-type="${thermostat.type}" value="${thermostat.id}" class="js-thermostatSelectionChanged" ${(fn:contains(param.thermostatIds, thermostat.id))? 'checked':''}>
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