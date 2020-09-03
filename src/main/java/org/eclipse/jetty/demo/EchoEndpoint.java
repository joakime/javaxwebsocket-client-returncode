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

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

/**
 * Basic Echo Client Endpoint
 */
public class EchoEndpoint extends Endpoint implements MessageHandler.Whole<String>
{
    private Session session;

    @Override
    public void onClose(Session session, CloseReason closeReason)
    {
        System.out.printf("Connection closed: Session.id=%s - %s%n", session.getId(), closeReason);
        this.session = null;
    }

    @Override
    public void onOpen(Session session, EndpointConfig config)
    {
        System.out.printf("Got open: Session.id=%s%n", session.getId());
        this.session = session;
        this.session.addMessageHandler(this);
        try
        {
            session.getBasicRemote().sendText("Hello");
            session.getBasicRemote().sendText("How are things?");
            session.getBasicRemote().sendText("Thanks for the conversation.");
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    @Override
    public void onMessage(String msg)
    {
        System.out.printf("Got msg: \"%s\"%n", msg);
        if (msg.contains("Thanks"))
        {
            App.stop(1);
        }
    }

    @Override
    public void onError(Session session, Throwable cause)
    {
        cause.printStackTrace();
    }
}
