enum FlashModeX {
  On,
  Off,
  Auto
}

enum CameraXFacing {
  Front,
  Back,
  External
}

enum ResolutionPresetX {
  /// 240p (320x240) on Android
  low,

  /// 480p (720x480) on Android
  medium,

  /// 720p (1280x720)
  high,

  /// 1080p (1920x1080)
  veryHigh,

  /// 2160p (3840x2160)
  ultraHigh,

  /// The highest resolution possible.
  max,
}

getCameraXFacingFromString(String facing){
  switch(facing){
    case 'Front':
      return CameraXFacing.Front;
    case 'Back':
      return CameraXFacing.Back;
    case 'External':
      return CameraXFacing.External;
  }
}

getStringFromCameraXFacing(CameraXFacing facing){
  switch(facing){
    case CameraXFacing.Back:
      return "Back";
    case CameraXFacing.Front:
      return "Front";
    case CameraXFacing.External:
      return "External";
  }
}

