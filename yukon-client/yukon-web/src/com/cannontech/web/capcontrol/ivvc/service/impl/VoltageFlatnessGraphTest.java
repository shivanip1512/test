package com.cannontech.web.capcontrol.ivvc.service.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.web.capcontrol.ivvc.models.VfLine;
import com.cannontech.web.capcontrol.ivvc.models.VfPoint;
import com.google.common.collect.Lists;

public class VoltageFlatnessGraphTest {

    @Test
    public void test_maxXPosition1() {
    	List<VfLine> lines = Lists.newArrayList();
    	VfLine line = new VfLine();
    	List<VfPoint> points = Lists.newArrayList();
    	VfPoint point1 = new VfPoint(0, 120);
    	points.add(point1);
    	VfPoint point2 = new VfPoint(3, 143);
    	points.add(point2);
    	VfPoint point3 = new VfPoint(6, 121);
    	points.add(point3);
    	VfPoint point4 = new VfPoint(7, 167);
    	points.add(point4);
    	VfPoint point5 = new VfPoint(12, 121);
    	points.add(point5);
    	line.setPoints(points);
    	lines.add(line);    	
    	
    	double maxXPosition = VoltageFlatnessGraphServiceImpl.getMaxXPosition(lines);
        Assert.assertEquals(12.0, maxXPosition, 0.0);
    }
    
    @Test
    public void test_maxXPosition2() {
    	List<VfLine> lines = Lists.newArrayList();
    	VfLine line1 = new VfLine();
    	List<VfPoint> points1 = Lists.newArrayList();
    	VfPoint point1 = new VfPoint(0, 120);
    	points1.add(point1);
    	VfPoint point2 = new VfPoint(3, 143);
    	points1.add(point2);
    	VfPoint point3 = new VfPoint(6, 121);
    	points1.add(point3);
    	line1.setPoints(points1);
    	
    	VfLine line2 = new VfLine();
    	List<VfPoint> points2 = Lists.newArrayList();
    	VfPoint point4 = new VfPoint(76, 120);
    	points2.add(point4);
    	VfPoint point5 = new VfPoint(14, 143);
    	points2.add(point5);
    	VfPoint point6 = new VfPoint(36, 121);
    	points2.add(point6);
    	line2.setPoints(points2);
    	lines.add(line2);    	
    	
    	double maxXPosition = VoltageFlatnessGraphServiceImpl.getMaxXPosition(lines);
    	Assert.assertEquals(76.0, maxXPosition, 0.0);
    }
    
    @Test
    public void test_maxXPosition3() {
    	List<VfLine> lines = Lists.newArrayList();
    	VfLine line1 = new VfLine();
    	List<VfPoint> points1 = Lists.newArrayList();
    	VfPoint point1 = new VfPoint(0, 120);
    	points1.add(point1);
    	VfPoint point2 = new VfPoint(0, 143);
    	points1.add(point2);
    	VfPoint point3 = new VfPoint(0, 121);
    	points1.add(point3);
    	line1.setPoints(points1);
    	
    	VfLine line2 = new VfLine();
    	List<VfPoint> points2 = Lists.newArrayList();
    	VfPoint point4 = new VfPoint(0, 120);
    	points2.add(point4);
    	VfPoint point5 = new VfPoint(0, 143);
    	points2.add(point5);
    	VfPoint point6 = new VfPoint(0, 121);
    	points2.add(point6);
    	line2.setPoints(points2);
    	lines.add(line2);    	
    	
    	double maxXPosition = VoltageFlatnessGraphServiceImpl.getMaxXPosition(lines);
    	Assert.assertEquals(0.0, maxXPosition, 0.0);
    }
    
    @Test
    public void test_maxXPosition4() {
    	List<VfLine> lines = Lists.newArrayList();
    	VfLine line = new VfLine();
    	List<VfPoint> points = Lists.newArrayList();
    	VfPoint point1 = new VfPoint(0, 120);
    	points.add(point1);
    	VfPoint point2 = new VfPoint(99998, 143);
    	points.add(point2);
    	VfPoint point3 = new VfPoint(2000, 121);
    	points.add(point3);
    	VfPoint point4 = new VfPoint(4000, 167);
    	points.add(point4);
    	VfPoint point5 = new VfPoint(99999, 121);
    	points.add(point5);
    	line.setPoints(points);
    	lines.add(line);    	
    	
    	double maxXPosition = VoltageFlatnessGraphServiceImpl.getMaxXPosition(lines);
        Assert.assertEquals(99999.0, maxXPosition, 0.0);
    }
}
