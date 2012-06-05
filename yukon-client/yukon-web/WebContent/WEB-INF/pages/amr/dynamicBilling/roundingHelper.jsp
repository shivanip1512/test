<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:msgScope paths="modules.amr.billing.roundingHelper">

<div style="font-size: 12px;">
<cti:msg2 key=".description" htmlEscape="false" />
<h3><cti:msg2 key=".title"/></h3>

<table border="1" cellpadding="5px" cellspacing="0px">
	<tr>
		<th rowspan="2"><cti:msg2 key=".input"/></th>
		<th colspan="7"><cti:msg2 key=".roundingModes"/></th>
	</tr>
	<tr>
	
		<th>UP</th>
		<th>DOWN</th>
		<th>CEILING</th>
		<th>FLOOR</th>
		<th>HALF_UP</th>
		<th>HALF_DOWN</th>
		<th>HALF_EVEN</th>
	</tr>
	<tr align=right>
		<td>5.5</td>
		<td>6</td>
		<td>5</td>
		<td>6</td>
		<td>5</td>
		<td>6</td>
		<td>5</td>
		<td>6</td>
	</tr>
	<tr align=right>
		<td>2.5</td>
		<td>3</td>
		<td>2</td>
		<td>3</td>
		<td>2</td>
		<td>3</td>
		<td>2</td>
		<td>2</td>
	</tr>
	<tr align=right>
		<td>1.6</td>
		<td>2</td>
		<td>1</td>
		<td>2</td>
		<td>1</td>
		<td>2</td>
		<td>2</td>
		<td>2</td>
	</tr>
	<tr align=right>
		<td>1.1</td>
		<td>2</td>
		<td>1</td>
		<td>2</td>
		<td>1</td>
		<td>1</td>
		<td>1</td>
		<td>1</td>
	</tr>
	<tr align=right>
		<td>1.0</td>
		<td>1</td>
		<td>1</td>
		<td>1</td>
		<td>1</td>
		<td>1</td>
		<td>1</td>
		<td>1</td>
	</tr>
	<tr align=right>
		<td>-1.0</td>
		<td>-1</td>
		<td>-1</td>
		<td>-1</td>
		<td>-1</td>
		<td>-1</td>
		<td>-1</td>
		<td>-1</td>
	</tr>
	<tr align=right>
		<td>-1.1</td>
		<td>-2</td>
		<td>-1</td>
		<td>-1</td>
		<td>-2</td>
		<td>-1</td>
		<td>-1</td>
		<td>-1</td>
	</tr>
	<tr align=right>
		<td>-1.6</td>
		<td>-2</td>
		<td>-1</td>
		<td>-1</td>
		<td>-2</td>
		<td>-2</td>
		<td>-2</td>
		<td>-2</td>
	</tr>
	<tr align=right>
		<td>-2.5</td>
		<td>-3</td>
		<td>-2</td>
		<td>-2</td>
		<td>-3</td>
		<td>-3</td>
		<td>-2</td>
		<td>-2</td>
	</tr>
	<tr align=right>
		<td>-5.5</td>
		<td>-6</td>
		<td>-5</td>
		<td>-5</td>
		<td>-6</td>
		<td>-6</td>
		<td>-5</td>
		<td>-6</td>
	</tr>
</table>
</div>
</cti:msgScope>