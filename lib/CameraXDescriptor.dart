import 'package:flutter_camera_x/CameraXMethodChannel.dart';
import 'package:flutter_camera_x/models/CameraXConstants.dart';
import 'package:flutter_camera_x/models/enums.dart';

class CameraXDescriptor{
  CameraXDescriptor({this.name, this.lensFacing, this.sensorOrientation});

  final String name;
  final CameraXFacing lensFacing;

  /// Clockwise angle through which the output image needs to be rotated to be upright on the device screen in its native orientation.
  final int sensorOrientation;

  static getAvailableCameras() async{
    final List<Map<dynamic, dynamic>> cameras = await CameraXMethodChannel.channel
        .invokeListMethod<Map<dynamic, dynamic>>(CameraXConstants.get_available_cameras_method_name);
    return cameras.map((Map<dynamic, dynamic> camera) {
      return CameraXDescriptor(
        name: camera['name'],
        lensFacing: getCameraXFacingFromString(camera['lensFacing']),
        sensorOrientation: camera['sensorOrientation'],
      );
    }).toList();
  }

  @override
  String toString() {
    return '$runtimeType($name, $lensFacing, $sensorOrientation)';
  }
}