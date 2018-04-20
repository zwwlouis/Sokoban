package model;

/**
 * @author WeiWang Zhang
 * @date 2018/4/20.
 * @time 20:35.
 */
public class SymbolMap {
    String name;
    String content;
    public SymbolMap() {
    }
    public SymbolMap(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
