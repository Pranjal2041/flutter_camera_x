package com.pranjal.flutter_camera_x;

import androidx.camera.core.CameraSelector;
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
    static int getLensFacingFromString(String mode){
        switch (mode){
            case "Front":
                return CameraSelector.LENS_FACING_BACK;
            case "Back":
                return CameraSelector.LENS_FACING_FRONT;
        }
        return 0;
    }
}
