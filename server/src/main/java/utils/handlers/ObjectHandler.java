package utils.handlers;

import messages.AuthMessage;
import messages.Commands;
import messages.FileMessage;
import tcp.TCPServer;
import utils.CommandMessage;

import java.io.IOException;

/**
 * The server class for recognizing command messages and control command handlers.
 */
public class ObjectHandler {

//    public void recognizeAndArrangeMessageObject(TCPServer tcpServer, utils.CommandMessage messageObject, String storageDir) {
//        try {
//            FileMessage fileMessage;
//            //выполняем операции в зависимости от типа полученного сообщения(команды)
//            switch (messageObject.getCommand()){
//                //обрабатываем полученный от клиента запрос на загрузку(сохранение) файла в облачное хранилище
//                case Commands.REQUEST_SERVER_FILE_UPLOAD:
//                    UploadCommandHandler uploadCommandHandler = (UploadCommandHandler)messageObject.getCommandHandler();
//                    fileMessage = uploadCommandHandler.getFileMessage();
//                    uploadCommandHandler.saveUploadedFile(fileMessage, storageDir);
//                    break;
//                //обрабатываем полученный от клиента запрос на скачивание файла из облачного хранилища
//                case Commands.REQUEST_SERVER_FILE_DOWNLOAD:
//                    uploadCommandHandler = (UploadCommandHandler)messageObject.getCommandHandler();
//                    fileMessage = uploadCommandHandler.getFileMessage();
//                    uploadCommandHandler.downloadFile(tcpServer, fileMessage, storageDir);
//                    break;
//                //обрабатываем полученный от клиента запрос на авторизацию в облачное хранилище
//                case Commands.REQUEST_SERVER_AUTH:
//                    ServiceCommandHandler serviceCommandHandler = (ServiceCommandHandler) messageObject.getCommandHandler();
//                    serviceCommandHandler.authorizeUser();
//                    break;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public void recognizeAndArrangeMessageObject(TCPServer tcpServer, CommandMessage messageObject) {
        try {
            FileMessage fileMessage;
            FileCommandHandler fileCommandHandler;

//            String currentDir;//TODO
            String clientDir;
            String storageDir;

            //выполняем операции в зависимости от типа полученного сообщения(команды)
            switch (messageObject.getCommand()){
//                //обрабатываем полученный от клиента запрос на загрузку(сохранение) файла в облачное хранилище
//                case Commands.REQUEST_SERVER_FILE_UPLOAD:
////                    FileCommandHandler uploadCommandHandler = (FileCommandHandler)messageObject.getCommandHandler();
////                    fileMessage = uploadCommandHandler.getFileMessage();
//                    fileMessage = (FileMessage) messageObject.getMessageObject();
//                    fileCommandHandler = new FileCommandHandler(fileMessage);
//                    currentDir = fileMessage.getRoot();
//                    fileCommandHandler.saveUploadedFile(fileMessage, currentDir);
//                    break;
                //обрабатываем полученный от клиента запрос на скачивание файла из облачного хранилища
                case Commands.REQUEST_SERVER_FILE_DOWNLOAD:
                    fileMessage = (FileMessage) messageObject.getMessageObject();
                    fileCommandHandler = new FileCommandHandler(fileMessage);
//                    currentDir = fileMessage.getRoot();
                    storageDir = fileMessage.getFromDir();
                    clientDir = fileMessage.getToDir();
                    fileCommandHandler.downloadFile(tcpServer, storageDir, clientDir, fileMessage.getFilename());
                    break;
                //обрабатываем полученный от клиента запрос на авторизацию в облачное хранилище
                case Commands.REQUEST_SERVER_AUTH:
//                    ServiceCommandHandler serviceCommandHandler = (ServiceCommandHandler) messageObject.getCommandHandler();
                    AuthMessage authMessage = (AuthMessage) messageObject.getMessageObject();
                    ServiceCommandHandler serviceCommandHandler = new ServiceCommandHandler(authMessage);
                    serviceCommandHandler.authorizeUser();
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
