package com.cannontech.web.support;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ThreadDumpController implements Controller {

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        Comparator<Thread> comparator = new Comparator<Thread>() {
            public int compare(Thread o1, Thread o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        };
        SortedMap<Thread, StackTraceElement[]> sortedStackTraces = new TreeMap<Thread, StackTraceElement[]>(comparator);
        sortedStackTraces.putAll(allStackTraces);
        ModelAndView mav = new ModelAndView("threadDump.jsp");
        mav.addObject("dump", sortedStackTraces);
        return mav;
    }

}
