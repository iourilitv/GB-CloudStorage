# Урок 8. Домашнее задание. 
GeekBrains. Курсовая работа: Итоговый проект Cloud Storage.
@Litvinenko Yuriy

Скриншшот GUI https://yadi.sk/i/AwTDqr84PshnQA
Видео https://youtu.be/ku0okBn_zGM

## Что сделано дополнительно к lesson7-hw после Code Review..
###Update операций с файлами.
1.	DONE. See the branch 1-after-CodeReview-fragmentCutting-to-FileUtils. 
Перенести в [shared]FileUtils два одинаковых метода [client]uploadFileByFrags и [server] downloadFileByFrags.
Теперь это возможно, т.к. в парметрах можно передать соответствующий ctx.

2.	REJECTED. See the branch 1-after-CodeReview-fragmentCutting-to-FileUtils.
В методе saveFileFragment() в строке 
Files.write(realToFragPath, fileFragMsg.getData(), StandardOpenOption.CREATE); 
опцию CREATE заменить на CREATE_NEW.
Отклонено, т.к. по логике моего приложения фрагмент файла нужно перезаписывать, чтобы не оставались старые файлы-фрагменты во временной папке(при разрыве связи например).
CREATE - Create a new file if it does not exist.
CREATE_NEW - Create a new file, failing if the file already exists.
Но на всяккий случай переделал создание временной директории. 
DONE Добавил, чтобы это происходило только на первом фрагменте. А то у меня на каждом фрагменте создавался файловый объект для временной директории для того чтобы только проверить нет ли временной папки.
И добавил удаление временной папки, если она есть, со всеми файлами внутри и создание новой.

3.	DONE. See the branch 1-after-CodeReview-fragmentCutting-to-FileUtils.
В методе compileFileFragments() в классе [shared]FileUtils переписать метод copyData(source, destination); - вместо создания байтбуфера применить инструмент java.nio.channels.filechannel transferto(). 
Результат: при загрузке и считывании файла 300Мб(по 10Мб) заметных улучшений использования памяти или ускорения процесса не зафиксировано.

###Update операций с GUI.
4.	DONE. See the branch 2-after-CodeReview-GUI-updating.
В классах [client]GUIController и  [client] CommandMessageManager исправлена проблема с не выводом стартового сообщения в noticeLabel в GUI.

5.	DONE. See the branch 2-after-CodeReview-GUI-updating.
В методе takeNewNameWindow() в классе [client]GUIController исправлена проблема с принудительным закрыванием NewNameWindow.

6.	DONE. See the branch 2-after-CodeReview-GUI-updating.
В методе cutAndSendFileByFrags() в классе [shared]FileUtils перенес процесс нарезки и отправки большого файла в отдельный поток. Теперь интерфейс не подвисает в момент upload.
Но это все равно не исправило ситуацию с ошибкой файла при загрузке. Причем при скачивании или при загрузке небольших файлов такого не наблюдалось ни разу. И также до добавления GUI загрузка также проходила без единой ошибки, а теперь удачно загружается 1 раз из многих(так и не понял, что на это влияет).

7.	DONE. See the branch 2-after-CodeReview-GUI-updating.
В классах [client]GUIController и  [client] CommandMessageManager добавлен вывод оповещения о процессах загрузки и скачивания и очистки noticeLabel в GUI.
После этого и при скачивании большого файла появилась такая же проблема целостности итогового файла, как это наблюдалось только при загрузке. Код живет своей жизнью...

8.	DONE. See the branch 2-after-CodeReview-GUI-updating.
В файле [client]MainClient.fxml и в классах [client]GUIController и [client] CommandMessageManager реализована логика изменения GUI в режиме авторизован, добавлены элементы для подключения к серверу и запроса авторизации.
Исправил ошибочную отправку запроса на авторизацию при закрытии окна авторизации по крестику выхода.

