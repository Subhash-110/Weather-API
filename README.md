# Weather API

A simple **Weather API** that fetches weather data from a third-party weather service and returns it to the client. This project demonstrates how to work with external APIs, caching, and environment variables.

The API fetches weather data from **Visual Crossing Weather API** and stores results in cache to reduce repeated requests.

---

# Features

* Fetch real-time weather data from a third-party API
* Cache weather responses for faster performance
* Use environment variables for secure API keys
* Handle invalid requests and API failures
* Reduce API usage with caching

---

# Technologies Used

* Java
* REST API
* HTTP Client
* JSON parsing
* Caching using **Redis**
* External Weather API

---

# Project Structure

```
weather-api
│
├── src
│   └── main
│        └── java
│             └── org.example
│                   ├── WeatherController.java
│                   ├── WeatherService.java
│                   └── Main.java
│
├── pom.xml
└── README.md
```

---

# How It Works

1. User sends a request with a **city name**.
2. The API checks if the weather data exists in **Redis cache**.
3. If cached data exists → return cached response.
4. If not cached → fetch data from the weather API.
5. Store the response in cache with expiration time (e.g., 12 hours).
6. Return the weather data to the user.

---

# Prerequisites

Before running the project, install:

* Java 11 or higher
* Maven
* Redis
* Weather API key from **Visual Crossing Weather API**

---

# Environment Variables

Store sensitive data using environment variables.

Example:

```
WEATHER_API_KEY=your_api_key
REDIS_URL=redis://localhost:6379
```

This prevents exposing API keys inside the source code.

---

# Installation

### 1 Clone the repository

```
git clone https://github.com/yourusername/weather-api.git
cd weather-api
```

---

### 2 Install dependencies

```
mvn clean install
```

---

### 3 Start Redis

Make sure Redis is running locally.

```
redis-server
```

---

### 4 Run the application

```
mvn spring-boot:run
```

---

# API Endpoint

Get weather data by city.

```
GET /weather?city={cityName}
```

Example request:

```
http://localhost:8080/weather?city=London
```

---

# Example Response

```
{
  "city": "London",
  "temperature": "18°C",
  "conditions": "Partly Cloudy"
}
```

---

# Caching Strategy

Weather responses are cached using **Redis**.

Cache settings:

* Key: City name
* Value: Weather API response
* Expiration: 12 hours

Example Redis command used internally:

```
SET london {weather_data} EX 43200
```

---

# Error Handling

The API returns appropriate messages for errors such as:

* Invalid city name
* Weather API failure
* Network issues

Example:

```
{
  "error": "City not found"
}
```

---

# Future Improvements

* Add multiple weather providers
* Implement request rate limiting
* Add weather forecast support
* Add Docker support
* Improve response formatting

---
