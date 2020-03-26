# Updating after the project acceptance. 
GeekBrains. Курсовая работа: Итоговый проект Cloud Storage.
@Litvinenko Yuriy

Скриншшот GUI https://yadi.sk/i/AwTDqr84PshnQA
Видео https://youtu.be/ku0okBn_zGM

## 2.Issues and developments.
1. GUI. Проверить и исправить вывод сообщений в метку уведомлений. Есть баги.
2. DONE. GUI. ListVew. 
	- CANCELED. b. Проверить и доработать контекстное меню. 
	Добавить clientListView.setContextMenu(contextMenu) в блок if, где выбирается показывать upload/download.
    - c. Добавить проваливание в папку по двойному клику.
4. Netty. Добавить проверку активности соединения на стороне клиента, чтобы не вылетало исключение при отваливании сервера.
5. Netty. При передаче файлов сериализовать только объект сообщения, а байты передавать средствами netty следом после команды.
21. System. Добавить логирование в файл и реорганизовать логирование в консоль.
26. File. Добавить upload/download папок с объектами. Можно папку предварительно архивировать 
    и отправлять как обычный файл.
28. System. Разобраться почему при download файла 16ГБ на другой ПК процесс нарезки и отправки 
    фрагментов просто остановился при срабатывании screensaver на сервере(а может на клиенте?).
29. NOT DONE! Maven. Разобраться почему не собирается jar without dependencies при сборке проекта. 
    Пробовал добавить блок <plugin> в <build><plugins> файла [server]pom.xml, 
    но в папке [server]target/classes(там же где и jar) нет классов модуля [shared].
30. GUI. Добавить отдельное модальное окно для отображения процесса upload/download.
    В окне процесс должен отражаться на слайсере и в виде процентов.
34. File. Заменить все io на nio и Stream API. 
    Иначе могут быть проблемы с внешними носителями.
    Но и сейчас работает с таким клиентстким путем: \\IOURI-X555L\Movies(сеть) и с usb и DVD.
    Как в проекте D:\GeekBrains\_MyJavaProjectsAndSamples\JavaFX\simple_file_manager.
35. GUI. Все предупреждения и ошибки реализовать через Alert.
    Как в проекте D:\GeekBrains\_MyJavaProjectsAndSamples\JavaFX\simple_file_manager.  
37. GUI. Добавить отображение размера файла в листвью.
    Как в проекте D:\GeekBrains\_MyJavaProjectsAndSamples\JavaFX\simple_file_manager.
38. JDBC. В [server]модуле в файл readme.txt добавить информацию о подключении к БД.
    Добавить в config.json поля для подключения к MySQL(логин, пароль и т.п.), чтобы 
    развернуть сервер на стороннем ПК.
40. CANCELED. GUI. В clientDirLabel и serverDirLabel исправить отображение длинного пути к текущей папке.
    Нужно показывать последние подпапки, которые входят в поле.
    Или заменить на TextField? Тогда получается что-то похожее.
    См. в проекте D:\GeekBrains\_MyJavaProjectsAndSamples\JavaFX\simple_file_manager.
    УЖЕ и так все работает - длинный текст переносится на новую строку.
41. CANCELED. GUI.[client]module. В clientDirLabel добавить отображать абсолютный путь, 
    если он задан в client.cfg?
    Но тогда нужно менять логику и позволять выйти выше корневой директории.
42. GUI. Добавить .css для MainClient и других окон.
    И перенести в него все стили из .fxml.
43. GUI. Исправить открытие окна "About", чтобы главное окно не закрывалось.
    Попробовать использовать Alert fileView = new Alert(Alert.AlertType.INFORMATION, contentText.toString());
    fileView.showAndWait();
44. DONE. GUI. Добавить в контекстное меню пункт "Set as Root" для папок в клиентском листвью. 
    Действие: устанавливает текущую директорию как корневую клиентскую директорию.
45. GUI. Скрывать в листвью системные папки(скрытые).
    Как это реализовать?
46. GUI. Разгрузить класс GUIController(сейчас 1000 строк). 
    Перенести контекстное меню в отдельный класс.
47. 