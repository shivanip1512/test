<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="thermostatSelect">
<cti:checkEnergyCompanyOperator showError="true" accountId="${accountId}">
    <script type="text/javascript">

    // credit to:
    // http://stackoverflow.com/questions/3954438/remove-item-from-array-by-value
    Array.prototype.removeByVal = function () {
        var args = arguments, arrEl, len = args.length, aidx;
        while (len && this.length) {
            len -= 1;
            arrEl = args[len];
            aidx = this.indexOf(arrEl);
            if (-1 !== aidx) {
                this.splice(aidx, 1);
            }
        }
        return this;
    };

    function checkboxChanged(this_cb) {

            var this_thermostatId = this_cb.id.split('-')[1],
                this_thermostatType = this_cb.id.split('-')[2],
                // thermostatIds value -> currentIds array
                currentIds = $('#thermostatIds').val(),
                convertToArray = function (thing) {
                    var arr = [],
                        buildArr = function (index, element) {
                            arr.push(element);
                        };
                    $.each(thing, buildArr);
                    return arr;
                },
                isInIds = function (thermostatId) {
                    var isPresent = false;
                    currentIds.forEach ( function(el, ind, arr) {
                        if (el === thermostatId) {
                            isPresent = true;
                        }
                    });
                    return isPresent;
                };
            if (currentIds === '') {
                currentIds = [];
            } else {
                currentIds = convertToArray(currentIds.split(","));
            }

            // add or remove from currentIds array
            if (this_cb.checked && !isInIds(this_thermostatId)) {
                currentIds.push(this_thermostatId);
            } else {
                currentIds = currentIds.removeByVal(this_thermostatId);
            }

            // currentIds array -> thermostatIds value
            $('#thermostatIds').val(currentIds.join());

            // loop over all thermostat checkboxes
            var thermos = $('input[id*="THERMOSTATCHECKBOX"]');
            $.each(thermos, function(ind, cb) {
                var cb_thermostatType;
                cb_thermostatType = cb.id.split('-')[2];
                // when checking, disable all others that are not of this type
                if (this_cb.checked && cb_thermostatType !== this_thermostatType) {
                    cb.disabled = true;
                // when unchecking, if this is the last to be unchecked re-enable all others
                } else if (!this_cb.checked && currentIds.length === 0) {
                    cb.disabled = false;
                }
            });
        }
    
    $(function () {
        var thermos = $('input[id*="THERMOSTATCHECKBOX"]');
        // if we check a box, press schedule, then return to this page, clear all checkboxes
        // Chrome "remembers" previous selections, IE and Firefox do not
        $.each(thermos, function (index, cb) {
            cb.checked = false;
            cb.disabled = false;
        });
        $('#thermostatIds').val('');
    });
    </script>

    <form id="themostatSelectForm" method="post" action="<cti:url value="/stars/operator/thermostatSelect/selectRedirect"/>">
        <cti:csrfToken/>
        <tags:sectionContainer2 nameKey="chooseThermostats" hideEnabled="false">

            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" id="thermostatIds" name="thermostatIds" value="${thermostatIds}">

            <table class="compact-results-table selectThermostatsTable">
                <thead>
                    <tr>
                        <th class="name"><i:inline key="yukon.web.modules.operator.thermostatSelect.name"/></th>
                        <th><i:inline key="yukon.web.modules.operator.thermostatSelect.type"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="thermostat" items="${thermostats}">
                        <tr>
                            <td class="name">
                                <input type="checkbox" id="THERMOSTATCHECKBOX-${thermostat.id}-${thermostat.type}" 
                                    onclick="checkboxChanged(this)">
                                <label for="THERMOSTATCHECKBOX-${thermostat.id}-${thermostat.type}">
                                    ${fn:escapeXml(thermostat.label)}
                                </label>
                            </td>
                            <td><cti:msg key="${thermostat.type}"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </tags:sectionContainer2>

        <div class="page-action-area">
            <cti:msg var="scheduleText" key="yukon.web.modules.operator.thermostatSelect.schedule"/>
            <cti:button type="submit" value="${scheduleText}" name="schedule" label="${scheduleText}"/>
            <cti:msg var="manualText" key="yukon.web.modules.operator.thermostatSelect.manual"/>
            <cti:button type="submit" value="${manualText}" label="${manualText}" name="manual"/>
        </div>
    </form>
</cti:checkEnergyCompanyOperator>
</cti:standardPage>