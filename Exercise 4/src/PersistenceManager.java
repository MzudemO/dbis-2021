import java.util.Hashtable;
import java.util.Set;

public class PersistenceManager {
    static final private PersistenceManager singleton;
    static private SynchronizedCounter lsn;
    static private SynchronizedCounter tid;
    static private Hashtable<Integer, Page> buffer;
    static private Hashtable<Integer, Transaction> transactions;


    static {
        try {
            singleton = new PersistenceManager();
            lsn = new SynchronizedCounter();
            tid = new SynchronizedCounter();
            buffer = new Hashtable<>();
            transactions = new Hashtable<>();
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private PersistenceManager() {
    }

    static public PersistenceManager getInstance() {
        return singleton;
    }

    public int beginTransaction() {
        tid.increment();
        int id = tid.value();
        // System.out.println(id);
        transactions.put(id, new Transaction(id));
        return id;
    }

    public void write(int taId, int pageId, String data) {
        lsn.increment();
        int nr = lsn.value();
        buffer.put(pageId, new Page(pageId, nr, data));

        Transaction transaction = transactions.get(taId);
        transaction.addPage(pageId);

        FileUtils.writeLogEntry(nr, taId, pageId, data);

        if (buffer.size() > 1000) {
            checkBuffer();
        }
    }

    public void commit(int taId) {
        Transaction transaction = transactions.get(taId);
        lsn.increment();
        int nr = lsn.value();
        FileUtils.writeLogEntry(nr, taId);
        transaction.commit();
    }

    private void checkBuffer() {
        Hashtable<Integer, Transaction> newTransactions = new Hashtable<>();

        transactions.forEach((taId, transaction) -> {
            if (transaction.isCommitted()) {
                Set<Integer> pageNumbers = transaction.getPageNumbers();
                pageNumbers.forEach((page) -> {
                    if (buffer.get(page) != null) {
                        FileUtils.pageToFile(buffer.get(page));
                        buffer.remove(page);
                    }
                });
            }
            else {
                newTransactions.put(taId, transaction);
            }
        });

        transactions = newTransactions;
    }

}
