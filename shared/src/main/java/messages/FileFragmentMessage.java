package messages;

import utils.Item;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.file.Paths;

/**
 * A Class of message objects for a files of a size more than
 * CONST_FRAG_SIZE in the FileFragmentMessage class.
 */
public class FileFragmentMessage extends AbstractMessage {
    //инициируем константу размера фрагментов файла в байтах
    public static final int CONST_FRAG_SIZE = 1024 * 1024 * 10;
    //принимаем объект родительской директории элемента в сетевом хранилище
    private Item toDirectoryItem;
    //принимаем объект элемента
    private Item item;
    //объявляем переменную размера файла(в байтах)
    private long fullFileSize;
    //объявляем переменную размера фрагмента файла(в байтах)
    private int fileFragmentSize;
    //инициируем байтовый массив с данными из файла
    private byte[] data;
    //объявляем переменную текущего фрагмента файла
    private int currentFragNumber;
    //объявляем переменную общего количества фрагментов файла
    private int totalFragsNumber;
    //объявляем переменную имени временной папки для хранения фрагментов в директории назначения
    private String toTempDirName;
    //объявляем переменную имени фрагмента файла
    private String fragName;
    //объявляем переменную контрольной суммы фрагмента файла
    private String fragChecksum;

    //this constructor is for upload or download FileByFrags operation
    public FileFragmentMessage(Item toDirectoryItem, Item item, long fullFileSize,
                               int currentFragNumber, int totalFragsNumber,
                               int fileFragmentSize, byte[] data) {
        this.toDirectoryItem = toDirectoryItem;
        this.item = item;
        this.fullFileSize = fullFileSize;
        this.currentFragNumber = currentFragNumber;
        this.totalFragsNumber = totalFragsNumber;
        this.fileFragmentSize = fileFragmentSize;
        this.data = data;
        //составляем имя фрагмента файла(для сохранения в директории)
        fragName = constructFileFragName(item.getItemName(), currentFragNumber, totalFragsNumber);
        //Пример: toUpload.txt$frg0001-1024 ... toUpload.txt$frg1024-1024
        //составляем имя временной папки в директории назначения для хранения фрагментов файла
        toTempDirName = constructTempDirectoryName(item.getItemName(), fullFileSize);
        //toUpload.txt$temp-51811813
    }

    /**
     * Метод собирает и возвращает строку имени текущего файла-фрагмента.
     * @param itemName - имя объекта(пока только файла)
     * @param currentFragNumber - номер текущего фрагмента
     * @param totalFragsNumber - общее количество фрагментров
     * @return - строку имени файла-фрагмента
     */
    private String constructFileFragName(String itemName, int currentFragNumber, int totalFragsNumber) {
        //высчитываем количество нолей к номеру фрагмента, чтобы привести его к разрядности
        // общего количества фрагментов
        //иначе файлы-фрагменты располагаются в неверном порядке(1, 10, .., 19, 2, )
        int count = String.valueOf(totalFragsNumber).length() -
                String.valueOf(currentFragNumber).length();
        //иницииуем объект временной переменной для конструирования строки имени файла-фрагмента
        StringBuilder sb = new StringBuilder(itemName).append("$frg");
        //добавляем необходимое количество нолей в имени перед номером фрагмента
        for (int i = 0; i < count; i++) {
            sb.append("0");
        }
        //добавляем хвост имени файла-фрагмента
        sb.append(currentFragNumber).append("-").append(totalFragsNumber);
        return Paths.get(sb.toString()).toString();
        //Пример: toUpload.txt$frg0001-1024 ... toUpload.txt$frg1024-1024
    }

    /**
     * Метод конструирует имя временной папки для соъранения файлов-фрагментов.
     * @param itemName - имя объекта(пока только файла)
     * @param fullFileSize - размер файла
     * @return - имя временной папки для соъранения файлов-фрагментов
     */
    private String constructTempDirectoryName(String itemName, long fullFileSize) {
        return itemName + "$temp-" + fullFileSize;
        //toUpload.txt$temp-51811813
    }

    /**
     * Метод чтения данных из определенного места файла в файтовый массив.
     * @param realItemPathname - строка реального имя к файлу источнику
     * @param startByte - индекс байта начала считывания байтов из файла источника
     * @throws IOException - исключение
     */
    public void readFileDataToFragment(String realItemPathname, long startByte) throws IOException {
        // открываем файл для чтения
        RandomAccessFile raf = new RandomAccessFile(realItemPathname, "r");
        //инициируем объект входного буферезированного потока с преобразованием raf в поток
        BufferedInputStream bis = new BufferedInputStream(Channels.newInputStream(raf.getChannel()));
        // ставим указатель на нужный вам символ
        raf.seek(startByte);
        //вычитываем данные из файла
        //считывает байты данных длиной до b.length из этого файла в массив байтов.
        int result = bis.read(data);
        //выгружаем потоки из памяти
        raf.close();
        bis.close();
    }

    public Item getToDirectoryItem() {
        return toDirectoryItem;
    }

    public Item getItem() {
        return item;
    }

    public long getFullFileSize() {
        return fullFileSize;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getFileFragmentSize() {
        return fileFragmentSize;
    }

    public boolean isFinalFileFragment(){
        return currentFragNumber == totalFragsNumber;
    }

    public int getTotalFragsNumber() {
        return totalFragsNumber;
    }

    public int getCurrentFragNumber() {
        return currentFragNumber;
    }

    public String getToTempDirName() {
        return toTempDirName;
    }

    public String getFragName() {
        return fragName;
    }

    public String getFragChecksum() {
        return fragChecksum;
    }

    public void setFragChecksum(String fragChecksum) {
        this.fragChecksum = fragChecksum;
    }

}