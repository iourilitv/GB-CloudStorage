package messages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * A Class for communication commands between client and server.
 */
public class CommandMessage extends AbstractMessage {
    public static final int CMD_MSG_AUTH_OK = 23792837;
    public static final int CMD_MSG_REQUEST_FILE_DOWNLOAD = 398472948;
    public static final int CMD_MSG__REQUEST_FILES_LIST = 340274982;
    public static final int CMD_MSG__REQUEST_SERVER_DELETE_FILE = 239622746;

    private int type;
    private Object[] attachment;

    public int getType() {
        return type;
    }

    public Object[] getAttachment() {
        return attachment;
    }

    public CommandMessage(int type, Object... attachment) {
        this.type = type;
        this.attachment = attachment;
    }

//    private String filename;
//    private byte[] data;
//
//    public CommandMessage(String root, String filename) throws IOException {
//        this.filename = filename;
//        this.data = Files.readAllBytes(Paths.get(root, filename));//TODO
//    }

    @Override
    public String toString() {
        return "messages.CommandMessage{" +
                "type=" + type +
                ", attachment=" + Arrays.toString(attachment) +
                '}';
    }

//    public String getFilename() {
//        return filename;
//    }
//
//    public byte[] getData() {
//        return data;
//    }
}


