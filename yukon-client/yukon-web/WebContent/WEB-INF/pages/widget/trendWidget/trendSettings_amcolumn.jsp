<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!-- Value between [] brackets, for example [#FFFFFF] shows default value which is used if this parameter is not set -->
<!-- This means, that if you are happy with this value, you can delete this line at all and reduce file size -->
<!-- value or explanation between () brackets shows the range or type of values you should use for this parameter -->
<!-- the top left corner has coordinates x = 0, y = 0                                                                -->
<!-- "!" before x or y position (for example: <x>!20</x>) means that the coordinate will be calculated from the right side or the bottom -->
<settings> 
  <type>column</type>                                         <!-- [column] (column / bar) -->
  <data_type>xml</data_type>                                  <!-- [xml] (xml / csv) -->
  <csv_separator>;</csv_separator>                            <!-- [;] (string) csv file data separator (you need it only if you are using csv file for your data) -->     
  <skip_rows>0</skip_rows>                                    <!-- [0] (Number) if you are using csv data type, you can set the number of rows which should be skipped here -->
  <font>Arial</font>                                          <!-- [Arial] (font name) use device fonts, such as Arial, Times New Roman, Tahoma, Verdana... -->
  <text_size>11</text_size>                                   <!-- [11] (Number) text size of all texts. Every text size can be set individually in the settings below -->
  <text_color>#000000</text_color>                            <!-- [#000000] (hex color code) main text color. Every text color can be set individually in the settings below-->
  <decimals_separator>.</decimals_separator>                  <!-- [,] (string) decimal separator. Note, that this is for displaying data only. Decimals in data xml file must be separated with a dot -->
  <thousands_separator>,</thousands_separator>                <!-- [ ] (string) thousand separator. use "none" if you don't want to separate -->
  <scientific_min></scientific_min>                           <!-- [0.000001] If absolute value of your number is equal or less then scientific_min, this number will be formatted using scientific notation, for example: 0.0000023 -> 2.3e-6 -->
  <scientific_max></scientific_max>                           <!-- [1000000000000000] If absolute value of your number is equal or bigger then scientific_max, this number will be formatted using scientific notation, for example: 15000000000000000 -> 1.5e16 -->
  <digits_after_decimal></digits_after_decimal>               <!-- [] (Number) if your value has less digits after decimal then is set here, zeroes will be added -->
  <redraw>false</redraw>                                      <!-- [false] (true / false) if your chart's width or height is set in percents, and redraw is set to true, the chart will be redrawn then screen size changes -->
                                                              <!-- this function is beta, be careful. Legend, buttons labels will not be repositioned if you set your x and y values for these objects -->
  <reload_data_interval>0</reload_data_interval>              <!-- [0] (Number) how often data should be reloaded (time in seconds) -->
  <preloader_on_reload></preloader_on_reload>                 <!-- [false] (true / false) Whether to show preloaded when data or settings are reloaded -->
  <add_time_stamp>false</add_time_stamp>                      <!-- [false] (true / false) if true, a unique number will be added every time flash loads data. Mainly this feature is useful if you set reload _data_interval -->
  <precision>2</precision>                                    <!-- [2] (Number) shows how many numbers should be shown after comma for calculated values (percents) -->
  <depth>10</depth>                                            <!-- [0] (Number) the depth of chart and columns (for 3D effect) -->
  <angle>35</angle>                                            <!-- [30] (0 - 90) angle of chart area and columns (for 3D effect) -->    
  <colors></colors>                                           <!-- [#FF6600,#FCD202,#B0DE09,#0D8ECF,#2A0CD0,#CD0D74,#CC0000,#00CC00,#0000CC,#DDDDDD,#999999,#333333,#990000] Colors of graphs. if the graph color is not set, color from this array will be used -->
  <js_enabled></js_enabled>                                   <!-- [true] (true / false) In case you don't use any flash - JavaScript communication, you shuold set this setting to false - this will save some CPU and will disable the security warning message which appears when opening the chart from hard drive. -->  
    
  <column>
    <type>clustered</type>                                    <!-- [clustered] (clustered, stacked, 100% stacked, 3d column) -->
    <width>80</width>                                         <!-- [80] (0 - 100) width of column (in percents)  -->
    <spacing>5</spacing>                                      <!-- [5] (Number) space between columns of one category axis value, in pixels. Negative values can be used. -->
    <grow_time></grow_time>                                  <!-- [0] (Number) grow time in seconds. Leave 0 to appear instantly -->
    <grow_effect></grow_effect>                        <!-- [elastic] (elastic, regular, strong) -->    
    <sequenced_grow></sequenced_grow>                     <!-- [false] (true / false) whether columns should grow at the same time or one after another -->    
    <alpha>100</alpha>                                        <!-- [100] (Number) alpha of all columns -->
    <border_color>#000000</border_color>                      <!-- [#FFFFFF] (hex color code) -->
    <border_alpha>40</border_alpha>                             <!-- [0] (Number) -->
    <data_labels>
      <![CDATA[]]>                                            <!-- [] ({title} {value} {series} {percents} {start} {difference} {total}) You can format any data label: {title} will be replaced with real title, {value} - with value and so on. You can add your own text or html code too. -->
    </data_labels>
    <data_labels_text_color>#000000</data_labels_text_color>         <!-- [text_color] (hex color code) --> 
    <data_labels_text_size></data_labels_text_size>           <!-- [text_size] (Number) -->
    <data_labels_position>above</data_labels_position>             <!-- [outside] (inside, outside, above). This setting is only for clustered chart. --> 
                                                              <!-- if you set "above" for column chart, the data label will be displayed inside column, rotated  by 90 degrees -->
    <data_labels_always_on></data_labels_always_on>           <!-- [false] (true / false) If the data label is placed inside of the bar or column (stacked chart) and the bar or column is too small for the label to fit, it is hidden. If you set this setting to "true", the data labels will always be visible. -->
    <balloon_text>                                                    
     <![CDATA[<b>{value}<br>{description}</b>]]>                <!-- [] ({title} {value} {series} {percents} {start} {difference} {total}) You can format any data label: {title} will be replaced with real title, {value} - with value and so on. You can add your own text or html code too. -->
    </balloon_text>    
    <link_target></link_target>                               <!-- [] (_blank, _top ...) -->
    <gradient></gradient>                                     <!-- [vertical] (horizontal / vertical) Direction of column gradient. Gradient colors are defined in graph settings below. -->
    <bullet_offset></bullet_offset>                           <!-- [0] (Number) distance from column / bar to the bullet -->
    <hover_brightness></hover_brightness>                     <!-- [0] (from -255 to 255) The column may darken/lighten when the use rolls over it. The intensity may be set here -->
    <hover_color>#EED600</hover_color>                        <!-- [] (hex color code) -->
    <corner_radius_top></corner_radius_top>                   <!-- [0] (Number, Number%) Corner radius of the column's top. Works only if depth is = 0 -->
    <corner_radius_bottom></corner_radius_bottom>             <!-- [0] (Number, Number%) Corner radius of the column's bottom. Works only if depth is = 0 -->             
  </column>
  
  <line>                                                      <!-- Here are general settings for "line" graph type. You can set most of these settings for individual lines in graph settings below -->
    <connect></connect>                                       <!-- [false] (true / false) whether to connect points if data is missing -->
    <width></width>                                           <!-- [2] (Number) line width -->
    <alpha></alpha>                                           <!-- [100] (Number) line alpha -->
    <fill_alpha></fill_alpha>                                 <!-- [0] (Number) fill alpha -->
    <bullet></bullet>                                         <!-- [] (square, round, square_outlined, round_outlined, square_outline, round_outline, filename.swf) can be used predefined bullets or loaded custom bullets. Leave empty if you don't want to have bullets at all. Outlined bullets use plot area color for outline color -->
    <bullet_size></bullet_size>                               <!-- [8] (Number) bullet size -->
    <data_labels>
       <![CDATA[]]>                                           <!-- [] ({title} {value} {series} {percents} {start} {difference} {total}) You can format any data label: {title} will be replaced with real title, {value} - with value and so on. You can add your own text or html code too. -->
    </data_labels>
    <data_labels_text_color></data_labels_text_color>         <!-- [text_color] (hex color code) --> 
    <data_labels_text_size></data_labels_text_size>           <!-- [text_size] (Number) -->
    <balloon_text>                                                    
      <![CDATA[]]>                                            <!-- [] use the same formatting rules as for data labels -->
    </balloon_text>      
    <link_target></link_target>                               <!-- [] (_blank, _top ...) -->
  </line>
    
  <background>                                                <!-- BACKGROUND -->
    <color>#FFFFFF</color>                                    <!-- [#FFFFFF] (hex color code) Separate color codes with comas for gradient -->
    <alpha>0</alpha>                                          <!-- [0] (0 - 100) use 0 if you are using custom swf or jpg for background -->
    <border_color>#000000</border_color>                      <!-- [#000000] (hex color code) -->
    <border_alpha>0</border_alpha>                            <!-- [0] (0 - 100) -->
    <file></file>                                             <!-- [] (filename) swf or jpg file of a background. Do not use progressive jpg file, it will be not visible with flash player 7 -->
                                                              <!-- The chart will look for this file in amline_path folder (amline_path is set in HTML) -->
  </background>
     
  <plot_area>                                                 <!-- PLOT AREA (the area between axes) -->
    <color>#000000</color>                                    <!-- [#FFFFFF](hex color code) Separate color codes with comas for gradient -->
    <alpha>0</alpha>                                          <!-- [0] (0 - 100) if you want it to be different than background color, use bigger than 0 value -->                                        
    <border_color></border_color>                             <!-- [#000000] (hex color code) -->                                        
    <border_alpha></border_alpha>                             <!-- [0] (0 - 100) -->                                            
    <margins>                                                 <!-- plot area margins -->
		<c:choose>
			<c:when test="${fn:length(units) * 10 > 70}">
				<left>${fn:length(units) * 11}</left>		  <!-- [60](Number) --> 
			</c:when>
			<c:otherwise>
				<left>70</left>                               <!-- [60](Number / Number%) -->
			</c:otherwise>
		</c:choose>
      <top>60</top>                                           <!-- [60](Number / Number%) --> 
      <right>30</right>                                       <!-- [60](Number / Number%) --> 
      <bottom>48</bottom>                                     <!-- [80](Number / Number%) --> 
    </margins>
  </plot_area>
  
  <grid>                                                      <!-- GRID -->
    <category>                                                <!-- category axis grid -->                                                     
      <color>#000000</color>                                  <!-- [#000000] (hex color code) -->
      <alpha>20</alpha>                                        <!-- [15] (0 - 100) -->
      <dashed>false</dashed>                                  <!-- [false](true / false) -->
      <dash_length>5</dash_length>                            <!-- [5] (Number) -->  
    </category>
    <value>                                                   <!-- value axis grid -->      
      <color>#000000</color>                                  <!-- [#000000] (hex color code) -->
      <alpha>20</alpha>                                        <!-- [15] (0 - 100) -->
      <dashed>false</dashed>                                  <!-- [false] (true / false) -->
      <dash_length>5</dash_length>                            <!-- [5] (Number) -->
      <approx_count>10</approx_count>                         <!-- [10] (Number) approximate number of gridlines -->      
      <fill_color></fill_color>                         <!-- [#FFFFFF] (hex color code) every second area between gridlines will be filled with this color (you will need to set fill_alpha > 0) -->
      <fill_alpha></fill_alpha>                              <!-- [0] (0 - 100) opacity of fill -->
    </value>
  </grid>
  
  <values>                                                    <!-- VALUES -->
    <category>                                                <!-- category axis -->
      <enabled>true</enabled>                                 <!-- [true] (true / false) -->
      <frequency>${gridFrequency}</frequency>                                <!-- [1] (Number) how often values should be placed -->
      <start_from></start_from>                               <!-- [1] (Number) you can set series from which category values will be displayed -->
      <rotate>35</rotate>                                     <!-- [0] (0 - 90) angle of rotation. If you want to rotate by degree from 1 to 89, you must have font.swf file in fonts folder -->      
      <color>#000000</color>                                         <!-- [text_color] (hex color code) -->
      <text_size>10</text_size>                                 <!-- [text_size] (Number) -->    
      <inside></inside>                                       <!-- [false] (true / false) if set to true, axis values will be displayed inside plot area. This setting will not work for values rotated by 1-89 degrees (0 and 90 only) -->      
    </category>
    <value>                                                   <!-- value axis -->
      <enabled>true</enabled>                                 <!-- [true] (true / false) -->
      <reverse></reverse>                                     <!-- [false] (true / false) whether to reverse this axis values or not. If set to true, values will start from biggest number and will end with a smallest number -->    
      <min>0</min>                                            <!-- [] (Number) minimum value of this axis. If empty, this value will be calculated automatically. -->
      <max></max>                                             <!-- [] (Number) maximum value of this axis. If empty, this value will be calculated automatically -->
      <strict_min_max></strict_min_max>                       <!-- [false] (true / false) by default, if your values are bigger then defined max (or smaller then defined min), max and min is changed so that all the chart would fit to chart area. If you don't want this, set this option to true. -->
      <frequency>1</frequency>                                 <!-- [1] (Number) how often values should be placed, 1 - near every gridline, 2 - near every second gridline... -->
      <rotate>0</rotate>                                       <!-- [0] (0 - 90) angle of rotation. If you want to rotate by degree from 1 to 89, you must have font.swf file in fonts folder -->
      <skip_first>false</skip_first>                               <!-- [true] (true / false) to skip or not first value -->
      <skip_last></skip_last>                                 <!-- [false] (true / false) to skip or not last value -->
      <color></color>                                         <!-- [text_color] (hex color code) --> 
      <text_size>10</text_size>                                 <!-- [text_size] (Number) -->
      <unit></unit>                                           <!-- [] (text) -->
      <unit_position>right</unit_position>                         <!-- [right] (right / left) -->
      <integers_only></integers_only>                         <!-- [false] (true / false) if set to true, values with decimals will be omitted -->
      <inside></inside>                                       <!-- [false] (true / false) if set to true, axis values will be displayed inside plot area. This setting will not work for values rotated by 1-89 degrees (0 and 90 only) -->
      <duration></duration>                                   <!-- [] (ss/mm/hh/DD) In case you want your axis to display formatted durations instead of numbers, you have to set the unit of the duration in your data file. For example, if your values in data file represents seconds, set "ss" here.-->                   
    </value>
  </values>
  
  <axes>                                                      <!-- axes -->
    <category>                                                <!-- category axis -->
      <color>#000000</color>                                  <!-- [#000000] (hex color code) -->
      <alpha>100</alpha>                                      <!-- [100] (0 - 100) -->
      <width>2</width>                                        <!-- [2] (Number) line width, 0 for hairline -->
      <tick_length>7</tick_length>                            <!-- [7] (Number) -->
    </category>
    <value>                                                   <!-- value axis -->
      <color>#000000</color>                                  <!-- [#000000] (hex color code) -->
      <alpha>100</alpha>                                      <!-- [100] (0 - 100) -->
      <width>2</width>                                        <!-- [2] (Number) line width, 0 for hairline -->
      <tick_length>7</tick_length>                            <!-- [7] (Number) -->
      <logarithmic></logarithmic>                             <!-- [false] (true / false) If set to true, this axis will use logarithmic scale instead of linear -->
    </value>
  </axes>  
  
  <balloon>                                                   <!-- BALLOON -->
    <enabled>true</enabled>                                   <!-- [true] (true / false) -->
    <color></color>                                           <!-- [] (hex color code) balloon background color. If empty, slightly darker then current column color will be used -->
    <alpha>100</alpha>                                        <!-- [100] (0 - 100) -->
    <text_color></text_color>                                 <!-- [#FFFFFF] (hex color code) -->
    <text_size>11</text_size>                                   <!-- [text_size] (Number) -->
    <max_width></max_width>                                   <!-- [220] (Number) The maximum width of a balloon -->
    <corner_radius></corner_radius>                           <!-- [0] (Number) Corner radius of a balloon. If you set it > 0, the balloon will not display arrow -->
    <border_width></border_width>                             <!-- [0] (Number) -->
    <border_alpha></border_alpha>                             <!-- [balloon.alpha] (Number) -->
    <border_color></border_color>                             <!-- [balloon.color] (hex color code) -->
  </balloon>
    
  <legend>                                                    <!-- LEGEND -->
    <enabled>false</enabled>                                  <!-- [true] (true / false) -->
    <x></x>                                                   <!-- [] (Number / Number% / !Number) if empty, will be equal to left margin -->
    <y></y>                                                   <!-- [] (Number / Number% / !Number) if empty, will be below plot area -->
    <width></width>                                           <!-- [] (Number / Number%) if empty, will be equal to plot area width -->
    <max_columns></max_columns>                               <!-- [] (Number) the maximum number of columns in the legend -->    
    <color></color>                                           <!-- [#FFFFFF] (hex color code) background color. Separate color codes with comas for gradient -->
    <alpha></alpha>                                           <!-- [0] (0 - 100) background alpha -->
    <border_color></border_color>                             <!-- [#000000] (hex color code) border color -->
    <border_alpha></border_alpha>                             <!-- [0] (0 - 100) border alpha -->
    <text_color></text_color>                                 <!-- [text_color] (hex color code) -->   
    <text_size></text_size>                                   <!-- [text_size] (Number) -->
    <spacing>5</spacing>                                       <!-- [10] (Number) vertical and horizontal gap between legend entries -->
    <margins>0</margins>                                       <!-- [0] (Number) legend margins (space between legend border and legend entries, recommended to use only if legend border is visible or background color is different from chart area background color) -->
    <reverse_order></reverse_order>                           <!-- [false] (true / false) whether to sort legend entries in a reverse order -->        
    <align></align>                                           <!-- [left] (left / center / right) alignment of legend entries -->
    <key>                                                     <!-- KEY (the color box near every legend entry) -->
      <size>16</size>                                         <!-- [16] (Number) key size-->
      <border_color></border_color>                           <!-- [] (hex color code) leave empty if you don't want to have border -->
    </key>
  </legend>
  
  <export_as_image>                                           <!-- export_as_image feature works only on a web server -->
    <file></file>                                             <!-- [] (filename) if you set filename here, context menu (then user right clicks on flash movie) "Export as image" will appear. This will allow user to export chart as an image. Collected image data will be posted to this file name (use amcolumn/export.php or amcolumn/export.aspx) -->
    <target></target>                                         <!-- [] (_blank, _top ...) target of a window in which export file must be called -->
    <x></x>                                                   <!-- [0] (Number / Number% / !Number) x position of "Collecting data" text -->
    <y></y>                                                   <!-- [] (Number / Number% / !Number) y position of "Collecting data" text. If not set, will be aligned to the bottom of flash movie -->
    <color></color>                                           <!-- [#BBBB00] (hex color code) background color of "Collecting data" text -->
    <alpha></alpha>                                           <!-- [0] (0 - 100) background alpha -->
    <text_color></text_color>                                 <!-- [text_color] (hex color code) -->
    <text_size></text_size>                                   <!-- [text_size] (Number) -->
  </export_as_image>
  
  <error_messages>                                            <!-- "error_messages" settings will be applied for all error messages except the one which is showed if settings file wasn't found -->
    <enabled></enabled>                                       <!-- [true] (true / false) -->
    <x></x>                                                   <!-- [] (Number / Number% / !Number) x position of error message. If not set, will be aligned to the center -->
    <y></y>                                                   <!-- [] (Number / Number% / !Number) y position of error message. If not set, will be aligned to the center -->
    <color></color>                                           <!-- [#BBBB00] (hex color code) background color of error message. Separate color codes with comas for gradient -->
    <alpha></alpha>                                           <!-- [100] (0 - 100) background alpha -->
    <text_color></text_color>                                 <!-- [#FFFFFF] (hex color code) -->
    <text_size></text_size>                                   <!-- [text_size] (Number) -->
  </error_messages>    
  
  <strings>
    <no_data>No data for selected period</no_data>            <!-- [No data for selected period] (text) if data is missing, this message will be displayed -->
    <export_as_image></export_as_image>                       <!-- [Export as image] (text) text for right click menu -->
    <collecting_data></collecting_data>                       <!-- [Collecting data] (text) this text is displayed while exporting chart to an image -->
    <!-- the strings below are only important if you format your axis values as durations -->
    <ss></ss>                                                <!-- [] unit of seconds -->
    <mm></mm>                                                <!-- [:] unit of minutes -->
    <hh></hh>                                                <!-- [:] unit of hours -->
    <DD></DD>                                                <!-- [d. ] unit of days -->    
  </strings>  
  
  
  <context_menu>                                              <!-- context menu allows you to controll right-click menu items. You can add custom menu items to create custom controls -->                                                              
                                                              <!-- "function_name" specifies JavaScript function which will be called when user clicks on this menu. You can pass variables, for example: function_name="alert('something')" -->
                                                              <!-- "title" sets menu item text. Do not use for title: Show all, Zoom in, Zoom out, Print, Settings... -->
                                                              <!-- you can have any number of custom menus. Uncomment the line below to enable this menu and add apropriate JS function to your html file. -->
     <!-- <menu function_name="printChart" title="Print chart"></menu> -->
     
     <default_items>
       <zoom></zoom>                                     <!-- [false] (true / false) to show or not flash players zoom menu -->
       <print></print>                                   <!-- [true] (true / false) to show or not flash players print menu -->
     </default_items>
  </context_menu>  
  
  
  <labels>                                                    <!-- LABELS -->
                                                              <!-- you can add as many labels as you want -->
                                                              <!-- labels can also be added in data xml file, using exactly the same structure like it is here -->
    <label>
      <x>0</x>                                                <!-- [0] (Number / Number% / !Number) -->
      <y>25</y>                                               <!-- [0] (Number / Number% / !Number) -->
      <rotate></rotate>                                       <!-- [false] (true / false) -->
      <width></width>                                         <!-- [] (Number / Number%) if empty, will stretch from left to right untill label fits -->
      <align>center</align>                                   <!-- [left] (left / center / right) -->  
      <text_color></text_color>                               <!-- [text_color] (hex color code) button text color -->
      <text_size></text_size>                                 <!-- [text_size](Number) button text size -->
      <text>                                                  <!-- [] (text) html tags may be used (supports <b>, <i>, <u>, <font>, <a href="">, <br/>. Enter text between []: <![CDATA[your <b>bold</b> and <i>italic</i> text]]>-->
        <![CDATA[<b>${trendTitle}</b>]]>
      </text>        
    </label>
    <label>
      <x>0</x>                                                <!-- [0] (Number) -->
      <y>115</y>                                               <!-- [0] (Number) -->
      <width></width>                                      	  <!-- [] (Number) if empty, will stretch from left to right untill label fits -->
      <align>left</align>                                   <!-- [left] (left / center / right) -->  
      <text_color></text_color>                               <!-- [text_color] (hex color code) button text color -->
      <text_size></text_size>                                 <!-- [text_size](Number) button text size -->
      <text>                                                  <!-- [] (text) html tags may be used (supports <b>, <i>, <u>, <font>, <a href="">, <br/>. Enter text between []: <![CDATA[your <b>bold</b> and <i>italic</i> text]]>-->
        <![CDATA[<b>${units}</b>]]>
      </text>        
    </label>
  </labels>
	
</settings>