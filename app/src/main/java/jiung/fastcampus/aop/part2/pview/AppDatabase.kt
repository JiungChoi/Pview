package jiung.fastcampus.aop.part2.pview

import androidx.room.Database
import androidx.room.RoomDatabase
import jiung.fastcampus.aop.part2.pview.dao.HistoryDao
import jiung.fastcampus.aop.part2.pview.model.History

@Database(entities = [History::class], version=1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun historyDao():HistoryDao
}