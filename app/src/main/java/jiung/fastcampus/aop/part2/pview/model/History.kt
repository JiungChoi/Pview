package jiung.fastcampus.aop.part2.pview.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    @PrimaryKey val uid: Int?,
    @ColumnInfo(name= "sex")val sex: String?,
    @ColumnInfo(name= "age")val age: String?,

    @ColumnInfo(name= "recommendAcne")val recommendAcne: String?,
    @ColumnInfo(name= "recommendWhitening")val recommendWhitening: String?,
    @ColumnInfo(name= "recommendStimulus")val recommendStimulus: String?,
    @ColumnInfo(name= "recommendWrinkle")val recommendWrinkle: String?,
    @ColumnInfo(name= "recommendMoisture")val recommendMoisture: String?,
    @ColumnInfo(name= "recommendMoisturizing")val recommendMoisturizing: String?,
    @ColumnInfo(name= "recommendOilly")val recommendOilly: String?,

    @ColumnInfo(name= "resultWrinkle")val resultWrinkle: String?,
    @ColumnInfo(name= "resultSkinTone")val resultSkinTone: String?,
    @ColumnInfo(name= "resultPoreDetect")val resultPoreDetect: String?,
    @ColumnInfo(name= "resultDeadSkin")val resultDeadSkin: String?,
    @ColumnInfo(name= "resultOilly")val resultOilly: String?,
    @ColumnInfo(name= "resultPih")val resultPih: String?,

    @ColumnInfo(name= "time")val time: String?

    )