9.	DONE. See the branch 2-after-CodeReview-GUI-updating.
В файле [client]Login.fxml и в классах [client]GUIController, LoginController, [client]CloudStorageClient добавил поля регистрации в authWindow и даработал логику взаимодействия в общем. И добавил в класс[client]CloudStorageClient черновик метода demandDisconnecting(). Осталось проработать взаимодействие с сервером и БД и доработать GUI окончательно.

10.	DONE. See the branch 2-after-CodeReview-GUI-updating.
В файле [client]MainClient.fxml и в классах [client]GUIController, CloudStorageClient и новом AboutText реализовано меню с пунктами: About и Disconnect. 
Пункт About открывает в новой сцене информацию о приложении. 
Пункт Disconnect – пока только сделана заглушка.
Также добавил блокировку/разблокировку кнопок сетевого хранилища в зависимости от режима авторизации.

###Добавление регистрации в БД.
11.	DONE. See the branch 3-after-CodeReview-JDBC-adding.
В файле [client]Login.fxml и в классах [client] LoginController, GUIController, CloudStorageClient добавил/изменил методы для отображения процессов регистрации и авторизации. 
Также добавил новые команды в класс [shared]Commands и их обработку классах [client]/[server] [client] CommandMessageManager. Переорганизовал и переименовал класс [server] UsersAuthController - добавил в нем коммуникацию с БД sqlite и черновики методов взаимодействия с БД.
Перенес Map<ChannelHandlerContext, String> authorizedUsers из класса [server]CloudStorageServer в класс [server]UsersAuthController.

12.	DONE. See the branch 3-after-CodeReview-JDBC-adding.
В классы [client] GUIController, CloudStorageClient, CommandMessageManager, 
[server] CloudStorageServer, CommandMessageManager, UsersAuthController, AuthGateway и
[shared] DirectoryMessage, Commands, FileUtils добавлены методы для обработки нажатия кнопки “NewFolder”, отправки запроса и получения ответа, новые команды и конструктор. 
Также добавлено создание новой корневой директории нового пользователя в сетевом хранилище и методы для регистрации нового пользователя.

13.	NOT DONE. See the branch 3-after-CodeReview-JDBC-adding.
Вынес из класса [server]UsersAuthController в отдельный класс [server] MySQLConnect все что связано с подключением к БД. Так и не удалось подключить БД корректно, ни SQLite, ни MySQL. Оба подключаются из в IDEA data source, но в коде работать не хотят. 
SQLite на простой запрос, типа String sql = "SELECT * FROM main";, требует сконфигурировать data source, а на запрос, типа String sql = String.format("SELECT * FROM '%s'", "main");, ничего не находит. Это все странно, потому, что я взял это блок кода и БД из моего рабочего проекта “network chat” и там все работало прекрасно.
MySQL при любой попытке обращения к БД выдает исключение java.sql.SQLNonTransientConnectionException: CLIENT_PLUGIN_AUTH is required. 
Потратил на безуспешные попытки 12 рабочих часов!
пришлось сделать имитацию работы с БД на HashMap в классе [server] UsersDB, чтобы сдать проект.
Оставил копию класса [server]UsersAuthController (UsersAuthController.java.txt) с кодом подключения к MySQLConnect.


###Финальные исправления и доработки.
14.	DONE. See the branch 4-after-CodeReview-final-fixing.
В файл [client]Login.fxml классы [client]GUIController, LoginController, CommandMessageManager, 
[server]CloudStorageServer, UsersAuthController, UserDB и
[shared]FileUtils для процесса авторизации и регистрации:
- добавлены/исправлены методы в режиме имитации работы с БД; 
- добавлены дополнительные проверки;
- обновлена логика работы GUI.

15. DONE. See the branch 4-after-CodeReview-final-fixing.
    В файл [client]MainClient.fxml классы [client]GUIController, CloudStorageClient, CommandMessageManager, 
    [server]UsersAuthController, CommandMessageManager, AuthGateway и
    [shared]Commands добавлена реализация отключения клиента и подключения вновь
    без исключений, добавлены новые команды для этого и в HashMap authorizedUsers
    ключи и значения поменяны местами - теперь логин - это ключ. 
    Все работает корректно.

