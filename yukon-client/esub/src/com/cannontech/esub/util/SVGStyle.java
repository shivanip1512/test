package com.cannontech.esub.util;

import java.awt.Color;

import com.loox.jloox.LxStyle;

/**
 * @author alauinger
 */
public class SVGStyle {
	private Float opacity;
	private Color strokeColor;
	private Float strokeWidth;
	private Color fillColor;
	private String fontFamily;
	private String fontStyle;
	private String fontWeight;
	private Integer fontSize;
	
	public SVGStyle(String styleStr) {
		init(styleStr);
	}
	
	private void init(String s) {
		String temp = parseValue("opacity:",s);
		if(temp != null) {
			opacity = new Float(temp);
		}
		
		temp = parseValue("stroke:rgb",s);
		if(temp != null) {
			strokeColor = parseColor(temp);
		}
		
		temp = parseValue("stroke-width:",s);
		if(temp != null) {
			strokeWidth = new Float(temp);
		}
		
		temp = parseValue("fill:rgb",s);
		if(temp != null) {
			fillColor = parseColor(temp);
		}
		
		fontFamily = parseValue("font-family:",s);
		fontStyle = parseValue("font-style:",s);
		fontWeight = parseValue("font-weight",s);
		
		temp = parseValue("font-size",s);
		if(temp != null) {
			fontSize = new Integer(temp);
		}
	}
	
	private String parseValue(String key, String s) {
		String val = null;
		try {		
			int start = s.indexOf(key)+key.length();
			int end = s.indexOf(';',start);
			if(end == -1) {
				end = s.length()-1;
			}
		
			val = s.substring(start,end);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return val;
	}
	
	private Color parseColor(String colorStr) {
		Color c = null;
		try {
			int start = colorStr.indexOf('(');
			int end = colorStr.indexOf(',');
			String redStr = colorStr.substring(start+1,end);
			
			start = end;
			end = colorStr.indexOf(',',end+1);
			String greenStr = colorStr.substring(start+1,end);
			
			start = end;
			end = colorStr.indexOf(')',end+1);
			String blueStr = colorStr.substring(start+1,end);
			
		    c = new Color(Integer.parseInt(redStr),Integer.parseInt(greenStr),Integer.parseInt(blueStr));		   
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return c;
	}
	/**
	 * Returns the fillColor.
	 * @return Color
	 */
	public Color getFillColor() {
		return fillColor;
	}

	/**
	 * Returns the fontFamily.
	 * @return String
	 */
	public String getFontFamily() {
		return fontFamily;
	}

	/**
	 * Returns the fontSize.
	 * @return Integer
	 */
	public Integer getFontSize() {
		return fontSize;
	}

	/**
	 * Returns the fontStyle.
	 * @return String
	 */
	public String getFontStyle() {
		return fontStyle;
	}

	/**
	 * Returns the fontWeight.
	 * @return String
	 */
	public String getFontWeight() {
		return fontWeight;
	}

	/**
	 * Returns the opacity.
	 * @return Float
	 */
	public Float getOpacity() {
		return opacity;
	}

	/**
	 * Returns the strokeColor.
	 * @return Color
	 */
	public Color getStrokeColor() {
		return strokeColor;
	}

	/**
	 * Returns the strokeWidth.
	 * @return Float
	 */
	public Float getStrokeWidth() {
		return strokeWidth;
	}

	/**
	 * Sets the fillColor.
	 * @param fillColor The fillColor to set
	 */
	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	/**
	 * Sets the fontFamily.
	 * @param fontFamily The fontFamily to set
	 */
	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	/**
	 * Sets the fontSize.
	 * @param fontSize The fontSize to set
	 */
	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}

	/**
	 * Sets the fontStyle.
	 * @param fontStyle The fontStyle to set
	 */
	public void setFontStyle(String fontStyle) {
		this.fontStyle = fontStyle;
	}

	/**
	 * Sets the fontWeight.
	 * @param fontWeight The fontWeight to set
	 */
	public void setFontWeight(String fontWeight) {
		this.fontWeight = fontWeight;
	}

	/**
	 * Sets the opacity.
	 * @param opacity The opacity to set
	 */
	public void setOpacity(Float opacity) {
		this.opacity = opacity;
	}

	/**
	 * Sets the strokeColor.
	 * @param strokeColor The strokeColor to set
	 */
	public void setStrokeColor(Color strokeColor) {
		this.strokeColor = strokeColor;
	}

	/**
	 * Sets the strokeWidth.
	 * @param strokeWidth The strokeWidth to set
	 */
	public void setStrokeWidth(Float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

}
