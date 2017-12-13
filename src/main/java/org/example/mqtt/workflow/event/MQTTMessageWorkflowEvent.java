/*
 *  Copyright 2000-2017 United Planet GmbH, Freiburg Germany
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


package org.example.mqtt.workflow.event;


import org.eclipse.paho.client.mqttv3.internal.wire.MqttReceivedMessage;


/**
 * @author <a href="mailto:alexander.veit@unitedplanet.com">Alexander Veit</a>
 * @version $Revision: 176368 $
 * @since Intrexx 8.0.
 */
public final class MQTTMessageWorkflowEvent implements IMQTTMessageWorkflowEvent
{
	private final String m_strEventSourceGuid;

	private final String m_strTopic;

	private final MqttReceivedMessage m_message;


	public MQTTMessageWorkflowEvent(String p_strEventSourceGuid, String p_strTopic, MqttReceivedMessage p_message)
	{
		m_strEventSourceGuid = p_strEventSourceGuid;
		m_strTopic           = p_strTopic;
		m_message            = p_message;
	}


	@Override
	public String getEventSourceGuid()
	{
		return m_strEventSourceGuid;
	}


	@Override
	public String getTopic()
	{
		return m_strTopic;
	}


	@Override
	public MqttReceivedMessage getMessage()
	{
		return m_message;
	}


	@Override
	public String toString()
	{
		final StringBuilder l_sbuf = new StringBuilder(128);

		l_sbuf.append(super.toString());
		l_sbuf.append('(');
		l_sbuf.append(m_strEventSourceGuid);
		l_sbuf.append(", ");
		l_sbuf.append(m_strTopic);
		l_sbuf.append(", ");
		l_sbuf.append(m_message != null ? m_message.getClass().getName() : "null");
		l_sbuf.append(')');

		return l_sbuf.toString();
	}
}
