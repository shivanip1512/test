<div style="font-size: 12px;">The rounding mode drop down list contains rounding options that are available for formatting your
reading. The rounding will be applied to the selected field when the billing file is generated. <br>
<br>
Acceptable rounding modes for the reading are: HALF_EVEN, CEILING, FLOOR, UP, DOWN, HALF_DOWN, HALF_UP. The default rounding mode selected
is defined by the Yukon [System] Billing Role property Rounding Mode. <br><br>
Rounding Mode specifies a rounding behavior for numerical operations capable of discarding precision. Each rounding mode indicates how the
least significant returned digit of a rounded result is to be calculated. If fewer digits are returned than the digits needed to
represent the exact numerical result, the discarded digits will be referred to as the discarded fraction regardless the digits'
contribution to the value of the number. In other words, considered as a numerical value, the discarded fraction could have an absolute value
greater than one.

<h3>Rounding Mode Examples</h3>

<table border="1" cellpadding="5px" cellspacing="0px">
	<tr>
		<th rowspan="2">Input</th>
		<th colspan="7">Rounding Modes</th>
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