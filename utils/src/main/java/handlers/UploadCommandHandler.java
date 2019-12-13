package handlers;

import messages.FileFragmentMessage;
import messages.FileMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class UploadCommandHandler extends CommandHandler{
    private FileMessage fileMessage;
    private FileFragmentMessage fileFragmentMessage;

    public UploadCommandHandler(FileMessage fileMessage) {
        this.fileMessage = fileMessage;
    }

    public UploadCommandHandler(FileFragmentMessage fileFragmentMessage) {
        this.fileFragmentMessage = fileFragmentMessage;
    }

    public FileMessage getFileMessage() {
        return fileMessage;
    }

    public FileFragmentMessage getFileFragmentMessage() {
        return fileFragmentMessage;
    }

    public void uploadFile(String storageDir) throws IOException {
        System.out.println("Server.onReceiveObject - fileMessage.getFilename(): " +
                fileMessage.getFilename() +
                ". Arrays.toString(fileMessage.getData()): " +
                Arrays.toString(fileMessage.getData()));

        Files.write(Paths.get(storageDir, fileMessage.getFilename()),
                fileMessage.getData(), StandardOpenOption.CREATE);
    }
}
