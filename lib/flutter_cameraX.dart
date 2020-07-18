import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_camerax/CameraXController.dart';
import 'package:flutter_camerax/CameraXMethodChannel.dart';
import 'package:flutter_camerax/models/CameraXConstants.dart';

class FlutterCameraX {
  static final MethodChannel _channel =
      CameraXMethodChannel.channel;

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}


//typedef void CameraXCreatedCallback(CameraXController controller);

class CameraXPreview extends StatefulWidget {

  const CameraXPreview({
    Key key,
    this.cameraXController,
  }) : super(key: key);

  final CameraXController cameraXController;

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
    if (widget.cameraXController == null) {
      return;
    }
    widget.cameraXController.initialize();
  }
}

