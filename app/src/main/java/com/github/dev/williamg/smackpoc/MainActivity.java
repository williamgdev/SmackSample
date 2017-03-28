package com.github.dev.williamg.smackpoc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.jiveproperties.JivePropertiesManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;

import needle.Needle;

public class MainActivity extends AppCompatActivity {

    private static final String HOST_NAME = "192.168.43.212";
    private static final String USER_EMAIL_ADDRESS = "@" + HOST_NAME;
    private static final String XMPP_DOMAIN = "openfire.test";
    private String TAG = "MainActivity ->";
    AbstractXMPPConnection connection;
    ChatManager chatManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Needle.onBackgroundThread().withThreadPoolSize(Needle.DEFAULT_POOL_SIZE).execute(new Runnable() {
            @Override
            public void run() {
                connectOpenFireServer();
            }
        });


    }



    public void connectOpenFireServer() {
        //
        // Create a connection to the jabber.org server.
//        AbstractXMPPConnection conn1 = null;
//        try {
//            conn1 = new XMPPTCPConnection("test", "test123", "openfire.test");
//        } catch (XmppStringprepException e) {
//            e.printStackTrace();
//        }
//        try {
//            conn1.connect();
//        } catch (SmackException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (XMPPException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

// Create a connection to the jabber.org server on a specific port.

        XMPPTCPConnectionConfiguration.Builder configBuilder;
        try {
            configBuilder = XMPPTCPConnectionConfiguration.builder();
            configBuilder.setUsernameAndPassword("test", "test123");
//            configBuilder.setUsernameAndPassword("admin", "randpass1");
            configBuilder.setXmppDomain(XMPP_DOMAIN);
            configBuilder.setHost(HOST_NAME);
            connection = new XMPPTCPConnection(configBuilder.build());
            try {
                connection.connect();
                connection.login();
                Log.d(TAG, "Connected: " + connection.isConnected() + " Authenticated: " + connection.isAuthenticated());

                // Assume we've created an XMPPConnection name "connection"._
                chatManager = ChatManager.getInstanceFor(connection);
                chatManager.addIncomingListener(new IncomingChatMessageListener() {
                    @Override
                    public void newIncomingMessage(EntityBareJid entityBareJid, Message message, Chat chat) {
                        Log.d(TAG, "newIncomingMessage: " + message.getSubject() + ": " + message.getBody());
                    }
                });
                Log.d(TAG, "connectOpenFireServer: Listener registered");

            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
                Log.d(TAG, "connectOpenFireServer: " + e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }



    }

    public void onLogin(View view) {
        Message message = new Message();
        message.setBody("What's uppp!!!");
        message.setSubject("Ey there");
        EntityBareJid jid = null;
        try {
            jid = JidCreate.entityBareFrom("admin" + "@openfire.test/auisczr9pv");

            Chat chat = chatManager.chatWith(jid);
            try {
                chat.send(message);
                Log.d(TAG, "onLogin: " + connection.getUser());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }

    }
}
