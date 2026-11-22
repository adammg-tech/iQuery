package com.adammg.iquery.util

object DeviceClassifier {
    fun supportsIPhone(supportedDevices: List<String>?): Boolean {
        return supportedDevices.orEmpty().any {
            it.startsWith("iPhone", ignoreCase = true)
        }
    }
}
