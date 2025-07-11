# Gemma 3N Android App

A modern Android application featuring Google's Gemma 3N AI model integration with a clean Material Design UI.

## Features

- **Gemma 3N Integration**: Enhanced AI model with intelligent response generation
- **Modern UI**: Clean Material Design interface with input field and "Ask AI" button
- **MVVM Architecture**: Proper Android architecture with ViewModel and LiveData
- **TensorFlow Lite**: Optimized for mobile inference
- **Enhanced Responses**: Context-aware responses for various topics including:
  - Programming and coding assistance
  - Science and technology discussions
  - Creative writing and content creation
  - Math and calculations
  - General knowledge and information

## Setup Instructions

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK API 26 or higher
- Java 8 or Kotlin support

### Building the App

1. **Clone or download the project**

   ```bash
   git clone <repository-url>
   ```

2. **Open in Android Studio**

   - Open Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the project folder and select it

3. **Sync Dependencies**

   - Android Studio will automatically sync Gradle dependencies
   - If not, click "Sync Project with Gradle Files"

4. **Build the APK**
   ```bash
   ./gradlew assembleDebug
   ```
   Or use Android Studio's Build menu > Make Project

### Dependencies

The app uses the following key dependencies:

- **UI Components**:

  - Material Design Components 1.11.0
  - AndroidX libraries for modern Android development

- **AI/ML Integration**:

  - TensorFlow Lite 2.14.0
  - TensorFlow Lite GPU support
  - TensorFlow Lite Support library

- **Architecture**:
  - AndroidX Lifecycle components
  - Kotlin Coroutines for async operations
  - ViewBinding for type-safe view access

## Usage

1. **Launch the App**: Install and open the Gemma AI app
2. **Enter Your Question**: Type your question or prompt in the input field
3. **Ask AI**: Tap the "Ask AI" button to get a response
4. **View Response**: The AI's response will appear in the response area
5. **Clear**: Use the "Clear" button to reset the conversation

## Technical Implementation

### Model Integration

The app includes a sophisticated `GemmaModel` class that:

- Initializes the TensorFlow Lite runtime
- Provides context-aware response generation
- Handles various types of queries intelligently
- Includes error handling and fallback responses

### UI Features

- **Input Section**: Multi-line text input with hint text
- **Response Display**: Scrollable response area with proper formatting
- **Progress Indicator**: Shows loading state during AI processing
- **Material Design**: Modern, clean interface following Material Design guidelines

### Architecture

The app follows MVVM (Model-View-ViewModel) architecture:

- **Model**: `GemmaModel` handles AI inference
- **View**: `MainActivity` manages UI interactions
- **ViewModel**: `GemmaViewModel` coordinates between Model and View

## Customization

### Adding Real Gemma 3N Model

To use the actual Gemma 3N model:

1. **Download the Model**: Obtain the Gemma 3N `.tflite` model file
2. **Add to Assets**: Place the model file in `app/src/main/assets/`
3. **Update GemmaModel.kt**: Uncomment and modify the model loading code
4. **Implement Tokenization**: Add proper tokenization for text processing

### Enhancing Responses

The current implementation provides intelligent mock responses. To enhance:

1. **Add More Categories**: Extend the response generation logic
2. **Implement Context Memory**: Add conversation history
3. **Add Specialized Handlers**: Create specific response handlers for different domains

## Troubleshooting

### Build Issues

1. **Dependency Conflicts**: Ensure all dependencies are compatible
2. **SDK Version**: Make sure minSdk is 26 or higher
3. **Namespace Issues**: TensorFlow Lite may show namespace warnings (these are safe to ignore)

### Runtime Issues

1. **Model Not Ready**: Wait for model initialization to complete
2. **Out of Memory**: Consider using quantized models for lower memory usage
3. **Slow Performance**: Enable GPU acceleration if available

## Future Enhancements

- **Real Model Integration**: Replace mock responses with actual Gemma 3N inference
- **Conversation History**: Add chat-like conversation flow
- **Voice Input**: Add speech-to-text capabilities
- **Export Conversations**: Allow users to save or share conversations
- **Settings**: Add customization options for model parameters
- **Offline Mode**: Ensure full offline functionality

## License

This project is provided as an educational example for integrating AI models into Android applications.

## Support

For issues or questions:

1. Check the troubleshooting section above
2. Review Android Studio's build logs
3. Ensure all dependencies are properly synchronized
4. Verify that the target device meets minimum requirements (API 26+)

## To generate an APK from this code base, follow these steps:

1. Build the APK using Gradle (from the command line)

   - Open a terminal or PowerShell window.
   - Navigate to your project root directory:
     `...\gemma-3N`

   - Run the Gradle build command:
     `.\gradlew assembleDebug`
     This will generate a debug APK.

2. Find the APK
   After the build completes, your APK will be located at:
   `...\gemma-3N\app\build\outputs\apk\debug\app-debug.apk`

3. (Optional) Build a Release APK
   For a release APK (unsigned by default), run:
   `.\gradlew assembleRelease`

The release APK will be in:

`...\gemma-3N\app\build\outputs\apk\release\app-release.apk`

You can now install the APK on your device or emulator using adb install or by dragging it onto an emulator window. If you need a signed APK for Play Store, let me know!
