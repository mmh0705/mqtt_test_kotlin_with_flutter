import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(

        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  @override
  void initState(){
    // _flutterServiceStart();
    // _flutterSubscribeMessage();
  }

  static const platform = MethodChannel('samples.flutter.dev/battery');
  Future<void> _brokerConnect() async {
    try {
      await platform.invokeMethod('brokerConnect');
    } on PlatformException catch (e) {
      //batteryLevel = "Failed to get battery level: '${e.message}'.";
    }
  }
  Future<void> _flutterPublishMessage() async {
    await platform.invokeMethod('flutterPublishMessage');
  }
  Future<void> _flutterSubscribeMessage() async {
    await platform.invokeMethod('flutterSubscribeMessage');
  }
  Future<void> _flutterServiceStart() async {
    await platform.invokeMethod('startService');
  }
  @override
  Widget build(BuildContext context) {
    return Material(
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            ElevatedButton(
              onPressed: _brokerConnect,
              child: const Text('Connect'),
            ),
            ElevatedButton(
              onPressed: _flutterPublishMessage,
              child: const Text('Publish'),
            ),
            ElevatedButton(
              onPressed: _flutterSubscribeMessage,
              child: const Text('Subscribe'),
            ),
            ElevatedButton(
              onPressed: _flutterServiceStart,
              child: const Text('Service Start'),
            ),
          ],
        ),
      ),
    );
  }
}
