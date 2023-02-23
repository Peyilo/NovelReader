package org.anvei.novelreader.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.anvei.novelreader.App;
import org.anvei.novelreader.database.dao.BookDao;
import org.anvei.novelreader.entity.BookItem;
import org.anvei.novelreader.entity.Bookshelf;

@Database(entities = {BookItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BookDao bookDao();

    public static final String DATABASE_NAME = "AppDatabase";
    private volatile static AppDatabase instance;
    public static AppDatabase getInstance() {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(App.getContext(),
                            AppDatabase.class, DATABASE_NAME).build();
                }
            }
        }
        return instance;
    }
}