## Вопросы
1. Вопрос по поводу применения switch/case.
   Проблема switch/case в том, что он работает только с int и не поддерживает eNum. Из-за чего неудобно, т.к. в логах видишь только номера команд, а не их имена.
   Может лучше все заменить на if и вынести команды в eNum, а не в виде класса с константами, как у меня сейчас?
   А какой подход сейчас в программировании принят?
2. Как правильно подключать БД(MySQL, например) к InteliJ IDEA и 
как реализовать в коде подключение к ней.
См.п.13.
Вообще тема с настройкой откружения Java не такая простая. 
Постоянно какие-то проблемы с Maven(это вроде уже освоил), 
подключением к БД и т.п.
К сожалению нет курса, на котором бы рассматривалась настройка окружения и рабочих инструментов 
в комплекте, а не отдельно отладчик InteliJ IDEA, система контроля версий GIT, 
база данных(популярная и универсальная для всего года обучения) и т.п.
3. Почему-то загрузка и скачивание больших файлов пофрагментно работает не стабильно.
Фрагменты сохраняются и собираются в целый файл без ошибки(суюя по размеру).
Все началось как только я подключил GUI. До этого работало идеально. 
Причем, сначала я добавил GUI на upload - и сразу заметил проблему. 
Download, при этом, работал идеально. Но после добавления вывода в метку GUI информации о загрузке
сразу стало заметна проблема и обратно после удаления, вроде, не исправилась.
Не хватило времени разобраться, но по коду вроде все впорядке. Я даже в метод нарезки на фрагменты 
добавил отдельный поток и даже контекстное меню перестало зависать на upload, но проблему целостности 
данных это не решило.


## Issues and developments.
1. GUI. Проверить и исправить вывод сообщений в метку уведомлений. Есть баги.
2. GUI. ListVew. 
	- Исправить сортировку - вверху должны быть папки, а ниже файлы. 
	- Проверить и доработать контекстное меню. Добавить clientListView.setContextMenu(contextMenu) в блок if, где выбирается показывать 		upload/download.
3. Netty. Разобраться как сохраняется ctx в HashMap athorizedUser - почему в его объекте указан AuthMessage/CommandMessage. Из зачего эти объекты распознаются как не одинаковые, хотя ID у них одинаковые. Из-за этого пришлось использовать логин в качестве ключа. Но тогда дольше искать соединение, если зарегистрированный клиент разрывает соединение нештатно, т.к. в этот момент AuthGateway известно только ctx. 
Или я путаю?
4. Netty. Добавить проверку активности соединения на стороне клиента, чтобы не вылетало исключение при отваливании сервера.
5. Netty. При передаче файлов сериализовать только объект сообщения, а байты передавать средствами netty следом после команды.
6. FileFrags. Исправить передачу и сшивание больших файлов. Возможно проблему целостности итогового файла(Вопрос 1) можно решить так:
	- создавать новый массив для каждого фрагмента;
	- следующий фрагмент запускать только после получения подтвержденния получения и сохранения текущего фрагмента.
7. File. Добавить проверку контрольной суммы при отправке байтового массива, целого файла и сравнивать его с сохраненным и собранным.
8. DB. Добавить базу данных в процесс регистрации пользователей.
	- скрывать пароли
	- применить хэширование для ускорения поиска?
9. DONE. Заменить класс команд на enum. По примеру:
    public enum Data {
        A, B, C;
    }
    public static void main(String[] args) {
        test(Data.A);
        test(Data.B);
        test(Data.C);
    }
    public static void test(Data e_num){
        switch (e_num) {
            case A:
                System.out.println(e_num);//A
                break;
            case B:
                System.out.println(e_num);//B
                break;
            case C:
                System.out.println(e_num);//C
                break;
        }
    }
