package vivaladev.com.dirtyclocky.recognizeProcessing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SoundRecognize {

    private static byte[] dbFileByte;
    private static byte[] newRecByte;

    private int errorFullCoincidence = 20; //ошибка полного сравнения
    private int errorComparisonOfHalves = 20; // ошибка сравнение половин
    private int errorComprassionOfTree = 20; // ошибка сравнение по 3
    private int errorComprassionOfFiveGroup = 20; // ошибка сравнение по 4
    private int errorSearchCoincidenceByte = 20; // ошибка поиска похожих


    private static void initErrorConstant(){

    }
    /**
     * The method allows you to compare 2 files
     *
     * @return true if recording equals, false if not
     */
    public static boolean recognizeSound(File dbFile, File newRec) {
        dbFileByte = convertFileToByteArray(dbFile);
        newRecByte = convertFileToByteArray(newRec);
        initErrorConstant();


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
            return true; // байты различны менее errorFullCoincidence
        } else {                                         //разная длина, просто пошаговое сравнение по меньшему массиву байт
            if (dbFileByte.length > newRecByte.length) {
                //исходная запись дольше, чекаем все байты из меньшей
                for (int i = 0; i < newRecByte.length; i++) {
                    if(newRecByte[i] != dbFileByte[i]){
                        countError++;
                        if (countError >= errorFullCoincidence) {
                            return false; // байты различны более errorFullCoincidence, вернуть false
                        }
                    }
                }
            } else {
                //исходная запись короче, чекаем все байты из бд
                for (int i = 0; i < dbFileByte.length; i++) {
                    if(dbFileByte[i] != newRecByte[i]){
                        countError++;
                        if (countError >= errorFullCoincidence) {
                            return false; // байты различны более errorFullCoincidence, вернуть false
                        }
                    }
                }
                return true;
            }
        }
        return true;
    }
}
