package com.demo.book.utils

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient

inline fun <reified T> HttpClient.get(url: String) = toBlocking().exchange(
    HttpRequest.GET<T>(url).basicAuth("admin", "l0g m3 1n"),
    T::class.java
)

inline fun <reified T> HttpClient.post(url: String, body: T) = toBlocking()
    .exchange(
        HttpRequest.POST(url, body).basicAuth("admin", "l0g m3 1n"),
        T::class.java
    )

inline fun <reified T, O> HttpClient.delete(url: String, requestBody: O) = toBlocking()
    .exchange(
        HttpRequest.DELETE(url, requestBody).basicAuth("admin", "l0g m3 1n"),
        T::class.java
    )

inline fun <reified T> HttpClient.getWithInvalidCredentials(url: String) = toBlocking().exchange(
    HttpRequest.GET<T>(url).basicAuth("adin", "l0g m3 1n"),
    T::class.java
)

inline fun <reified T, O> HttpClient.put(url: String, body: O) = toBlocking()
    .exchange(
        HttpRequest.PUT(url, body).basicAuth("admin", "l0g m3 1n"),
        T::class.java
    )
