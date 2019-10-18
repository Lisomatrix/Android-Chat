package pt.lisomatrix.chatapplication.service;

import pt.lisomatrix.chatapplication.model.Message;

public interface SocketServiceCallbacks {

    void addMessage(Message message);
}
