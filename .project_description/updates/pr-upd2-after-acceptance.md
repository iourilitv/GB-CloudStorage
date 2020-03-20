# Updating after the project acceptance. 
GeekBrains. Курсовая работа: Итоговый проект Cloud Storage.
@Litvinenko Yuriy

Скриншшот GUI https://yadi.sk/i/AwTDqr84PshnQA
Видео https://youtu.be/ku0okBn_zGM

## 2.Issues and developments.
1. GUI. Проверить и исправить вывод сообщений в метку уведомлений. Есть баги.
2. GUI. ListVew. 
	- Done. a. Исправить сортировку - вверху должны быть папки, а ниже файлы. 
	- b. Проверить и доработать контекстное меню. 
	Добавить clientListView.setContextMenu(contextMenu) в блок if, где выбирается показывать upload/download.
    - c. Добавить проваливание в папку по двойному клику.
4. Netty. Добавить проверку активности соединения на стороне клиента, чтобы не вылетало исключение при отваливании сервера.
5. Netty. При передаче файлов сериализовать только объект сообщения, а байты передавать средствами netty следом после команды.
20. DONE. Maven. Исключить предупреждение о кодировке при сборке проекта. 
    Добавил в <properties> pom.xml[project] строку 
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>.
21. System. Добавить логирование в файл и реорганизовать логирование в консоль.
22. DONE. GUI. Устранить дублирование открытия модальных окон.
    DONE. Выделить сервис модальных окон в отдельный класс WindowsManager.
23. GUI. Добавить в пункт меню Disconnect режим Connect. 
    Disconnect показывать в режиме подключен, Connect - отключен.
24. DONE. GUI. Добавить механизм усиленной проверки логина и пароля в 
    формах авторизации и регистрации.
    Можно взять из проекта netChat.
25. DONE. GUI. Добавить механизм проверки email в форме регистрации.
26. File. Добавить upload/download папок с объектами. Можно папку предварительно архивировать 
    и отправлять как обычный файл.
27. DONE. File. Исправить ошибки при отправке файлов более 2,6ГБ.
    Exception/Причина: 
    1. java. lang. ArrayIndexOutOfBoundsException/Слишком большой размер массива
        (индекс больше int);
    2. OutOfMemoryError Exception/Не хватает памяти кучи, чтобы рассчитать контрольную сумму 
    целого большого файла при нарезке и отправке фрагментов. По умолчанию jvm использует 512МБ ОЗУ.
    Решение. Удалил проверку контрольной суммы целого большого файла, достаточно проверки 
    контрольной суммы фрагментов.
    Выявил особенность приведения типов - Приводимое выражение должно быть в скобках!
    Иначе сначала fullFileSize(long) приводится к int, что приводит к ошибке при больших чем int значениях.
    int totalEntireFragsNumber = (int) (fullFileSize / FileFragmentMessage.CONST_FRAG_SIZE);
28. System. Разобраться почему при download файла 16ГБ на другой ПК процесс нарезки и отправки 
    фрагментов просто остановился при срабатывании screensaver на сервере(а может на клиенте?).
29. NOT DONE! Maven. Разобраться почему не собирается jar without dependencies при сборке проекта. 
    Пробовал добавить блок <plugin> в <build><plugins> файла [server]pom.xml, 
    но в папке [server]target/classes(там же где и jar) нет классов модуля [shared].
30. GUI. Добавить отдельное модальное окно для отображения процесса upload/download.
    В окне процесс должен отражаться на слайсере и в виде процентов.
31. CANCELED. GUI. В метод isNewPathnameCorrect() класса ChangeRootController добавить проверку правильности 
    введенной строки абсолютного пути к новой директории(через Path).
32. CANCELED. GUI. В метод isNewItemNameCorrect() класса RenameController добавить проверки правильности 
    введенное новое имя объекта списка(файла или папки) для разных файловых систем.
33. DONE. GUI. В метод menuItemDelete() класса GUIController добавить диалоговое окно - 
    предупреждение-подтверждение на удаление.
34. File. Заменить все io на nio и Stream API. 
    Иначе могут быть проблемы с внешними носителями.
    Но и сейчас работает с таким клиентстким путем: \\IOURI-X555L\Movies(сеть) и с usb и DVD.
    Как в проекте D:\GeekBrains\_MyJavaProjectsAndSamples\JavaFX\simple_file_manager.
35. GUI. Все предупреждения и ошибки реализовать через Alert.
    Как в проекте D:\GeekBrains\_MyJavaProjectsAndSamples\JavaFX\simple_file_manager.  
36. DONE. GUI. Размер файла.
    a. Добавить в Item: размер файла. 
    b. Добавить его наполнение при создании Item.
37. GUI. Добавить отображение размера файла в листвью.
    Как в проекте D:\GeekBrains\_MyJavaProjectsAndSamples\JavaFX\simple_file_manager.
38. JDBC. В [server]модуле в файл readme.txt добавить информацию о подключении к БД.
    Добавить в config.json поля для подключения к MySQL(логин, пароль и т.п.), чтобы 
    развернуть сервер на стороннем ПК.
39. DONE. GUI.[client]module. При повторном запуске клиента.
    Устранить исключение, если указаный в client.cfg абсолютный путь не существует.
    Например, если указать usb-носитель и вынуть его. 
    Можно установить директорию клиента по умолчанию.
40. GUI. В clientDirLabel и serverDirLabel исправить отображение длинного пути к текущей папке.
    Нужно показывать последние подпапки, которые входят в поле.
    Или заменить на TextField? Тогда получается что-то похожее.
    См. в проекте D:\GeekBrains\_MyJavaProjectsAndSamples\JavaFX\simple_file_manager.
41. ?GUI.[client]module. В clientDirLabel добавить отображать абсолютный путь, 
    если он задан в client.cfg?
    Но тогда нужно менять логику и позволять выйти выше корневой директории.
42. GUI. Добавить .css для MainClient и других окон.
    И перенести в него все стили из .fxml.
43. GUI. Исправить открытие окна "About", чтобы главное окно не закрывалось.
    Попробовать использовать Alert fileView = new Alert(Alert.AlertType.INFORMATION, contentText.toString());
    fileView.showAndWait();
44. 