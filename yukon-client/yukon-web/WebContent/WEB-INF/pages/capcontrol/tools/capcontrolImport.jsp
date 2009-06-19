<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="CapControl Importer" module="capcontrol">

    <cti:standardMenu/>

   	<cti:breadCrumbs>
	   <cti:crumbLink url="/spring/capcontrol/tier/areas?isSpecialArea=false" title="Home" />
	   &gt; CapControl Importer
  	</cti:breadCrumbs>
        
	<tags:boxContainer title="File Upload" id="updaterContainer" hideEnabled="false">
	
		<table>
			<tr valign="top">
			    <td style="padding-right:20px;">
			       <div class="normalBoldLabel" style="display:inline;">CapControl Import File:</div>
			    </td>
				<td width="70%" style="padding-right:20px;">
					<form id="capcontrolImportForm" method="post" action="/spring/capcontrol/tools/importFile" enctype="multipart/form-data">
						<%-- file select --%>
			            <input type="file" name="dataFile" size="30px">
			            <tags:slowInput myFormId="capcontrolImportForm" label="Process" labelBusy="Processing" />
					</form>
				</td>
			</tr>
		</table>
	</tags:boxContainer>

    <br>
   
	<tags:boxContainer title="Import Results" hideEnabled="false">
        <table>
	        <c:choose>
	            <c:when test="${!success}">
	                <c:forEach var="error" items="${errors}">
	                    <tr valign="top">
							<td width="30%" style="padding-right:20px;">
							    <div class="errorRed">-${error}</div>
							</td>
	                     </tr>
	                </c:forEach>
	            </c:when>
	            <c:when test="${success}">
	                <tr valign="top">
	                    <td width="30%" style="padding-right:20px;">
	                        <div class="okGreen">Imported Successfully</div>
	                    </td>
	                </tr>
	            </c:when>
	        </c:choose>
        </table>
	</tags:boxContainer>
</cti:standardPage>