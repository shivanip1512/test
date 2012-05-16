<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.capcontrol.import.pageTitle"/>

<cti:standardPage title="${pageTitle}" module="capcontrol">
    <cti:msgScope paths="modules.capcontrol.import">
        <cti:standardMenu/>
    
       	<cti:breadCrumbs>
	        <cti:crumbLink url="/spring/capcontrol/tier/areas?isSpecialArea=false" title="Home" />
    	   
            <cti:crumbLink>${pageTitle}</cti:crumbLink> 
      	</cti:breadCrumbs>
            
    	<tags:boxContainer title="File Upload" id="updaterContainer" hideEnabled="false">
    		<table>
    			<tr valign="top">
    			    <td style="padding-right:20px;">
    			       <span class="normalBoldLabel"><i:inline key='.cbcImportFile'/></span>
    			    </td>
    				<td width="70%" style="padding-right:20px;">
    					<form method="post" action="/spring/capcontrol/import/cbcFile" enctype="multipart/form-data">
    						<%-- file select --%>
    			            <input type="file" name="dataFile" size="30px">
                            <cti:button nameKey="import" type="submit" styleClass="f_blocker"/>
    					</form>
    				</td>
    			</tr>
    			<tr valign="top">
    			    <td style="padding-right:20px;">
    			       <span class="normalBoldLabel"><i:inline key='.hierarchyImportFile'/></span>
    			    </td>
    				<td width="70%" style="padding-right:20px;">
    					<form method="post" action="/spring/capcontrol/import/hierarchyFile" enctype="multipart/form-data">
    						<%-- file select --%>
    			            <input type="file" name="dataFile" size="30px">
    			            <cti:button nameKey="import" type="submit" styleClass="f_blocker"/>
    					</form>
    				</td>
    			</tr>
    		</table>
    	</tags:boxContainer>
    	
        <br>
        <c:if test="${!empty results}">
    	<tags:boxContainer title="Import Results" hideEnabled="false">
        	<table class="tierTable">
            	<c:forEach var="result" items="${results}">
            		<c:choose>
    					<c:when test="${result.success}">
    						<c:set var="fontColor" value="successMessage"/>
    					</c:when>
    					<c:otherwise>
    						<c:set var="fontColor" value="errorMessage"/>
    					</c:otherwise>
    				</c:choose>
                	<tr valign="top">
                 		<td width="10%" style="padding-right:20px;">
       						<div class="${fontColor}"><i:inline key="${result.message}"/></div>
    					</td>
             		</tr>
             	</c:forEach>
        	</table>
    	</tags:boxContainer>
    	</c:if>
    </cti:msgScope>
</cti:standardPage>