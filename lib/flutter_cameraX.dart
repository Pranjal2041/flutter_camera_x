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


