package org.keremulutas.mockeyjockey.utils;

import com.google.gson.*;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class Utils {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final GsonBuilder gsonBuilder = new GsonBuilder()
        .serializeNulls()
        .setLenient()
        .disableHtmlEscaping()
        .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeSerializer());
    public static final Gson gson = gsonBuilder.create();

    public static double formatDouble_old(double value, int decimalDigits) {
        double multiplier = Math.pow(10, decimalDigits);
        return ((int) (value * multiplier)) / multiplier;
    }

    public static double formatDouble(double value, int decimalDigits) {
        return Double.valueOf(String.format("%." + decimalDigits + "f", value));
    }

    public static String formatDuration(Duration duration) {
        return duration.toString()
            .substring(2)
            .replaceAll("(\\d[HMS])(?!$)", "$1 ")
            .toLowerCase();
    }

    public static String convertStreamToString(java.io.InputStream is, String charsetName) {
        java.util.Scanner s = new java.util.Scanner(is, charsetName).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static void writeJson(String file, List<?> list) {
        try (
            FileWriter writer = new FileWriter(file);
        ) {
            for (int i = 0, j = list.size(); i < j; i++) {
                Object next = list.get(i);
                String jsonStr = Utils.gson.toJson(next);

                writer.write(jsonStr);
                writer.write(Constants.newline);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // try (
        //     BufferedOutputStream bos = new BufferedOutputStream(
        //         new FileOutputStream(outputFile)
        //     );
        // ) {
        //     for (int i = 0, j = objList.size(); i < j; i++) {
        //         Object next = objList.get(i);
        //         String jsonStr = Utils.gson.toJson(next);
        //
        //         bos.write(jsonStr.getBytes());
        //         bos.write(newline.getBytes());
        //         bos.flush();
        //     }
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }

    public static void writeJsonAndGzip(String file, String gzipFile, List<?> list) {
        try (
            FileWriter writer = new FileWriter(file);

            FileOutputStream outputStream = new FileOutputStream(gzipFile);
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
        ) {
            for (int i = 0, j = list.size(); i < j; i++) {
                Object next = list.get(i);
                String jsonStr = Utils.gson.toJson(next);

                writer.write(jsonStr);
                writer.write(Constants.newline);
                writer.flush();

                gzipOutputStream.write(jsonStr.getBytes());
                gzipOutputStream.write(Constants.newline.getBytes());
                gzipOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ZonedDateTimeSerializer implements JsonSerializer<ZonedDateTime> {

        public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toInstant().toEpochMilli());
        }
    }

}
