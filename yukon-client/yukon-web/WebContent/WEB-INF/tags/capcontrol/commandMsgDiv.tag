<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<style type="text/css">
    div#outerDiv {
        position: fixed;
    }   
</style>

<!--[if IE]>
<style type="text/css">
    div#outerDiv {
        position: absolute;
    }
</style>
<![endif]--> 

<div id="outerDiv" style="display: none; width: 200px; bottom: 1%; left: 1%; background: white;">
	<div style="border: 2px solid #444;">
	    <div align="center" id="cmd_msg_div" style="padding: 5px;"></div>
	</div>
</div>