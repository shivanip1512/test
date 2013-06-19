<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<cti:standardPage module="operator" page="thermostatSelect">

    <script type="text/javascript">

    // credit to:
    // http://stackoverflow.com/questions/3954438/remove-item-from-array-by-value
    Array.prototype.removeByVal = function() {
        var args = arguments, arrEl, len = args.length, aidx;
        while(len && this.length) {
            len -= 1;
            arrEl = args[len];
            aidx = this.indexOf(arrEl);
            if(-1 !== aidx) {
                this.splice(aidx, 1);
            }
        }
        return this;
    };
    // from MDN: Array forEach implementation for IE <= 8
    // Production steps of ECMA-262, Edition 5, 15.4.4.18
    // Reference: http://es5.github.com/#x15.4.4.18
    if ( !Array.prototype.forEach ) {
     
      Array.prototype.forEach = function forEach( callback, thisArg ) {
     
        var T, k;
     
        if ( this == null ) {
          throw new TypeError( "this is null or not defined" );
        }
     
        // 1. Let O be the result of calling ToObject passing the |this| value as the argument.
        var O = Object(this);
     
        // 2. Let lenValue be the result of calling the Get internal method of O with the argument "length".
        // 3. Let len be ToUint32(lenValue).
        var len = O.length >>> 0; // Hack to convert O.length to a UInt32
     
        // 4. If IsCallable(callback) is false, throw a TypeError exception.
        // See: http://es5.github.com/#x9.11
        if ( {}.toString.call(callback) !== "[object Function]" ) {
          throw new TypeError( callback + " is not a function" );
        }
     
        // 5. If thisArg was supplied, let T be thisArg; else let T be undefined.
        if ( thisArg ) {
          T = thisArg;
        }
     
        // 6. Let k be 0
        k = 0;
     
        // 7. Repeat, while k < len
        while( k < len ) {
     
          var kValue;
     
          // a. Let Pk be ToString(k).
          //   This is implicit for LHS operands of the in operator
          // b. Let kPresent be the result of calling the HasProperty internal method of O with argument Pk.
          //   This step can be combined with c
          // c. If kPresent is true, then
          if ( Object.prototype.hasOwnProperty.call(O, k) ) {
     
            // i. Let kValue be the result of calling the Get internal method of O with argument Pk.
            kValue = O[ k ];
     
            // ii. Call the Call internal method of callback with T as the this value and
            // argument list containing kValue, k, and O.
            callback.call( T, kValue, k, O );
          }
          // d. Increase k by 1.
          k++;
        }
        // 8. return undefined
      };
    }
    function checkboxChanged(this_cb) {

            var this_thermostatId = this_cb.id.split('-')[1],
                this_thermostatType = this_cb.id.split('-')[2],
                // thermostatIds value -> currentIds array
                currentIds = jQuery('#thermostatIds').val(),
                convertToArray = function(thing) {
                    var arr = [],
                        buildArr = function(element, index, array) {
                            arr.push(element);
                        };
                    thing.forEach(buildArr);
                    return arr;
                };
            if(currentIds === '') {
                currentIds = [];
            } else {
                currentIds = convertToArray(currentIds.split(","));
            }

            // add or remove from currentIds array
            if(this_cb.checked) {
                currentIds.push(this_thermostatId);
            } else {
                currentIds = currentIds.removeByVal(this_thermostatId);
            }

            // currentIds array -> thermostatIds value
            jQuery('#thermostatIds').val(currentIds.join());

            // loop over all thermostat checkboxes
            var thermos = jQuery('input[id*="THERMOSTATCHECKBOX"]');
            jQuery.each(thermos, function(ind, cb) {
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
    
    </script>

       <form id="themostatSelectForm" method="post" action="/stars/operator/thermostatSelect/selectRedirect">
    <tags:boxContainer2 nameKey="chooseThermostats" hideEnabled="false">
        
        
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" id="thermostatIds" name="thermostatIds" value="${thermostatIds}">
            
            <table class="compactResultsTable selectThermostatsTable">
            
                <tr>
                    <th class="name">
                        <i:inline key="yukon.web.modules.operator.thermostatSelect.name"/>
                    </th>
                    <th><i:inline key="yukon.web.modules.operator.thermostatSelect.type"/></th>
                </tr>
            
                <c:forEach var="thermostat" items="${thermostats}">
                    
                     <tr class="<tags:alternateRow odd="" even="altRow"/>">
                        <td  class="name">
                            <input type="checkbox" id="THERMOSTATCHECKBOX-${thermostat.id}-${thermostat.type}" onclick="checkboxChanged(this)">
                            <label for="THERMOSTATCHECKBOX-${thermostat.id}-${thermostat.type}">
                                <spring:escapeBody htmlEscape="true">${thermostat.label}</spring:escapeBody>
                            </label>
                        </td>
                        <td>
                            <cti:msg key="${thermostat.type}"/>
                        </td>
                    </tr>
                
                </c:forEach>
            
            </table>
        
    
    </tags:boxContainer2>
    
    <br>
    <cti:msg var="scheduleText" key="yukon.web.modules.operator.thermostatSelect.schedule"/>
    <input type="submit" value="${scheduleText}" name="schedule" style="width:80px;"/>
    
    <cti:msg var="manualText" key="yukon.web.modules.operator.thermostatSelect.manual" />
    <input type="submit" value="${manualText}" name="manual" style="width:80px"/>
    
       </form>
    
    

</cti:standardPage>