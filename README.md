# KMM Images

Generate images for iOS and Android from a single image source. This will also generate an Images.kt that contains definitions for each image.

# Setup

```
swift package generate-xcodeproj
```

# Build

```
swift build -c release
```

# Requirements

Running the CommonImages depends on a couple of command line tools which must be installed.

## Image Magick

Image Magick is used for all image conversions and itself uses ghostscript and potrace.

<https://imagemagick.org>

Installation:

```
brew install imagemagick
```

```
brew install ghostscript
```

```
brew install potrace
```

## vd-tool

vd-tool is a tool used to convert svg to Android vector drawables. This tool is a pre-requisite if sgv files are used. See https://www.androiddesignpatterns.com/2018/11/android-studio-svg-to-vector-cli.html for more information. To setup vd-tool do the following:

- Download the vd-tool.zip file
- Extract the contents of the zip file to a folder of choice
- Configure the plugin with the path to vd-tool. The recommended way is to add vd-tool/bin to your path so the only configuration needed is the following: pathToVdTool.set("vd-tool")
- Restart the IDE or terminal to make sure the path settings are taken into account.

Please note this tool is only required if raw svg files are used.

## svg2vd (deprecated, not used anymore, see vd-tool above)

svg2vd is used to convert svg images to Android xml format

<https://github.com/alexjlockwood/svg2vd>

Installation:

```
npm install -g svg2vd
```

# Run

todo

# Supported images

Images are expected to be at `[project folder]/common/images`

## PDF Vector

- iOS: images are copied
- Android: images are converted into xml drawables with svg

## PNG

Always provide a 4x version of the image.

- iOS: converts into @1x, @2x and @3x (respectively 25%, 50% and 75% of the 4x image)
- Android: converts into mdpi, hdpi, xdpi, xxdpi and xxxdpi (respectively 25%, 37.5%, 50%, 75% and 100% of the 4x image)

## JPG

JPG files (which should be used for photos) are not converted and have a fixed dimension. It's assumed that these are resized dynamically.

- iOS: single image copied
- Android: copied into mdpi, hdpi, xdpi, xxdpi and xxxdpi all with the same dimensions

## SVG

- iOS: images are converted to PDF and copied
- Android: images are converted into xml drawables with vd-tool

# Output

- Images.kt: `[project folder]/common/src/commonMain/[path to package]/Images.kt`
- iOS Images: compiled Assets catalog in `[project folder]/common/src/commonMain/resources/ios/Assets.car`
- Android Images: `[project folder]/common/src/main/res/drawable*`
