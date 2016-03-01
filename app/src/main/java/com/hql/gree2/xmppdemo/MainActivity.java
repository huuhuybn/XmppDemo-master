package com.hql.gree2.xmppdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.sasl.provided.SASLDigestMD5Mechanism;
import org.jivesoftware.smack.sasl.provided.SASLPlainMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;


public class MainActivity extends ActionBarActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text_view_message);
        Button button = (Button) findViewById(R.id.button_send_test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });
    }

    public void connect() {
        AsyncTask<Void, Void, Void> connectionThread = new AsyncTask<Void, Void, Void>() {

            public String USERNAME = "1111@gmail.com";
            public String PASSWORD = "1111";

            public String USERNAME_GREE2 = "gree2";

          //  public String DOMAIN = "52.32.181.170";
            public String DOMAIN = "203.162.166.48";
           // public String DOMAIN = "192.168.111.129";
            public int PORT = 5222;

            @Override
            protected Void doInBackground(Void... arg0) {
                XMPPTCPConnection mConnection;

                XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
                config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                config.setUsernameAndPassword(USERNAME, PASSWORD);
                config.setServiceName(DOMAIN);
                config.setHost(DOMAIN);
                config.setPort(PORT);
                config.setDebuggerEnabled(true);
                SASLMechanism mechanism = new SASLDigestMD5Mechanism();
                SASLAuthentication.registerSASLMechanism(mechanism);
                SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
                SASLAuthentication.unBlacklistSASLMechanism("DIGEST-MD5");

                mConnection = new XMPPTCPConnection(config.build());
                try {

                    Log.e("start", "start connect!");
                    mConnection.connect();

                    Log.e("start", "start connected!");
                    mConnection.login();
                    Log.e("start", "logged!");
                } catch (SmackException | IOException | XMPPException e) {
                    Log.e("AsyncTask", e.toString());
                }

                final ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
                chatManager.addChatListener(new ChatManagerListener() {
                    @Override
                    public void chatCreated(Chat chat, boolean b) {
                        chat.addMessageListener(new ChatMessageListener() {
                            @Override
                            public void processMessage(Chat chat, Message message) {
                                Log.d("AsyncTask", message.toString());

                                final String msg = message.toString();

                                textView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String text = textView.getText().toString();
                                        textView.setText(text + "\n" + msg);
                                    }
                                });
                            }
                        });
                    }
                });

                Chat chat2 = chatManager.createChat(USERNAME_GREE2 + "@" + DOMAIN);
                try {
                    chat2.sendMessage("This message is from Android");
                } catch (SmackException.NotConnectedException e) {
                    Log.d("AsyncTask", e.toString());
                }

                return null;
            }

        };
        connectionThread.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
