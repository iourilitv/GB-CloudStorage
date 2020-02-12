You could change some properties in the file "client.cfg" as far as:
"port_custom":0(default);
"root_absolute":""(default).
Users files are in the "storage"(default).

ATTENTION!!!
Don't change any DEFAULT properties.
Also "root_absolute" should be used only with "\\" instead of "\"!
for instance:
//D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target>java -jar CloudStorageServer_LYS-jar-with-dependencies.jar
//Properties.setConfiguration() - fromJsonString: {"PORT_DEFAULT":8189,"ROOT_DEFAULT":"storage","port_custom":9999,"root_absolute":"D:\\GeekBrains\\20191130_GB-Разработка_сетевого_хранилища_на_Java\\cloudstorage\\server\\target"}
//Properties.readConfiguration() - properties.toString()Properties{PORT_DEFAULT=8189, ROOT_DEFAULT='storage', port_custom=9999, root_absolute='D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target'}
//CloudStorageClient() - PORT: 9999
//CloudStorageClient() - STORAGE_ROOT_PATH: D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target