package com.cannontech.common.rfn.simulation.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.tree.RfnVertex;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.simulation.service.NetworkTreeSimulatorService;
import com.cannontech.common.rfn.simulation.util.NetworkDebugHelper;
import com.cannontech.common.util.tree.Node;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.google.common.collect.Lists;

public class NetworkTreeSimulatorServiceImpl implements NetworkTreeSimulatorService {

    private static final Logger log = YukonLogManager.getLogger(NetworkTreeSimulatorServiceImpl.class);
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    private Random random = new Random();
    private Integer nodeNullPercent;
    private Integer branchMax;
    private Integer devicesAroundTheGateway;

    private void initSettings() {
        nodeNullPercent = yukonSimulatorSettingsDao
                .getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_TREE_PERCENT_NULL);
        branchMax = yukonSimulatorSettingsDao
                .getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_TREE_MAX_HOP);
        devicesAroundTheGateway = yukonSimulatorSettingsDao
                .getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_TREE_NODES_ONE_HOP);
        log.info("nodeNullPercent:{} branchMax:{} devicesAroundTheGateway:{}", nodeNullPercent,
                branchMax, devicesAroundTheGateway);
    }

    /**
     * Builds randomized node from all devices
     */
    private Node<RfnIdentifier> buildNode(RfnDevice gateway) {
        initSettings();
        Node<RfnIdentifier> node = new Node<RfnIdentifier>(gateway.getRfnIdentifier());
        List<RfnDevice> allDevices = rfnDeviceDao.getDevicesForGateway(gateway.getPaoIdentifier().getPaoId());
        int totalDevices = allDevices.size();

        log.info("\nCreating a tree (Node) for {} devices {} ", gateway, totalDevices);
        if (allDevices.isEmpty()) {
            return node;
        }
        // System.out.println(allDevices);
        List<RfnDevice> aroundGateway = new ArrayList<>(allDevices.subList(0,  devicesAroundTheGateway));
        allDevices.removeAll(aroundGateway);
        log.debug("Devices around the gateway:{} remaining devices:{}",  aroundGateway.size(), allDevices.size());
        List<List<RfnDevice>> branches = Lists.partition(allDevices, branchMax);
        
        List<Node<RfnIdentifier>> endNodes = new ArrayList<>();
        aroundGateway.forEach(device -> endNodes.add(addNode(node, device)));
       
        Node<RfnIdentifier> parentNode = getRandomParentNode(endNodes);
        for (List<RfnDevice> devices : branches) {
            for (int i = 0; i < devices.size(); i++) {
                int deviceId = devices.get(i).getPaoIdentifier().getPaoId();
                Node<RfnIdentifier> newNode = addNode(parentNode, devices.get(i));
                // last device - assign a new parent node
                if (i == (devices.size() - 1)) {
                    if (deviceId % 3 == 0 || deviceId % 7 == 0) {
                        parentNode = newNode;
                    } else if (deviceId % 6 == 0) {
                        parentNode = parentNode.getParent();
                    } else if (deviceId % 8 == 0) {
                        parentNode = getRandomParentNode(endNodes);
                    }
                   // keep current node as parent
                }
            }
        }
       
        
        log.debug(node.print());
        return node;
    }

    private Node<RfnIdentifier> getRandomParentNode(List<Node<RfnIdentifier>> endNodes) {
        return endNodes.get(getRandomNumberInRange(0, endNodes.size() - 1));
    }
    
    private Node<RfnIdentifier> addNode(Node<RfnIdentifier> parentNode, RfnDevice device) {
        RfnIdentifier rfnIdentifier = device.getRfnIdentifier();
        Node<RfnIdentifier> newNode = nodeNullPercent == 0 ? new Node<>(
                rfnIdentifier) : random.nextInt(100) < nodeNullPercent ? getNullNode(rfnIdentifier) : new Node<>(
                        rfnIdentifier);
        parentNode.addChild(newNode);
        return newNode;
    }
    
    private Node<RfnIdentifier> getNullNode(RfnIdentifier identifier) {
        if(random.nextBoolean()) {
            return new Node<>(null);
        }
        return new Node<>(new RfnIdentifier("_EMPTY_", identifier.getSensorManufacturer(), identifier.getSensorModel()));
        
    }

    private int getRandomNumberInRange(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    @Override
    public RfnVertex buildVertex(RfnDevice gateway) {
        Node<RfnIdentifier> node = buildNode(gateway);
        RfnVertex vertex = new RfnVertex();
        vertex.setRfnIdentifier(node.getData());
        AtomicInteger totalNodesAdded = new AtomicInteger(1);
        copy(node, vertex, totalNodesAdded);
        log.info("NM VERTEX:{} Vertex nodes:{} added nodes:{}", vertex.getRfnIdentifier(),
                NetworkDebugHelper.count(vertex), totalNodesAdded);
        log.trace("{}", NetworkDebugHelper.print(vertex));
        return vertex;
    }

    /**
     * Makes a RfnVertex out of Node
     */
    private void copy(Node<RfnIdentifier> node, RfnVertex parent, AtomicInteger totalNodesAdded) {
        for (Iterator<Node<RfnIdentifier>> it = node.getChildren().iterator(); it.hasNext();) {
            Node<RfnIdentifier> nextNode = it.next();
            RfnVertex child = new RfnVertex();
            child.setParent(parent);
            child.setRfnIdentifier(nextNode.getData());
            parent.getChildren().add(child);
            totalNodesAdded.incrementAndGet();
            copy(nextNode, child, totalNodesAdded);
        }
    }
}