/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.andes.server.handler;

import org.wso2.andes.AMQException;
import org.wso2.andes.framing.AMQShortString;
import org.wso2.andes.framing.ExchangeBoundBody;
import org.wso2.andes.framing.ExchangeBoundOkBody;
import org.wso2.andes.framing.MethodRegistry;
import org.wso2.andes.server.exchange.Exchange;
import org.wso2.andes.server.protocol.AMQProtocolSession;
import org.wso2.andes.server.queue.AMQQueue;
import org.wso2.andes.server.queue.QueueRegistry;
import org.wso2.andes.server.state.AMQStateManager;
import org.wso2.andes.server.state.StateAwareMethodListener;
import org.wso2.andes.server.virtualhost.VirtualHost;

/**
 * @author Apache Software Foundation
 *
 *
 */
public class ExchangeBoundHandler implements StateAwareMethodListener<ExchangeBoundBody>
{
    private static final ExchangeBoundHandler _instance = new ExchangeBoundHandler();

    public static final int OK = 0;

    public static final int EXCHANGE_NOT_FOUND = 1;

    public static final int QUEUE_NOT_FOUND = 2;

    public static final int NO_BINDINGS = 3;

    public static final int QUEUE_NOT_BOUND = 4;

    public static final int NO_QUEUE_BOUND_WITH_RK = 5;

    public static final int SPECIFIC_QUEUE_NOT_BOUND_WITH_RK = 6;

    public static ExchangeBoundHandler getInstance()
    {
        return _instance;
    }

    private ExchangeBoundHandler()
    {
    }

    public void methodReceived(AMQStateManager stateManager, ExchangeBoundBody body, int channelId) throws AMQException
    {
        AMQProtocolSession session = stateManager.getProtocolSession();
        
        VirtualHost virtualHost = session.getVirtualHost();
        QueueRegistry queueRegistry = virtualHost.getQueueRegistry();
        MethodRegistry methodRegistry = session.getMethodRegistry();

        


        AMQShortString exchangeName = body.getExchange();
        AMQShortString queueName = body.getQueue();
        AMQShortString routingKey = body.getRoutingKey();
        if (exchangeName == null)
        {
            throw new AMQException("Exchange exchange must not be null");
        }
        Exchange exchange = virtualHost.getExchangeRegistry().getExchange(exchangeName);
        ExchangeBoundOkBody response;
        if (exchange == null)
        {

            response = methodRegistry.createExchangeBoundOkBody(EXCHANGE_NOT_FOUND,
                                                                new AMQShortString("Exchange " + exchangeName + " not found"));
        }
        else if (routingKey == null)
        {
            if (queueName == null)
            {
                if (exchange.hasBindings())
                {
                    response = methodRegistry.createExchangeBoundOkBody(OK, null);
                }
                else
                {

                    response = methodRegistry.createExchangeBoundOkBody(NO_BINDINGS,	// replyCode
                        null);	// replyText
                }
            }
            else
            {

                AMQQueue queue = queueRegistry.getQueue(queueName);
                if (queue == null)
                {

                    response = methodRegistry.createExchangeBoundOkBody(QUEUE_NOT_FOUND,	// replyCode
                        new AMQShortString("Queue " + queueName + " not found"));	// replyText
                }
                else
                {
                    if (exchange.isBound(queue))
                    {

                        response = methodRegistry.createExchangeBoundOkBody(OK,	// replyCode
                            null);	// replyText
                    }
                    else
                    {

                        response = methodRegistry.createExchangeBoundOkBody(QUEUE_NOT_BOUND,	// replyCode
                            new AMQShortString("Queue " + queueName + " not bound to exchange " + exchangeName));	// replyText
                    }
                }
            }
        }
        else if (queueName != null)
        {
            AMQQueue queue = queueRegistry.getQueue(queueName);
            if (queue == null)
            {

                response = methodRegistry.createExchangeBoundOkBody(QUEUE_NOT_FOUND,	// replyCode
                    new AMQShortString("Queue " + queueName + " not found"));	// replyText
            }
            else
            {
                if (exchange.isBound(body.getRoutingKey(), queue))
                {

                    response = methodRegistry.createExchangeBoundOkBody(OK,	// replyCode
                        null);	// replyText
                }
                else
                {

                    response = methodRegistry.createExchangeBoundOkBody(SPECIFIC_QUEUE_NOT_BOUND_WITH_RK,	// replyCode
                        new AMQShortString("Queue " + queueName + " not bound with routing key " +
                        body.getRoutingKey() + " to exchange " + exchangeName));	// replyText
                }
            }
        }
        else
        {
            if (exchange.isBound(body.getRoutingKey()))
            {

                response = methodRegistry.createExchangeBoundOkBody(OK,	// replyCode
                    null);	// replyText
            }
            else
            {

                response = methodRegistry.createExchangeBoundOkBody(NO_QUEUE_BOUND_WITH_RK,	// replyCode
                    new AMQShortString("No queue bound with routing key " + body.getRoutingKey() +
                    " to exchange " + exchangeName));	// replyText
            }
        }
        session.writeFrame(response.generateFrame(channelId));
    }
}
