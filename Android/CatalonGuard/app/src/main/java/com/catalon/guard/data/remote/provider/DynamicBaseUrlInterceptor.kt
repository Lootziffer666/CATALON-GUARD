package com.catalon.guard.data.remote.provider

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicBaseUrlInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val targetBase = original.header("X-Target-Base-Url")
            ?: return chain.proceed(original)

        val baseUrl = targetBase.trimEnd('/') + "/"
        val parsedBase = baseUrl.toHttpUrl()

        val newUrl = original.url.newBuilder()
            .scheme(parsedBase.scheme)
            .host(parsedBase.host)
            .port(parsedBase.port)
            .build()

        val newRequest = original.newBuilder()
            .url(newUrl)
            .removeHeader("X-Target-Base-Url")
            .build()

        return chain.proceed(newRequest)
    }
}
