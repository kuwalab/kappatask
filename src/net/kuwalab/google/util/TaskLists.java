package net.kuwalab.google.util;

import java.util.List;

public class TaskLists {
    private String kind;
    private String etag;
    private String nextPageToken;
    private List<TaskList> items;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<TaskList> getItems() {
        return items;
    }

    public void setItems(List<TaskList> items) {
        this.items = items;
    }
}
