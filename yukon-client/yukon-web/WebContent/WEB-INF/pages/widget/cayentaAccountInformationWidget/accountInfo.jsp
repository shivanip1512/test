<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:choose>

	<c:when test="${msg != null}">
		${msg}
	</c:when>

	<c:otherwise>

		<table class="compactResultsTable">
		    <tr>
		        <th colspan="2" align="left"><i:inline key=".custInfo"/></th>
		    </tr>
		    <tr>
		    	<td>
		    		<ct:nameValueContainer2>
				    	<ct:nameValue2 nameKey=".acctNumber">
				    		${meterInfo.accountNumber}
				    	</ct:nameValue2>
				    	<ct:nameValueGap2 gapHeight="6px" />
		    		
				    	<ct:nameValue2 nameKey=".name">
				    		${meterInfo.name}
				    	</ct:nameValue2>
				    	<ct:nameValueGap2 gapHeight="6px" />
					    
					    <ct:nameValue2 nameKey=".locationNum">
				    		${meterInfo.locationNumber}
				    	</ct:nameValue2>
				    	<ct:nameValueGap2 gapHeight="6px" />
					    
					    <ct:nameValue2 nameKey=".serviceAddr">
				    		<tags:address address="${address}" />
				    	</ct:nameValue2>
				    	<ct:nameValueGap2 gapHeight="6px" />
					    
					    <ct:nameValue2 nameKey=".meterSerial">
				    		${meterInfo.serialNumber}
				    	</ct:nameValue2>
				    	<ct:nameValueGap2 gapHeight="6px" />
					    
					    <ct:nameValue2 nameKey=".phoneNum">
				    		<cti:formatPhoneNumber value="${phoneInfo.phoneNumber}" htmlEscape="true"/>
				    	</ct:nameValue2>
				    	<ct:nameValueGap2 gapHeight="6px" />
					    
					    <ct:nameValue2 nameKey=".mapNum">
				    		${locationInfo.mapNumber}
				    	</ct:nameValue2>
				    	<ct:nameValueGap2 gapHeight="6px" />
				    </ct:nameValueContainer2>
		        </td>
		    </tr>
		</table>
		
	</c:otherwise>
</c:choose>
    
