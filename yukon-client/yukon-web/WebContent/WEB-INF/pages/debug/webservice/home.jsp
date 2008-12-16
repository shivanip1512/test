<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Web Service XML Test Page" module="debug">
    <cti:standardMenu menuSelection="webservice|xml" />
    
    <style type="text/css">
      textarea.xml {
            width: 1200px;
            height: 300px;
            border: 3px solid #BBBBBB;
            padding: 2px;;
            background-color:#FAFAFA;
        }
    </style>
    
    <script type="text/javascript">

		function xmlTemplateChange() {

			$('selectedTemplateIndex').value = $('xmlTemplate').selectedIndex;

			var url = '/spring/debug/webservice/xml/xmlTemplateChange';
            var args = {};
            args.xmlTemplate = $('xmlTemplate').options[$('xmlTemplate').selectedIndex].value;
            
            var onComplete = function(transport, json) {
            	$('xmlRequest').value = json.exampleXml;
          	};
            
            new Ajax.Request(url, {'method': 'post', 'parameters': args, 'onComplete': onComplete});
		}

		function uriChange() {

			$('uri').value = $('uriSelect').options[$('uriSelect').selectedIndex].value;
			$('selectedUriIndex').value = $('uri').selectedIndex;
			
		}
		
		function executeRequestForm() {

			$('executeRequestForm').submit();
		}
		
	</script>
    
    <h2>Web Service XML Test Page</h2>
    <br>
    
    <form id="executeRequestForm" action="/spring/debug/webservice/xml/executeRequest" method="post">
    <table cellspacing="10">
    
    	<tr>
    	
    	<%-- EXAMPLE FILE --%>
    		<td>
    			<B>Request Type:</B>
    			
    			<input type="hidden" id="selectedTemplateIndex" name="selectedTemplateIndex" value="${selectedTemplateIndex}">
    			<select id="xmlTemplate" name="xmlTemplate" onchange="xmlTemplateChange()">
    				<option value="">Choose...</option>
    				<c:forEach var="fileName" items="${exampleFileNames}" varStatus="status">
    					<c:choose>
	    					<c:when test="${status.count == selectedTemplateIndex}">
	    						<option value="${fileName}" selected>${fileName}</option>
	    					</c:when>
	    					<c:otherwise>
	    						<option value="${fileName}">${fileName}</option>
	    					</c:otherwise>
    					</c:choose>
    				</c:forEach>
    			</select>
    			<br><br>
    		</td>
    		
    		<%-- URI --%>
    		<td>
    		
    			<input type="hidden" id="selectedUriIndex" name="selectedUriIndex" value="${selectedUriIndex}">
    			<table>
    				<tr>
    					<td>
    						<B>URI:</B>
    					</td>
    					<td>
    						<select id="uriSelect" name="uriSelect" onchange="uriChange()">
			    				<c:forEach var="uriName" items="${uriNames}" varStatus="status">
			    					<c:choose>
				    					<c:when test="${status.count - 1 == selectedUriIndex}">
				    						<option value="${uriName}" selected>${uriName}</option>
				    					</c:when>
				    					<c:otherwise>
				    						<option value="${uriName}">${uriName}</option>
				    					</c:otherwise>
			    					</c:choose>
			    				</c:forEach>
			    			</select>
    					</td>
    				</tr>
    				<tr>
    					<td>&nbsp;</td>
    					<td>
    						<input type="text" name="uri" id="uri" value="http://127.0.0.1:8081/api/soap/loadManagement" size="66">
    					</td>
    				</tr>
    			</table>
    			
    		</td>
    		<td align="right">
    			<input type="button" value="Submit Request" onclick="executeRequestForm()"> 
    			<br><br>
    		</td>
    	</tr>
    	
    	<%-- REQUEST AREA --%>
    	<tr>
    		<td colspan="3">
		        <textarea id="xmlRequest" name="xmlRequest" class="xml">${xmlRequest}</textarea>
		    </td>
    	</tr>
    
    	<%-- RESPONSE AREA --%>
    	<tr><td><B>RESPONSE</B></td></tr>
    	<tr>
    		<td colspan="3">
	    		<textarea id="xmlResponse" name="xmlResponse" class="xml">${xmlResponse}</textarea>
			</td>
    	</tr>
    
    </table>
    </form>
    
    
    
</cti:standardPage>