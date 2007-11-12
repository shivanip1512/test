<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url var="url" value="/spring/stars/hardware/deviceactivation" />
<c:url var="verificationUrl"
    value="/spring/stars/hardware/deviceverification" />

<script language="JavaScript" type="text/javascript"
    src="/JavaScript/prototype.js"></script>
<script langauge="JavaScript" type="text/javascript">
    Event.observe(window, 'load', function() {
        $("accountnumberinput").focus();
        
        Event.observe('accountnumberinput', 'keydown', function(event) {
            var keyCode = getEventKeyCode(event);
            if (keyCode == 13) {
                $("serialnumberinput").focus();                
                event.preventDefault();
            }
            false;    
        });
    });
    
    function getEventKeyCode(event) {
        var code;
        if (!event) var event = window.event;
        if (event.keyCode) {
            code = event.keyCode;
        } else if (event.which) {
            code = event.which;
        }
        return code;
    }
</script>

<html>
    <body>
    <head>
        <title>Device Activation</title>
    </head>

    <center>
        <div id="main">
            <h2>
                Device Activation
            </h2>
            <div style="float: left">
                <a href="${verificationUrl}">Device Verification</a>
            </div>
            <div style="float: center">
                <form id="activateform" method="POST" action="${url}"
                    style="margin: 0px; padding: 0px;">
                    <table cellspacing="5">
                        <tr align="center">
                            <td valign="top" style="text-align: right">
                                Account Number :
                            </td>
                            <td valign="top">
                                <input id="accountnumberinput"
                                    type="text" name="accountnumber"
                                    value="" />
                            </td>
                        </tr>
                        <tr align="center">
                            <td valign="top" style="text-align: right">
                                Serial Number :
                            </td>
                            <td valign="top">
                                <input id="serialnumberinput"
                                    type="text" name="serialnumber"
                                    value="" />
                            </td>
                        </tr>
                        <tr>
                            <td />
                            <td align="right">
                                <input type="submit" name="confirmation"
                                    value="Activate" />
                            </td>
                        </tr>
                    </table>
                </form>
                <div>
                </div>
    </center>

    </body>
</html>
