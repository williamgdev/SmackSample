package com.github.dev.williamg.smackpoc;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.List;

import needle.Needle;

public class OpenFireServer implements ConnectionListener {
    private static OpenFireServer instance = new OpenFireServer();

    AbstractXMPPConnection connection;
    private MultiUserChat multiUserChat;
    OpenFireServerListener listener;

    private OpenFireServer(){}

    public static OpenFireServer getInstance(String UID) {
        if (instance.connection == null)
            instance.connectOpenFireServer(UID);
        return instance;
    }

    public boolean isConnected(){
        return instance.connection.isConnected();
    }

    public boolean isAuthenticated(){
        return instance.connection.isAuthenticated();
    }

    public void setListener(OpenFireServerListener listener){
        this.listener = listener;
    }

    private void connectOpenFireServer(String uniqueUID) {
        XMPPTCPConnectionConfiguration.Builder configBuilder;
        try {
            configBuilder = XMPPTCPConnectionConfiguration.builder();
            /**
             * Notice that the user and password are the same
             */
            configBuilder.setUsernameAndPassword(uniqueUID, uniqueUID);
            configBuilder.setXmppDomain("XMPP_DOMAIN");
            configBuilder.setHost("HOST_NAME");
            connection = new XMPPTCPConnection(configBuilder.build());
            connection.addConnectionListener(this);

            Needle.onBackgroundThread().withThreadPoolSize(Needle.DEFAULT_POOL_SIZE).execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        connection.connect();
                    } catch (SmackException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });


        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }


    public void sendMessage(String subject, String body) {
        if (!isAuthenticated()) {
            listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.ERROR, "Error authenticating");
            return;
        }
        Message message = new Message();
        message.setSubject(subject);
        message.setBody(body);

        if (multiUserChat != null) {
            try {
                if (!multiUserChat.isJoined()) {
                    multiUserChat.join(connection.getUser().getResourcepart());
                }
                multiUserChat.sendMessage(message);

            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (MultiUserChatException.NotAMucServiceException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void connected(XMPPConnection xmppConnection) {
        listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.CONNECTED, "");
        boolean success = false;
        try {
            connection.login();
            success = true;
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!success)
            listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.ERROR, "Error login");

    }

    @Override
    public void authenticated(XMPPConnection xmppConnection, boolean b) {
        MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        DomainBareJid groupChatService;
        try {
            /**
             * multiUserChatManager.getXMPPServiceDomains().get(1) set the second service to this demo
             */
            groupChatService = multiUserChatManager.getXMPPServiceDomains().get(1);

            List<HostedRoom> rooms;
            rooms = multiUserChatManager.getHostedRooms(groupChatService.asDomainBareJid());
            /**
             * rooms.get(0) set the first group chat to this demo
             */
            multiUserChat = multiUserChatManager.getMultiUserChat(rooms.get(0).getJid());
            if (multiUserChat != null) {
                if (!multiUserChat.isJoined()) {
                    multiUserChat.join(connection.getUser().getResourcepart());
                }
                multiUserChat.addMessageListener(new MessageListener() {
                    @Override
                    public void processMessage(final Message message) {
                        listener.notifyMessage(message.getSubject(), message.getBody());
                    }
                });
            }

            listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.AUTHENTICATED, "");
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (MultiUserChatException.NotAMucServiceException e) {
            e.printStackTrace();
        }

        listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.ERROR, "Error authenticating");
    }

    @Override
    public void connectionClosed() {
        listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.CONNECTION_CLOSED, "Connection Closed");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.CONNECTION_CLOSED, "Connection Closed: " + e.getMessage());

    }

    @Override
    public void reconnectionSuccessful() {
        listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.RECONNECTION_SUCCESS, "Reconnection Successfully");

    }

    @Override
    public void reconnectingIn(int i) {
        listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.RECONNECTION_SUCCESS, "Reconnection in: " + i);

    }

    @Override
    public void reconnectionFailed(Exception e) {
        listener.notifyStatusOpenFireServer(OpenFireServerListener.STATE.RECONNECTION_FAILED,("Reconnection Failed"));

    }

    public interface OpenFireServerListener{
        enum STATE {ERROR, CONNECTION_CLOSED, RECONNECTION_SUCCESS, RECONNECTION_FAILED, AUTHENTICATED, CONNECTED};
        void notifyStatusOpenFireServer(STATE state, String message);
        void notifyMessage(String subject, String body);
    }

}
