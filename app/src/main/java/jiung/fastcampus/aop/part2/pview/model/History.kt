package jiung.fastcampus.aop.part2.pview.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    @PrimaryKey val uid: Int?,
    @ColumnInfo(name= "sex")val sex: String?,
    @ColumnInfo(name= "age")val age: String?,

    @ColumnInfo(name= "acne")val Acne: String?,
    @ColumnInfo(name= "whitening")val Whitening: String?,
    @ColumnInfo(name= "stimulus")val Stimulus: String?,
    @ColumnInfo(name= "wrinkle")val Wrinkle: String?,
    @ColumnInfo(name= "moisture")val Moisture: String?,
    @ColumnInfo(name= "moisturizing")val Moisturizing: String?,
    @ColumnInfo(name= "oilly")val Oilly: String?

    )