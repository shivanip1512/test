<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<c:set var="actionUrl" value="/spring/stars/consumer/optout/view2"/>

<cti:standardPage module="consumer" page="optout">
    <cti:standardMenu />
    
    <h3><cti:msg key="yukon.dr.consumer.optout.header"/></h3>
    
    <div align="center">
        <cti:msg key="yukon.dr.consumer.optout.description"/>
        
        <br>
        <br>
        
        <form action="${actionUrl}" method="POST">
	        <table>
	            <tr>
	                <td align="right">
	                    <cti:msg key="yukon.dr.consumer.optout.startDate"/>
	                </td>
	                
	                <cti:formatDate  value="${currentDate}" type="DATE" var="formattedDate"/>
	                
	                <td align="left">
	                    <ct:dateInputCalendar fieldName="startDate" fieldValue="${formattedDate}"/>
	                </td>
	            </tr>
	            
	            <tr>
	                <td align="right">
	                    <cti:msg key="yukon.dr.consumer.optout.duration"/>
	                </td>
	                
	                <td align="left">
	                    <select name="durationInDays">
		                    
		                    <c:forEach var="x" begin="1" end="7" step="1">
		                    
		                       <c:set var="key" value="${(x == 1) ? 'yukon.dr.consumer.optout.day' : 'yukon.dr.consumer.optout.days' }"/>
	                        
	                           <option value="${x}">${x} <cti:msg key="${key}"/></option>
		                    
		                    </c:forEach>
		                    
	                    </select>
	                </td>
	            </tr>
	            
	            <tr>
	                <td align="center" colspan="2">
	                    <br>
	                    <input type="submit" value="<cti:msg key='yukon.dr.consumer.optout.apply'/>"></input>
	                </td>
	            </tr>
	        </table>
        </form>
        
    </div>     
    
</cti:standardPage>    
    