import 'package:flutter/services.dart';
import 'package:flutter_camera_x/models/CameraXConstants.dart';
import 'package:flutter_camera_x/models/enums.dart';

class CameraXController{
//  CameraXController._(id)
//      : _channel = new MethodChannel('${CameraXConstants.channel_id}_$id');

  var _cameraXDescriptor;

  CameraXController(cameraXDescriptor){
    this._cameraXDescriptor = cameraXDescriptor;
    this._channel = new MethodChannel('${CameraXConstants.channel_id}_0');
  }



//  CameraXController._(int id)
//      : _channel = new MethodChannel('flutter_pluginer_$id');

  MethodChannel _channel;

  Future<void> setFlashMode(FlashModeX mode) async {
    if(mode==FlashModeX.On)
      return _channel.invokeMethod(CameraXConstants.set_flash_method_name, {"data": "On"});
    if(mode==FlashModeX.Off)
      return _channel.invokeMethod(CameraXConstants.set_flash_method_name,{"data": "Off"});
    if(mode==FlashModeX.Auto)
      return _channel.invokeMethod(CameraXConstants.set_flash_method_name,{"data": "Auto"});
    }

  Future<void> initialize() async {
    if(_cameraXDescriptor==null)
      return;
    _channel.invokeMethod("initializeCamera",{"lensFacing": getStringFromCameraXFacing(_cameraXDescriptor.lensFacing)});
  }

  Future takePicture(String path) async {
      var image =  await _channel.invokeMethod(CameraXConstants.capture_image_method_name, {"data": path});
      return image;
  }

  Future<void> setAspectRatio(int num,int denom){
    return _channel.invokeMethod(CameraXConstants.set_preview_aspect_ratio_method_name,{"num":num,"denom":denom});
  }

}