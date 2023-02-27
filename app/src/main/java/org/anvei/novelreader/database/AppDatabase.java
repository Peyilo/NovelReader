package org.anvei.novelreader.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import org.anvei.novelreader.App;
import org.anvei.novelreader.database.dao.BookDao;
import org.anvei.novelreader.database.dao.BookshelfDao;
import org.anvei.novelreader.database.entity.BookItem;
import org.anvei.novelreader.database.entity.BookshelfItem;

@Database(entities = {BookItem.class, BookshelfItem.class}, version = 1)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract BookDao bookDao();

    public abstract BookshelfDao bookshelfDao();

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
