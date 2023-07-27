/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.citrusframework.camel.xml;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.citrusframework.TestCase;
import org.citrusframework.TestCaseMetaInfo;
import org.citrusframework.camel.actions.RemoveCamelRouteAction;
import org.citrusframework.xml.XmlTestLoader;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
public class RemoveRoutesTest extends AbstractXmlActionTest {

    @Test
    public void shouldLoadCamelActions() throws Exception {
        XmlTestLoader testLoader = createTestLoader("classpath:org/citrusframework/camel/xml/camel-remove-routes-test.xml");

        CamelContext citrusCamelContext = new DefaultCamelContext();
        citrusCamelContext.addRoutes(new RouteBuilder(citrusCamelContext) {
            @Override
            public void configure() throws Exception {
                from("direct:hello")
                        .routeId("route_1")
                        .autoStartup(false)
                        .to("log:info");

                from("direct:goodbye")
                        .routeId("route_2")
                        .autoStartup(false)
                        .to("log:info");

                from("direct:goodnight")
                        .routeId("route_3")
                        .autoStartup(false)
                        .to("log:info");
            }
        });

        citrusCamelContext.start();

        context.getReferenceResolver().bind("citrusCamelContext", citrusCamelContext);
        context.getReferenceResolver().bind("camelContext", citrusCamelContext);

        testLoader.load();

        TestCase result = testLoader.getTestCase();
        Assert.assertEquals(result.getName(), "CamelRemoveRouteTest");
        Assert.assertEquals(result.getMetaInfo().getAuthor(), "Christoph");
        Assert.assertEquals(result.getMetaInfo().getStatus(), TestCaseMetaInfo.Status.FINAL);
        Assert.assertEquals(result.getActionCount(), 2L);
        Assert.assertEquals(result.getTestAction(0).getClass(), RemoveCamelRouteAction.class);
        Assert.assertEquals(result.getTestAction(0).getName(), "remove-routes");

        int actionIndex = 0;

        RemoveCamelRouteAction action = (RemoveCamelRouteAction) result.getTestAction(actionIndex++);
        Assert.assertNotNull(action.getCamelContext());
        Assert.assertEquals(action.getCamelContext(), context.getReferenceResolver().resolve("citrusCamelContext", CamelContext.class));
        Assert.assertEquals(action.getRouteIds().size(), 1);

        action = (RemoveCamelRouteAction) result.getTestAction(actionIndex);
        Assert.assertNotNull(action.getCamelContext());
        Assert.assertEquals(action.getCamelContext(), context.getReferenceResolver().resolve("camelContext", CamelContext.class));
        Assert.assertEquals(action.getRouteIds().size(), 2);
    }
}