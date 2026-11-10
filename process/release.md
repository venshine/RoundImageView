# Release parameters (release-seal)

The generic steps live in the global `release-seal` skill. This file only records the parameters and exceptions for this project.

## Version

- Semantic versioning `X.Y.Z`: bump Z for bug fixes only, bump Y for new APIs or attributes.
- **The version is bumped at release time.** Do not bump to the next version after sealing (skip the skill's "bump version" step).
- Update all of these in the release commit (`grep -rn "<old version>" roundimageview/build.gradle sample/build.gradle README.md README-CN.md` should hit every one):
  - `version` under `publishing` in `roundimageview/build.gradle`
  - `versionName` in `sample/build.gradle` (bump `versionCode` by one)
  - The dependency coordinates (Groovy and Kotlin) in `README.md` and `README-CN.md`

## Pre-release checks

```bash
./gradlew :roundimageview:testDebugUnitTest :roundimageview:assembleRelease :sample:assembleDebug :sample:testDebugUnitTest
```

Library changes must also be re-tested on a real device for the affected scenarios.

## Tag

- The tag name is the bare version, without a `v` prefix: `2.0.0`
- Annotated tag with the message `RoundImageView X.Y.Z`, created on the release commit and pushed to `origin`
- JitPack builds from the tag (`jitpack.yml`, openjdk17); the coordinate is `com.github.venshine:RoundImageView:X.Y.Z`. After pushing, check `https://jitpack.io/api/builds/com.github.venshine/RoundImageView/X.Y.Z`

## GitHub Release

- Title is the version number. Notes are written in English, with sections such as "Added / Fixed / Breaking changes / Installation", kept or dropped as the release requires
- Attachment: the sample APK, named `RoundImageView-sample-X.Y.Z.apk`
  - Build: `./gradlew :sample:assembleDebug`; output at `sample/build/outputs/apk/debug/sample-debug.apk`, rename it to `RoundImageView-sample-X.Y.Z.apk` before attaching
  - Sign with the same local debug certificate as the other sample apps so users can install over an older version

## Afterwards

- There is no product doc or ROADMAP; the GitHub Release is the source of truth for release notes, so no other docs need syncing

## README screenshots

Taken on a real device (not part of CI). If the sample UI changed, re-shoot the affected pages before tagging and review each image. Output goes to `screenshot/`.
