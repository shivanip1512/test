<div style="font-size: 12px;">

    The reading pattern input box will accept a textual pattern that represents a number format.  The format will be applied
    to the selected field when the billing file is generated. <br><br>Acceptable symbols for the pattern are: ,(comma) .(decimal) 
    -(minus sign) $(currency symbols). Digits are represented by a 0(zero) or a #(pound sign). A zero will display a number if
    one exists for that position in the reading or a '0' filler will be displayed. A pound sign will display a number if one 
    exists for that position in the reading or nothing will be displayed in that position.<br><br>
    If no pattern is specified, the default pattern is: '#####.00' 
    
    <h3>Example Formats</h3>
    
    <table  border="1" cellpadding="5px" cellspacing="0px">
        <tr>
            <th>Input</th>
            <th>Result</th>
            <th>Description</th>
        </tr>
        <tr>
            <td>####,###.##</td>
            <td>123,456 (for a reading of 123456)</td>
            <td>Display 7 digits and 2 decimals (if digits exist at each position)</td>
        </tr>
        <tr>
            <td>0000,000.00</td>
            <td>0123,456.00 (for a reading of 123456)</td>
            <td>Display 7 digits and 2 decimals (filling in '0's for missing digits)</td>
        </tr>
        <tr>
            <td>####,000.0#</td>
            <td>123,456.0 (for a reading of 123456)</td>
            <td>Display 7 digits and 2 decimals (filling in '0's for missing digits)</td>
        </tr>
    </table>
    
</div>