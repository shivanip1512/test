<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<cti:standardPage module="capcontrol" title="Cap Bank Addtional Info">
	<jsp:directive.page import="com.cannontech.util.ParamUtil" />
	<jsp:directive.page
		import="com.cannontech.database.data.lite.LiteYukonPAObject" />
	<jsp:directive.page import="com.cannontech.core.dao.DaoFactory" />
	<jsp:directive.page
		import="com.cannontech.database.db.capcontrol.CapBankAdditional" />
	<jsp:directive.page import="com.cannontech.web.util.CBCDBUtil" />
	<jsp:directive.page import="java.sql.Connection" />

	<%
	                CapBankAdditional addtional = (CapBankAdditional) request.getAttribute("capBankAdd");
	                LiteYukonPAObject lite = (LiteYukonPAObject) request.getAttribute("lite");
	%>

	<h3>
		<c:out value="<%=lite.getPaoName()%>" />
	</h3>
	<c:choose>
		<c:when test="<%=addtional.getDeviceID() > 0%>">
			<table>
				<tr>
					<td>
						Maint Area ID
					</td>
					<td>
						<%=addtional.getMaintAreaID()%>
					</td>
				</tr>
				<tr>
					<td>
						Pole Number
					</td>
					<td>
						<%=addtional.getPoleNumber()%>
					</td>
				</tr>
				<tr>
					<td>
						Driving Directions
					</td>
					<td>
						<%=addtional.getDriveDir()%>
					</td>
				</tr>
				<tr>
					<td>
						Latitude
					</td>
					<td>
						<%=addtional.getLatit()%>
					</td>
				</tr>
				<tr>
					<td>
						Longitude
					</td>
					<td>
						<%=addtional.getLongtit()%>
					</td>
				</tr>
				<tr>
					<td>
						Cap Bank Configuration
					</td>
					<td>
						<%=addtional.getCapBankConfig()%>
					</td>
				</tr>
				<tr>
					<td>
						Communication Medium
					</td>
					<td>
						<%=addtional.getCommMedium()%>
					</td>
				</tr>
				<tr>
					<td>
						External Antenna
					</td>
					<td>
						<%=addtional.getExtAntenna()%>
					</td>
				</tr>
				<tr>
					<td>
						Antenna Type
					</td>
					<td>
						<%=addtional.getAntennaType()%>
					</td>
				</tr>
				<tr>
					<td>
						Last Maintenance Visit
					</td>
					<td>
						<%=addtional.getLastMaintVisit().toString()%>
					</td>
				</tr>
				<tr>
					<td>
						Last Inspection
					</td>
					<td>
						<%=addtional.getLastInspVisit().toString()%>
					</td>
				</tr>
				<tr>
					<td>
						Op Count Reset Date
					</td>
					<td>
						<%=addtional.getOpCountResetDate()
                                                .toString()%>
					</td>
				</tr>
				<tr>
					<td>
						Potential Transformer
					</td>
					<td>
						<%=addtional.getOpCountResetDate()
                                                .toString()%>
					</td>
				</tr>
				<tr>
					<td>
						Maintenance Request Pending
					</td>
					<td>
						<%=addtional.getMaintReqPending()%>
					</td>
				</tr>
				<tr>
					<td>
						Other Comments
					</td>
					<td>
						<%=addtional.getOtherComments()%>
					</td>
				</tr>
				<tr>
					<td>
						Opteam Comments
					</td>
					<td>
						<%=addtional.getOpTeamComments()%>
					</td>
				</tr>
				<tr>
					<td>
						CBC Battery Install Date
					</td>
					<td>
						<%=addtional.getCbcBattInstallDate()%>
					</td>
				</tr>

			</table>
		</c:when>
	</c:choose>

</cti:standardPage>
