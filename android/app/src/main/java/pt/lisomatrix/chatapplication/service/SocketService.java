package pt.lisomatrix.chatapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

import io.socket.client.IO;
import io.socket.client.Socket;
import pt.lisomatrix.chatapplication.model.Message;

import static pt.lisomatrix.chatapplication.constant.APIConstants.API_URL;


public class SocketService extends Service {

    private static final String NEW_MESSAGE = "new_message";
    private static final String IS_TYPING = "is_typing";
    private static final String NOT_TYPING = "not_typing";

    /**
     *  Token for user authentication
     * */
    private String token;

    /**
     * Service callbacks to notify UI
     */
    private SocketServiceCallbacks mSocketServiceCallbacks;

    /**
     * Socket.io instance
     */
    private Socket mSocket;

    /**
     * Json serializer
     */
    private Gson gson = new Gson();

    /**
     * Service Binder Instance
     */
    private final LocalBinder mBinder = new LocalBinder();

    /**
     *  Called when binding service
     *
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        token = intent.getExtras().getString("token");
        connect();

        return mBinder;
    }

    /**
     * Sends a WebSocket message to the server
     *
     * @param message
     */
    public void sendMessageEvent(Message message) {
        mSocket.emit(NEW_MESSAGE, gson.toJson(message));
    }

    public void sendTypingEvent(boolean isTyping) {
        if (isTyping) {
            mSocket.emit(IS_TYPING);
        } else {
            mSocket.emit(NOT_TYPING);
        }
    }

    /**
     * Attempts to connect to the server
     */
    private void connect() {
        // Create WebSocket instance
        try {
            mSocket = IO.socket(API_URL);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Set Event listeners
        mSocket.on(Socket.EVENT_CONNECT, args -> {
            mSocket.emit("auth", token);
        });

        // On connection event
        mSocket.on(NEW_MESSAGE, args -> {
            if (mSocketServiceCallbacks != null) {
                String json = args[0].toString();
                mSocketServiceCallbacks.addMessage(gson.fromJson(json, Message.class));
            }
        });

        mSocket.on(IS_TYPING, args -> {
            Log.d("DEBUG", "This user is typing");
        });

        mSocket.on(NOT_TYPING, args -> {
            Log.d("DEBUG", "This stoped user is typing");
        });

        // Connect
        mSocket.connect();
    }

    /**
     * Binding class
     */
    public class LocalBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }

        public void setCallbacks(SocketServiceCallbacks socketServiceCallbacks) {
            mSocketServiceCallbacks = socketServiceCallbacks;
        }
    }
}
