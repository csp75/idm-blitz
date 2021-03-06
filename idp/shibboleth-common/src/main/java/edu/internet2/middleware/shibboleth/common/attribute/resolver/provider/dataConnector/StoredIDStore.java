/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.dataConnector;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents as persistent, database-backed, store of identifiers.
 * 
 * The DDL for the database is
 * 
 * <tt>CREATE TABLE shibpid {localEntity VARCHAR NOT NULL, peerEntity VARCHAR NOT NULL, principalName VARCHAR NOT NULL, localId VARCHAR NOT NULL, persistentId VARCHAR NOT NULL, peerProvidedId VARCHAR, creationDate TIMESTAMP NOT NULL, deactivationDate TIMESTAMP}</tt>
 * .
 */
public class StoredIDStore {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(StoredIDStore.class);

    /** JDBC data source for retrieving connections. */
    private DataSource dataSource;

    /** Timeout of SQL queries in seconds. */
    private int queryTimeout;

    /** Name of the database table. */
    private final String table = "shibpid";

    /** Name of the local entity ID column. */
    private final String localEntityColumn = "localEntity";

    /** Name of the peer entity ID name column. */
    private final String peerEntityColumn = "peerEntity";

    /** Name of the principal name column. */
    private final String principalNameColumn = "principalName";

    /** Name of the local ID column. */
    private final String localIdColumn = "localId";

    /** Name of the persistent ID column. */
    private final String persistentIdColumn = "persistentId";

    /** ID, provided by peer, associated with the persistent ID. */
    private final String peerProvidedIdColumn = "peerProvidedId";

    /** Name of the creation time column. */
    private final String createTimeColumn = "creationDate";

    /** Name of the deactivation time column. */
    private final String deactivationTimeColumn = "deactivationDate";

    /** Partial select query for ID entries. */
    private final String idEntrySelectSQL = "SELECT * FROM " + table + " WHERE ";

    /** SQL used to deactivate an ID. */
    private final String deactivateIdSQL = "UPDATE " + table + " SET " + deactivationTimeColumn + "= ? WHERE "
            + persistentIdColumn + "= ?";

    /**
     * Constructor.
     * 
     * @param source datasource used to communicate with the database
     * @param timeout SQL query timeout in seconds
     */
    public StoredIDStore(DataSource source, int timeout) {
        dataSource = source;
        queryTimeout = timeout;
    }

    /**
     * Gets the number of persistent ID entries for a (principal, peer, local) tuple.
     * 
     * @param localEntity entity ID of the ID issuer
     * @param peerEntity entity ID of the peer the ID is for
     * @param localId local ID part of the persistent ID
     * 
     * @return the number of identifiers
     * 
     * @throws java.sql.SQLException thrown if there is a problem communication with the database
     */
    public int getNumberOfPersistentIdEntries(String localEntity, String peerEntity, String localId)
            throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT");
        sqlBuilder.append(" count(").append(persistentIdColumn).append(")");
        sqlBuilder.append(" FROM ").append(table).append(" WHERE ");
        sqlBuilder.append(localEntityColumn).append(" = ?");
        sqlBuilder.append(" AND ");
        sqlBuilder.append(peerEntityColumn).append(" = ?");
        sqlBuilder.append(" AND ");
        sqlBuilder.append(localIdColumn).append(" = ?");

