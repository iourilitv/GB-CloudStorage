# Updating after the project acceptance. 
GeekBrains. Курсовая работа: Итоговый проект Cloud Storage.
@Litvinenko Yuriy

Скриншшот GUI https://yadi.sk/i/AwTDqr84PshnQA
Видео https://youtu.be/ku0okBn_zGM

## 2.Issues and developments.
1. Перенесено в pr-upd3-after-acceptance.md.
2. GUI. ListVew. 
	- Done. a. Исправить сортировку - вверху должны быть папки, а ниже файлы. 
	- b. Перенесено в pr-upd3-after-acceptance.md.
	Добавить clientListView.setContextMenu(contextMenu) в блок if, где выбирается показывать upload/download.
    - c. Перенесено в pr-upd3-after-acceptance.md.
4. Перенесено в pr-upd3-after-acceptance.md.
5. Перенесено в pr-upd3-after-acceptance.md.
20. DONE. Maven. Исключить предупреждение о кодировке при сборке проекта. 
    Добавил в <properties> pom.xml[project] строку 
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>.
21. Перенесено в pr-upd3-after-acceptance.md.
22. DONE. GUI. Устранить дублирование открытия модальных окон.
    DONE. Выделить сервис модальных окон в отдельный класс WindowsManager.
23. DONE. GUI. Добавить в пункт меню Disconnect режим Connect. 
    Disconnect показывать в режиме подключен, Connect - отключен.
24. DONE. GUI. Добавить механизм усиленной проверки логина и пароля в 
    формах авторизации и регистрации.
    Можно взять из проекта netChat.
25. DONE. GUI. Добавить механизм проверки email в форме регистрации.
26. Перенесено в pr-upd3-after-acceptance.md.
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
28. Перенесено в pr-upd3-after-acceptance.md.
29. Перенесено в pr-upd3-after-acceptance.md.
30. Перенесено в pr-upd3-after-acceptance.md.
31. CANCELED. GUI. В метод isNewPathnameCorrect() класса ChangeRootController добавить проверку правильности 
    введенной строки абсолютного пути к новой директории(через Path).
32. CANCELED. GUI. В метод isNewItemNameCorrect() класса RenameController добавить проверки правильности 
    введенное новое имя объекта списка(файла или папки) для разных файловых систем.
33. DONE. GUI. В метод menuItemDelete() класса GUIController добавить диалоговое окно - 
    предупреждение-подтверждение на удаление.
34. Перенесено в pr-upd3-after-acceptance.md.
35. Перенесено в pr-upd3-after-acceptance.md.  
36. DONE. GUI. Размер файла.
    a. Добавить в Item: размер файла. 
    b. Добавить его наполнение при создании Item.
37. Перенесено в pr-upd3-after-acceptance.md.
38. Перенесено в pr-upd3-after-acceptance.md.
39. DONE. GUI.[client]module. При повторном запуске клиента.
    Устранить исключение, если указаный в client.cfg абсолютный путь не существует.
    Например, если указать usb-носитель и вынуть его. 
    Можно установить директорию клиента по умолчанию.
40. Перенесено в pr-upd3-after-acceptance.md.
41. Перенесено в pr-upd3-after-acceptance.md.
42. Перенесено в pr-upd3-after-acceptance.md.
43. Перенесено в pr-upd3-after-acceptance.md.