package messages;

import java.io.IOException;

/**
 * A Class for a list of files objects.
 */
public class FileListMessage extends AbstractMessage {

    public FileListMessage(String root, String filename) throws IOException {//TODO
        super(root, filename);
    }
}
