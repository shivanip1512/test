<settings> 
  <data_type>xml</data_type>
  <add_time_stamp>false</add_time_stamp>
  <font>Tahoma</font>
  <decimals_separator>.</decimals_separator>
  <thousands_separator>,</thousands_separator>
  <digits_after_decimal>0</digits_after_decimal>
  <precision>1</precision>
  <redraw>true</redraw>
  <add_time_stamp>true</add_time_stamp>
  <pie>
    <x>200</x>
    <y>130</y>
    <colors>0xFF7777,0x77FF77,0x7777FF,0x888888,0xFF8800,0xFFFF00,0x00FFFF,0x772266</colors>
    <inner_radius>0</inner_radius>     
    <radius>60</radius>
    <gradient>radial</gradient>
    <gradient_ratio>0,0,0,0,0,0,5,-100</gradient_ratio>
    <hover_brightness>-30</hover_brightness>
  </pie>
  
  <data_labels>
    <radius></radius>
    <show>
       <![CDATA[{title}: {percents}%]]>        
    </show>
    <line_color>#000000</line_color>           
    <line_alpha>30</line_alpha>
  </data_labels>  
  
  <balloon>
    <alpha>80</alpha>
    <text_color>000000</text_color>
    <show>
       <![CDATA[{title}: {percents}%]]>
    </show>
  </balloon>
  
  <legend>
    <enabled>true</enabled>
    <max_columns>1</max_columns>
    <x>355</x>
    <y>75</y>
    <width>1</width>
    <values>
        <enabled>true</enabled>             
        <width>60</width>
        <text><![CDATA[ {value} ({percents}%)]]></text>
    </values>
  </legend>
  
  <labels>
    <label>
      <x>290</x>
      <y>20</y>
      <text_size>14</text_size>
      <text>
        <![CDATA[<b>Phase Distribution</b>]]>
      </text>       
    </label>
  </labels>
</settings>
