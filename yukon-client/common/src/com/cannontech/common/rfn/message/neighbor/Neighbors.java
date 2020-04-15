package com.cannontech.common.rfn.message.neighbor;

import java.util.HashSet;

/**
 * The set of Neighbors represents all latest unique entries retrieved from
 *     its neighbor data recently (default 9 days).
 * You won't have null Neighbor object but you may have null rfnIdentifier
 *     and/or null nodeSerialNumber in the object of Neighbor.
 * 
 * @author lizhu2
 */
public class Neighbors extends HashSet<Neighbor> {
    
    private static final long serialVersionUID = 1L;

}
