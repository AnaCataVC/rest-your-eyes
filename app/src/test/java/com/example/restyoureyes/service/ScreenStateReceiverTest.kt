package com.example.restyoureyes.service

import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class ScreenStateReceiverTest {

    @Test
    fun testOnReceive_withScreenOnAction_triggersCallbackWithTrue() {
        var receivedState: Boolean? = null
        val receiver = ScreenStateReceiver { state ->
            receivedState = state
        }

        val intent = mock(Intent::class.java)
        `when`(intent.action).thenReturn(Intent.ACTION_SCREEN_ON)
        receiver.onReceive(null, intent)

        assertEquals(true, receivedState)
    }

    @Test
    fun testOnReceive_withScreenOffAction_triggersCallbackWithFalse() {
        var receivedState: Boolean? = null
        val receiver = ScreenStateReceiver { state ->
            receivedState = state
        }

        val intent = mock(Intent::class.java)
        `when`(intent.action).thenReturn(Intent.ACTION_SCREEN_OFF)
        receiver.onReceive(null, intent)

        assertEquals(false, receivedState)
    }

    @Test
    fun testOnReceive_withOtherAction_doesNotTriggerCallback() {
        var receivedState: Boolean? = null
        val receiver = ScreenStateReceiver { state ->
            receivedState = state
        }

        val intent = mock(Intent::class.java)
        `when`(intent.action).thenReturn(Intent.ACTION_BATTERY_LOW)
        receiver.onReceive(null, intent)

        assertNull(receivedState)
    }
}
