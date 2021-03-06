/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.andes.server.cluster;

import org.wso2.andes.kernel.AndesException;
import org.wso2.andes.server.cluster.error.detection.NetworkPartitionListener;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * This is responsible for handling cluster communication
 */
public interface ClusterAgent {

    /**
     * Gets address of all the members in the cluster.
     *
     * @return A list of address of the nodes in a cluster
     */
     List<String> getAllClusterNodeAddresses() throws AndesException;

    /**
     * Return all ids of the connected nodes.
     *
     * @return list of member ids
     */
    List<String> getAllNodeIdentifiers() throws AndesException;

    /**
     * Get id of the local node
     *
     * @return local node id
     */
    String getLocalNodeIdentifier();

    /**
     * Get a unique id for local node. This can be used for ID generation algorithm
     *
     * @return unique id for local node
     */
    int getUniqueIdForLocalNode();

    /**
     * Check if the current node is the coordinator
     *
     * @return true if the current is the coordinator, else false
     */
    boolean isCoordinator();

    /**
     * Return the socket address of the coordinator Node. This socket address can be used to communicate with the
     * coordinator node using thrift.
     *
     * @return Socket address of the coordinator thrift server if present, null otherwise
     */
    InetSocketAddress getThriftAddressOfCoordinator();

    /**
     * Start listening to cluster events
     *
     * @param manager
     *         Cluster manager for the current node
     */
    void start(ClusterManager manager) throws AndesException;

    /**
     * Stop listening to cluster events
     */
    void stop();

    /**
     * Allows to register a listeners when there are network partitions. Hence
     * any implementation of Cluster Agent should provide a mechanism
     * to detect network partitions (if allowed via configuration)
     *
     * @param priority listener priority (lower value has higher priority)
     * @param listener any party required act on a network partition.
     */
    void addNetworkPartitionListener(int priority, NetworkPartitionListener listener);
}
