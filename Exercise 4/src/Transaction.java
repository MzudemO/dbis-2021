import java.util.HashSet;
import java.util.Set;

public class Transaction {
    private int tid;
    private boolean isCommitted;
    private HashSet<Integer> pageNumbers;


    public Transaction(int tid) {
        this.tid = tid;
        this.isCommitted = false;
        this.pageNumbers = new HashSet<>();
    }

    public void commit() {
        isCommitted = true;
    }

    public boolean isCommitted() {
        return isCommitted;
    }

    public Set<Integer> getPageNumbers() {
        return pageNumbers;
    }

    public void addPage(int page) {
        pageNumbers.add(page);
    }
}
