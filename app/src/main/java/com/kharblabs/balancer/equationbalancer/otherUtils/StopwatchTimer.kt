package com.kharblabs.balancer.equationbalancer.otherUtils
class StopwatchTimer {

    private var startTime: Long = 0
    private var endTime: Long = 0
    private var isRunning: Boolean = false

    // Starts the timer
    fun start() {
        if (isRunning) {
            println("Timer is already running.")
            return
        }
        startTime = System.nanoTime() // Or System.currentTimeMillis() for millisecond precision
        isRunning = true
    }

    // Stops the timer and returns the elapsed time
    fun stop(): Long {
        if (!isRunning) {
            println("Timer has not been started yet.")
            return -1
        }
        endTime = System.nanoTime() // Or System.currentTimeMillis()
        isRunning = false
        return getElapsedTime() // Get the elapsed time
    }

    // Retrieves the elapsed time in milliseconds
    private fun getElapsedTime(): Long {
        return (endTime - startTime) / 1_000_000 // Convert nanoseconds to milliseconds
    }
}