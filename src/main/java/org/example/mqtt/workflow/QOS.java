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


package org.example.mqtt.workflow;


import de.uplanet.lucy.server.workflow.WorkflowConfigurationException;


/**
 * Quality of service as defined in the
 * <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.html#_Toc385349365">
 * MQTT specification</a>.
 * @author <a href="mailto:alexander.veit@unitedplanet.com">Alexander Veit</a>
 */
public final class QOS
{
	/**
	 * QoS 0: At most once delivery.
	 */
	public static final int AT_MOST_ONCE_DELIVERY = 0;

	/**
	 * QoS 1: At least once delivery.
	 */
	public static final int AT_LEAST_ONCE_DELIVERY = 1;

	/**
	 * QoS 2: Exactly once delivery.
	 */
	public static final int EXACTLY_ONCE_DELIVERY = 2;


	private QOS()
	{
	}


	/**
	 * Check if the given value is valid according to the specification.
	 * @param p_iQoS The value to be tested.
	 * @return The given value.
	 * @throws WorkflowConfigurationException If the given value is not a valid QoS.
	 */
	public static int checkValidQoS(int p_iQoS)
		throws WorkflowConfigurationException
	{
		if (!isValidQoS(p_iQoS))
			throw new WorkflowConfigurationException("The QoS must be 0, 1, or 2.");

		return p_iQoS;
	}


	/**
	 * Test if the given value is valid according to the specification.
	 * @param p_iQoS The value to be tested.
	 * @return <code>true</code> if the given value is a valid QoS value,
	 *    or <code>false</code> otherwise.
	 */
	public static boolean isValidQoS(int p_iQoS)
	{
		return
			p_iQoS == QOS.AT_MOST_ONCE_DELIVERY ||
			p_iQoS == QOS.AT_LEAST_ONCE_DELIVERY ||
			p_iQoS == QOS.EXACTLY_ONCE_DELIVERY;
	}
}
