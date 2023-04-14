/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ManyToOneRelation<LocalType, RemoteType, RemoteKeyType> extends Column<LocalType, RemoteType> {

    RemoteKeyType getBeanValue(LocalType bean);

    RemoteKeyType getParamValue(Object bean);

    void getRemoteValue(LocalType bean, ResultSet resultSet, Connection connection, RelationQuery relations)
            throws SQLException;

    void add(LocalType bean, Connection connection, RelationQuery relations) throws SQLException;

    void update(LocalType bean, Connection connection, RelationQuery relations) throws SQLException;

    Field getField();
}
