package com.cannontech.stars.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.database.data.lite.stars.LiteInventoryBase;

public class AbstractFilterTest {
    private static final int listSize = 4;
    private static final String trueString = Boolean.toString(true);
    private static final String falseString = Boolean.toString(false);
    private static final int allTrue = 0;
    private static final int allFalse = 1;
    private static final int bothTrueFalse = 2;
    private Comparator<Integer> mockMapComparator;
    private AbstractFilter filter;
    private List<FilterWrapper> allTrueFilterList;
    private List<FilterWrapper> allTrueFilterList2;
    private List<FilterWrapper> allFalseFilterList;
    private List<FilterWrapper> allFalseFilterList2;
    private List<FilterWrapper> bothTrueFalseFilterList;
    private List<FilterWrapper> bothTrueFalseFilterList2;
    private List<LiteInventoryBase> inventoryList;
    
    @Before
    public void setUp() {
        filter = createMockFilter();
        
        mockMapComparator = createMockMapComparator();
        
        allTrueFilterList = createMockFilterList(0, allTrue);
        allTrueFilterList2 = createMockFilterList(1, allTrue);
        allFalseFilterList = createMockFilterList(2, allFalse);
        allFalseFilterList2 = createMockFilterList(3, allFalse);
        bothTrueFalseFilterList = createMockFilterList(4, bothTrueFalse);
        bothTrueFalseFilterList2 = createMockFilterList(5, bothTrueFalse);
        
        inventoryList = createInventoryList();
    }

    @After
    public void tearDown() {
        filter = null;
        
        mockMapComparator = null;
        
        allTrueFilterList = null;
        allTrueFilterList2 = null;
        allFalseFilterList = null;
        allFalseFilterList2 = null;
        bothTrueFalseFilterList = null;
        bothTrueFalseFilterList2 = null;
        
        inventoryList = null;
    }
    
    @Test
    public void test_OR_Check() {
        boolean result = filter.doOrCheck(allTrueFilterList, trueString);
        assertTrue("Expected true: ( true || true )", result);
        
        boolean result2 = filter.doOrCheck(allFalseFilterList, falseString);
        assertFalse("Expected false: (false || false)", result2);
        
        boolean result3 = filter.doOrCheck(bothTrueFalseFilterList, trueString + falseString);
        assertTrue("Expected true: (true || false)", result3);
    }
    
    @Test
    public void test_AND_Check() {

        // (true || true) && (true || true)
        List<FilterWrapper> filterList = new ArrayList<FilterWrapper>();
        filterList.addAll(allTrueFilterList);
        filterList.addAll(allTrueFilterList2);
        List<LiteInventoryBase> resultList = filter.filter(inventoryList, filterList);
        assertEquals(inventoryList, resultList);
        
        // (true || false) && (true || false)
        filterList = new ArrayList<FilterWrapper>();
        filterList.addAll(bothTrueFalseFilterList);
        filterList.addAll(bothTrueFalseFilterList2);
        resultList = filter.filter(inventoryList, filterList);
        assertEquals(inventoryList, resultList);

        // (true || true) && (false || true)
        filterList = new ArrayList<FilterWrapper>();
        filterList.addAll(allTrueFilterList);
        filterList.addAll(bothTrueFalseFilterList);
        resultList = filter.filter(inventoryList, filterList);
        assertEquals(inventoryList, resultList);

        // (false || false) && (false || false)
        filterList = new ArrayList<FilterWrapper>();
        filterList.addAll(allFalseFilterList);
        filterList.addAll(allFalseFilterList2);
        resultList = filter.filter(inventoryList, filterList);
        assertEquals(Collections.emptyList(), resultList);

        // (true || true) && (false || false)
        filterList = new ArrayList<FilterWrapper>();
        filterList.addAll(allTrueFilterList);
        filterList.addAll(allFalseFilterList);
        resultList = filter.filter(inventoryList, filterList);
        assertEquals(Collections.emptyList(), resultList);
        
    }
    
    private AbstractFilter createMockFilter() {
        AbstractFilter filter = new AbstractFilter() {
            @Override
            protected boolean doFilterCheck(Object o, FilterWrapper filter) {
                
                boolean result = Boolean.parseBoolean(filter.getFilterText());
                return result;
            }
            
            @Override
            protected Comparator<Integer> getFilterMapComparator() {
                return mockMapComparator;
            }
        };
        return filter;
    }
    
    private List<LiteInventoryBase> createInventoryList() {
        final List<LiteInventoryBase> list = new ArrayList<LiteInventoryBase>();
        for (int x = 0; x < listSize; x++) {
            LiteInventoryBase inventoryBase = new LiteInventoryBase();
            inventoryBase.setInventoryID(x);
            list.add(inventoryBase);
        }
        return list;
    }
    
    private List<FilterWrapper> createMockFilterList(final int typeId, final int mode) {
        final String typeIdString = Integer.toString(typeId);
        final List<FilterWrapper> filterList = new ArrayList<FilterWrapper>();
        
        switch (mode) {
            case allTrue : {
                filterList.add(new FilterWrapper(typeIdString, trueString, trueString));
                filterList.add(new FilterWrapper(typeIdString, trueString, trueString));
                break;
            }
            case allFalse : {
                filterList.add(new FilterWrapper(typeIdString, falseString, falseString));
                filterList.add(new FilterWrapper(typeIdString, falseString, falseString));
                break;
            }
            case bothTrueFalse : {
                filterList.add(new FilterWrapper(typeIdString, trueString, trueString));
                filterList.add(new FilterWrapper(typeIdString, falseString, falseString));
                break;
            }
        }

        return filterList;
    }
    
    private Comparator<Integer> createMockMapComparator() {
        return new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        };        
    }
    
}
