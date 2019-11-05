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


package org.example.mqtt.workflow.action;


import java.nio.charset.StandardCharsets;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.example.mqtt.workflow.QOS;

import de.uplanet.lucy.server.ContextValue;
import de.uplanet.lucy.server.IProcessingContext;
import de.uplanet.lucy.server.businesslogic.BlException;
import de.uplanet.lucy.server.businesslogic.IBusinessLogicProcessingContext;
import de.uplanet.lucy.server.businesslogic.rtdata.PERMISSION_CHECK;
import de.uplanet.lucy.server.businesslogic.util.IDataRecord;
import de.uplanet.lucy.server.dataobjects.IValueHolder;
import de.uplanet.lucy.server.workflow.IWorkflowProcessingContext;
import de.uplanet.lucy.server.workflow.InvalidProcessingContextWorkflowException;
import de.uplanet.lucy.server.workflow.WorkflowException;
import de.uplanet.lucy.server.workflow.WorkflowTransition;
import de.uplanet.lucy.server.workflow.action.AbstractWorkflowAction;
import de.uplanet.lucy.server.workflow.event.IWorkflowEvent;
import de.uplanet.lucy.server.workflow.util.BusinessLogicWorkflowUtil;
import de.uplanet.util.InvalidPropertyException;


/**
 * @author <a href="mailto:alexander.veit@unitedplanet.com">Alexander Veit</a>
 */
public final class MQTTMessageProducerWorkflowAction extends AbstractWorkflowAction
{
	public static final class DataCfg
	{
		private String m_strTextData;

		private String m_strContextVariableName;

		private String m_strDataFieldGuid;

		public String getText()
		{
			return m_strTextData;
		}

		public void setText(String p_strText)
		{
			m_strTextData = p_strText;
		}

		protected boolean hasText()
		{
			return m_strTextData != null;
		}

		public String getContextVariableName()
		{
			return m_strContextVariableName;
		}

		public void setContextVariableName(String p_strContextVariableName)
		{
			m_strContextVariableName = p_strContextVariableName;
		}

		protected boolean hasContextVariableName()
		{
			return m_strContextVariableName != null;
		}

		public String getDataFieldGuid()
		{
			return m_strDataFieldGuid;
		}

		public void setDataFieldGuid(String p_strDataFieldGuid)
		{
			m_strDataFieldGuid = p_strDataFieldGuid;
		}

		protected boolean hasDataFieldGuid()
		{
			return m_strDataFieldGuid != null;
		}
	}


	private String m_strServerUri;

	private String m_strTopic;

	private String m_strClientId;

	private int m_iQoS = QOS.AT_MOST_ONCE_DELIVERY;

	private String m_strUserName;

	private String m_strPassword;

	private DataCfg m_dataCfg = new DataCfg();

	private boolean m_bNoMessageDataIsError = true;

	private int m_iConnectionTimeout = 5;

	private long m_lQuiesceTimeout = 5000L;


