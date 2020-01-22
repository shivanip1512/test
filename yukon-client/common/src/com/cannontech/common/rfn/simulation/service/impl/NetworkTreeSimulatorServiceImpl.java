package com.cannontech.common.rfn.simulation.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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

public class NetworkTreeSimulatorServiceImpl implements NetworkTreeSimulatorService {
    
    private static final Logger log = YukonLogManager.getLogger(NetworkTreeSimulatorServiceImpl.class);
    @Autowired private RfnDeviceDao rfnDeviceDao;  
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    private Random random = new Random();
    private Integer nodeNullPercent;
    private Integer branchMin;
    private Integer branchMax;
    private Integer devicesAroundTheGateway;
    
    private void initSettings() {
        nodeNullPercent = yukonSimulatorSettingsDao
                .getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_TREE_PERCENT_NULL);
        branchMin = yukonSimulatorSettingsDao
                .getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_TREE_MIN_HOP);
        branchMax = yukonSimulatorSettingsDao
                .getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_TREE_MAX_HOP);
        devicesAroundTheGateway = yukonSimulatorSettingsDao
                .getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_TREE_NODES_ONE_HOP);
        log.info("nodeNullPercent:{} branchMin:{} branchMax:{} devicesAroundTheGateway:{}", nodeNullPercent,
                branchMin, branchMax, devicesAroundTheGateway);
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
        if(allDevices.isEmpty()) {
            return node;
        }
        //System.out.println(allDevices);
       
        ListIterator<RfnDevice> it = allDevices.listIterator();
        
        List<Node<RfnIdentifier>> endNodes = new ArrayList<>();
        
        while (devicesAroundTheGateway-- > 0) {
            List<Node<RfnIdentifier>> branch = getNodes(it);
            // System.out.println("Added devices " + branch.size());
            endNodes.addAll(fork(it, addToBranch(node, branch)));
        }

        while (it.hasNext()) {
            if (endNodes.size() > 3) {
                // remove half end nodes
                endNodes.subList(0, (int) endNodes.size() / 2).clear();
            }
            // fork from a random node
            endNodes.addAll(fork(it, endNodes.get(random.nextInt(endNodes.size()))));
        }        

       // log.info(root.print());
        return node; 
    }

    /**
     * Creates a randomized fork
     */
    private List<Node<RfnIdentifier>> fork(ListIterator<RfnDevice> it, Node<RfnIdentifier> node) {
        if(node == null) {
            return new ArrayList<>();
        }
        List<Node<RfnIdentifier>> endNodes = new ArrayList<>();
        List<Node<RfnIdentifier>> left = getNodes(it);
        endNodes.add(addToBranch(node, left));

        if (random.nextBoolean()) {
            List<Node<RfnIdentifier>> right = getNodes(it);
            // System.out.println("-- Added devices " + branch.size() + " node "+ node + " branch "+branch);
            endNodes.add(addToBranch(node, right));
        }
        return endNodes;
    }

    /**
     * Adds branch to a node
     */
    private Node<RfnIdentifier> addToBranch(Node<RfnIdentifier> endNode, List<Node<RfnIdentifier>> branch) {
        if(endNode == null) {
            return null;
        }
        Iterator<Node<RfnIdentifier>> branchIt = branch.iterator();
        Node<RfnIdentifier> lastNode = null;
        while(branchIt.hasNext()) {
            Node<RfnIdentifier> nextNode = branchIt.next();
            if(lastNode == null) {
                lastNode = endNode.addChild(nextNode);
            } else {
                lastNode = lastNode.addChild(nextNode);
            }
        }
        return lastNode;
    }

    /**
     * Returns random number of nodes in range between min and max number of nodes
     */
    private List<Node<RfnIdentifier>> getNodes(Iterator<RfnDevice> it) {
        List<Node<RfnIdentifier>> nodes = new ArrayList<>();
        int randomNodeCount = getRandomNumberInRange(branchMin, branchMax);
        while (randomNodeCount-- > 0 && it.hasNext()) {
            RfnDevice nextNode = it.next();
            nodes.add(random.nextInt(100) < nodeNullPercent ? new Node<>(null): new Node<>(nextNode.getRfnIdentifier()));
            it.remove();
        }
        return nodes;
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
        log.info("{} Created Yukon NODE total node count {} null node count {}",
                node.getData(), node.count(false), node.count(true));
        log.info("{} Created NM VERTEX from Yukon Node node count {} added nodes {}", vertex.getRfnIdentifier(),
                NetworkDebugHelper.count(vertex), totalNodesAdded);
        //log.info(NetworkDebugHelper.print(vertex));
        return vertex;
    }
   
    /**
     * Makes a RfnVertex out of Node
     */
    private void copy(Node<RfnIdentifier> node, RfnVertex parent, AtomicInteger totalNodesAdded) {
        for (Iterator<Node<RfnIdentifier>> it = node.getChildren().iterator(); it.hasNext();) {
            Node<RfnIdentifier> nextNode = it.next();
            if(parent.getChildren() == null) {
                parent.setChildren(new HashSet<RfnVertex>());
            }
            RfnVertex child = new RfnVertex();
            child.setParent(parent);
            child.setRfnIdentifier(nextNode.getData());
            parent.getChildren().add(child);
            totalNodesAdded.incrementAndGet();
            copy(nextNode, child, totalNodesAdded);
        }
    }
}