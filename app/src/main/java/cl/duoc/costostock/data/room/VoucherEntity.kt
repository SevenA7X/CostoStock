package cl.duoc.costostock.data.room

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "vouchers")
data class VoucherEntity (
    @PrimaryKey(autoGenerate = true)
    val idVoucher: Long = 0,
    val codigoOrdenQR: String,
    val nombreProducto: String,
    val nombreComercio: String,
    val direccionExactaRetiro: String,
    val horarioValidoRetiro: String,
    val estadoVoucher: String,
    val kgCo2Evitado: Double,
    val fechaEmisionMillis: Long,
)

@Entity(tableName = "impacto_metricas")
data class ImpactoEntity(
    @PrimaryKey val idMetrica: Int = 1,
    val totalKgCo2Evitado: Double,
    val equivalenciaArbolesPlantados: Int,
    val totalLotesVendidosOComprados: Int
)

@Dao
interface VoucherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVoucher(voucher: VoucherEntity): Long

    @Query("SELECT * FROM vouchers ORDER BY fechaEmisionMillis DESC")
    fun obtenerTodosLosVouchers(): Flow<List<VoucherEntity>>
}

@Dao
interface ImpactoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun actualizarMetricas(impacto: ImpactoEntity)

    @Query("SELECT * FROM impacto_metricas WHERE idMetrica = 1")
    fun obtenerMetricas(): Flow<ImpactoEntity?>
}

@Database(entities = [VoucherEntity::class, ImpactoEntity::class], version = 1, exportSchema = false)
abstract class CostoStockDataBase : RoomDatabase() {
    abstract fun voucherDao(): VoucherDao
    abstract fun impactoDao(): ImpactoDao

    companion object {
        @Volatile
        private var INSTANCE: CostoStockDataBase? = null

        fun getDatabase(context: Context): CostoStockDataBase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    CostoStockDataBase::class.java,
                    "costostock_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}