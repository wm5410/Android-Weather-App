# AssignmentThree: Android Maps and APIs

**Course:** COMPX202/242 (HAM/TGA)

**Due Date:** Friday, 20th October 2025, 11:59 PM 2023

---

## Project Overview
This Android application allows users to search for a destination and view weather data and live webcam feeds for that location on an interactive map. It demonstrates use of Google Maps and Places APIs, integration with external weather and webcam services, responsive UI design, and robust testing.

## Features

- **Introduction Screen**: A welcoming MainActivity with a Start button.
- **Places Autocomplete**: Search bar powered by Google Places SDK.
- **Current Location**: Map centers on the user’s location (with runtime permissions).
- **Destination Markers**: Selected destination marked on map.
- **Weather Markers**: Weather icon retrieved from OpenWeatherMap API; expands to show details.
- **Webcam Markers**: Displays nearest 5 webcams via Windy API both on map and in a ListView.
- **Webcam Detail**: Clicking a camera marker or list item opens details with thumbnail, location, and metadata.
- **Offline Resilience**: Caches recent searches to handle brief network outages.

## Application Architecture

- **Language:** Java
- **Min SDK:** API 24 (Android 7.0)
- **Architecture Pattern:** MVC with custom ArrayAdapters
- **Networking:** Volley for HTTP requests
- **Data Parsing:** JSON (OpenWeatherMap, Windy) and Google Places objects

## Setup & Installation

1. **Clone the repository:**

   _git clone https://gitlab.example.com/<your-group>/AssignmentThree.git_

2. **Open in Android Studio (Flamingo):**

   * Select _Open an Existing Project → AssignmentThree_
  

3. **Add API Keys:**
   * Insert your keys:

     ```  
     <resources>
          <string name="google_maps_key">AIzaSyA5pUxD_2Xi1s-bga4itPVaq-VblEHmxg8</string>
          <string name="openweather_key">YOUR_OPENWEATHERMAP_KEY</string>
          <string name="windy_key">YOUR_WINDY_API_KEY</string>
     </resources>
      ```  




4. **Build & Run:**

   * Connect a phone or use an emulator (portrait/landscape supported)
   * Run `app` configuration

## Usage Instructions

1. **Start** the app and tap **Start** on the introduction screen.
2. **Grant** location permissions when prompted.
3. **Search** for a destination via the autocomplete bar.
4. View markers for the **destination**, **weather**, and **nearest webcams**.
5. **Tap** weather icon to expand details.
6. **Select** a webcam from the map or list to see full details.
<be>

## Screenshot
![Screenshot](image.png)
