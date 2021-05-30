import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

public class FileUtils {

    public static void pageToFile(Page page) {

        File file = new File("pages/" + Integer.toString(page.getPageId()) + ".txt");
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            String data = String.join(",", Integer.toString(page.getLsn()), page.getData());
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            System.out.println("Error while handling files:\n" + e);
        }
    }

    public static void writeLogEntry(int lsn, int taId, int pageId, String data) {
        File file = new File("logs/" + "log.txt");
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();

            FileWriter writer = new FileWriter(file, true);
            String entry = String.join(",", Integer.toString(lsn), Integer.toString(taId), Integer.toString(pageId), data + "\n");
            writer.write(entry);
            writer.close();
        } catch (IOException e) {
            System.out.println("Error while handling files:\n" + e);
        }
    }

    public static void writeLogEntry(int lsn, int taId) {
        File file = new File("logs/" + "log.txt");
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();

            FileWriter writer = new FileWriter(file, true);
            String entry = String.join(",", Integer.toString(lsn), Integer.toString(taId), "EOT\n");
            writer.write(entry);
            writer.close();
        } catch (IOException e) {
            System.out.println("Error while handling files:\n" + e);
        }
    }

    public static Page readPage(int pageId) {
        File file = new File("pages/" + Integer.toString(pageId) + ".txt");
        if (file.exists()) {
            String data = "";
            try {
                Scanner reader = new Scanner(file);
                // we know our files are one line so we don't need proper handling here
                data = reader.nextLine();
                reader.close();
            } catch (FileNotFoundException e) {

            }
            // format: <LSN>,<DATA>
            String[] arr = data.split(",");
            return new Page(pageId, Integer.parseInt(arr[0]), arr[1]);
        }
        else {
            // if the page is missing, we'll return an empty page
            // the recovery will set the values correctly
            return new Page(pageId, 0, "");
        }
    }

    public static LinkedList<LogRecord> readLog() {
        LinkedList<LogRecord> records = new LinkedList<>();
        File file = new File("logs/" + "log.txt");
        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String logString = reader.nextLine();
                // format: <LSN>,<TAID>,<EOT> or  <LSN>,<TAID>,<PAGEID>,<DATA>
                String[] arr = logString.split(",");
                LogRecord record = new LogRecord(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
                if (arr.length == 4) {
                    record.setPageId(Integer.parseInt(arr[2]));
                    record.setData(arr[3]);
                } else {
                    record.setAsCommit();
                }

                records.add(record);
            }
            reader.close();
        } catch (FileNotFoundException e) {

        }
        return records;
    }
}
