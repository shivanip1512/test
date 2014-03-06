<html>
    <head>
    <meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<cti:url var="url" value="/stars/hardware/deviceverification" />
<cti:url var="activationUrl"
    value="/stars/hardware/deviceactivation" />

<cti:includeScript link="${jqueryPath}" force="true"/>
<script langauge="JavaScript" type="text/javascript">
    jQuery(function () {
        jQuery("#serialnumberinput").focus();
    });
</script>

        <title>Device Verification</title>
    </head>

    <body>
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
                    <cti:csrfToken/>
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
