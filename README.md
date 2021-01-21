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

## svg2vd

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

# Output

- Images.kt: `[project folder]/common/src/commonMain/[path to package]/Images.kt`
- iOS Images: compiled Assets catalog in `[project folder]/common/src/commonMain/resources/ios/Assets.car`
- Android Images: `[project folder]/common/src/main/res/drawable*`
