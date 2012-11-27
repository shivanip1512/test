<script language="JavaScript">

var oldExtraInfo = new Array();

function saveOldPoint () 
{
	$('oldPointID').value = $(cbcPointPicker.destItemIdFieldId).value;
	oldExtraInfo.push($('cbcDevice').innerHTML);
  	oldExtraInfo.push($('ctlPoint').innerHTML);
}

function attachPoint (selected) 
{
	   new Ajax.Request ('/capcontrol/checkCapPoint', 
			{	 
				 method:'post', 
				 parameters:'pointID='+selected.pointId, 
				 asynchronous:true,
				 onSuccess: function (resp) {
				 	var numberUsingThisPoint = parseInt(resp.responseText);
				 	if (numberUsingThisPoint > 0)
				    {
				        var msg = 'This point is already attached to a CBC.';
				        msg    += 'Please choose a different point...\n';
				        msg    += 'Control point will be reset to the old value.';
				        alert(msg);
				        $(cbcPointPicker.destItemIdFieldId).value = $('oldPointID').value;
				        for (i=0; i < cbcPointPicker.extraInfo.length; i+=1) {
				            info = cbcPointPicker.extraInfo[i];
				            $(info.fieldid).innerHTML = oldExtraInfo.pop();
				        }
				        
				    }
				 }
			 });

}
</script>

<input type="hidden" id="oldPointID"/>