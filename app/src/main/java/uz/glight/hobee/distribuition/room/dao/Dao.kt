package uz.glight.hobee.distribuition.room.dao

import androidx.room.*
import androidx.room.Dao
import uz.glight.hobee.distribuition.room.entity.SavedMedEntity

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(savedMedEntity: SavedMedEntity)

    @Query("select * from saved_meds where pharmacy_id=:pharmacy_id")
    fun getMedsBySavedId(pharmacy_id: Int): List<SavedMedEntity>

    @Query("delete from saved_meds where pharmacy_id=:pharmacy_id")
    fun deleteMedsbyPharmId(pharmacy_id: Int)

    @Query("delete from saved_meds where saved_med_id=:savedId")
    fun deleteMedsbySavedId(savedId: Int)

    @Query("delete from saved_meds")
    fun clearDb()

    @Query("select* from saved_meds")
    fun getAll(): List<SavedMedEntity>


}