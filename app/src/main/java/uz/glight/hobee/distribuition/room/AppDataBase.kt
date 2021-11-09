package uz.glight.hobee.distribuition.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.glight.hobee.distribuition.room.dao.Dao
import uz.glight.hobee.distribuition.room.entity.SavedMedEntity

@Database(entities = [SavedMedEntity::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun dao(): Dao

    companion object {
        private var instanse: AppDataBase? = null

        fun getInstanse(context: Context): AppDataBase {
            if (instanse == null) {
                instanse = Room
                    .databaseBuilder(
                        context.applicationContext,
                        AppDataBase::class.java,
                        "saved_meds_db"
                    ).fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instanse!!
        }
    }
}