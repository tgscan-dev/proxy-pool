# Proxy Pool 🌐
Preview: https://proxy.tgscan.xyz

A simple, lightweight proxy pool implementation using Spring Boot. This project fetches free proxies from various sources, validates their availability and anonymity, and provides a simple API for accessing them.

## Features ✨

You got it! Here's the updated feature list with the Java coroutines advantage:

## Features ✨

* **Automatic Scraping and Validation:**  Continuously scrapes proxies from seed websites and checks their validity.
* **Anonymity Check:**  Determines if a proxy masks your IP address for enhanced privacy.
* **Geolocation:**  Identifies the country and city of the proxy exit node.
* **Performance Sorting:**  Sorts proxies by response time, allowing you to pick the fastest ones.
* **Flexible Proxy Extraction:**  Uses regex to extract proxies in various formats from websites or local files. Works seamlessly with plain text, HTML, and even JSON sources.
* **Easy Integration:**  Provides a RESTful API endpoint to retrieve a list of validated proxies.
* **Enhanced Performance with Java Coroutines:** Leverages the efficiency of Java coroutines for highly concurrent operations, resulting in faster proxy scraping and validation.

## Getting Started 🚀

1. **Prerequisites:**
   * Java 22 or later
   * Maven

2. **Clone the Repository:**
   ```bash
   git clone https://github.com/tgscan-dev/proxy-pool
   cd proxy-pool
   ```

3. **Configuration:**
    * **Seed Websites:** Update the `proxy.http.seed` property in your `application.properties` file with the URLs of websites that list free proxies.

4. **Build and Run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## API Usage 💻

**GET /proxy**

Retrieve a paginated list of proxies.

**Query Parameters:**

* `isAnonymous` (optional): Filter by anonymity (true/false).
* `country` (optional): Filter by country code (e.g., "US", "CN").
* `city` (optional): Filter by city name.
* `page` (optional): Page number (default: 1).
* `sortBy` (optional): Sort by "lastCheckTime" (default) or "responseTime".

**Example:**

```
http://localhost:8080/proxy?isAnonymous=true&country=US&sortBy=responseTime
```

## Contributing 🤝

Contributions are welcome! Feel free to open issues or pull requests.

## License 📄

This project is licensed under the MIT License.