        String sql = sqlBuilder.toString();
        Connection dbConn = dataSource.getConnection();
        try {
            log.debug("Selecting number of persistent ID entries based on prepared sql statement: {}", sql);
            PreparedStatement statement = dbConn.prepareStatement(sql);
            statement.setQueryTimeout(queryTimeout);

            log.debug("Setting prepared statement parameter {}: {}", 1, localEntity);
            statement.setString(1, localEntity);
            log.debug("Setting prepared statement parameter {}: {}", 2, peerEntity);
            statement.setString(2, peerEntity);
            log.debug("Setting prepared statement parameter {}: {}", 3, localId);
            statement.setString(3, localId);

            ResultSet rs = statement.executeQuery();
            rs.next();
            return rs.getInt(1);
        } finally {
            try {
                if (dbConn != null && !dbConn.isClosed()) {
                    dbConn.close();
                }
            } catch (SQLException e) {
                log.error("Error closing database connection", e);
            }
        }
    }

    /**
     * Gets all the persistent ID entries for a (principal, peer, local) tuple.
     * 
     * @param localId local ID part of the persistent ID
     * @param peerEntity entity ID of the peer the ID is for
     * @param localEntity entity ID of the ID issuer
     * 
     * @return the active identifier
     * 
     * @throws java.sql.SQLException thrown if there is a problem communication with the database
     */
    public List<PersistentIdEntry> getPersistentIdEntries(String localEntity, String peerEntity, String localId)
            throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder(idEntrySelectSQL);
        sqlBuilder.append(localEntityColumn).append(" = ?");
        sqlBuilder.append(" AND ").append(peerEntityColumn).append(" = ?");
        sqlBuilder.append(" AND ").append(localIdColumn).append(" = ?");
        String sql = sqlBuilder.toString();

        log.debug("Selecting all persistent ID entries based on prepared sql statement: {}", sql);

        Connection dbConn = dataSource.getConnection();
        try {
            PreparedStatement statement = dbConn.prepareStatement(sql);
            statement.setQueryTimeout(queryTimeout);

            log.debug("Setting prepared statement parameter {}: {}", 1, localEntity);
            statement.setString(1, localEntity);
            log.debug("Setting prepared statement parameter {}: {}", 2, peerEntity);
            statement.setString(2, peerEntity);
            log.debug("Setting prepared statement parameter {}: {}", 3, localId);
            statement.setString(3, localId);

            return buildIdentifierEntries(statement.executeQuery());
        } finally {
            try {
                if (dbConn != null && !dbConn.isClosed()) {
                    dbConn.close();
                }
            } catch (SQLException e) {
                log.error("Error closing database connection", e);
            }
        }
    }

    /**
     * Gets the persistent ID entry for the given ID.
     * 
     * @param persistentId the persistent ID
     * 
     * @return the ID entry for the given ID
     * 
     * @throws java.sql.SQLException thrown if there is a problem communication with the database
     */
    public PersistentIdEntry getActivePersistentIdEntry(String persistentId) throws SQLException {
        return getPersistentIdEntry(persistentId, true);
    }

    /**
     * Gets the persistent ID entry for the given ID.
     * 
     * @param persistentId the persistent ID
     * @param onlyActiveId true if only an active ID should be returned, false if a deactivated ID may be returned
     * 
     * @return the ID entry for the given ID
     * 
     * @throws java.sql.SQLException thrown if there is a problem communication with the database
     */
    public PersistentIdEntry getPersistentIdEntry(String persistentId, boolean onlyActiveId) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder(idEntrySelectSQL);
        sqlBuilder.append(persistentIdColumn).append(" = ?");
        if (onlyActiveId) {
            sqlBuilder.append(" AND ").append(deactivationTimeColumn).append(" IS NULL");
        }
        String sql = sqlBuilder.toString();

        log.debug("Selecting persistent ID entry based on prepared sql statement: {}", sql);

        Connection dbConn = dataSource.getConnection();
        try {
            PreparedStatement statement = dbConn.prepareStatement(sql);
            statement.setQueryTimeout(queryTimeout);

            log.debug("Setting prepared statement parameter {}: {}", 1, persistentId);
            statement.setString(1, persistentId);

            List<PersistentIdEntry> entries = buildIdentifierEntries(statement.executeQuery());

            if (entries == null || entries.size() == 0) {
                return null;
            }

            if (entries.size() > 1) {
                log.warn("More than one identifier found, only the first will be used");
            }

            return entries.get(0);
        } finally {
            try {
                if (dbConn != null && !dbConn.isClosed()) {
                    dbConn.close();
                }
            } catch (SQLException e) {
                log.error("Error closing database connection", e);
            }
        }
    }

    public PersistentIdEntry getActivePersistentIdEntry(String localEntity, String peerEntity, String localId,
            boolean isActive) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder(idEntrySelectSQL);
        sqlBuilder.append(localEntityColumn).append(" = ?");
        sqlBuilder.append(" AND ").append(peerEntityColumn).append(" = ?");
        sqlBuilder.append(" AND ").append(localIdColumn).append(" = ?");
        if(isActive){
            sqlBuilder.append(" AND ").append(deactivationTimeColumn).append(" IS NULL");
        }else{
            sqlBuilder.append(" AND ").append(deactivationTimeColumn).append(" IS NOT NULL");
        }
        String sql = sqlBuilder.toString();

        log.debug("Selecting persistent ID entry based on prepared sql statement: {}", sql);
        Connection dbConn = dataSource.getConnection();
        try {
            PreparedStatement statement = dbConn.prepareStatement(sql);
            statement.setQueryTimeout(queryTimeout);

            log.debug("Setting prepared statement parameter {}: {}", 1, localEntity);
            statement.setString(1, localEntity);
            log.debug("Setting prepared statement parameter {}: {}", 2, peerEntity);
            statement.setString(2, peerEntity);
            log.debug("Setting prepared statement parameter {}: {}", 3, localId);
            statement.setString(3, localId);

            log.debug("Getting active persistent Id entries.");
            List<PersistentIdEntry> entries = buildIdentifierEntries(statement.executeQuery());

            if (entries == null || entries.size() == 0) {
                return null;
            }

            if (entries.size() > 1) {
                log.warn("More than one active identifier, only the first will be used");
            }

            return entries.get(0);
        } finally {
            try {
                if (dbConn != null && !dbConn.isClosed()) {
                    dbConn.close();
                }
            } catch (SQLException e) {
                log.error("Error closing database connection", e);
            }
        }
    }

    /**
     * Gets the currently active identifier entry for a (principal, peer, local) tuple.
     * 
     * @param localId local ID part of the persistent ID
     * @param peerEntity entity ID of the peer the ID is for
     * @param localEntity entity ID of the ID issuer
     * 
     * @return the active identifier
     * 
     * @throws java.sql.SQLException thrown if there is a problem communication with the database
     */
    public PersistentIdEntry getActivePersistentIdEntry(String localEntity, String peerEntity, String localId)
            throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder(idEntrySelectSQL);
        sqlBuilder.append(localEntityColumn).append(" = ?");
        sqlBuilder.append(" AND ").append(peerEntityColumn).append(" = ?");
        sqlBuilder.append(" AND ").append(localIdColumn).append(" = ?");
        sqlBuilder.append(" AND ").append(deactivationTimeColumn).append(" IS NULL");
        String sql = sqlBuilder.toString();

        log.debug("Selecting active persistent ID entry based on prepared sql statement: {}", sql);
        Connection dbConn = dataSource.getConnection();
        try {
            PreparedStatement statement = dbConn.prepareStatement(sql);
            statement.setQueryTimeout(queryTimeout);

            log.debug("Setting prepared statement parameter {}: {}", 1, localEntity);
            statement.setString(1, localEntity);
            log.debug("Setting prepared statement parameter {}: {}", 2, peerEntity);
            statement.setString(2, peerEntity);
            log.debug("Setting prepared statement parameter {}: {}", 3, localId);
            statement.setString(3, localId);

            log.debug("Getting active persistent Id entries.");
            List<PersistentIdEntry> entries = buildIdentifierEntries(statement.executeQuery());

            if (entries == null || entries.size() == 0) {
                return null;
            }

            if (entries.size() > 1) {
                log.warn("More than one active identifier, only the first will be used");
            }

            return entries.get(0);
        } finally {
            try {
                if (dbConn != null && !dbConn.isClosed()) {
                    dbConn.close();
                }
            } catch (SQLException e) {
                log.error("Error closing database connection", e);
            }
        }
    }

    /**
     * Gets the list of deactivated IDs for a given (principal, peer, local) tuple.
     * 
     * @param localId local component of the Id
     * @param peerEntity entity ID of the peer the ID is for
     * @param localEntity entity ID of the ID issuer
     * 
     * @return list of deactivated identifiers
     * 
     * @throws java.sql.SQLException thrown if there is a problem communication with the database
     */
    public List<PersistentIdEntry> getDeactivatedPersistentIdEntries(String localEntity, String peerEntity,
            String localId) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder(idEntrySelectSQL);
        sqlBuilder.append(localEntityColumn).append(" = ?");
        sqlBuilder.append(" AND ").append(peerEntityColumn).append(" = ?");
        sqlBuilder.append(" AND ").append(localIdColumn).append(" = ?");
        sqlBuilder.append(" AND ").append(deactivationTimeColumn).append(" IS NOT NULL");
        String sql = sqlBuilder.toString();

        log.debug("Selecting deactivated persistent ID entries based on prepared sql statement: {}", sql);
        Connection dbConn = dataSource.getConnection();
        try {
            PreparedStatement statement = dbConn.prepareStatement(sql);
            statement.setQueryTimeout(queryTimeout);

            log.debug("Setting prepared statement parameter {}: {}", 1, localEntity);
            statement.setString(1, localEntity);
            log.debug("Setting prepared statement parameter {}: {}", 2, peerEntity);
            statement.setString(2, peerEntity);
            log.debug("Setting prepared statement parameter {}: {}", 3, localId);
            statement.setString(3, localId);

            log.debug("Getting deactivated persistent Id entries");
            List<PersistentIdEntry> entries = buildIdentifierEntries(statement.executeQuery());

            if (entries == null || entries.size() == 0) {
                return null;
            }

            return entries;
        } finally {
            try {
                if (dbConn != null && !dbConn.isClosed()) {
                    dbConn.close();
                }
            } catch (SQLException e) {
                log.error("Error closing database connection", e);
            }
        }
    }

    /**
     * Stores a persistent ID entry into the database.
     * 
     * @param entry entry to persist
     * 
     * @throws java.sql.SQLException thrown is there is a problem writing to the database
     */
    public void storePersistentIdEntry(PersistentIdEntry entry) throws SQLException {

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append(table).append(" (");
        sqlBuilder.append(localEntityColumn).append(", ");
        sqlBuilder.append(peerEntityColumn).append(", ");
        sqlBuilder.append(principalNameColumn).append(", ");
        sqlBuilder.append(localIdColumn).append(", ");
        sqlBuilder.append(persistentIdColumn).append(", ");
        sqlBuilder.append(peerProvidedIdColumn).append(", ");
        sqlBuilder.append(createTimeColumn);
        sqlBuilder.append(") VALUES (?, ?, ?, ?, ?, ?, ?)");

        String sql = sqlBuilder.toString();

        Connection dbConn = dataSource.getConnection();
        try {
            log.debug("Storing persistent ID entry based on prepared sql statement: {}", sql);
            PreparedStatement statement = dbConn.prepareStatement(sql);
            statement.setQueryTimeout(queryTimeout);

            log.debug("Setting prepared statement parameter {}: {}", 1, entry.getLocalEntityId());
            statement.setString(1, entry.getLocalEntityId());
            log.debug("Setting prepared statement parameter {}: {}", 2, entry.getPeerEntityId());
            statement.setString(2, entry.getPeerEntityId());
            log.debug("Setting prepared statement parameter {}: {}", 3, entry.getPrincipalName());
            statement.setString(3, entry.getPrincipalName());
            log.debug("Setting prepared statement parameter {}: {}", 4, entry.getLocalId());
            statement.setString(4, entry.getLocalId());
            log.debug("Setting prepared statement parameter {}: {}", 5, entry.getPersistentId());
            statement.setString(5, entry.getPersistentId());

            if (entry.getPeerProvidedId() == null) {
                log.debug("Setting prepared statement parameter {}: {}", 6, Types.VARCHAR);
                statement.setNull(6, Types.VARCHAR);
            } else {
                log.debug("Setting prepared statement parameter {}: {}", 6, entry.getPeerProvidedId());
                statement.setString(6, entry.getPeerProvidedId());
            }
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            log.debug("Setting prepared statement parameter {}: {}", 7, timestamp.toString());
            statement.setTimestamp(7, timestamp);

            statement.executeUpdate();
        } finally {
            try {
                if (dbConn != null && !dbConn.isClosed()) {
                    dbConn.close();
                }
            } catch (SQLException e) {
                log.error("Error closing database connection", e);
            }
        }
    }

    /**
     * Deactivates a given persistent ID.
     * 
     * @param persistentId ID to deactivate
     * @param deactivation deactivation time, if null the current time is used
     * 
     * @throws java.sql.SQLException thrown if there is a problem communication with the database
     */
    public void deactivatePersistentId(String persistentId, Timestamp deactivation) throws SQLException {
        Timestamp deactivationTime = deactivation;
        if (deactivationTime == null) {
            deactivationTime = new Timestamp(System.currentTimeMillis());
        }

        Connection dbConn = dataSource.getConnection();
        try {
            log.debug("Deactivating persistent id {} as of {}", persistentId, deactivationTime.toString());
            PreparedStatement statement = dbConn.prepareStatement(deactivateIdSQL);
            statement.setQueryTimeout(queryTimeout);
            statement.setTimestamp(1, deactivationTime);
            statement.setString(2, persistentId);
            statement.executeUpdate();
        } finally {
            try {
                if (dbConn != null && !dbConn.isClosed()) {
                    dbConn.close();
                }
            } catch (SQLException e) {
                log.error("Error closing database connection", e);
            }
        }
    }

    /**
     * Builds a list of {@link edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.dataConnector.StoredIDStore.PersistentIdEntry}s from a result set.
     * 
     * @param resultSet the result set
     * 
     * @return list of {@link edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.dataConnector.StoredIDStore.PersistentIdEntry}s
     * 
     * @throws java.sql.SQLException thrown if there is a problem reading the information from the database
     */
    protected List<PersistentIdEntry> buildIdentifierEntries(ResultSet resultSet) throws SQLException {
        ArrayList<PersistentIdEntry> entries = new ArrayList<PersistentIdEntry>();

        PersistentIdEntry entry;
        while (resultSet.next()) {
            entry = new PersistentIdEntry();
            entry.setLocalEntityId(resultSet.getString(localEntityColumn));
            entry.setPeerEntityId(resultSet.getString(peerEntityColumn));
            entry.setPrincipalName(resultSet.getString(principalNameColumn));
            entry.setPersistentId(resultSet.getString(persistentIdColumn));
            entry.setLocalId(resultSet.getString(localIdColumn));
            entry.setPeerProvidedId(resultSet.getString(peerProvidedIdColumn));
            entry.setCreationTime(resultSet.getTimestamp(createTimeColumn));
            entry.setDeactivationTime(resultSet.getTimestamp(deactivationTimeColumn));
            entries.add(entry);

            log.trace("");
        }

        return entries;
    }

    /** Data object representing a persistent identifier entry in the database. */
    public class PersistentIdEntry implements Serializable {

        /** Serial version UID . */
        private static final long serialVersionUID = -8711779466442306767L;

        /** ID of the entity that issued that identifier. */
        private String localEntityId;

        /** ID of the entity to which the identifier was issued. */
        private String peerEntityId;

        /** Name of the principal represented by the identifier. */
        private String principalName;

        /** Local component portion of the persistent ID entry. */
        private String localId;

        /** The persistent identifier. */
        private String persistentId;

        /** ID, associated with the persistent identifier, provided by the peer. */
        private String peerProvidedId;

        /** Time the identifier was created. */
        private Timestamp creationTime;

        /** Time the identifier was deactivated. */
        private Timestamp deactivationTime;

        /** Constructor. */
        public PersistentIdEntry() {
        }

        /**
         * Gets the ID of the entity that issued the identifier.
         * 
         * @return ID of the entity that issued the identifier
         */
        public String getLocalEntityId() {
            return localEntityId;
        }

        /**
         * Sets the ID of the entity that issued the identifier.
         * 
         * @param id ID of the entity that issued the identifier
         */
        public void setLocalEntityId(String id) {
            localEntityId = id;
        }

        /**
         * Gets the ID of the entity to which the identifier was issued.
         * 
         * @return ID of the entity to which the identifier was issued
         */
        public String getPeerEntityId() {
            return peerEntityId;
        }

        /**
         * Sets the ID of the entity to which the identifier was issued.
         * 
         * @param id ID of the entity to which the identifier was issued
         */
        public void setPeerEntityId(String id) {
            peerEntityId = id;
        }

        /**
         * Gets the name of the principal the identifier represents.
         * 
         * @return name of the principal the identifier represents
         */
        public String getPrincipalName() {
            return principalName;
        }

        /**
         * Sets the name of the principal the identifier represents.
         * 
         * @param name name of the principal the identifier represents
         */
        public void setPrincipalName(String name) {
            principalName = name;
        }

        /**
         * Gets the local ID component of the persistent identifier.
         * 
         * @return local ID component of the persistent identifier
         */
        public String getLocalId() {
            return localId;
        }

        /**
         * Sets the local ID component of the persistent identifier.
         * 
         * @param id local ID component of the persistent identifier
         */
        public void setLocalId(String id) {
            localId = id;
        }

        /**
         * Gets the persistent identifier.
         * 
         * @return the persistent identifier
         */
        public String getPersistentId() {
            return persistentId;
        }

        /**
         * Set the persistent identifier.
         * 
         * @param id the persistent identifier
         */
        public void setPersistentId(String id) {
            persistentId = id;
        }

        /**
         * Gets the ID, provided by the peer, associated with this ID.
         * 
         * @return ID, provided by the peer, associated with this ID
         */
        public String getPeerProvidedId() {
            return peerProvidedId;
        }

        /**
         * Sets the ID, provided by the peer, associated with this ID.
         * 
         * @param id ID, provided by the peer, associated with this ID
         */
        public void setPeerProvidedId(String id) {
            peerProvidedId = id;
        }

        /**
         * Gets the time the identifier was created.
         * 
         * @return time the identifier was created
         */
        public Timestamp getCreationTime() {
            return creationTime;
        }

        /**
         * Sets the time the identifier was created.
         * 
         * @param time time the identifier was created
         */
        public void setCreationTime(Timestamp time) {
            creationTime = time;
        }

        /**
         * Gets the time the identifier was deactivated.
         * 
         * @return time the identifier was deactivated
         */
        public Timestamp getDeactivationTime() {
            return deactivationTime;
        }

        /**
         * Sets the time the identifier was deactivated.
         * 
         * @param time the time the identifier was deactivated
         */
        public void setDeactivationTime(Timestamp time) {
            this.deactivationTime = time;
        }

        /** {@inheritDoc} */
        public String toString() {
            StringBuilder stringForm = new StringBuilder("PersistentIdEntry{");
            stringForm.append("persistentId:").append(persistentId).append(", ");
            stringForm.append("localEntityId:").append(localEntityId).append(", ");
            stringForm.append("peerEntityId:").append(peerEntityId).append(", ");
            stringForm.append("localId:").append(localId).append(", ");
            stringForm.append("principalName:").append(principalName).append(", ");
            stringForm.append("peerProvidedId:").append(peerProvidedId).append(", ");
            stringForm.append("creationTime:").append(creationTime).append(", ");
            stringForm.append("deactivationTime:").append(deactivationTime).append(", ");
            stringForm.append("}");
            return stringForm.toString();
        }
    }
}