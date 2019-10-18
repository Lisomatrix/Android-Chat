package pt.lisomatrix.chatapplication.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import pt.lisomatrix.chatapplication.dao.MessageDao;
import pt.lisomatrix.chatapplication.dao.UserDao;
import pt.lisomatrix.chatapplication.model.Message;
import pt.lisomatrix.chatapplication.model.User;

@Database(entities = {User.class, Message.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract MessageDao messageDao();

}
