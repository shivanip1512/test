<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/resources/js/pages/yukon.tools.commander.js"/>
<cti:msgScope paths="modules.tools.commander,yukon.common">
<script>
$(function() {
    var spinner = $('#commandPriority').spinner();
       $('#commandPriority').spinner({
          min: 1,
          max: 14
       }).blur(function () {
           var value = spinner.val();
           $('.warning').addClass('dn');
           if (value < 1){
               $('.warning').removeClass('dn');
               spinner.val(1);
           }
           if (value > 14) {
               $('.warning').removeClass('dn');
              spinner.val(14);
           }
       });
});
</script>
<form:form commandName="commandParams">
    <cti:dataGrid cols="2"
        tableClasses="collectionActionAlignment collectionActionCellPadding">
    <cti:dataGridCell>
        <tags:nameValueContainer2>
            <tags:nameValue2
                nameKey="modules.tools.commander.priority">
                <input type="number" id="commandPriority" name="commandPriority"
                        value="${priority}" />
                <div class="warning dn">Priority must be between 1 and 14.</div>
            </tags:nameValue2> 
            <tags:nameValue2 nameKey=".queue_commands">
                <label>
                <tags:switchButton path="queueCommand" onNameKey=".yes" 
                    offNameKey=".no" offClasses="M0" id="queueCommand" inputClass="js-queueCmd" />
                </label>
                </tags:nameValue2>
        </tags:nameValueContainer2>
    </cti:dataGridCell>
    </cti:dataGrid>
    </form:form>
</cti:msgScope>