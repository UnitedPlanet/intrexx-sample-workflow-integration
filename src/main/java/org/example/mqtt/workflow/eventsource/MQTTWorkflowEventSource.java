/*
 *  Copyright 2000-2019 United Planet GmbH, Freiburg Germany
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */


package org.example.mqtt.workflow.eventsource;


import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttReceivedMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.example.mqtt.util.MQTTUtil;
import org.example.mqtt.workflow.QOS;
import org.example.mqtt.workflow.event.AfterStopMQTTWorkflowEvent;
import org.example.mqtt.workflow.event.BeforeStartMQTTWorkflowEvent;
import org.example.mqtt.workflow.event.MQTTMessageWorkflowEvent;
import org.slf4j.Logger;

import de.uplanet.lucy.server.SharedState;
import de.uplanet.lucy.server.workflow.ISupportAfterStopEvent;
import de.uplanet.lucy.server.workflow.ISupportBeforeStartEvent;
import de.uplanet.lucy.server.workflow.ISupportGlobalSharedState;
import de.uplanet.lucy.server.workflow.WorkflowLogger;
import de.uplanet.lucy.server.workflow.eventsource.AbstractSingleThreadedWorkflowEventSource;


/**
 * @author <a href="mailto:alexander.veit@unitedplanet.com">Alexander Veit</a>
 */
