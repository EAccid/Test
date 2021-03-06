package com.eaccid.hocreader.data.local.db.entity;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "books")
public class Book implements Serializable {

    @DatabaseField(columnName = "id", canBeNull = false, id = true)
    private String path;

    @DatabaseField
    private String name;

    @ForeignCollectionField(foreignFieldName = "book")
    private ForeignCollection<Word> words;

    public Book() {
        this.path = "";
        this.name = "";
    }

    public Book(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id = '" + path + '\'' +
                ", name = '" + name +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return path != null ? path.equals(book.path) : book.path == null
                && (name != null ? name.equals(book.name) : book.name == null);
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

}
