<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<%-- give "dialogQuestion" style a more generic name --%>
<h1 class="dialogQuestion">Select group and hardware for program Feeder 12</h1>

<form:form>
    <tags:nameValueContainer>
        <tags:nameValue name="Group Assigned" nameColumnWidth="150px">
            <select>
                <option value="0">ALL RES AC RELAY 3</option>
                <option value="1">other group</option>
            </select>
        </tags:nameValue>

        <tags:nameValue name="Hardware Assigned">
            <div style="border: 1px solid gray;">
	        <table class="compactResultsTable rowHighlighting">
	            <tr class="<tags:alternateRow odd="" even="altRow"/>">
	                <th></th>
	                <th>Serial #</th>
	                <th>Relay</th>
	            </tr>
	            <tr class="<tags:alternateRow odd="" even="altRow"/>">
	               <td><input type="checkbox"/></td>
	               <td>777000001</td>
	               <td>
			            <select>
			                <option value="0">none</option>
			                <option value="1">1</option>
			            </select>
	               </td>
	            </tr>
	            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                   <td><input type="checkbox"/></td>
                   <td>777000002</td>
                   <td>
                        <select >
                            <option value="0" >none</option>
                            <option value="1" selected="selected">1</option>
                        </select>
                   </td>
	            </tr>
	        </table>
            </div>
        </tags:nameValue>

    </tags:nameValueContainer>

    <div class="actionArea">
        <input type="button" value="<cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.editAssignedPrograms.ok"/>"/>
        <input type="button" value="<cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.editAssignedPrograms.cancel"/>"
            onclick="parent.$('peDialog').hide()"/>
    </div>

</form:form>
