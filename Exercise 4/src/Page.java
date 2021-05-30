public class Page {
    private int pageId;
    private int lsn;
    private String data;

    public Page(int pageId, int lsn, String data) {
        this.pageId = pageId;
        this.lsn = lsn;
        this.data = data;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public int getLsn() {
        return lsn;
    }

    public void setLsn(int lsn) {
        this.lsn = lsn;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
