//
//  ========================================================================
//  Copyright (c) 1995-2020 Mort Bay Consulting Pty Ltd and others.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.demo;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;

import org.eclipse.jetty.util.component.LifeCycle;

public class App
{
    private static final App MAIN_INSTANCE = new App();

    private WebSocketContainer client;

    public static void main(String[] args) throws IOException, DeploymentException, URISyntaxException
    {
        App.MAIN_INSTANCE.connect();
    }

    public static void stop(int returnCode)
    {
        // Trigger stop on thread that does not belong to Container.
        new Thread(() ->
        {
            LifeCycle.stop(App.MAIN_INSTANCE.client);
            System.exit(returnCode);
        }).start();
    }

    private WebSocketContainer getClientContainer()
    {
        if (client == null)
            client = ContainerProvider.getWebSocketContainer();
        return client;
    }

    public void connect() throws IOException, DeploymentException, URISyntaxException
    {
        URI echoUri = URI.create("wss://echo.websocket.org");

        ClientEndpointConfig clientEndpointConfig = ClientEndpointConfig.Builder.create()
            .configurator(new OriginServerConfigurator(echoUri))
            .build();

        EchoEndpoint echoEndpoint = new EchoEndpoint();
        getClientContainer().connectToServer(echoEndpoint, clientEndpointConfig, echoUri);
        System.out.printf("Connected to : %s%n", echoUri);
    }
}
