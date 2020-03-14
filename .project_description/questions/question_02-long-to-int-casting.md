@filename question_02-long-to-int-casting.
@since 14.03.2020
@author Yuriy Litvinenko
@project(s):
 D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage 
@class [shared]GUIController, метод updateListView().compare()
 D:\GeekBrains\_MyJavaProjectsAndSamples\JavaFX\simple_file_manager
@class Controller, метод goToPath().compare()

Вопрос. Какой из вариантов ниже правильнее? Оба рабочие.
#Вариант 1. Сравнение long и преобразование к int с использованием (int).
(int) (o1.getLength() - o2.getLength());

#Вариант 2. Сравнение long и преобразование к int с использованием .intValue().
new Long(o1.getLength() - o2.getLength()).intValue();