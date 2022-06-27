package org.tww.netty;

public class MessageBean {
    private int type;
    /**
     * 通讯数据
     */
    private String content;

    public MessageBean() {
    }

    public MessageBean(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
