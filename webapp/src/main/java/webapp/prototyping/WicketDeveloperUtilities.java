/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package webapp.prototyping;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.viewer.wicket.viewer.applib.WicketDeveloperUtilitiesService;

/**
 * These overrides are simply to 'move' the action underneath the 
 * 'Prototyping' menu.
 */
@DomainService(menuOrder = "40.3")
public class WicketDeveloperUtilities extends WicketDeveloperUtilitiesService {

    @Named("Clear i18n Cache")
    @MemberOrder(name="Prototyping", sequence="90.3")
    @Override
    public void resetI18nCache() {
        super.resetI18nCache();
    }

}