	/**
	 * @param p_strGuid The GUID of the workflow action.
	 * @throws IllegalArgumentException If the GUID parameter is <code>null</code>
	 *    or if it does not represent a valid GUID.
	 */
	public MQTTMessageProducerWorkflowAction(String p_strGuid)
	{
		super(p_strGuid);
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
	 * @param p_qos The quality of service.
	 */
	public void setQos(int p_qos)
	{
		m_iQoS = p_qos;
	}


	/**
	 * Get the client identifier for the MQTT connection.
	 * <p>The default client identifier is prefixed with
	 * <code>ix-mqtt-action-OBJECT_GUID</code>.</p>
	 * @return The client identifier for the MQTT connection.
	 */
	public String getClientId()
	{
		return m_strClientId;
	}

	/**
	 * Set the client identifier for the MQTT connection.
	 * <p>The default client identifier is prefixed with
	 * <code>ix-mqtt-action-OBJECT_GUID</code>.</p>
	 * @param p_strClientId The client identifier for the MQTT connection.
	 */
	public void setClientId(String p_strClientId)
	{
		m_strClientId = p_strClientId;
	}

	/**
	 * Get either the configured {@link #getClientId() client identifier}
	 * or return a newly created unique one.
	 * @param p_wfCtx The workflow processing context.
	 * @return The client identifier to be used when connecting
	 *    the server.
	 */
	private String _getClientId(IWorkflowProcessingContext p_wfCtx)
	{
		if (getClientId() != null)
			return getClientId();
		else
			return "ix-mqtt-action-" + getGuid() + "-" + p_wfCtx.getRunId(); // must be unique
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
	 * Get the message data configuration.
	 * @return The message data configuration.
	 */
	public DataCfg getData()
	{
		return m_dataCfg;
	}

	/**
	 * Set the message data configuration.
	 * @param p_dataCfg The message data configuration.
	 */
	public void setData(DataCfg p_dataCfg)
	{
		m_dataCfg = p_dataCfg;
	}


	/**
	 * This property determines if it is an error when no
	 * data are available at runtime.
	 * <p>If it is an error, an exception is thrown. Otherwise
	 * this action does nothing.</p>
	 * @return <code>true</code> if it is an error when no data are
	 *    available, or <code>false</code> otherwise.
	 */
	public boolean isNoMessageDataIsError()
	{
		return m_bNoMessageDataIsError;
	}

	/**
	 * This property determines if it is an error when no
	 * data are available at runtime.
	 * <p>If it is an error, an exception is thrown. Otherwise
	 * this action does nothing.</p>
	 * @param p_bNoMessageDataIsError <code>true</code>
	 *    if it is an error when no data are available,
	 *    or <code>false</code> otherwise.
	 */
	public void setNoMessageDataIsError(boolean p_bNoMessageDataIsError)
	{
		m_bNoMessageDataIsError = p_bNoMessageDataIsError;
	}


	/**
	 * Get the connection timeout in <b>seconds</b>.
	 * <p>The default value is 5 seconds. A value of 0 disables timeout
	 * processing meaning the client will wait until the network connection
	 * is made successfully or fails.</p>
	 * @return The connection timeout.
	 */
	public int getConnectionTimeout()
	{
		return m_iConnectionTimeout;
	}

	/**
	 * Set the connection timeout in <b>seconds</b>.
	 * <p>The default value is 5 seconds. A value of 0 disables timeout
	 * processing meaning the client will wait until the network connection
	 * is made successfully or fails.</p>
	 * @param p_iTimeout The connection timeout.
	 */
	public void setConnectionTimeout(int p_iTimeout)
	{
		m_iConnectionTimeout = p_iTimeout;
	}


	/**
	 * Get the amount of time in milliseconds to allow for existing
	 * work to finish before disconnecting. A value of zero or less
	 * means the client will not quiesce.
	 * @return The quiesce timeout.
	 */
	public long getQuiesceTimeout()
	{
		return m_lQuiesceTimeout;
	}

	/**
	 * Set the amount of time in milliseconds to allow for existing
	 * work to finish before disconnecting. A value of zero or less
	 * means the client will not quiesce.
	 * @param p_lTimeout The quiesce timeout.
	 */
	public void setQuiesceTimeout(long p_lTimeout)
	{
		m_lQuiesceTimeout = p_lTimeout;
	}


	/**
	 * Create the MQTT client used to connect to the server.
	 * @return A MQTT client.
	 * @throws MqttException If an error occurred.
	 */
	private IMqttClient _createClient(IWorkflowProcessingContext p_wfCtx)
		throws MqttException
	{
		final MqttClient l_client;

		if (getServerUri() == null) // cannot occur under normal circumstances; see configurator
			throw new IllegalStateException("No server URI given.");

		l_client = new MqttClient(getServerUri(), _getClientId(p_wfCtx), new MemoryPersistence());

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

		l_options.setCleanSession(true); // do not remember state
		l_options.setAutomaticReconnect(false);
		l_options.setConnectionTimeout(getConnectionTimeout());

		if (getUserName() != null)
		{
			l_options.setUserName(getUserName());

			if (getPassword() != null)
				l_options.setPassword(getPassword().toCharArray());
		}

		return l_options;
	}


	@Override
	public WorkflowTransition process(IWorkflowEvent p_evt, IWorkflowProcessingContext p_wfCtx, IProcessingContext p_ctx)
		throws InterruptedException, Exception
	{
		final MqttMessage l_msg;
		final IMqttClient l_client;

		if (!isActive())
			return m_wftEfferent;

		// determine the data to be sent
		l_msg = _getMessageData(p_evt, p_ctx);

		if (l_msg == null)
		{
			if (isNoMessageDataIsError())
				throw new WorkflowException("No data to create the MQTT message.");

			return m_wftEfferent;
		}

		l_client = _createClient(p_wfCtx);

		try
		{
			l_client.connect(_getOptions());

			l_msg.setQos(getQos());

			l_client.publish(getTopic(), l_msg);
		}
		finally
		{
			l_client.disconnect(getQuiesceTimeout());
			l_client.close();
		}

		return m_wftEfferent;
	}


	/**
	 * Get the MQTT message. Either character data, or a byte arrays
	 * are supported as input.
	 * @param p_evt The current workflow event.
	 * @param p_ctx The processing context.
	 * @return The message or <code>null</code>.
	 * @throws InvalidPropertyException
	 * @throws WorkflowException
	 * @throws BlException
	 */
	private MqttMessage _getMessageData(IWorkflowEvent p_evt, IProcessingContext p_ctx)
		throws Exception
	{
		Object       l_value;
		final byte[] l_payload;

		if (m_dataCfg.hasText())
		{
			l_value = m_dataCfg.getText();
		}
		else if (m_dataCfg.hasContextVariableName())
		{
			l_value = ContextValue.getContextValue(p_ctx, m_dataCfg.getContextVariableName());
		}
		else if (m_dataCfg.hasDataFieldGuid())
		{
			final IDataRecord l_recSrcFull;

			if (!(p_ctx instanceof IBusinessLogicProcessingContext))
				throw new InvalidProcessingContextWorkflowException(p_ctx);

			if (!BusinessLogicWorkflowUtil.hasRecord((IBusinessLogicProcessingContext)p_ctx))
				throw new WorkflowException("No data record in the processing context.");

			l_recSrcFull = BusinessLogicWorkflowUtil.readFullRecord
				((IBusinessLogicProcessingContext)p_ctx, PERMISSION_CHECK.NO);

			l_value = l_recSrcFull.getValueHolderByFieldGuid(m_dataCfg.getDataFieldGuid());
		}
		else
		{
			// should not occur - see configurator
			throw new WorkflowException("No message data configured.");
		}

		if (l_value instanceof IValueHolder<?>)
			l_value = ((IValueHolder<?>)l_value).getValue();

		if (l_value == null)
			l_payload = null;
		else if (l_value instanceof CharSequence)
			l_payload = l_value.toString().getBytes(StandardCharsets.UTF_8);
		else if (l_value instanceof byte[])
			l_payload = (byte[])l_value;
		else
			throw new WorkflowException("Sorry, the type " + l_value.getClass().getName() + " is not supported yet.");

		return l_payload != null ? new MqttMessage(l_payload) : null;
	}
}
