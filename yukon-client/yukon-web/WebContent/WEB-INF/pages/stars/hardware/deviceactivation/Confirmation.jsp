<html>
    <body>
    <head>
    <meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<cti:url var="url" value="/stars/hardware/deviceactivation" />

        <title>Device Activation Confirmation</title>
    </head>
    <center>
        <div id="main">
            <h2>
                Device Activation Confirmation
            </h2>
            <form id="activateform" method="POST" action="${url}"
                style="margin: 0px; padding: 0px;">
                <table cellspacing="5">
                    <tr>
                        <td valign="top" style="text-align: right">
                            Account Number :
                        </td>
                        <td valign="top" style="text-align: left">${accountNumber}</td>
                    </tr>
                    <tr>
                        <td valign="top" style="text-align: right">
                            Serial Number :
                        </td>
                        <td valign="top" style="text-align: left">${serialNumber}</td>
                    </tr>
                    <tr>
                        <td valign="top" style="text-align: right">
                            Contact Name:
                        </td>
                        <td valign="top" style="text-align: left">
                            <c:if test='${not empty contact}'>
                                ${contact.contFirstName} ${contact.contLastName}
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <td valign="top" style="text-align: right">
                            Address:
                        </td>
                        <td valign="top" style="text-align: left">
                            <ct:formattedAddress address="${address}"/>
                        </td>        
                    </tr>    
                    <tr>
                        <td/>
                        <td align="left">
                            <input type="submit" name="activate" value="Yes" />
                            <input type="submit" name="view" value="No"/>
                        </td>
                    </tr>
                </table>
                <input type="hidden" name="accountnumber" value="${accountNumber}"/>
                <input type="hidden" name="serialnumber" value="${serialNumber}"/>
            </form>
        </div>
    </center>
    </body>
</html>