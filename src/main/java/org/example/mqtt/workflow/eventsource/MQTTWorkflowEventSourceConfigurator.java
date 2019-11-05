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


import org.example.mqtt.workflow.QOS;

import de.uplanet.lucy.server.workflow.AbstractWorkflowObjectConfigurator;
import de.uplanet.lucy.server.workflow.IWorkflowConfigurationContext;
import de.uplanet.lucy.server.workflow.IWorkflowObject;
import de.uplanet.lucy.server.workflow.WorkflowConfigurationException;


/**
 * @author <a href="mailto:alexander.veit@unitedplanet.com">Alexander Veit</a>
 */
public final class MQTTWorkflowEventSourceConfigurator extends AbstractWorkflowObjectConfigurator
{
	public MQTTWorkflowEventSourceConfigurator()
	{
	}


	@Override
	public void configurePublish(IWorkflowConfigurationContext p_ctx,
	                             IWorkflowObject               p_wfNew,
	                             IWorkflowObject               p_wfExisting)
		throws WorkflowConfigurationException
	{
		_checkConfiguration((MQTTWorkflowEventSource)p_wfNew);
	}


	@Override
	public void configureEngage(IWorkflowConfigurationContext p_ctx,
	                            IWorkflowObject               p_wfObj)
		throws WorkflowConfigurationException
	{
		_checkConfiguration((MQTTWorkflowEventSource)p_wfObj);
	}


	private void _checkConfiguration(MQTTWorkflowEventSource p_wfObj)
		throws WorkflowConfigurationException
	{
		if (p_wfObj == null)
			throw new IllegalArgumentException("No workflow object given.");

		if (p_wfObj.getServerUri() == null || p_wfObj.getServerUri().isEmpty())
			throw new WorkflowConfigurationException("No server URI given.");

		if (p_wfObj.getTopic() == null || p_wfObj.getTopic().isEmpty())
			throw new WorkflowConfigurationException("No topic name given.");

		QOS.checkValidQoS(p_wfObj.getQos());
	}
}
