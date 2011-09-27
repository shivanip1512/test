<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
			       <div class="normalBoldLabel" style="display:inline;">CapControl CBC Import File:</div>
			    </td>
				<td width="70%" style="padding-right:20px;">
					<form id="capcontrolCbcFileForm" method="post" action="/spring/capcontrol/tools/importCbcFile" enctype="multipart/form-data">
						<%-- file select --%>
			            <input type="file" name="dataFile" size="30px">
			            <tags:slowInput myFormId="capcontrolCbcFileForm" label="Process" labelBusy="Processing" />
					</form>
				</td>
			</tr>
			<tr valign="top">
			    <td style="padding-right:20px;">
			       <div class="normalBoldLabel" style="display:inline;">CapControl Hierarchy Import File:</div>
			    </td>
				<td width="70%" style="padding-right:20px;">
					<form id="capcontrolHierarchyForm" method="post" action="/spring/capcontrol/tools/importHierarchyFile" enctype="multipart/form-data">
						<%-- file select --%>
			            <input type="file" name="dataFile" size="30px">
			            <tags:slowInput myFormId="capcontrolHierarchyForm" label="Process" labelBusy="Processing" />
					</form>
				</td>
			</tr>
		</table>
	</tags:boxContainer>
	
    <br>
    <c:if test="${!empty results}">
	<tags:boxContainer title="Import Results" hideEnabled="false">
    	<table class="tierTable">
	    	<tr>
	    		<th>Result</th>
    		</tr>
        	<c:forEach var="result" items="${results}">
        		<c:choose>
					<c:when test="${fn:contains(result, 'Success')}">
						<c:set var="fontColor" value="successMessage"/>
					</c:when>
					<c:otherwise>
						<c:set var="fontColor" value="errorMessage"/>
					</c:otherwise>
				</c:choose>
            	<tr valign="top">
             		<td width="10%" style="padding-right:20px;">
   						<div class="${fontColor}">${result}</div>
					</td>
         		</tr>
         	</c:forEach>
    	</table>
	</tags:boxContainer>
	</c:if>
</cti:standardPage>