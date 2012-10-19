<html>
    <body>
    <head>
    <meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<cti:url var="url" value="/spring/stars/hardware/deviceverification" />

<cti:includeScript link="PROTOTYPE" force="true"/>
<script language="JavaScript">
    var counter = 4;

    jQuery(function() {
        setInterval(updateDisplay, 1000);    
    });
    
    function updateDisplay() {
        if (counter > 0) {
            $("reloaddiv").innerHTML = 'Reload in ' + counter--;
        }
    }
</script>

<meta http-equiv="Refresh" content="5; URL=${url}" />

        <title>Device Verification Status</title>
    </head>

    <center>
        <div id="main">
            <table cellspacing="10">
                <tr>
                    <td valign="top">
                            <h3>Verification Details :</h3>
                    </td>
                    <td valign="top">
                            <h3>${message}</h3>
                    </td>
                </tr>
            </table>
            <div id="reloaddiv">
                Reload in 5    
            </div>
        </div>
    </center>

    </body>
</html>
