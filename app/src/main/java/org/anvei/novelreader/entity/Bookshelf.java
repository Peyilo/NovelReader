package org.anvei.novelreader.entity;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Bookshelf {
    public String name;
    public int priority;

    @Expose
    public List<BookItem> bookList = new ArrayList<>();
}
