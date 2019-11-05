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


/**
 * @author <a href="mailto:alexander.veit@unitedplanet.com">Alexander Veit</a>
 */
public final class BeforeStartMQTTWorkflowEvent implements IBeforeStartMQTTWorkflowEvent
{
	private final String m_strEventSourceGuid;


	public BeforeStartMQTTWorkflowEvent(String p_strEventSourceGuid)
	{
		m_strEventSourceGuid = p_strEventSourceGuid;
	}


	@Override
	public String getEventSourceGuid()
	{
		return m_strEventSourceGuid;
	}


	@Override
	public String toString()
	{
		final StringBuilder l_sbuf = new StringBuilder(128);

		l_sbuf.append(super.toString());
		l_sbuf.append('(');
		l_sbuf.append(m_strEventSourceGuid);
		l_sbuf.append(')');

		return l_sbuf.toString();
	}
}
