public class LogRecord {
    private int lsn;
    private int taId;
    private int pageId;
    private boolean isCommit = false;
    private String data = "";

    public LogRecord(int lsn, int taId) {
        this.lsn = lsn;
        this.taId = taId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public void setAsCommit() {
        isCommit = true;
    }

    public void setData(String data) {
        this.data = data;
    }

    // we don't need to make these getters safe because we can control when we call them
    // this is not good practice of course
    public int getPageId() {
        return pageId;
    }

    public boolean isCommit() {
        return isCommit;
    }

    public int getLsn() {
        return lsn;
    }

    public int getTaId() {
        return taId;
    }

    public String getData() {
        return data;
    }
}
