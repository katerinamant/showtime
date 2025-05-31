# ShowTime – Theater Booking Chatbot

A mobile chatbot interface for booking theater tickets.

## Features

- Natural-language ticket booking and cancellation via chat
- Display of upcoming shows, times, prices, and seating sections
- Suggests follow-up questions to guide user interaction
- Human support integration (optional handoff to a real assistant)
- Text-only interaction (no need for accounts or online payment)
- Fully functional Android prototype built in Android Studio

## Powered by GPT (OpenAI)

The core chatbot logic is backed by **OpenAI's GPT API**, simulating a helpful theater assistant ("UsherBot"). The assistant:
- Understands and responds to user intents (e.g. new booking, modify, cancel)
- Outputs structured responses including:
    - `message`: user-facing reply
    - `intent`: type of action detected
    - `reservation`: structured reservation data
    - `suggestedQuestions`: dynamic reply suggestions
    - `showExtra`: visual enhancements (e.g. seating chart)

> ⚠️ **API KEY REQUIRED**: You must provide an OpenAI API key for the app to function.

## API Key Setup

Inject your key programmatically in the OpenAI client setup in `ChatPageActivity.java` 

**Never commit your API key to version control.**

## Demo Video

Watch the [video demo](https://www.loom.com/share/3626c753e3364b008128fa93cace793b)!

## Authors

Created by Alex Papadopoulos and Katerina Mantaraki for 🎓
