<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<settings>
	<font>Arial</font>
	<reload_data_interval>0</reload_data_interval>
	<decimals_separator>.</decimals_separator>
	<vertical_lines>
		<mask>true</mask>
		<width>1</width>
	</vertical_lines>
	
	<plot_area>
		<border_color>#e6e6e6</border_color>
		<border_alpha>100</border_alpha>
		<margins>
			<left>60</left>
			<top>50</top>
			<right>10</right>
			<bottom>100</bottom>
		</margins>
	</plot_area>
	
	<indicator>
		<color>#8EE681</color>
	</indicator>
	
	<grid>
		<x>
			<alpha>40</alpha>
			<approx_count>10</approx_count>
		</x>
		<y_left>
			<alpha>40</alpha>
		</y_left>
	</grid>
	
	<values>
		<x>
			<frequency>1</frequency>
		</x>
		<y_left>
			<min>${graphSettings.YLowerBound-5}</min>
			<max>${graphSettings.YUpperBound+5}</max>
			<strict_min_max>true</strict_min_max>
		</y_left>
	</values>
	
	<axes>
		<x>
			<color>#e6e6e6</color>
			<width>1</width>
		</x>
		<y>
			<color>#e6e6e6</color>
			<width>1</width>
		</y>
	</axes>
	
	<labels>
		<label>
      		<x>0</x>
      		<y>10</y>
			<align>center</align>  
      		<text>
        		<![CDATA[<b>${graphSettings.graphTitle}</b>]]>
      		</text>
    	</label>
    	<label>
      		<x>0</x>
      		<y>175</y>
      		<rotate>90</rotate>
			<align>left</align>  
      		<text>
        		<![CDATA[<b>${graphSettings.YAxisLabel}</b>]]>
      		</text>        
    	</label>
    	<label>
      		<x>0</x>
      		<y>301</y>
			<align>center</align>  
      		<text>
        		<![CDATA[<b>${graphSettings.XAxisLabel}</b>]]>
      		</text>
    	</label>
  	</labels>

	<legend>                                                    <!-- LEGEND -->
	    <enabled>true</enabled>                                   <!-- [true] (true / false) -->
	    <y>315</y>                                                   <!-- [] (Number / Number% / !Number) if empty, will be 20px below x axis values -->
	    <color>#FFFFFF</color>                                    <!-- [#FFFFFF] (hex color code) background color -->
	    <alpha>0</alpha>                                          <!-- [0] (0 - 100) background alpha -->
	    <border_color>#000000</border_color>                      <!-- [#000000] (hex color code) border color -->
	    <border_alpha>0</border_alpha>                            <!-- [0] (0 - 100) border alpha -->
	    <text_color_hover>#BBBB00</text_color_hover>              <!-- [#BBBB00] (hex color code) -->    
	    <spacing>10</spacing>                                     <!-- [10] (Number) vertical and horizontal gap between legend entries -->
	    <margins>0</margins>                                      <!-- [0] (Number) legend margins (space between legend border and legend entries, recommended to use only if legend border is visible or background color is different from chart area background color) -->    
	    <graph_on_off>false</graph_on_off>                         <!-- [true] (true / false) if true, color box gains "checkbox" function - it is possible to make graphs visible/invisible by clicking on this checkbox -->
	    <key>                                                     <!-- KEY (the color box near every legend entry) -->
	    	<size>12</size>                                         <!-- [16] (Number) key size-->
	    	<border_color></border_color>                           <!-- [] (hex color code) leave empty if you don't want to have border-->
	    	<key_mark_color>#FFFFFF</key_mark_color>                <!-- [#FFFFFF] (hex color code) key tick mark color -->
	    </key>
	    <values>
	    	<enabled>true</enabled>          
		    <text>{value}</text>
		    <width>40</width>                                         <!-- [80] (Number) width of text field for value -->
		    <align>left</align>                                     <!-- [right] (right / left) -->
	    </values>
  	</legend>
  	
  	<help>
  		<button>
			<color>#FCD202</color>
			<text_color>#000000</text_color>
		</button>
		<balloon>
			<color>#FCD202</color>
			<text_color>#000000</text_color>
			<text><![CDATA[Click on the graph's line to turn on/off value balloon]]></text>
		</balloon>
  	</help>

	<!-- These guides display the top and bottom grey areas of the graph -->
	<guides>
		<max_min>false</max_min>
		<guide>
			<start_value>${graphSettings.YUpperBound+100}</start_value>
			<end_value>${graphSettings.YUpperBound}</end_value>
			<width>2</width>
			<fill_alpha>10</fill_alpha>
			<fill_color>#000000</fill_color>
		</guide>
		<guide>
			<start_value>${graphSettings.YLowerBound}</start_value>
			<end_value>${graphSettings.YLowerBound-100}</end_value>
			<width>2</width>
			<fill_alpha>10</fill_alpha>
			<fill_color>#000000</fill_color>
		</guide>
	</guides>
	
	
	<c:choose>
        <c:when test="${hasEditingRole}">
            <c:set var="editKey" value="edit"/>
        </c:when>
        <c:otherwise>
            <c:set var="editKey" value="info"/>
        </c:otherwise>
    </c:choose>
	
	<data>
		<chart>
			<series>
				<c:set var="prevVal" value=""/>
				<c:forEach var="line" items="${graph.lines}">
					<!-- -3 is the line.id of zone transition lines and should not have their own x-axis value -->
					<c:if test="${line.id != '-3'}">
						<c:forEach var="point" items="${line.points}">
							<c:if test="${prevVal != point.x}">
								<value xid="${point.x}">${point.x}</value>
							</c:if>
							<c:set var="prevVal" value="${point.x}"/>
						</c:forEach>
					</c:if>
				</c:forEach>
			</series>
			<graphs>
				<c:forEach var="line" items="${graph.lines}">
					<graph gid="${line.id}" title="${line.zoneName}"
						bullet="${line.settings.bullet}"
						fill_alpha="${line.settings.fillAlpha}"
						min_max="${line.settings.minMax}"
						color="${line.settings.color}"
						selected="${line.settings.showBullet}"
						vertical_lines="${line.settings.verticalLines}"
						visible_in_legend="${line.settings.showBullet}"
						data_labels="{value}">
						
						<c:forEach var="point" items="${line.points}">
							<value xid="${point.x}" description="${point.description}">${point.y}</value>
						</c:forEach>
					</graph>
				</c:forEach>
			</graphs>
		</chart>
	</data>
</settings>