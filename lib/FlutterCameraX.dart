import 'package:flutter_camerax/CameraXMethodChannel.dart';
import 'package:flutter_camerax/models/CameraXConstants.dart';

class FlutterCameraX {
  static Future availableCameras(){
    var availableCameras = CameraXMethodChannel.channel.invokeMethod(CameraXConstants.get_available_cameras_method_name);
  }
}