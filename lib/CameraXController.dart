import 'package:flutter/services.dart';
import 'package:flutter_camera_x/models/CameraXConstants.dart';
import 'package:flutter_camera_x/models/enums.dart';

class CameraXController {
//  CameraXController._(id)
//      : _channel = new MethodChannel('${CameraXConstants.channel_id}_$id');

  var _cameraXDescriptor;
  var _saveToFile;

  CameraXController(cameraXDescriptor,{saveToFile = true}) {
    this._cameraXDescriptor = cameraXDescriptor;
    this._channel = new MethodChannel('${CameraXConstants.channel_id}_0');
    this._saveToFile = saveToFile;
  }

//  CameraXController._(int id)
//      : _channel = new MethodChannel('flutter_pluginer_$id');

  MethodChannel _channel;

  listenForPictureClick(var callback){
    try {
      Future<dynamic> handleMethodCall(MethodCall call) {
        if (call.method == "pictureClicked") {
          callback();
        }
      }
      _channel.setMethodCallHandler(handleMethodCall);
    }catch(e){
      print(e);
    }
  }




  Future<void> setFlashMode(FlashModeX mode) async {
    if (mode == FlashModeX.On)
      return _channel
          .invokeMethod(CameraXConstants.set_flash_method_name, {"data": "On"});
    if (mode == FlashModeX.Off)
      return _channel.invokeMethod(
          CameraXConstants.set_flash_method_name, {"data": "Off"});
    if (mode == FlashModeX.Auto)
      return _channel.invokeMethod(
          CameraXConstants.set_flash_method_name, {"data": "Auto"});
    if (mode == FlashModeX.Torch)
      return _channel.invokeMethod(
          CameraXConstants.set_flash_method_name, {"data": "Torch"});
  }

//  Future<void> enableTorch(bool mode) async {
//      return _channel.invokeMethod(
//          CameraXConstants.set_torch_method_name, {"data": mode});
//  }

  Future<void> initialize() async {
    print("before Initializing camera here");

    if (_cameraXDescriptor == null)
      return;
    print("Initializing camera here");
    _channel.invokeMethod("initializeCamera", {
      "lensFacing": getStringFromCameraXFacing(_cameraXDescriptor.lensFacing),
      "saveToFile": _saveToFile
    });
  }

  Future takePicture(String path) async {
    var image = await _channel.invokeMethod(
        CameraXConstants.capture_image_method_name, {"data": path});
    return image;
  }

  Future<void> setAspectRatio(int num, int denom) {
    return _channel.invokeMethod(
        CameraXConstants.set_preview_aspect_ratio_method_name,
        {"num": num, "denom": denom});
  }

  Future enableClickSound(bool val) {
    return _channel.invokeMethod(
        CameraXConstants.play_sound_on_click_method_name, {"data": val});
  }
}
