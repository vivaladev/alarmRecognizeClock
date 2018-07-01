package vivaladev.com.dirtyclocky.recognizeProcessing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SoundRecognize {

    private static byte[] dbFileByte;
    private static byte[] newRecByte;

    private static int errorFullCoincidence = 20; //ошибка полного сравнения
    private static int errorComparisonOfHalves = 20; // ошибка сравнение половин
    private static int errorComprassionOfTree = 20; // ошибка сравнение по 3
    private static int errorComprassionOfFiveGroup = 20; // ошибка сравнение по 4
    private static int errorSearchCoincidenceByte = 20; // ошибка поиска похожих
    private static int sizeCoincidenceByteGroup = 20; // ошибка поиска похожих


    public static void setErrorConstant(int erFull, int erHal, int erTree, int erFive, int erGroup) {
        errorFullCoincidence = erFull; //ошибка полного сравнения
        errorComparisonOfHalves = erHal; // ошибка сравнение половин
        errorComprassionOfTree = erTree; // ошибка сравнение по 3
        errorComprassionOfFiveGroup = erFive; // ошибка сравнение по 4
        errorSearchCoincidenceByte = erGroup; // ошибка поиска похожих
    }

    /**
     * The method allows you to compare 2 files
     *
     * @return true if recording equals, false if not
     */
    public static boolean recognizeSound(File dbFile, File newRec) {
        dbFileByte = convertFileToByteArray(dbFile);
        newRecByte = convertFileToByteArray(newRec);

        //TODO: распознавать тут
        return true;
    }

    private static byte[] convertFileToByteArray(File f) {
        byte[] byteArray = null;
        try {
            InputStream inputStream = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 8];
            int bytesRead;

            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    private boolean firstCompareOnFullCoincidence() {
        int countError = 0;
        if (dbFileByte.length == newRecByte.length) {   //равная длина, просто пошаговое сравнение байтов
            for (int i = 0; i < dbFileByte.length; i++) {
                if (dbFileByte[i] != newRecByte[i]) {
                    countError++;
                    if (countError >= errorFullCoincidence) {
                        return false; // байты различны более errorFullCoincidence, вернуть false
                    }
                }
            }
        } else {                                         //разная длина, просто пошаговое сравнение по меньшему массиву байт
            if (dbFileByte.length > newRecByte.length) {
                //исходная запись дольше, чекаем все байты из меньшей
                for (int i = 0; i < newRecByte.length; i++) {
                    if (newRecByte[i] != dbFileByte[i]) {
                        countError++;
                        if (countError >= errorFullCoincidence) {
                            return false; // байты различны более errorFullCoincidence, вернуть false
                        }
                    }
                }
            } else {
                //исходная запись короче, чекаем все байты из бд
                for (int i = 0; i < dbFileByte.length; i++) {
                    if (dbFileByte[i] != newRecByte[i]) {
                        countError++;
                        if (countError >= errorFullCoincidence) {
                            return false; // байты различны более errorFullCoincidence, вернуть false
                        }
                    }
                }
            }
        }
        return true;
    }

    private List<Byte> getByteList(byte[] array) {
        List<Byte> res = new ArrayList<>();
        for (byte b : array) {
            res.add(b);
        }
        return res;
    }

    private boolean searchCoincedenceByte() {
        List<Byte> dbFileList = getByteList(dbFileByte);
        List<Byte> newRecList = getByteList(newRecByte);
        int errorCount = 0;
        //получили байт листы

        if (dbFileList.size() > newRecList.size()) {
            //исходная запись дольше, чекаем все байты из новой записи
            for (int i = 0; i < newRecList.size(); i++) {
                byte item = newRecList.get(i); // берем байт из активного листа
                if (dbFileList.contains(item)) { // чекаем этот байт в другом листе
                    boolean compareRes = compareTwoGroupOfByte(newRecList.subList(i, newRecList.size()),
                            dbFileList.subList(dbFileList.indexOf(item), dbFileList.size())); // вызываем проверку группы одинаковых байтов
                    if (!compareRes) {
                        errorCount++;
                        if (errorCount >= errorSearchCoincidenceByte) {
                            return false;
                        }
                    }
                }
            }
        } else {
            //исходная запись короче, чекаем все байты из бд
            for (int i = 0; i < dbFileList.size(); i++) {
                byte item = dbFileList.get(i); // берем байт из активного листа
                if (newRecList.contains(item)) { // чекаем этот байт в другом листе
                    boolean compareRes = compareTwoGroupOfByte(dbFileList.subList(i, dbFileList.size()),
                            newRecList.subList(newRecList.indexOf(item), newRecList.size())); // вызываем проверку группы одинаковых байтов
                    if (!compareRes) {
                        errorCount++;
                        if (errorCount >= errorSearchCoincidenceByte) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean compareTwoGroupOfByte(List<Byte> mainList, List<Byte> list2) {
        if(!(mainList.size() >= sizeCoincidenceByteGroup && list2.size() >= sizeCoincidenceByteGroup)){
            return false;
        }
        for (int i = 0; i < sizeCoincidenceByteGroup; i++) {
            if(!mainList.get(i).equals(list2.get(i))){
                return false;
            }
        }
        return true;
    }
}
