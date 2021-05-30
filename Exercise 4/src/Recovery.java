import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

public class Recovery {
    public static void recover() {
        System.out.println("Starting Recovery");
        PersistenceManager pm = PersistenceManager.getInstance();

        LinkedList<LogRecord> logRecords = FileUtils.readLog();

        Hashtable<Integer, Page> pagesToRecover = new Hashtable<>();

        // we don't need a synchronized counter here since we don't have multi-threading
        int lastLsn = logRecords.size();

        HashSet<Integer> winnerTransactions = new HashSet<>();
        logRecords.forEach((record) -> {
            if (record.isCommit()) {
                winnerTransactions.add(record.getTaId());
            }
        });

        // we have to use old for here to be able to change lastLsn
        for (LogRecord record : logRecords) {
            if (!record.isCommit() && winnerTransactions.contains(record.getTaId())) {

                int pageId = record.getPageId();
                Page page = FileUtils.readPage(pageId);

                if (record.getLsn() > page.getLsn()) {
                    page.setData(record.getData());
                    pagesToRecover.put(pageId, page);
                }
            }
        }

        // go through pages and write to file
        Iterator<Page> it = pagesToRecover.values().iterator();
        while (it.hasNext()) {
            Page page = it.next();

            lastLsn++;
            page.setLsn(lastLsn);
            FileUtils.pageToFile(page);
        }
    }
}
