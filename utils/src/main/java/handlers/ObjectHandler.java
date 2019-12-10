package handlers;

import messages.AbstractMessage;
import messages.AuthMessage;
import messages.CommandMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class ObjectHandler {

    public void recognizeAndArrangeMessageObject(AbstractMessage messageObject, String storageDir) {
        try {
            //выполняем операции в зависимости от типа полученного сообщения(команды)
            switch (messageObject.getClass().getSimpleName()){
                case "CommandMessage":
                    CommandMessage commandMessage = (CommandMessage) messageObject;
                    System.out.println("ByteServer.onReceiveObject - commandMessage.getFilename(): " +
                            commandMessage.getFilename() +
                            ". Arrays.toString(commandMessage.getData()): " +
                            Arrays.toString(commandMessage.getData()));

                    Files.write(Paths.get(storageDir, commandMessage.getFilename()),
                            commandMessage.getData(), StandardOpenOption.CREATE);
                    break;
                case "AuthMessage":
                    AuthMessage authMessage = (AuthMessage) messageObject;
                    System.out.println("ByteServer.onReceiveObject - commandMessage.getLogin(): " +
                            authMessage.getLogin() +
                            ". commandMessage.getPassword(): " + authMessage.getPassword());
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
