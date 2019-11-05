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


import de.uplanet.annotation.Scriptable;
import de.uplanet.lucy.server.workflow.event.IBeforeStartWorkflowEventSourceWorkflowEvent;


/**
 * @author <a href="mailto:alexander.veit@unitedplanet.com">Alexander Veit</a>
 */
@Scriptable
public interface IBeforeStartMQTTWorkflowEvent
	extends IMQTTWorkflowEvent, IBeforeStartWorkflowEventSourceWorkflowEvent
{
}
