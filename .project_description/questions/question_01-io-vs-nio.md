@filename question_01-io-vs-nio.
@since 14.03.2020
@author Yuriy Litvinenko
@project(s):
 D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage 
@class [shared]ItemUtils
 D:\GeekBrains\_MyJavaProjectsAndSamples\JavaFX\simple_file_manager
@class Controller

Вопрос. Какой из вариантов ниже правильнее? Оба рабочие.
Я использовал Вариант 1, т.к. он короче.
#Вариант 1. На .io. С использованием класса File(about.27 strings).
    public Item[] getItemsList(Item directoryItem, Path rootPath) {
        //инициируем временный файловый объект заданной директории
        File dirFileObject = new File(getRealPath(directoryItem.getItemPathname(), rootPath).toString());
        //инициируем и получаем массив файловых объектов заданной директории
        File[] files = dirFileObject.listFiles();
        assert files != null;
        //инициируем массив объектов элементов в заданной директории
        Item[] items = new Item[files.length];
        for (int i = 0; i < files.length; i++) {
            //инициируем переменную имени элемента
            String itemName = files[i].getName();
            //инициируем строковыю переменную пути к элементу относительно директории по умолчанию
            String itemPathname = getItemPathname(files[i].getPath(), rootPath);
            //инициируем объект элемента в заданной директории
            items[i] = new Item(itemName, directoryItem.getItemName(), itemPathname,
                    directoryItem.getItemPathname(), files[i].isDirectory());
            //если элемент не является директорией
            if(!files[i].isDirectory()) {
                //сохраняем размер файла
                items[i].setItemSize(files[i].length());
                
                //TODO Temporarily
                System.out.println(items[i].getItemName() + ": " + items[i].getItemSize());
            
            }
        }
        return items;
    }

#Вариант 2.1. На .nio. Напрямую в массив(about.45 strings).
    public Item[] getItemsList(Item directoryItem, Path rootPath) {
        //инициируем временную коллекцию объектов путей к файловым объектам заданной директории
        List<Path> paths = null;
        try {
            //наполняем коллекцию
            paths = Files.list(getRealPath(directoryItem.getItemPathname(),
                    rootPath)).collect(Collectors.toList());
        } catch (IOException e) {
            //инициируем новое окно с сообщением об ошибке
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "ItemUtils.getItemsList() - Something wrong with files in the directory: " + directoryItem.getItemName());
            //открываем окно и ждем действий пользователя
            alert.showAndWait();
        }
        //инициируем массив объектов элементов в заданной директории
        assert paths != null;
        Item[] items = new Item[paths.size()];
        for (int i = 0; i < paths.size(); i++) {
            //инициируем переменную имени элемента
            String itemName = paths.get(i).getFileName().toString();
            //инициируем строковую переменную пути к элементу относительно директории по умолчанию
            String itemPathname = getItemPathname(paths.get(i).toString(), rootPath);
            //инициируем объект элемента в заданной директории
            items[i] = new Item(itemName, directoryItem.getItemName(), itemPathname,
                    directoryItem.getItemPathname(), Files.isDirectory(paths.get(i)));
            //если элемент не является директорией
            if(!Files.isDirectory(paths.get(i))) {
                try {
                    //сохраняем размер файла
                    items[i].setItemSize(Files.size(paths.get(i)));

                    //TODO Temporarily
                    System.out.println("ItemUtils.getItemsList() - " + items[i].getItemName() +
                            ": " + items[i].getItemSize() + " bytes.");

                } catch (IOException e) {
                    //инициируем новое окно с сообщением об ошибке
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "ItemUtils.getItemsList() - Something wrong with file: " + itemPathname);
                    //открываем окно и ждем действий пользователя
                    alert.showAndWait();
                }
            }
        }
        return items;
    }

#Вариант 2.2. На .nio. Через коллекцию и с использованием Consumer и лямбда(about.56 strings).
    public Item[] getItemsList(Item directoryItem, Path rootPath) {
        //инициируем временную коллекцию объектов путей к файловым объектам заданной директории
        List<Path> paths;
        List<Item> items = new ArrayList<>();
        //инициируем временную переменную размера коллекции(для преобразования в массив)
        int pathsSize = 0;
        try {
            //наполняем коллекцию путей
            paths = Files.list(getRealPath(directoryItem.getItemPathname(),
                    rootPath)).collect(Collectors.toList());
            //сохраняем размер коллекции
            pathsSize = paths.size();
            //наполняем коллекцию элементов
            paths.forEach(path -> createItem(path, items, directoryItem, rootPath));
        } catch (IOException e) {
            //инициируем новое окно с сообщением об ошибке
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "ItemUtils.getItemsList() - Something wrong with files in the directory: " +
                            directoryItem.getItemName());
            //открываем окно и ждем действий пользователя
            alert.showAndWait();
        }

        //TODO Temporarily
        System.out.println("ItemUtils.getItemsList() - " + items);
        //ItemUtils.getItemsList() - [Item{itemName='folderToDelete', parentName='', itemPathname='folderToDelete', parentPathname='', isDirectory=true}, Item{itemName='folderToDownloadFile', parentName='', itemPathname='folderToDownloadFile', parentPathname='', isDirectory=true}, Item{itemName='folderToDownloadFolder', parentName='', itemPathname='folderToDownloadFolder', parentPathname='', isDirectory=true}, Item{itemName='folderToRenameFolder', parentName='', itemPathname='folderToRenameFolder', parentPathname='', isDirectory=true}, Item{itemName='folderToUploadFolder', parentName='', itemPathname='folderToUploadFolder', parentPathname='', isDirectory=true}, Item{itemName='folderWithArtefToDownloadFile', parentName='', itemPathname='folderWithArtefToDownloadFile', parentPathname='', isDirectory=true}, Item{itemName='toDelete.png', parentName='', itemPathname='toDelete.png', parentPathname='', isDirectory=false}, Item{itemName='toRename.png', parentName='', itemPathname='toRename.png', parentPathname='', isDirectory=false}, Item{itemName='toUpload.txt', parentName='', itemPathname='toUpload.txt', parentPathname='', isDirectory=false}, Item{itemName='toUpload2.txt', parentName='', itemPathname='toUpload2.txt', parentPathname='', isDirectory=false}]

//        return (Item[])items.toArray();
        //Exception in thread "JavaFX Application Thread" java.lang.ClassCastException: [Ljava.lang.Object; cannot be cast to [Lutils.Item;

        return items.toArray(new Item[pathsSize]);//Works!
    }

    private void createItem(Path path, List<Item> items,
                            Item directoryItem, Path rootPath) {
        //инициируем переменную имени элемента
        String itemName = path.getFileName().toString();
        //инициируем строковую переменную пути к элементу относительно директории по умолчанию
        String itemPathname = getItemPathname(path.toString(), rootPath);
        //инициируем объект элемента в заданной директории
        Item item = new Item(itemName, directoryItem.getItemName(), itemPathname,
                directoryItem.getItemPathname(), Files.isDirectory(path));
        items.add(item);
        //если элемент не является директорией
        if(!Files.isDirectory(path)) {
            //сохраняем размер файла
            try {
                item.setItemSize(Files.size(path));
            } catch (IOException e) {
                    //инициируем новое окно с сообщением об ошибке
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "ItemUtils.createItem() - Something wrong with file: " + itemPathname);
                    //открываем окно и ждем действий пользователя
                    alert.showAndWait();
            }

            //TODO Temporarily
            System.out.println("ItemUtils.createItem() - " + item.getItemName() +
                    ": " + item.getItemSize() + " bytes.");

        }
    }
