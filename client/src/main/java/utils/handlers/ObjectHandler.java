package utils.handlers;

import messages.AuthMessage;
import messages.Commands;
import messages.FileMessage;
import tcp.TCPClient;
import utils.CommandMessage;

import java.io.IOException;

/**
 * The client class for recognizing command messages and control command handlers.
 */
public class ObjectHandler {
    //принимаем объект клиента
    TCPClient client;
    //объявляем объект файлового сообщения для полного файла
    FileMessage fileMessage;
    //объявляем объект файлового хендлера
    FileCommandHandler fileCommandHandler;
    //объявляем пременную для клиентской директории
    String clientDir;
    //объявляем пременную для директории в сетевом хранилище
    String storageDir;

    public ObjectHandler(TCPClient client) {
        this.client = client;
    }

    //    public void recognizeAndArrangeMessageObject(CommandMessage messageObject) {
//        try {
//            FileMessage fileMessage;
//            FileCommandHandler fileCommandHandler;
//            String clientDir;
//            String storageDir;
//
//            //выполняем операции в зависимости от типа полученного сообщения(команды)
//            switch (messageObject.getCommand()){
//                case Commands.SERVER_RESPONSE_FILE_DOWNLOAD_OK:
//                    fileMessage = (FileMessage) messageObject.getMessageObject();
//                    fileCommandHandler = new FileCommandHandler(fileMessage);
//                    storageDir = fileMessage.getFromDir();
//                    clientDir = fileMessage.getToDir();
//
//                    if(fileMessage != null){//FIXME
//                        fileCommandHandler.saveDownloadedFile(storageDir, clientDir, fileMessage);
//                    } else {
//                        System.out.println("There is no any file attached!");//FIXME выводить в GUI?
//                    }
//
//                    //проверяем сохраненный файл по контрольной сумме//FIXME
//
//                    //отправляем сообщение на сервер: подтверждение, что все прошло успешно
//                    // или запрос на повторную отправку файла//FIXME
//                    break;
//                case Commands.SERVER_RESPONSE_AUTH_OK:
//                    AuthMessage authMessage = (AuthMessage) messageObject.getMessageObject();
//                    ServiceCommandHandler serviceCommandHandler = new ServiceCommandHandler(authMessage);
//                    serviceCommandHandler.isAuthorized();
//                    break;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    /**
     * Метот распознает тип команды и обрабатывает ее.
     * @param messageObject - объект сообщения(команды)
     */
    public void recognizeAndArrangeMessageObject(CommandMessage messageObject) {
        //выполняем операции в зависимости от типа полученного сообщения(команды)
        switch (messageObject.getCommand()) {
            case Commands.SERVER_RESPONSE_FILE_DOWNLOAD_OK:
                //вызываем метод обработки ответа сервера со скачанным целым файлом
                respondOnDownloadFileOK(messageObject);
                break;
            case Commands.SERVER_RESPONSE_AUTH_OK:
                AuthMessage authMessage = (AuthMessage) messageObject.getMessageObject();
                ServiceCommandHandler serviceCommandHandler = new ServiceCommandHandler(authMessage);
                serviceCommandHandler.isAuthorized();
                break;
        }
    }

    private void respondOnDownloadFileOK(CommandMessage messageObject) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        fileMessage = (FileMessage) messageObject.getMessageObject();
        //инициируем объект файлового хендлера
        fileCommandHandler = new FileCommandHandler(fileMessage);
        //вынимаем заданную директорию сетевого хранилища из объекта сообщения(команды)
        storageDir = fileMessage.getFromDir();
        //вынимаем заданную клиентскую директорию из объекта сообщения(команды)
        clientDir = fileMessage.getToDir();
        //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
        int command = Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_ERROR;
        //если сохранение прошло удачно
        if(fileCommandHandler.saveDownloadedFile(client, clientDir, fileMessage)){
            //проверяем сохраненный файл по контрольной сумме//FIXME
            if(true){
                //отправляем сообщение на сервер: подтверждение, что все прошло успешно
                command = Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_OK;
            }
        }
        //создаем объект файлового сообщения
        fileMessage = new FileMessage(storageDir, clientDir, fileMessage.getFilename());
        //отправляем объект сообщения(команды) на сервер
        client.getConnection().sendMessageObject(new CommandMessage(command, fileMessage));
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
