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


package org.example.mqtt.util;


import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author <a href="mailto:alexander.veit@unitedplanet.com">Alexander Veit</a>
 */
public final class MQTTUtil
{
	/** Helper for logging.*/
	private static final Logger ms_log = LoggerFactory.getLogger(MQTTUtil.class);


	private MQTTUtil()
	{
	}


	/**
	 * Close a persistence object without throwing an exception.
	 * @param p_persistence The persistence object, or <code>null</code>.
	 * @return Always <code>null</code>.
	 */
	public static MqttClientPersistence close(MqttClientPersistence p_persistence)
	{
		if (p_persistence != null)
		{
			try
			{
				p_persistence.close();
			}
			catch (Exception l_e)
			{
				ms_log.error("Error while closing persistence.", l_e);
			}
		}

		return null;
	}


	/**
	 * Close a MQTT client without throwing an exception.
	 * @param p_client The MQTT client, or <code>null</code>.
	 * @return Always <code>null</code>.
	 */
	public static IMqttClient close(IMqttClient p_client)
	{
		if (p_client != null)
		{
			try
			{
				p_client.close();
			}
			catch (Exception l_e)
			{
				ms_log.error("Error while closing the MQTT client.", l_e);
			}
		}

		return null;
	}


	/**
	 * Disconnect and close a MQTT client without throwing an exception.
	 * @param p_client The MQTT client, or <code>null</code>.
	 * @param p_lTimeout A timeout for disconnecting.
	 * @return Always <code>null</code>.
	 */
	public static IMqttClient disconnectAndClose(IMqttClient p_client, long p_lTimeout)
	{
		if (p_client != null && p_client.isConnected())
		{
			try
			{
				try
				{
					p_client.disconnect(p_lTimeout);
				}
				catch (MqttException l_e)
				{
					p_client.disconnectForcibly(p_lTimeout / 2L, p_lTimeout / 2L);
				}
			}
			catch (Exception l_e)
			{
				ms_log.error("Error while closing the MQTT client.", l_e);
			}
		}

		return close(p_client);
	}


	/**
	 * Unsubscribe a MQTT client without throwing an exception.
	 * @param p_client The MQTT client, or <code>null</code>.
	 * @param p_strTopic The topic to unsubscribe from.
	 */
	public static void unsubscribe(IMqttClient p_client, String p_strTopic)
	{
		if (p_client != null && p_client.isConnected())
		{
			try
			{
				p_client.unsubscribe(p_strTopic);
			}
			catch (Exception l_e)
			{
				ms_log.error("Error while unsubscribing MQTT the client from topic " + p_strTopic + ".", l_e);
			}
		}
	}
}
