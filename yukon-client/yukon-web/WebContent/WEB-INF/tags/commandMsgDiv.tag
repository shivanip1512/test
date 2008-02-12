<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

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

<div id="outerDiv" style="display: none; width: 200px; bottom: 0%; left: 0%; background: white;">
    <cti:titledContainer title="Current Status">
        <div align="center" id="cmd_msg_div"></div>
     </cti:titledContainer>
</div>