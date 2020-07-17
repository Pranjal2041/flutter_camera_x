package com.pranjal.flutter_camerax;

import androidx.camera.core.ImageCapture;

public class Utils {

    static int getFlashModeFromString(String mode){
        switch (mode){
            case "Auto":
                return ImageCapture.FLASH_MODE_AUTO;
            case "On":
                return ImageCapture.FLASH_MODE_ON;
            case "Off":
                return ImageCapture.FLASH_MODE_OFF;
        }
        return 0;
    }

}
