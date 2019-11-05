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


package org.example.mqtt.workflow.event;


import org.eclipse.paho.client.mqttv3.internal.wire.MqttReceivedMessage;

import de.uplanet.annotation.Scriptable;


/**
 * @author <a href="mailto:alexander.veit@unitedplanet.com">Alexander Veit</a>
 */
@Scriptable
public interface IMQTTMessageWorkflowEvent extends IMQTTWorkflowEvent
{
	/**
	 * Get the topic the message was published to.
	 * @return The topic.
	 */
	public String getTopic();


	/**
	 * Get the received MQTT message that is associated with this event.
	 * @return The received MQTT message that is associated with this event.
	 */
	public MqttReceivedMessage getMessage();
}
