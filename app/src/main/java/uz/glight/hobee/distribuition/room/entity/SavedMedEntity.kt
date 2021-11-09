package uz.glight.hobee.distribuition.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.glight.hobeedistribuition.network.model.CreateOrderModel

@Entity(tableName = "saved_meds")
data class SavedMedEntity(
    @PrimaryKey(autoGenerate = true) var saved_med_id: Int=0,
    var med_id: Int,
    var warehouseId: Int,
    var pharmacy_id: Int,
    var name: String,
    var priceForOne: String,
    var amount: Int,
    var allPrice: Double)