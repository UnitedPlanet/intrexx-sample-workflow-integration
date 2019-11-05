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


package org.example.mqtt.workflow.eventhandler;


import org.example.mqtt.workflow.event.IAfterStopMQTTWorkflowEvent;
import org.example.mqtt.workflow.event.IBeforeStartMQTTWorkflowEvent;
import org.example.mqtt.workflow.event.IMQTTMessageWorkflowEvent;
import org.example.mqtt.workflow.event.IMQTTWorkflowEvent;

import de.uplanet.lucy.server.IProcessingContext;
import de.uplanet.lucy.server.workflow.IWorkflowProcessingContext;
import de.uplanet.lucy.server.workflow.WorkflowTransition;
import de.uplanet.lucy.server.workflow.event.IWorkflowEvent;
import de.uplanet.lucy.server.workflow.eventhandler.AbstractWorkflowEventHandler;


/**
 * @author <a href="mailto:alexander.veit@unitedplanet.com">Alexander Veit</a>
 */
public final class MQTTWorkflowEventHandler extends AbstractWorkflowEventHandler
{
	private String m_strMQTTSourceGuid;

	private boolean m_bHandleBeforeStartEvent;

	private boolean m_bHandleAfterStopEvent;

	private boolean m_bHandleMessageEvent = true;


	public MQTTWorkflowEventHandler(String p_strGuid)
	{
		super(p_strGuid);
	}


	public String getMQTTSourceGuid()
	{
		return m_strMQTTSourceGuid;
	}

	public void setMQTTSourceGuid(String p_strMQTTSourceGuid)
	{
		m_strMQTTSourceGuid = p_strMQTTSourceGuid;
	}


	public boolean isHandleBeforeStartEvent()
	{
		return m_bHandleBeforeStartEvent;
	}

	public void setHandleBeforeStartEvent(boolean p_bHandleBeforeStartEvent)
	{
		m_bHandleBeforeStartEvent = p_bHandleBeforeStartEvent;
	}


	public boolean isHandleAfterStopEvent()
	{
		return m_bHandleAfterStopEvent;
	}

	public void setHandleAfterStopEvent(boolean p_bHandleAfterStopEvent)
	{
		m_bHandleAfterStopEvent = p_bHandleAfterStopEvent;
	}


	public boolean isHandleMessageEvent()
	{
		return m_bHandleMessageEvent;
	}

	public void setHandleMessageEvent(boolean p_bHandleMessageEvent)
	{
		m_bHandleMessageEvent = p_bHandleMessageEvent;
	}


	@Override
	public boolean isHandlerFor(IWorkflowEvent p_evt, IWorkflowProcessingContext p_wfCtx)
	{
		if (!isActive())
			return false;

		if (_matchesEvent(p_evt, p_wfCtx))
			return true;
		else
			return false;
	}


	@Override
	public WorkflowTransition process(IWorkflowEvent             p_evt,
	                                  IWorkflowProcessingContext p_wfCtx,
	                                  IProcessingContext         p_ctx)
		throws Exception
	{
		if (!isActive())
			return null;

		if (_matchesEvent(p_evt, p_wfCtx))
			return m_wftEfferent;
		else
			return null;
	}


	private boolean _matchesEvent(IWorkflowEvent p_evt, IWorkflowProcessingContext p_wfCtx)
	{
		// we only handle MQTT workflow events
		if (p_evt instanceof IMQTTWorkflowEvent)
		{
			if (m_strMQTTSourceGuid != null)
			{
				// if an event source filter is given, it must match
				if (!m_strMQTTSourceGuid.equals(((IMQTTWorkflowEvent)p_evt).getEventSourceGuid()))
					return false;
			}
		}
		else
		{
			return false;
		}

		// the rest of the filter chain...
		if (p_evt instanceof IMQTTMessageWorkflowEvent)
			return m_bHandleMessageEvent;
		else if (p_evt instanceof IBeforeStartMQTTWorkflowEvent)
			return m_bHandleBeforeStartEvent;
		else if (p_evt instanceof IAfterStopMQTTWorkflowEvent)
			return m_bHandleAfterStopEvent;
		else
			return false; // unkown event type
	}
}
