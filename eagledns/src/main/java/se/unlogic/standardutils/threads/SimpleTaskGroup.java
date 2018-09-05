/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.threads;

import java.util.concurrent.BlockingQueue;


public class SimpleTaskGroup implements TaskGroup {

	private BlockingQueue<? extends Runnable> queue;
	
	public SimpleTaskGroup(BlockingQueue<? extends Runnable> queue) {
		
		this.queue = queue;
	}

	public BlockingQueue<? extends Runnable> getTasks() {
		
		return queue;
	}
}