public final class MQTTWorkflowEventSource extends AbstractSingleThreadedWorkflowEventSource
	implements ISupportGlobalSharedState, ISupportBeforeStartEvent, ISupportAfterStopEvent
{
	/** Helper for logging.*/
	private static final Logger ms_log = new WorkflowLogger(MQTTWorkflowEventSource.class);

	private boolean m_bSendBeforeStartEvent;

	private boolean m_bSendAfterStopEvent;

	private long m_lOnErrorRestartWaitTimeout = 60000L;

	private String m_strServerUri;

	private String m_strTopic;

	private String m_strClientId;

	private boolean m_bRandomizeClientId = true;

	private int m_iQoS = QOS.AT_MOST_ONCE_DELIVERY;

	private String m_strUserName;

	private String m_strPassword;

	private boolean m_bReconnect = true;

	private boolean m_bGlobalSharedState;


	public MQTTWorkflowEventSource(String p_strGuid)
	{
		super(p_strGuid);

		m_strClientId = "ix-mqtt-consumer-" + p_strGuid;
	}



	/**
	 * Get the time in milliseconds the event source will wait
	 * before it tries to restart after an error occurred that
	 * prevented it to running the source loop.
	 * <p>The default value is 60000 milliseconds (one minute).</p>
	 * @return The timeout in milliseconds.
	 */
	public long getOnErrorRestartWaitTimeout()
	{
		return m_lOnErrorRestartWaitTimeout;
	}


	/**
	 * Set the time in milliseconds the event source will wait
	 * before it tries to restart after an error occurred that
	 * prevented it to running the source loop.
	 * @param p_lOnErrorRestartWaitTimeout The timeout in milliseconds.
	 */
	public void setOnErrorRestartWaitTimeout(long p_lOnErrorRestartWaitTimeout)
	{
		m_lOnErrorRestartWaitTimeout = p_lOnErrorRestartWaitTimeout;
	}


	@Override
	public boolean isGlobalSharedState()
	{
		return m_bGlobalSharedState;
	}


	@Override
	public boolean isSendBeforeStartEvent()
	{
		return m_bSendBeforeStartEvent;
	}


	@Override
	public void setGlobalSharedState(boolean p_bGlobalSharedState)
	{
		m_bGlobalSharedState = p_bGlobalSharedState;
	}


	@Override
	public void setSendBeforeStartEvent(boolean p_bSendBeforeStartEvent)
	{
		m_bSendBeforeStartEvent = p_bSendBeforeStartEvent;
	}


	@Override
	public boolean isSendAfterStopEvent()
	{
		return m_bSendAfterStopEvent;
	}


	@Override
	public void setSendAfterStopEvent(boolean p_bSendAfterStopEvent)
	{
		m_bSendAfterStopEvent = p_bSendAfterStopEvent;
	}


	/**
	 * Get the server URI.
	 * @return The server URI.
	 */
	public String getServerUri()
	{
		return m_strServerUri;
	}


	/**
	 * Set the server URI.
	 * @param p_strServerUri The server URI.
	 */
	public void setServerUri(String p_strServerUri)
	{
		m_strServerUri = p_strServerUri;
	}


	/**
	 * Get the user identity that used when creating the MQTT connection.
	 * @return The user identity.
	 */
	public String getUserName()
	{
		return m_strUserName;
	}


	/**
	 * Set the user identity that used when creating the MQTT connection.
	 * <p>If this property is <code>null</code> (default) the default
	 * user identity will be used.</p>
	 * @param p_strUserName The user identity.
	 */
	public void setUserName(String p_strUserName)
	{
		m_strUserName = p_strUserName;
	}


	/**
	 * Get the password that is used when creating the MQTT connection.
	 * @return The password.
	 */
	public String getPassword()
	{
		return m_strPassword;
	}


	/**
	 * Set the password that is used when creating the MQTT connection.
	 * @param p_strPassword The password.
	 */
	public void setPassword(String p_strPassword)
	{
		m_strPassword = p_strPassword;
	}


	/**
	 * Get the client identifier for the MQTT connection.
	 * <p>The default client identifier is <code>ix-mqtt-consumer-OBJECT_GUID</code>.</p>
	 * @return The client identifier for the MQTT connection.
	 */
	public String getClientId()
	{
		return m_strClientId;
	}


	/**
	 * Set the client identifier for the MQTT connection.
	 * <p>The default client identifier is <code>ix-mqtt-consumer-OBJECT_GUID</code>.</p>
	 * @param p_strClientId The client identifier for the MQTT connection.
	 */
	public void setClientId(String p_strClientId)
	{
		m_strClientId = p_strClientId;
	}


	/**
	 * This property determines if the client should connect with
	 * a unique randomized identifier.
	 * <p>If <code>true</code> (default) a random suffix will be
	 * appended to the client ID that would normally be used.</p>
	 * @return <code>true</code> if the client ID is random, or
	 *    <code>false</code> otherwise.
	 */
	public boolean isRandomizeClientId()
	{
		return m_bRandomizeClientId;
	}


	/**
	 * This property determines if the client should connect with
	 * a unique randomized identifier.
	 * <p>If <code>true</code> (default) a random suffix will be
	 * appended to the client ID that would normally be used.</p>
	 * @param p_bRandomizeClientId <code>true</code> if the client ID
	 *    should be random, or <code>false</code> otherwise.
	 */
	public void setRandomizeClientId(boolean p_bRandomizeClientId)
	{
		m_bRandomizeClientId = p_bRandomizeClientId;
	}


	/**
	 * Get the topic name;
	 * @return The topic name.
	 */
	public String getTopic()
	{
		return m_strTopic;
	}


	/**
	 * Set the topic name;
	 * @param p_strTopicName The topic name.
	 */
	public void setTopic(String p_strTopicName)
	{
		m_strTopic = p_strTopicName;
	}


	/**
	 * Get the quality of service.
	 * @return The quality of service.
	 */
	public int getQos()
	{
		return m_iQoS;
	}


	/**
	 * Set the quality of service.
	 * @param p_iQoS The quality of service.
	 */
	public void setQos(int p_iQoS)
	{
		m_iQoS = p_iQoS;
	}


	/**
	 * Check whether the client will automatically attempt to
	 * reconnect to the server if the connection is lost.
	 * <p>The default value is <code>true</code>.</p>
	 * @return <code>true</code> if the client will attempt
	 *    to reconnect, or <code>false</code> otherwise.
	 */
	public boolean isReconnect()
	{
		return m_bReconnect;
	}


	/**
	 * Sets whether the client will automatically attempt to
	 * reconnect to the server if the connection is lost.
	 * <p>The default value is <code>true</code>.</p>
	 * @param p_bReconnect <code>true</code> if the client should
	 *    attempt to reconnect, or <code>false</code> otherwise.
	 */
	public void setReconnect(boolean p_bReconnect)
	{
		m_bReconnect = p_bReconnect;
	}


	/**
	 * Create the persistence to be used by the MQTT client.
	 * @return The persistence.
	 */
	private MqttClientPersistence _createPersistence()
	{
		final MqttClientPersistence l_persistence;

		l_persistence = new MemoryPersistence();

		return l_persistence;
	}


	/**
	 * Create the MQTT client used to connect to the server.
	 * @return The MQTT client.
	 * @throws MqttException
	 */
	private IMqttClient _createClient(MqttClientPersistence p_persistence)
		throws MqttException
	{
		final String     l_strClientId;
		final MqttClient l_client;

		if (getServerUri() == null) // cannot occur under normal circumstances; see configurator
			throw new IllegalStateException("No server URI given.");

		if (isRandomizeClientId())
			l_strClientId = getClientId() + "-" + UUID.randomUUID();
		else
			l_strClientId = getClientId();

		l_client = new MqttClient(getServerUri(), l_strClientId, p_persistence);

		return l_client;
	}


	/**
	 * Get the MQTT options used to connect to the server.
	 * @return MQTT options.
	 */
	private MqttConnectOptions _getOptions()
	{
		final MqttConnectOptions l_options;

		l_options = new MqttConnectOptions();

		l_options.setAutomaticReconnect(isReconnect());

		if (getUserName() != null)
		{
			l_options.setUserName(getUserName());

			if (getPassword() != null)
				l_options.setPassword(getPassword().toCharArray());
		}

		return l_options;
	}


	@Override
	protected void run()
	{
		final long        l_lRestartWaitTimeout;
		final SharedState l_globalSharedState;

		l_lRestartWaitTimeout = Math.max(100L, getOnErrorRestartWaitTimeout()); // avoid busy waiting
		l_globalSharedState   = isGlobalSharedState() ? new SharedState() : null;

		// signal processes that we are about to start processing MQTT events
		if (isSendBeforeStartEvent())
			dispatchEvent(new BeforeStartMQTTWorkflowEvent(getGuid()), l_globalSharedState, null);

		// normally this outer loop will be executed exactly once
		run_loop:
		while (shouldRun())
		{
			try
			{
				runLoop(l_globalSharedState);
			}
			catch (RuntimeException l_e)
			{
				ms_log.error("Error in MQTT source loop.", l_e);
			}

			if (!shouldRunWithWait(l_lRestartWaitTimeout))
				break run_loop;
		}

		// signal processes that we are about to stop processing MQTT events
		if (isSendAfterStopEvent())
			dispatchEvent(new AfterStopMQTTWorkflowEvent(getGuid()), l_globalSharedState, null);
	}


	/**
	 * Run the message processing loop.
	 * @param p_globalSharedState A global shared state, or <code>null</code>
	 *    if {@link #isGlobalSharedState()} is <code>false</code>.
	 */
	protected void runLoop(SharedState p_globalSharedState)
	{
		final MqttConnectOptions l_options;
		MqttClientPersistence    l_persistence;
		IMqttClient              l_client;

		l_persistence = null;
		l_client      = null;

		try
		{
			l_options     = _getOptions();
			l_persistence = _createPersistence();
			l_client      = _createClient(l_persistence);

			l_client.setCallback(new MqttCallbackExtended()
			{
				@Override
				public void messageArrived(String p_strTopic, MqttMessage p_message)
					throws Exception
				{
					final MQTTMessageWorkflowEvent l_evt;

					l_evt = new MQTTMessageWorkflowEvent(getGuid(), p_strTopic, (MqttReceivedMessage)p_message);

					dispatchEvent(l_evt, p_globalSharedState, null);
				}

				@Override
				public void connectComplete(boolean p_bReconnect, String p_strServerURI)
				{
					if (p_bReconnect)
						ms_log.info(getLogPrologue() + " Reconnect to " + p_strServerURI + " complete.");
					else
						ms_log.info(getLogPrologue() + " Connect to " + p_strServerURI + " complete.");
				}

				@Override
				public void connectionLost(Throwable p_cause)
				{
					ms_log.error(getLogPrologue() + " Connection lost.", p_cause);
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken p_token)
				{
					assert false; // not called
				}
			});

			l_client.connect(l_options);
			l_client.subscribe(getTopic(), getQos());
		}
		catch (MqttException l_e)
		{
			ms_log.error(getLogPrologue() + " Cannot create a MQTT client.", l_e);

			MQTTUtil.unsubscribe(l_client, getTopic());
			MQTTUtil.disconnectAndClose(l_client, Math.max(getStopWaitTimeout() / 2L, 0L));
			MQTTUtil.close(l_persistence);

			return; // give up
		}

		assert l_client != null;

		while (shouldRunWithWait(1000L))
		{
			// do nothing, just wait
		}

		MQTTUtil.unsubscribe(l_client, getTopic());
		MQTTUtil.disconnectAndClose(l_client, Math.max(getStopWaitTimeout() / 2L, 0L));
		MQTTUtil.close(l_persistence);
	}
}

