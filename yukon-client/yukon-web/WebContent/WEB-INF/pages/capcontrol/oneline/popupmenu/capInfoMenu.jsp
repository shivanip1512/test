<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div>
    <table>
        <tr>
            <td align="right" style="color:#9FBBAC; font-weight: bold; font-size: 16;">${fn:escapeXml(paoName)}</td>
            <td align="right">
                <a href="javascript:void(0);" style="color: gray; font-weight: bold; font-size: 16;" title="Click To Close" onclick="closePopupWindow();">x</a>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <hr style="color: gray;"/>
            </td>
        </tr>
	   <c:forEach var="info" items="${infoMap}">
	       <tr>
	           <td align="left" style="color:white; font-size: 16;">${info.key}</td>
		      <td align="left" style="color:white; font-size: 14;">${info.value}</td>
	       </tr>
	   </c:forEach>
    </table>
</div>    