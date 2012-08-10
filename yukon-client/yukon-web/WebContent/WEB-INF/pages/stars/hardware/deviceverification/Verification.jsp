<html>
    <body>
    <head>
    <meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<cti:url var="url" value="/spring/stars/hardware/deviceverification" />
<cti:url var="activationUrl"
    value="/spring/stars/hardware/deviceactivation" />

<cti:includeScript link="PROTOTYPE" force="true"/>
<script langauge="JavaScript" type="text/javascript">
    Event.observe(window, 'load', function() {
        $("serialnumberinput").focus();
    });
</script>

        <title>Device Verification</title>
    </head>

    <center>
        <div id="main">
            <h2>
                Device Verification
            </h2>
            <div style="float: left">
                <a href="${activationUrl}">Device Activation</a>
            </div>
            <div style="float: center">
                <form id="verificationform" method="POST"
                    action="${url}" style="margin: 0px; padding: 0px;">
                    <table cellspacing="5">
                        <tr align="center">
                            <td valign="top" style="text-align: right">
                                ExpressCom Serial Number :
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
                                <input type="submit" name="verify"
                                    value="Submit" />
                            </td>
                        </tr>
                    </table>
                </form>
                <div>
                </div>
    </center>

    </body>
</html>
