/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao.script;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public interface ScriptDAO {

	void executeScript(InputStream inputStream) throws SQLException, IOException;
	
	void executeScript(String script) throws SQLException;

}
