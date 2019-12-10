import messages.AbstractMessage;

import java.io.Serializable;

/**
 * A class of an object of any types of messages.
 */
public class MessageObject implements Serializable {
    private String messageType;
    private AbstractMessage message;

    public MessageObject(String messageType, AbstractMessage message) {
        this.messageType = messageType;
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public AbstractMessage getMessage() {
        return message;
    }
}
