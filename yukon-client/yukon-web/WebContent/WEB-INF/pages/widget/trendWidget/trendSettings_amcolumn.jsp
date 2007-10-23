<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!-- Value between [] brackets, for example [#FFFFFF] shows default value which is used if this parameter is not set -->
<!-- This means, that if you are happy with this value, you can delete this line at all and reduce file size -->
<!-- value or explanation between () brackets shows the range or type of values you should use for this parameter -->

<settings> 
  <type>column</type>                                         <!-- [column] (column / bar) -->

  <width></width>                                             <!-- [] (Number) if empty, will be equal to width of your flash movie -->
  <height></height>                                           <!-- [] (Number) if empty, will be equal to height of your flash movie -->
	
  <data_type>xml</data_type>                                  <!-- [xml] (xml / csv) -->
  <csv_separator>;</csv_separator>                            <!-- [;] (string) csv file data separator (you need it only if you are using csv file for your data) -->     
  <skip_rows>0</skip_rows>                                    <!-- [0] (Number) if you are using csv data type, you can set the number of rows which should be skipped here -->
  <font>Arial</font>                                          <!-- [Arial] (font name) use device fonts, such as Arial, Times New Roman, Tahoma, Verdana... -->
  <text_size>11</text_size>                                   <!-- [11] (Number) text size of all texts. Every text size can be set individually in the settings below -->
  <text_color>#000000</text_color>                            <!-- [#000000] (hex color code) main text color. Every text color can be set individually in the settings below-->
  <decimals_separator>.</decimals_separator>                  <!-- [,] (string) decimal separator. Note, that this is for displaying data only. Decimals in data xml file must be separated with a dot -->
  <thousands_separator> </thousands_separator>                <!-- [ ] (string) thousand separator -->
  <redraw>false</redraw>                                      <!-- [false] (true / false) if your chart's width or height is set in percents, and redraw is set to true, the chart will be redrawn then screen size changes -->
                                                              <!-- this function is beta, be careful. Legend, buttons labels will not be repositioned if you set your x and y values for these objects -->
  <reload_data_interval>0</reload_data_interval>              <!-- [0] (Number) how often data should be reloaded (time in seconds) -->
  <add_time_stamp>false</add_time_stamp>                      <!-- [false] (true / false) if true, a unique number will be added every time flash loads data. Mainly this feature is useful if you set reload _data_interval -->
  <precision>2</precision>                                    <!-- [2] (Number) shows how many numbers should be shown after comma for calculated values (percents) -->
  <depth>10</depth>                                            <!-- [0] (Number) the depth of chart and columns (for 3D effect) -->
  <angle>35</angle>                                            <!-- [30] (0 - 90) angle of chart area and columns (for 3D effect) -->
  <export_image_file></export_image_file>                     <!-- [] (filename) If you set filename here context menu (then user right clicks on flash movie) "Export as image" will appear. This will allow user to export chart as an image -->  
                                                              <!-- Note, that this works only on a web server -->
    
                                                                                
  
  <column>
    <type>clustered</type>                                    <!-- [clustered] (stacked, 100% stacked) -->
    <width>80</width>                                         <!-- [80] (0 - 100) width of column (in percents)  -->
    <spacing>5</spacing>                                      <!-- [5] (Number) space between columns of one category axis value, in pixels. Negative values can be used. -->
    <grow_time>1.5</grow_time>                                  <!-- [0] (Number) grow time in seconds. Leave 0 to appear instantly -->
    <grow_effect>elastic</grow_effect>                        <!-- [elastic] (elastic, regular, strong) -->    
    
    <alpha>100</alpha>                                        <!-- [100] (Number) alpha of all columns -->
    <border_color>#000000</border_color>                      <!-- [#FFFFFF] (hex color code) -->
    <border_alpha>40</border_alpha>                             <!-- [0] (Number) -->
    <data_labels>
      <![CDATA[]]>                                            <!-- [] ({title} {value} {series} {percents} {start} {difference} {total}) You can format any data label: {title} will be replaced with real title, {value} - with value and so on. You can add your own text or html code too. -->
    </data_labels>
    <data_labels_text_color>#000000</data_labels_text_color>         <!-- [text_color] (hex color code) --> 
    <data_labels_text_size></data_labels_text_size>           <!-- [text_size] (Number) -->
    <data_labels_position>above</data_labels_position>             <!-- [] (inside, outside, above). This setting is only for clustered chart. --> 
                                                              <!-- if you set "above" for column chart, the data label will be displayed inside column, rotated  by 90 degrees -->
    <balloon_text>                                                    
     <![CDATA[<b>{value}</b>]]>                <!-- [] ({title} {value} {series} {percents} {start} {difference} {total}) You can format any data label: {title} will be replaced with real title, {value} - with value and so on. You can add your own text or html code too. -->
    </balloon_text>    
    <link_target></link_target>                               <!-- [] (_blank, _top ...) -->
    <gradient></gradient>                                     <!-- [vertical] (horizontal / vertical) Direction of column gradient. Gradient colors are defined in graph settings below. -->                         
  </column>
  
  <line>                                                      <!-- Here are general settings for "line" graph type. You can set most of these settings for individual lines in graph settings below -->
    <connect></connect>                                       <!-- [false] (true / false) whether to connect points if data is missing -->
    <width></width>                                           <!-- [2] (Number) line width -->
    <alpha></alpha>                                           <!-- [100] (Number) line alpha -->
    <fill_alpha></fill_alpha>                                 <!-- [0] (Number) fill alpha -->
    <bullet></bullet>                                         <!-- [] (round, square, filename) -->
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
    <color>#FFFFFF</color>                                    <!-- [#FFFFFF] (hex color code) -->
    <alpha>0</alpha>                                          <!-- [0] (0 - 100) use 0 if you are using custom swf or jpg for background -->
    <border_color>#000000</border_color>                      <!-- [#FFFFFF] (hex color code) -->
    <border_alpha>0</border_alpha>                            <!-- [0] (0 - 100) -->
    <file></file>                                             <!-- [] (filename) swf or jpg file of a background. Do not use progressive jpg file, it will be not visible with flash player 7 -->
                                                              <!-- The chart will look for this file in amline_path folder (amline_path is set in HTML) -->
  </background>
  
  
  <plot_area>                                                 <!-- PLOT AREA (the area between axes) -->
    <color>#000000</color>                                    <!-- [#FFFFFF](hex color code) -->
    <alpha>0</alpha>                                          <!-- [0] (0 - 100) if you want it to be different than background color, use bigger than 0 value -->                                        
    <margins>                                                 <!-- plot area margins -->
		<c:choose>
			<c:when test="${fn:length(units) * 10 > 70}">
				<left>${fn:length(units) * 11}</left>		  <!-- [60](Number) --> 
			</c:when>
			<c:otherwise>
				<left>70</left>                               <!-- [60](Number) --> 
			</c:otherwise>
		</c:choose>
      
      <top>60</top>                                           <!-- [60](Number) --> 
      <right>30</right>                                       <!-- [60](Number) --> 
      <bottom>48</bottom>                                     <!-- [80](Number) --> 
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
    </value>
  </grid>
  
 <values>                                                    <!-- VALUES -->
    <category>                                                <!-- category axis -->
      <enabled>true</enabled>                                 <!-- [true] (true / false) -->
      <frequency>${gridFrequency}</frequency>                                <!-- [1] (Number) how often values should be placed -->
      <rotate>35</rotate>                                     <!-- [0] (0 - 90) angle of rotation. If you want to rotate by degree from 1 to 89, you must have font.swf file in fonts folder -->      
      <color>#000000</color>                                         <!-- [text_color] (hex color code) -->
      <text_size>10</text_size>                                 <!-- [text_size] (Number) -->    
    </category>
    <value>                                                   <!-- value axis -->
      <enabled>true</enabled>                                 <!-- [true] (true / false) -->    
      <min>0</min>                                            <!-- [] (Number) minimum value of this axis. If empty, this value will be calculated automatically. -->
      <max></max>                                             <!-- [] (Number) maximum value of this axis. If empty, this value will be calculated automatically -->
                                                              <!-- max and min values are recalculated and may be different from entered here. For example, if you set min value -1000 and max value 994, the max value will be changed to 1000. -->
      <frequency>1</frequency>                                 <!-- [1] (Number) how often values should be placed, 1 - near every gridline, 2 - near every second gridline... -->
      <rotate>0</rotate>                                       <!-- [0] (0 - 90) angle of rotation. If you want to rotate by degree from 1 to 89, you must have font.swf file in fonts folder -->
      <skip_first>false</skip_first>                               <!-- [true] (true / false) to skip or not first value -->
      <skip_last></skip_last>                                 <!-- [false] (true / false) to skip or not last value -->
      <color></color>                                         <!-- [text_color] (hex color code) --> 
      <text_size>10</text_size>                                 <!-- [text_size] (Number) -->
      <unit></unit>                                           <!-- [] (text) -->
      <unit_position>right</unit_position>                    <!-- [right] (right / left) -->
      <integers_only></integers_only>                         <!-- [false] (true / false) if set to true, values with decimals will be omitted -->             
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
    </value>
  </axes>  
  
  <balloon>                                                   <!-- BALLOON -->
    <enabled>true</enabled>                                   <!-- [true] (true / false) -->
    <color></color>                                           <!-- [] (hex color code) balloon background color. If empty, slightly darker then current column color will be used -->
    <alpha>100</alpha>                                        <!-- [100] (0 - 100) -->
    <text_color></text_color>                                 <!-- [#FFFFFF] (hex color code) -->
    <text_size>11</text_size>                                 <!-- [text_size] (Number) -->    
  </balloon>
    
  <legend>                                                    <!-- LEGEND -->
    <enabled>false</enabled>                                   <!-- [true] (true / false) -->
    <x></x>                                                   <!-- [] (Number) if empty, will be equal to left margin -->
    <y></y>                                                   <!-- [] (Number) if empty, will be below plot area -->
    <width></width>                                           <!-- [] (Number) if empty, will be equal to plot area width -->
    <color>#FFFFFF</color>                                    <!-- [#FFFFFF] (hex color code) background color -->
    <alpha>0</alpha>                                          <!-- [0] (0 - 100) background alpha -->
    <border_color>#000000</border_color>                      <!-- [#000000] (hex color code) border color -->
    <border_alpha>0</border_alpha>                            <!-- [0] (0 - 100) border alpha -->
    <text_color></text_color>                                 <!-- [text_color] (hex color code) -->   
    <text_size></text_size>                                   <!-- [text_size] (Number) -->
    <spacing>5</spacing>                                      <!-- [10] (Number) vertical and horizontal gap between legend entries -->
    <margins>0</margins>                                      <!-- [0] (Number) legend margins (space between legend border and legend entries, recommended to use only if legend border is visible or background color is different from chart area background color) -->    
    <key>                                                     <!-- KEY (the color box near every legend entry) -->
      <size>16</size>                                         <!-- [16] (Number) key size-->
      <border_color></border_color>                           <!-- [] (hex color code) leave empty if you don't want to have border -->
    </key>
  </legend>
  
 
  <strings>
    <no_data>No data for selected period</no_data>            <!-- [No data for selected period] (text) if data for selected period is missing, this message will be displayed -->
    <export_as_image></export_as_image>                       <!-- [Export as image] (text) text for right click menu -->
    <collecting_data></collecting_data>                       <!-- [Collecting data] (text) this text is displayed while exporting chart to an image -->
  </strings> 
  
  <labels>                                                    <!-- LABELS -->
                                                              <!-- you can add as many labels as you want. Some html tags supported: <b>, <i>, <u>, <font>, <a href=""> -->
    <label>
      <x>0</x>                                                <!-- [0] (Number) -->
      <y>25</y>                                               <!-- [0] (Number) -->
      <rotate></rotate>                                       <!-- [false] (true / false) -->
      <width></width>                                      	  <!-- [] (Number) if empty, will stretch from left to right untill label fits -->
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
	
</settings>