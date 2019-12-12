package utils.handlers;

import messages.AuthMessage;
import messages.Commands;
import messages.FileMessage;
import utils.CommandMessage;

import java.io.IOException;

/**
 * The client class for recognizing command messages and control command handlers.
 */
public class ObjectHandler {

    public void recognizeAndArrangeMessageObject(CommandMessage messageObject) {
        try {
            FileMessage fileMessage;
            FileCommandHandler fileCommandHandler;
            String clientDir;
            String storageDir;

            //выполняем операции в зависимости от типа полученного сообщения(команды)
            switch (messageObject.getCommand()){
                case Commands.SERVER_RESPONSE_FILE_DOWNLOAD_OK:
                    fileMessage = (FileMessage) messageObject.getMessageObject();
                    fileCommandHandler = new FileCommandHandler(fileMessage);
                    storageDir = fileMessage.getFromDir();
                    clientDir = fileMessage.getToDir();

                    if(fileMessage != null){//FIXME
                        fileCommandHandler.saveDownloadedFile(storageDir, clientDir, fileMessage);
                    } else {
                        System.out.println("There is no any file attached!");//FIXME выводить в GUI?
                    }

                    //проверяем сохраненный файл по контрольной сумме//FIXME

                    //отправляем сообщение на сервер: подтверждение, что все прошло успешно
                    // или запрос на повторную отправку файла//FIXME
                    break;
                case Commands.SERVER_RESPONSE_AUTH_OK:
                    AuthMessage authMessage = (AuthMessage) messageObject.getMessageObject();
                    ServiceCommandHandler serviceCommandHandler = new ServiceCommandHandler(authMessage);
                    serviceCommandHandler.isAuthorized();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//TODO Delete at the finish.

//    public void recognizeAndArrangeMessageObject(AbstractMessage messageObject, String storageDir) {
//        try {
//            //выполняем операции в зависимости от типа полученного сообщения(команды)
//            switch (messageObject.getClass().getSimpleName()){
//                case "utils.CommandMessage":
//                    utils.CommandMessage commandMessage = (utils.CommandMessage) messageObject;
//                    System.out.println("ByteServer.onReceiveObject - commandMessage.getFilename(): " +
//                            commandMessage.getFilename() +
//                            ". Arrays.toString(commandMessage.getData()): " +
//                            Arrays.toString(commandMessage.getData()));
//
//                    Files.write(Paths.get(storageDir, commandMessage.getFilename()),
//                            commandMessage.getData(), StandardOpenOption.CREATE);
//                    break;
//                case "AuthMessage":
//                    AuthMessage authMessage = (AuthMessage) messageObject;
//                    System.out.println("ByteServer.onReceiveObject - commandMessage.getLogin(): " +
//                            authMessage.getLogin() +
//                            ". commandMessage.getPassword(): " + authMessage.getPassword());
//                    break;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    public void recognizeAndArrangeMessageObject(AbstractMessage messageObject, String storageDir) {
//        try {
//            //выполняем операции в зависимости от типа полученного сообщения(команды)
//            switch (messageObject.getClass().getSimpleName()){
////                case "FileFragmentMessage":
////                    FileFragmentMessage fileFragmentMessage = (FileFragmentMessage) messageObject;
////                    System.out.println("Server.onReceiveObject - fileFragmentMessage.getFilename(): " +
////                            fileFragmentMessage.getFilename() +
////                            ". Arrays.toString(fileFragmentMessage.getData()): " +
////                            Arrays.toString(fileFragmentMessage.getData()));
////
////                    Files.write(Paths.get(storageDir, fileFragmentMessage.getFilename()),
////                            fileFragmentMessage.getData(), StandardOpenOption.CREATE);
////                    break;
//                case "FileMessage":
//                    FileMessage fileMessage = (FileMessage) messageObject;
//                    System.out.println("Server.onReceiveObject - fileMessage.getFilename(): " +
//                            fileMessage.getFilename() +
//                            ". Arrays.toString(fileMessage.getData()): " +
//                            Arrays.toString(fileMessage.getData()));
//
//                    Files.write(Paths.get(storageDir, fileMessage.getFilename()),
//                            fileMessage.getData(), StandardOpenOption.CREATE);
//                    break;
//                case "AuthMessage":
//                    AuthMessage authMessage = (AuthMessage) messageObject;
//                    System.out.println("Server.onReceiveObject - authMessage.getLogin(): " +
//                            authMessage.getLogin() +
//                            ". authMessage.getPassword(): " + authMessage.getPassword());
//                    break;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
