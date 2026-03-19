package model;

import java.util.List;

public class PageBeam {
    private Long total;
    private List rows;  // 使用泛型<?>表示可以接受任意类型的List

    // total 的 getter 方法
    public Long getTotal() {
        return total;
    }

    // total 的 setter 方法
    public void setTotal(Long total) {
        this.total = total;
    }

    // rows 的 getter 方法
    public List getRows() {
        return rows;
    }

    // rows 的 setter 方法
    public void setRows(List rows) {
        this.rows = rows;
    }
}
