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

public class NetworkTreeSimulatorServiceImpl implements NetworkTreeSimulatorService {
    
    private static final Logger log = YukonLogManager.getLogger(NetworkTreeSimulatorServiceImpl.class);
    @Autowired private RfnDeviceDao rfnDeviceDao;  
    private Random random = new Random();
    
    /**
     * Builds randomized node from all devices 
     */
    private Node<RfnIdentifier> buildNode(RfnDevice gateway) {
        Node<RfnIdentifier> root = new Node<RfnIdentifier>(gateway.getRfnIdentifier());
        List<RfnDevice> allDevices = Collections 
                .synchronizedList(rfnDeviceDao.getDevicesForGateway(gateway.getPaoIdentifier().getPaoId()));

        int totalDevices = allDevices.size();
        
        log.info("\nCreating a tree (Node) for {} devices {} ", gateway, totalDevices);
        if(allDevices.isEmpty()) {
            return root;
        }
        //System.out.println(allDevices);
        //create 350 devices around the gateway
        int devicesAroundTheGateway = 350;
        ListIterator<RfnDevice> it = allDevices.listIterator();
        
        List<Node<RfnIdentifier>> endNodes = new ArrayList<>();
        
        while (devicesAroundTheGateway-- > 0) {
            List<Node<RfnIdentifier>> branch = getNodes(2, 8, it);
            // System.out.println("Added devices " + branch.size());
            endNodes.addAll(fork(it, addToBranch(root, branch)));
        }

        while (it.hasNext()) {
            if (endNodes.size() > 3) {
                // remove half end nodes
                endNodes.subList(0, (int) endNodes.size() / 2).clear();
            }
            Node<RfnIdentifier> node = endNodes.get(random.nextInt(endNodes.size()));
            // fork from a random node
            endNodes.addAll(fork(it, node));
        }

        log.info("---------------NODE-- total devices {} node count {} ", totalDevices, NetworkDebugHelper.count(root));
        //log.info(root.print());
        return root; 
    }

    /**
     * Creates a randomized fork
     */
    private List<Node<RfnIdentifier>> fork(ListIterator<RfnDevice> it, Node<RfnIdentifier> node) {
        if(node == null) {
            return new ArrayList<>();
        }
        List<Node<RfnIdentifier>> endNodes = new ArrayList<>();
        List<Node<RfnIdentifier>> left = getNodes(6, 20, it);
        endNodes.add(addToBranch(node, left));

        if (random.nextBoolean()) {
            List<Node<RfnIdentifier>> right = getNodes(5, 15, it);
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
            if(lastNode == null) {
                lastNode = endNode.addChild(branchIt.next());
            } else {
                lastNode = lastNode.addChild(branchIt.next());
            }
        }
        return lastNode;
    }

    /**
     * Returns random number of nodes in range between min and max number of nodes
     */
    private List<Node<RfnIdentifier>> getNodes(int min, int max, Iterator<RfnDevice> it) {
        List<Node<RfnIdentifier>> nodes = new ArrayList<>();
        int randomNodeCount = getRandomNumberInRange(min, max);
        while (randomNodeCount-- > 0 && it.hasNext()) {
            nodes.add(new Node<>(it.next().getRfnIdentifier()));
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
        log.info("---------------VERTEX-- # of nodes added {} nodes counted {}", totalNodesAdded, NetworkDebugHelper.count(vertex));
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
