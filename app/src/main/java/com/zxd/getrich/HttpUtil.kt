package com.zxd.getrich

import okhttp3.OkHttpClient
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


/**
 * @author  zhongxd
 * @date 2024/7/21.
 * description：
 */

object HttpUtil {

    @JvmStatic
    fun buildTrustManagers(): Array<TrustManager>{
        return arrayOf(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )
    }

    @JvmStatic
    fun buildOKHttpClient(): OkHttpClient.Builder? {
        return try {
            val trustAllCerts = buildTrustManagers()
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).writeTimeout(10,
                TimeUnit.SECONDS)
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { hostname: String?, session: SSLSession? -> true }
            builder
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            OkHttpClient.Builder()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
            OkHttpClient.Builder()
        }
    }
}
