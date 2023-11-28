# android-compose-bar-chart-sample(最小限の実装)

JetpackComposeのCanvasを使用したグラフ描画のサンプルコードです。グラフの内容がはみ出さないようにグラフの寸法を決めました。

## 想定環境
使用言語: Kotlin  
Android Studio Giraffe | 2022.3.1  
OS: Windows 11  
minSdkVersion: 26  
targetSdkVersion: 33  

## アプリのメイン画面

<img src="img/screenshot_bar_chart.png" width="320px">

## グラフの見た目を変える変数

### グラフの寸法値
```kotlin
// dimension value
val width = constraints.maxWidth
val topChartAreaGap = convertDpToPx(20f, context)
val chartAreaGap = convertDpToPx(12f, context)
val textGap = convertDpToPx(4f, context)
val axisStrokeWidth = 3f
```
#### グラフの上部の空白

<img src="img/topChartAreaGap.png" width="320px">

```kotlin
val topChartAreaGap = convertDpToPx(20f, context)
```

#### グラフの下と左右の空白

<img src="img/chartAreaGap.png" width="320px">

```kotlin
val chartAreaGap = convertDpToPx(12f, context)
```

#### テキストと他の要素の間の空白

<img src="img/textGap.png" width="320px">

```kotlin
val textGap = convertDpToPx(4f, context)
```

#### 軸の線の幅

<img src="img/axisStrokeWidth.png" width="320px">

```kotlin
val axisStrokeWidth = 3f
```

### グラフの色
```kotlin
// color
val xAxisColor: Color = Color.Black
val yAxisColor: Color = Color.Black
```

#### x軸の線の色

<img src="img/xAxisColor.png" width="320px">

```kotlin
val xAxisColor: Color = Color.Black
```

#### y軸の線の色

<img src="img/yAxisColor.png" width="320px">

```kotlin
val yAxisColor: Color = Color.Black
```

## 参考サイト
このサンプルコードでは以下のサイトを参考にしました。

* グラフの軸の目盛りの値を自動計算するアルゴリズム

https://qiita.com/yo16/items/ea620dc234286130e348

* dpとpxを相互変換する方法

https://qiita.com/SnowMonkey/items/6edcd875d78913c50d62



