<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="CapControl Importer" module="capcontrol">

    <cti:standardMenu/>

   	<cti:breadCrumbs>
	   <cti:crumbLink url="/spring/capcontrol/tier/areas?isSpecialArea=false" title="Home" />
	   &gt; File Upload
  	</cti:breadCrumbs>
        
	<tags:boxContainer title="File Upload" id="updaterContainer" hideEnabled="false">
	
		<table>
			<tr valign="top">
				<td width="30%" style="padding-right:20px;">
	
					<form id="uploadForm" method="post" action="/spring/capcontrol/tools/importFile" enctype="multipart/form-data">
					
						<%-- file select --%>
			            <div class="normalBoldLabel" style="display:inline;">Update File:</div>
			            <input type="file" name="dataFile" size="30px">
			            <tags:slowInput myFormId="uploadForm" label="Process" labelBusy="Processing" />
					
					</form>
					
					<br>
					<c:choose>
						<c:when test="${not empty error}">
							<div class="errorRed">${error}</div>
						</c:when>
						<c:when test="${success}">
							<div class="okGreen">Imported Successfully</div>
						</c:when>
					</c:choose>
				</td>
			</tr>	
		</table>
	
	</tags:boxContainer>
	
</cti:standardPage>