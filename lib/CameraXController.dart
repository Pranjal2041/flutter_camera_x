import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_camerax/models/CameraXConstants.dart';
import 'package:flutter_camerax/models/enums.dart';

class CameraXController{
  CameraXController._(id)
      : _channel = new MethodChannel('${CameraXConstants.channel_id}_$id');

//  CameraXController._(int id)
//      : _channel = new MethodChannel('flutter_pluginer_$id');

  final MethodChannel _channel;

  Future<void> setFlashMode(FlashModeX mode) async {
    if(mode==FlashModeX.On)
      return _channel.invokeMethod(CameraXConstants.set_flash_method_name, {"data": "On"});
    if(mode==FlashModeX.Off)
      return _channel.invokeMethod(CameraXConstants.set_flash_method_name,{"data": "Off"});
    if(mode==FlashModeX.Auto)
      return _channel.invokeMethod(CameraXConstants.set_flash_method_name,{"data": "Auto"});
  }

  Future<void> takePicture(String path) async {
      return _channel.invokeMethod(CameraXConstants.capture_image_method_name, {"data": path});
  }

}

typedef void CameraXCreatedCallback(CameraXController controller);

class CameraXPreview extends StatefulWidget {

  const CameraXPreview({
    Key key,
    this.onCameraXCreated,
  }) : super(key: key);

  final CameraXCreatedCallback onCameraXCreated;

  @override
  _CameraXPreviewState createState() => _CameraXPreviewState();
}

class _CameraXPreviewState extends State<CameraXPreview> {
  @override
  Widget build(BuildContext context) {
    return AndroidView(
      viewType: CameraXConstants.previewViewType,
      onPlatformViewCreated: _onPlatformViewCreated,
    );
  }

  void _onPlatformViewCreated(int id) {
    if (widget.onCameraXCreated == null) {
      return;
    }
    widget.onCameraXCreated(new CameraXController._(id));
  }
}