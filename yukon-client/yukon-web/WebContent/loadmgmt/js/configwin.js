<SCRIPT>

var GB_IMG_DIR = "../../editor/css/greybox/";

function showtgconfig (idx) 
{
    config = document.getElementById('tgconfig');
    document.getElementById('tgconfig').style.display = 'none';
    var tcgears = document.getElementsByName('targetcyclegear');
    if (tcgears) 
    {
        for (i=0; i < tcgears.length; i++) 
        {
            if (("tcg_" + idx) == tcgears[i].id) 
            {
                if (!$('manualstop').checked)
                    config.style.display = 'inline'
            }
        }
    }
}

function dateChanged (t) 
{
    var hidden = null;
    var d = new Date();
    if (t == 'start') {
        hidden = $('h_starttime');
        s = document.cmdForm.startdate.value + ' ' + document.cmdForm.startTime1.value; 
        d.setTime (Date.parse(s));
    }
    if (t == 'stop') {
        hidden = $('h_stoptime');
        s = document.cmdForm.stopdate.value + ' ' + document.cmdForm.stopTime1.value;
        d.setTime (Date.parse(s));
    }
}

               
function openConfigWin (gear, idx, period) 
{
    
    var  start =  new Date();
    var stop = new Date ();
    url = 'config.jsp?gearName=' + gear + '&start=' + document.cmdForm.h_starttime.value + '&stop=' + document.cmdForm.h_stoptime.value;
    s = '&idx=' + idx;
    s = '&prd=' + period;
    url += s;
    start.setTime (Date.parse (document.cmdForm.h_starttime.value));
    stop.setTime (Date.parse (document.cmdForm.h_stoptime.value));
    
    if (validateDate(start, stop)) 
    {
        w = ((stop - start)/(1000 * 60)) + 60;   
    }
    else
    {
        w = 250;
    }
    return GB_show ('Target Cycle Gear Config', url, w, 300);

}

function closeConfirmWin () 
{
    return GB_hide();
}

function submitResults (r) 
{
    window.parent.document.getElementById('h_adjustments').value = r;
    window.parent.closeConfirmWin();
}

function check_and_submit () 
{
    table = document.getElementById('tgctable');
    cells = table.getElementsByTagName ('input');
    adjustments = 'adjustments '; 
    wrong = 0;
    for (i=0; i < cells.length; i++) 
    {
         cell = parseInt(cells[i].value);
         if (!((80 <= cell) && (cell <=120))) 
         {
            cells[i].style.backgroundColor = 'red';
            wrong++;
         }
         else 
         {
            cells[i].style.backgroundColor = 'white';
            adjustments += cell;
            if (i < (cells.length-1))
            {
                adjustments += ' ';
            }
            
         }
    }
    if (wrong > 0) 
    {
        adjustments = null;
        document.getElementById('b_submit').disabled = true;   
    }
    else 
    {
        submitResults (adjustments);
    }
}

function setAdjustments ()
{
    inputs = document.getElementsByName('tcg_input');
    old = window.parent.document.getElementById('h_adjustments').value;
    if (old)
    {
        strings = old.split(' ');
        for (i=1; i <strings.length;i++)
        {
        inputs[i-1].value = strings[i];
        }
    }
    else
    {
        for(i=0; i<inputs.length;i++)
        {
            inputs[i].value = 100;
        }
    }
}
//cancel our adjustments if the last gear selected wasn't
//target cycle
function isCancel () 
{
    config = document.getElementById('tgconfig');
    if (config)
    {
	    if (config.style.display == 'none')
	        document.getElementById('cancelPrev').value = 'true';
	}
}
       
</SCRIPT> <!--end javascript for trend/view menus-->