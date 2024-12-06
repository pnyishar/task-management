package oasis.task.tech.dao;

public class Param {

    private int page;
    private int size;
    private String sort;
    private String url;

    private Param() {
    }

    public Param(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public Param(int page, int size, String sort) {
        this.page = page;
        this.size = size;
        this.sort = sort;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }


    public void setSize(int size) {
        this.size = size;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getOffset(){
        return this.page * this.size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
