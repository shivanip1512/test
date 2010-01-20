<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.deviceReconfig.batchDetail.pageTitle"/>
<cti:url var="deleteImg" value="/WebConfig/yukon/Icons/delete.gif"/>

<cti:standardPage title="Device Reconfiguration Result Detail" module="dr">

    <cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/spring/stars/operator/deviceReconfig/home" title="Device Reconfiguration" />
	    <cti:crumbLink>${pageTitle}</cti:crumbLink>
	</cti:breadCrumbs>
	
	<cti:standardMenu menuSelection="blank"/>

    <h2>Device Reconfiguration Result Detail</h2>
    <br>
    
	<tags:boxContainer title="'Golay Group 1' Reconfiguration Result Detail">
	
		<%-- NOTE --%>
		<table>
		    <tr>
		        <td valign="top" class="smallBoldLabel">Note:</td>
		        <td style="font-size:11px;">
		            Progress is updated periodically. Processing will continue if you wish to navigate away from this page at any time.
					<br><br>
		        </td>
		    </tr>
		</table>
		
		
		
		<%-- PROGRESS --%>
		<span class="normalBoldLabel">Progress: </span>
		Complete
		
		<div style="padding:10px;">
		
		    <table cellpadding="0px" border="0px" class="noStyle">
			    <tr>
			        <td>
			            <div id="progressBorder_${pbarId}" class="progressBarBorder" align="left">
			                <div id="progressInner_${pbarId}" class="progressBarInner" style="width:100px;">
			                </div>
			            </div>
			        </td>
			        <td>
			            <span id="percentComplete_${pbarId}" class="progressBarPercentComplete">100%</span>
			        </td>
			        <td>
			            <span class="progressBarCompletedCount" <c:if test="${hideCount}">style="display:none;"</c:if>>1650/1650</span>
			        </td>
			    </tr>
			</table>
			
			
	
		</div>

		<br>

		<%-- SUCCESS --%>
		<span class="normalBoldLabel">Successfully Paged: </span>
		<span class="okGreen">1646</span>
		
		<div style="padding:10px;">
		
		    <a href="" class="small">Choose New Operation For These Devices</a> <img src="/WebConfig/yukon/Icons/magnifier.gif">
	
		</div>

		<br>

		<%-- FAILED --%>
		<span class="normalBoldLabel">Failed To Page: </span>
		<span class="errorRed">4</span>
		
		<div style="padding:10px;">
		
		    <a href="" class="small">Choose New Operation For These Devices</a> <img src="/WebConfig/yukon/Icons/magnifier.gif">
	
		</div>
		
	
	
	</tags:boxContainer>
    
</cti:standardPage>