/**
 * 
 */
package com.cannontech.common.device.model;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;

public class DisplayableDevice implements DisplayablePao {
	private PaoIdentifier paoIdentifier;
	private String name;
	
	public DisplayableDevice(PaoIdentifier paoIdentifier, String name) {
		super();
		this.paoIdentifier = paoIdentifier;
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public PaoIdentifier getPaoIdentifier() {
		return paoIdentifier;
	}
}