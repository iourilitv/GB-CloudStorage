package handlers;

import messages.*;

import java.io.IOException;

public class ObjectHandler {

    public void recognizeAndArrangeMessageObject(CommandMessage messageObject, String storageDir) {
        try {
            //выполняем операции в зависимости от типа полученного сообщения(команды)
            switch (messageObject.getCommand()){
                case CommandMessage.CMD_MSG_REQUEST_FILE_UPLOAD:
                    UploadCommandHandler uploadCommandHandler = (UploadCommandHandler)messageObject.getCommandHandler();
                    uploadCommandHandler.uploadFile(storageDir);
                    break;
                case CommandMessage.CMD_MSG_REQUEST_AUTH:
                    ServiceCommandHandler serviceCommandHandler = (ServiceCommandHandler) messageObject.getCommandHandler();
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
//                case "CommandMessage":
//                    CommandMessage commandMessage = (CommandMessage) messageObject;
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
