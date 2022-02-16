package jiung.fastcampus.aop.part2.pview

import android.app.Application
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig

class PictureInit : Application(), CameraXConfig.Provider{
    override fun getCameraXConfig(): CameraXConfig = Camera2Config.defaultConfig()

}

