package com.eaton.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.openqa.selenium.WebElement;

public class SortWebColumn {

    public static ArrayList<String> getSortDesc(List<WebElement> columnList) {

		ArrayList<String> obtainedList = new ArrayList<>();
              
        for (WebElement element : columnList) {
        	 obtainedList.add(element.getText());

        }
          
		ArrayList<String> sortedList = new ArrayList<>();   
    	for(String s:obtainedList){
    	sortedList.add(s);
    	}
        
    	Collections.sort(sortedList, String.CASE_INSENSITIVE_ORDER);
    	Collections.reverse(sortedList);
    	return sortedList;
    }

    public static ArrayList<String> getSortAsc(List<WebElement> columnList) {

		ArrayList<String> obtainedList = new ArrayList<>();
              
        for (WebElement element : columnList) {
        	 obtainedList.add(element.getText());

        }
          
		ArrayList<String> sortedList = new ArrayList<>();   
    	for(String s:obtainedList){
    	sortedList.add(s);
    	}
        
    	Collections.sort(sortedList, String.CASE_INSENSITIVE_ORDER);
    	return sortedList;
    }
    
}
