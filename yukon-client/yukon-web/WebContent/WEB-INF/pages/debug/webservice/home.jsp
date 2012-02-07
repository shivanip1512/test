<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage page="webServices.webservice" module="support">
    
    
    <script type="text/javascript">

		function xmlTemplateChange() {

			var url = '/spring/debug/webservice/xml/xmlTemplateChange';
            var args = {};
            args.xmlTemplateIdx = $('xmlTemplate').selectedIndex;
            
            var onComplete = function(transport, json) {
            	$('xmlRequest').value = json.exampleXml;
          	};
            
            new Ajax.Request(url, {'method': 'post', 'parameters': args, 'onComplete': onComplete});
		}

		function uriChange() {

			$('uri').value = $('uriSelect').options[$('uriSelect').selectedIndex].value;
		}
		
		function executeRequestForm() {

			$('selectedTemplateIndex').value = $('xmlTemplate').selectedIndex;
			$('selectedUriIndex').value = $('uriSelect').selectedIndex;
			
			$('executeRequestForm').submit();
		}

		function resetUserName() {

            var onComplete = function(transport, json) {
            	$('userName').value = json.userName;
          	};
            
            new Ajax.Request('/spring/debug/webservice/xml/resetUserName', {'method': 'post', 'parameters': {}, 'onComplete': onComplete});
		}
		
		jQuery(document).ready(function() {
			if (${formatResponse}) {
				document.getElementById("xmlResponse").value = getformattedXml(document.getElementById("xmlResponse").value);
			}
		});
		
		/*
		* Format the passed in unformatted xml by adding newlines and indentation, then return it
		*/
		function getformattedXml(unformattedXml) {
		    var formatted = '';
		    var reg = /(>)(<)(\/*)/g;
		    unformattedXml = unformattedXml.replace(reg, '$1\r\n$2$3');
		    var pad = 0;
		    jQuery.each(unformattedXml.split('\r\n'), function(index, node) {
		        var indent = 0;
		        if (node.match( /.+<\/\w[^>]*>$/ )) {
		            indent = 0;
		        } else if (node.match( /^<\/\w/ )) {
		            if (pad != 0) {
		                pad -= 1;
		            }
		        } else if (node.match( /^<\w[^>]*[^\/]>.*$/ )) {
		            indent = 1;
		        } else {
		            indent = 0;
		        }

		        var padding = '';
		        for (var i = 0; i < pad; i++) {
		            padding += '  ';
		        }

		        formatted += padding + node + '\r\n';
		        pad += indent;
		    });
			return formatted;
		}
		
	</script>
    
    <form id="executeRequestForm" action="/spring/debug/webservice/xml/executeRequest" method="post">
    <table cellspacing="10">
    
    	<tr>
    	
    	<%-- EXAMPLE FILE --%>
    		<td>
    		
    			<table>
    				<tr>
    					<td><B><i:inline key=".requestType"/></B></td>
    					<td>
    						<input type="hidden" id="selectedTemplateIndex" name="selectedTemplateIndex" value="${selectedTemplateIndex}">
			    			<select id="xmlTemplate" name="xmlTemplate" onchange="xmlTemplateChange()">
			    				<option value=""><i:inline key=".choose"/></option>
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
    					</td>
    				</tr>
    				<tr>
    					<td><B><i:inline key=".username"/></B></td>
    					<td>
    						<input type="text" id="userName" name="userName" value="${userName}">
                            <cti:button nameKey="resetUsernameBtn" styleClass="f_blocker" onclick="resetUserName()"/>
    					</td>
    				</tr>
    			</table>
    			
    		</td>
    		
    		<%-- URI --%>
    		<td>
    		
    			<input type="hidden" id="selectedUriIndex" name="selectedUriIndex" value="${selectedUriIndex}">
    			<table>
    				<tr>
    					<td>
    						<B><i:inline key=".uri"/></B>
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
    						<input type="text" name="uri" id="uri" value="${uri}" size="66">
    					</td>
    				</tr>
    			</table>
    			
    		</td>
    	</tr>
    	
    	<%-- REQUEST AREA --%>
    	<tr>
    		<td colspan="2">
		        <textarea id="xmlRequest" name="xmlRequest" class="xml">${xmlRequest}</textarea>
		        
		        
		        <%-- SUBMIT --%>
		        <br>
                <cti:button nameKey="submitRequestBtn" styleClass="f_blocker" onclick="executeRequestForm()"/>

				<c:if test="${formatResponse == true}">
					<c:set var="checked" value="checked=\"true\""/>
				</c:if>
				<input id="formatResponseId" type="checkbox" name="formatResponse" ${checked}/>
				<label for="formatResponseId" class="fwb"><i:inline key=".formatResponse"/>
		    </td>
		    
    	</tr>
    
    	<%-- RESPONSE AREA --%>
    	<tr><td><B><i:inline key=".response"/></B></td></tr>
    	<tr>
    		<td colspan="2">
	    		<textarea id="xmlResponse" name="xmlResponse" class="xml">${xmlResponse}</textarea>
			</td>
    	</tr>
    
    </table>
    </form>
    
    
    
</cti:standardPage>