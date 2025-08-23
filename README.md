# U-Study 🚀

A modern, native Android application designed to help students organize their academic life. Track exams, manage to-do lists, discover study libraries, and monitor your progress, all in one place. Built with Jetpack Compose, Kotlin, and Supabase.

---

## ✨ Features

U-Study provides a comprehensive toolkit for students, including:

* **👤 User Authentication**: Secure sign-up and sign-in with Email/Password and Google Sign-In.
* **🎓 Exam Tracking**: Add, edit, and delete exams. Mark them as upcoming with a date or completed with a grade.
* **✅ To-Do List**: A dedicated to-do manager to keep track of study tasks. Features include adding, deleting, completing, and filtering tasks.
* **📚 Library Discovery**: Browse and search a list of local libraries, view their details, and see their location on a map.
* **⭐ Favorites & Visited System**: Mark libraries as favorites or track which ones you've visited using your device's GPS.
* **🗺️ Interactive Map**: An integrated map view powered by OpenStreetMap to visualize library locations and your current position.
* **⚙️ Personalized Experience**: A settings screen to manage your profile, change the app theme (Light/Dark/System), and switch the application language (English/Italian) on the fly.
* **📊 Statistics**: A dedicated screen to visualize your study progress.

---

## 📸 Gallery


*aggiungere screenshot dell'applicazione*

---

## 🛠️ Tech Stack & Architecture

This project is built using modern Android development practices and a robust tech stack:

* **UI**: 100% Jetpack Compose using Material 3 design principles.
* **Language**: Kotlin
* **Architecture**: MVVM (Model-View-ViewModel) with a Repository Pattern.
* **Asynchronous Programming**: Kotlin Coroutines & Flow for managing background tasks and reactive state.
* **Dependency Injection**: Koin for managing dependencies in a clean and scalable way.
* **Navigation**: Jetpack Navigation for Compose with type-safe routes.
* **Backend**: Supabase (Authentication, PostgreSQL Database, Storage).
* **Image Loading**: Coil for loading profile pictures from the network.
* **Maps**: osmdroid for the interactive map view.

---

## 🚀 Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

* Android Studio (latest version recommended)
* Git

### Installation

1.  **Clone the repository**
    ```sh
    git clone [https://github.com/soficabe/U-Study.git]
    ```
2.  **Open the project** in Android Studio.
3.  **Configure Supabase Credentials**
    * Create a `local.properties` file in the root directory of the project.
    * Add your Supabase URL and Anon Key to the file. You can get these from your Supabase project dashboard.

        ```properties
        supabase.url=SUPABASE_URL
        supabase.anon.key=SUPABASE_ANON_KEY
        ```
4.  **Build and Run**
    * Let Android Studio sync the Gradle files, then build and run the application on an emulator or a physical device.

---

## 👥 Authors

* **Caberletti Sofia** - sofia.caberletti@studio.unibo.it
* **Lotti Irene Sofia** - irenesofia.lotti@studio.unibo.it
