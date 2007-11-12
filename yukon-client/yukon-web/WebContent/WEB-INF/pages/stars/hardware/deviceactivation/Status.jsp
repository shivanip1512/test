<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url var="url" value="/spring/stars/hardware/deviceactivation" />

<script language="JavaScript" type="text/javascript" src="/JavaScript/prototype.js"></script>
<script language="JavaScript">
    var counter = 4;

    Event.observe(window, 'load', function() {
        setInterval(updateDisplay, 1000);    
    });
    
    function updateDisplay() {
        if (counter > 0) {
            $("reloaddiv").innerHTML = 'Reload in ' + counter--;
        }
    }
</script>

<meta http-equiv="Refresh" content="5; URL=${url}" />

<html>
    <body>
    <head>
        <title>Thermostat Activation Status</title>
    </head>

    <center>
        <div id="main">
            <table cellspacing="10">
                <tr>
                    <td valign="top">
                            <h3>Activation Details :</h3>
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
