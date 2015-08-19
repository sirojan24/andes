/*
 *
 *  * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  *
 *  * WSO2 Inc. licenses this file to you under the Apache License,
 *  * Version 2.0 (the "License"); you may not use this file except
 *  * in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.wso2.andes.server.cluster.coordination;

import org.wso2.andes.kernel.AndesException;
import org.wso2.andes.kernel.slot.Slot;
import org.wso2.andes.kernel.slot.SlotState;

import java.util.Set;
import java.util.TreeSet;

public interface SlotAgent {

	/**
	 * Create a slot in database
	 * @param startMessageId start message id
	 * @param endMessageId end message id end message id
	 * @param storageQueueName name of storage queue
	 * @param assignedNodeId Assigned node id of slot
	 */
	public void createSlot(long startMessageId, long endMessageId,
						   String storageQueueName, String assignedNodeId)
							throws AndesException;

	/**
	 * Delete a slot from database
	 *
	 * @param nodeId
	 * @param queueName
	 * @param startMessageId start message id
	 * @param endMessageId end message id
	 */
	public void deleteSlot(String nodeId, String queueName, long startMessageId, long endMessageId) throws AndesException;

	/**
	 * Delete slot assignments related to a specific queue name by
	 * setting assigned_node and assigned_queue fields to NULL and set slot state to unassigned in database
	 *
	 * @param nodeId id of node
	 * @param queueName name of queue
	 */
	public void deleteSlotAssignmentByQueueName(String nodeId, String queueName) throws AndesException;

	/**
	 * Select rows with unassigned slots
	 *
	 * @param queueName name of queue
	 * @return unassigned slot object
	 */

	public Slot getUnAssignedSlot(String queueName) throws AndesException;

	/**
	 * Update slot assignment in database
	 *  @param nodeId id of node
	 * @param queueName name of queue
	 * @param allocatedSlot
	 */
	public void updateSlotAssignment(String nodeId, String queueName, Slot allocatedSlot)
			throws AndesException;

	/**
	 * Get last assigned id of a queue from database
	 *
	 * @param queueName name of queue
	 * @return last assigned id
	 */
	public long getQueueToLastAssignedId(String queueName) throws AndesException;

	/**
	 * Set last assigned id of a queue to database
	 *
	 * @param queueName name of queue
	 * @param lastAssignedId last assigned id
	 */
	public void setQueueToLastAssignedId(String queueName, long lastAssignedId) throws AndesException;

	/**
	 * Get last published id of a node from database
	 *
	 * @param nodeId id of node
	 * @return last published id of node
	 */
	public Long getNodeToLastPublishedId(String nodeId) throws AndesException;

	/**
	 * Set last published id of a node to database
	 *
	 * @param nodeId id of node
	 * @param lastPublishedId last published id of node
	 */
	public void setNodeToLastPublishedId(String nodeId, long lastPublishedId) throws AndesException;

	/**
	 * Remove entries for a publishing node when it leaves the cluster
	 *
	 * @param nodeId
	 *         id of the leaving node
	 * @throws AndesException
	 */
	public void removePublisherNode(String nodeId) throws AndesException;

	/**
	 * Get all message published nodes from NodeToLastPublishedId table
	 *
	 * @return set of message published nodes
	 */
	public TreeSet<String> getMessagePublishedNodes() throws AndesException;


	/**
	 * Unassign a given slot
	 * @param slotToBeReAssigned
	 * @throws AndesException
	 */
	public void reassignSlot(Slot slotToBeReAssigned) throws AndesException;

	/**
	 * Update slot state in database
	 *
	 * @param startMessageId start message id
	 * @param endMessageId end message id
	 * @param slotState state of slot
	 */
	public void setSlotState(long startMessageId, long endMessageId, SlotState slotState)
																			throws AndesException;

	/**
	 * Get overlapped slots from database
	 *
	 *
	 * @param nodeId
	 * @param queueName name of queue
	 * @return overlapped slot
	 */
	public Slot getOverlappedSlot(String nodeId, String queueName) throws AndesException;

	/**
	 * Delete overlapped slots for a given node
	 * @param nodeId
	 * @throws AndesException
	 */
	public void deleteOverlappedSlots(String nodeId) throws AndesException;

	/**
	 * Add Message ids to database
	 * @param queueName name of queue
	 * @param messageId id of message
	 * @throws org.wso2.andes.kernel.AndesException
	 */
	public void addMessageId(String queueName, long messageId) throws AndesException;

	/**
	 * Get message ids from database
	 */
	public TreeSet<Long> getMessageIds(String queueName) throws AndesException;

	/**
	 * Delete message ids
	 *
	 * @param queueName
	 * @param messageId id of message
	 * @throws org.wso2.andes.kernel.AndesException
	 */
	public void deleteMessageId(String queueName, long messageId) throws AndesException;

	/**
	 * Delete record in slot table for a given queue
	 * @param queueName name of queue
	 * @throws org.wso2.andes.kernel.AndesException
	 */
	public void deleteSlotsByQueueName(String queueName) throws AndesException;

	/**
	 * Delete record in message ids table for a given queue
	 * @param queueName name of queue
	 * @throws org.wso2.andes.kernel.AndesException
	 */
	public void deleteMessageIdsByQueueName(String queueName) throws AndesException;
	/**
	 * Get all assigned slots for a given node id
	 * @param nodeId id of node
	 * @return set of assigned slot objects
	 * @throws org.wso2.andes.kernel.AndesException
	 */
	public TreeSet<Slot> getAssignedSlotsByNodeId(String nodeId) throws AndesException;

	/**
	 * Get all slots for a given queue
	 *
	 * @param nodeId
	 * @param queueName name of queue
	 * @return set of slot objects
	 * @throws org.wso2.andes.kernel.AndesException
	 */
	public TreeSet<Slot> getAllSlotsByQueueName(String nodeId, String queueName) throws AndesException;

	/**
	 * Update slots to overlapped state
	 * @param nodeId
	 * @param queueName
	 * @param overlappedSlots
	 * @throws AndesException
	 */
	public void updateOverlappedSlots(String nodeId, String queueName, TreeSet<Slot> overlappedSlots) throws AndesException;

    /**
     * Get list of all queues
     * @return Set of queue names
     * @throws AndesException
     */
	public Set<String> getAllQueues() throws AndesException;
}