# Урок 6. Домашнее задание
@Litvinenko Yuriy

## Что сделано дополнительно к lesson5-hw.
1. Обнаружена и исправлена ошибка сборки файла из файлов-фрагментов.
    Из-за того, что файлы сортируются как строки, получалось, что файл с 10-32 в названии оказывался раньше файла с 1-32 в названии.
    Пришлось добавить массив названий фрагментов, формировать его на отправляющей стороне и передавать его вместе с данными фрагмента.
    Да, это усложнение кода, но ниначе пришлось бы отказаться от этого функционала.

2. Исправлен код по рекомендациям в code Review(не все пункты):
    1.[client] StorageTest.uploadFileByFrags().
        a. DONE Добавить ссылку на один массив в параметры метода read…, чтобы не создавать дубликаты массивов, что забивает память.
        b. DONE Заменить while на for.
    2.[server] FileCommandHandler. compileUploadedFileFragments().
    DONE Переделать на .nio Files, Channels? Чтобы перекачка данных шла напрямую, а не через оперативную память, как сейчас.
    В цикле:
    for (int i = 1; i <= fileFragmentMessage.getFragsNames().length; i++) {
        //строку:
        Files.write(pathToFile,
                            Files.readAllBytes(Paths.get(toTempDir, fileFragmentMessage.getFragsNames()[i - 1])),
                            StandardOpenOption.APPEND)
        //заменил на код:
        ReadableByteChannel source = Channels.newChannel(
                            Files.newInputStream(Paths.get(toTempDir, fileFragmentMessage.getFragsNames()[i - 1])));
        WritableByteChannel destination = Channels.newChannel(
                            Files.newOutputStream(pathToFile, StandardOpenOption.APPEND));
        copyData(source, destination);
        source.close();
            destination.close();
    }
    private void copyData(ReadableByteChannel source, WritableByteChannel destination) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
            while (source.read(buffer) != -1) {
                // The Buffer Is Used To Be Drained
                buffer.flip();
                // Make Sure That The Buffer Was Fully Drained
                while (buffer.hasRemaining()) {
                    destination.write(buffer);
                }
                // Now The Buffer Is Empty!
                buffer.clear();
            }
    }
3. Добавлена обратная передача файлов от сервера по запросу клиента, причем
    независимо от размера файла. 
    Правда пришлось дублировать много кода в моделе сервер и клиенте, т.к. 
    перенести в shared нельзя из-за разных способов отправки сообщений.

## Вопросы
1. Правильно ли теперь организована перекачка данных из файлов-фрагментов в файл-назначения
    (теперь не через оперативную память?)?
    И нужно ли еще оптимизировать код в этом цикле(сейчас в каждой итерации создаются и 
    закрываются новые каналы и потоки)?
    for (int i = 1; i <= fileFragmentMessage.getFragsNames().length; i++) {
        //строку:
        Files.write(pathToFile,
                            Files.readAllBytes(Paths.get(toTempDir, fileFragmentMessage.getFragsNames()[i - 1])),
                            StandardOpenOption.APPEND)
        //заменил на код:
        ReadableByteChannel source = Channels.newChannel(
                            Files.newInputStream(Paths.get(toTempDir, fileFragmentMessage.getFragsNames()[i - 1])));
        WritableByteChannel destination = Channels.newChannel(
                            Files.newOutputStream(pathToFile, StandardOpenOption.APPEND));
        copyData(source, destination);
        source.close();
            destination.close();
        
2. Поясните пожалуйста, что неверно в таком способе сбора пути к папке:
        String fromDir = userStorageRoot; //"storage/server_storage"
        fromDir = fromDir.concat("/").concat(storageDir);
    В итоге получается строка "storage/server_storage/folderToUploadFile".
  Мне не понятно, чем это принципиально отличается от "storage/server_storage"(userStorageRoot).
  Почему concat("/") не будет работать?
  И как в таком случае лучше делать?
  
  Или лучше просто писать так?:
        String fromDir = userStorageRoot + "/" + storageDir;
  Но тогда зачем в java есть метод concat? 
  Я думал, что с ним будет правильнее, но, правда, длиннее.
  
  Или лучше вообще делать через .nio.Paths?:
          Path fromDirPath = Paths.get(userStorageRoot, storageDir);
  Но тогда нужно будет постоянно делать обратное преобразование Path в String, чтобы
  работал метод Paths.get(fromDir, filename), который требует только String в параметрах.
  То есть, ему нельзя передать fromDirPath(объект Path), а придется сделать сначала 
          String fromDir = fromDirPath.toString();
  
  Вообще тема с путями не такая простая, как кажется на первый взгляд.
  Буду благодарен, если поясните суть работы с путями.
}

