<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<cti:standardPage module="consumer" page="programDetails">
    <cti:standardMenu />
    
    <h3><cti:msg key="yukon.dr.consumer.programDetails.header" /></h3>
    <br><br>
    
    
    <div align="center">
	    <c:choose>
	        <c:when test="${hasUrl}">
			    <c:import url="${descriptionUrl}"></c:import>
	        </c:when>
	        <c:otherwise>
                <table>
                    <tr>
                        <td style="font-weight: bold;">
                            <cti:msg key="${applianceType}"/><br><br>
                        </td>
                        <td>&nbsp;</td>
                    </tr>
				    <c:forEach var="program" items="${programs}">
	                    <tr>
                            <td style="font-weight: bold;">
                                <cti:msg key="${program.program.displayName}"/>
                            </td>
                            <td>
                                <spring:escapeBody htmlEscape="true">${program.program.description}</spring:escapeBody><br>
						    </td>
					    </tr>
				    </c:forEach>
                </table>	        
	        </c:otherwise>    
	    </c:choose>
	    <br><br>
	    <a href="/spring/stars/consumer/enrollment">
	        <cti:msg key="yukon.dr.consumer.programDetails.backToEnrollment" />
	    </a>
	
	</div>
</cti:standardPage>