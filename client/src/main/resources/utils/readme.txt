ATTENTION!!!
Don't change any DEFAULT properties.
Don't insert manually into <Root_absolute></Root_absolute>
 - change it in GUI the menu item "Change Client Root" only.

You could change some properties in the file "client.cfg" as far as:
<IP_ADDR></IP_ADDR>;
<PORT></PORT>;
To set new properties, just insert your data between ">" and "<".
For instance:
<IP_ADDR>192.168.1.103</IP_ADDR>

A default directory is the "client_storage" in the folder
where the jar-archive has been executed.



Also "Root_absolute" should be used only with "\\" instead of "\"!
for instance:
D:\\GeekBrains\\20191130_GB-Разработка_сетевого_хранилища_на_Java\\cloudstorage\\server\\target