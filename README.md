# iQuery

A native Android application for exploring the Apple App Store.

iQuery lets users search for iOS applications, browse app and game charts, view application details, and read customer reviews across different App Store storefronts.

## Features

- **App Charts:** Browse top free, top paid, and new applications.
- **Game Charts:** Browse top free, top paid, and new games.
- **Categories:** Explore applications and games by category.
- **Search:** Search for applications by name or keyword, with an option to filter for iPhone-compatible apps.
- **App Details:** View descriptions, screenshots, ratings, prices, version information, release notes, and supported devices.
- **Customer Reviews:** Read user reviews, ratings, and app versions, with pagination.
- **Developer Profiles:** Browse other applications published by the same developer.
- **Storefronts:** Browse App Store content from 175 supported country and region storefronts.
- **Settings:** Configure available app preferences, including dynamic colors.

## Data Sources

iQuery uses Apple's public iTunes APIs:

- **Search API:** Search for applications.
- **Lookup API:** Retrieve application information.
- **RSS Charts:** Retrieve application and game charts.
- **Customer Reviews RSS:** Retrieve customer reviews.

API base URL: `https://itunes.apple.com/`

## Technologies

- Kotlin
- Jetpack Compose
- Material 3
- MVVM
- Kotlin Coroutines and StateFlow
- Retrofit
- OkHttp
- Kotlinx Serialization
- Coil
- Navigation Compose

## Requirements

- Android 8.0 (API 26) or later.
- Internet connection.

## License

This project is licensed under the Apache License 2.0. See [LICENSE](LICENSE) for details.
