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


package org.example.mqtt.workflow.action;


import org.example.mqtt.workflow.QOS;

import de.uplanet.lucy.server.workflow.AbstractWorkflowObjectConfigurator;
import de.uplanet.lucy.server.workflow.IWorkflowConfigurationContext;
import de.uplanet.lucy.server.workflow.IWorkflowObject;
import de.uplanet.lucy.server.workflow.WorkflowConfigurationException;
import de.uplanet.util.BooleanUtil;


/**
 * @author <a href="mailto:alexander.veit@unitedplanet.com">Alexander Veit</a>
 * @version $Revision: 176456 $
 */
public final class MQTTMessageProducerWorkflowActionConfigurator
	extends AbstractWorkflowObjectConfigurator
{
	public MQTTMessageProducerWorkflowActionConfigurator()
	{
	}


	@Override
	public void configurePublish(IWorkflowConfigurationContext p_ctx,
	                             IWorkflowObject               p_wfNew,
	                             IWorkflowObject               p_wfExisting)
		throws WorkflowConfigurationException
	{
		if (p_wfNew == null)
			throw new IllegalArgumentException("No workflow object given.");

		_checkConfiguration((MQTTMessageProducerWorkflowAction)p_wfNew);
	}


	@Override
	public void configureEngage(IWorkflowConfigurationContext p_ctx, IWorkflowObject p_wfObj)
		throws WorkflowConfigurationException
	{
		_checkConfiguration((MQTTMessageProducerWorkflowAction)p_wfObj);
	}


	private void _checkConfiguration(MQTTMessageProducerWorkflowAction p_wfObj)
		throws WorkflowConfigurationException
	{
		final MQTTMessageProducerWorkflowAction.DataCfg l_dataCfg;
		final int                                       l_iPropertiesSet;

		if (p_wfObj.getServerUri() == null || p_wfObj.getServerUri().isEmpty())
			throw new WorkflowConfigurationException("No server URI given.");

		if (p_wfObj.getTopic() == null || p_wfObj.getTopic().isEmpty())
			throw new WorkflowConfigurationException("No topic name given.");

		if (p_wfObj.getConnectionTimeout() < 0)
			throw new WorkflowConfigurationException("The connection timeout must not be negative.");

		QOS.checkValidQoS(p_wfObj.getQos());

		l_dataCfg = p_wfObj.getData();

		l_iPropertiesSet = BooleanUtil.countTrue(l_dataCfg.hasText(),
		                                         l_dataCfg.hasContextVariableName(),
		                                         l_dataCfg.hasDataFieldGuid());

		if (l_iPropertiesSet == 0)
		{
			throw new WorkflowConfigurationException("No message data configured.");
		}
		else if (l_iPropertiesSet != 1)
		{
			throw new WorkflowConfigurationException
				("Exactly one of the properties data.text, or data.contextVariableName, or data.dataFieldGuid must be set.");
		}
	}
}
