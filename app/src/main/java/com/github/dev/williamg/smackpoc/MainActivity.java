package com.github.dev.williamg.smackpoc;

import android.os.Handler;
import android.os.Looper;
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

public class MainActivity extends AppCompatActivity implements OpenFireServer.OpenFireServerListener{

    private static final String HOST_NAME = "192.168.43.212";
    private static final String USER_EMAIL_ADDRESS = "@" + HOST_NAME;
    private static final String XMPP_DOMAIN = "openfire.test";
    private String TAG = "MainActivity ->";
    private OpenFireServer openFireServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openFireServer = OpenFireServer.getInstance("USER_ID");
        openFireServer.setListener(this);

    }

    @Override
    public void notifyStatusOpenFireServer(STATE state, String message) {
        Log.d(TAG, "notifyStatusOpenFireServer: State:" + state.toString());
        switch (state) {
            case ERROR:
                break;
            case CONNECTION_CLOSED:
                break;
            case RECONNECTION_SUCCESS:
                break;
            case RECONNECTION_FAILED:
                break;
            case CONNECTED:
                break;
        }
    }

    @Override
    public void notifyMessage(final String subject, final String body) {
        Log.d(TAG, "processMessage: " + subject + ": " + body);

    }
}
