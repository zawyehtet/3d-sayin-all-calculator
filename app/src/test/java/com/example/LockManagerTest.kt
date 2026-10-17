package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.security.LockManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LockManagerTest {

    private lateinit var lockManager: LockManager

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        lockManager = LockManager(context)
        lockManager.resetToDefault()
        lockManager.setLockEnabled(true)
    }

    @Test
    fun testDefaultPinIs7777() {
        assertEquals("7777", lockManager.getPin())
        assertTrue(lockManager.verifyPin("7777"))
    }

    @Test
    fun testBurmeseDigitsPinVerification() {
        // Burmese ၇ = 7
        assertTrue(lockManager.verifyPin("၇၇၇၇"))
    }

    @Test
    fun testIncorrectPinRejection() {
        assertFalse(lockManager.verifyPin("1234"))
        assertFalse(lockManager.verifyPin("0000"))
        assertFalse(lockManager.verifyPin(""))
    }

    @Test
    fun testChangePinAndReset() {
        lockManager.setPin("8888")
        assertEquals("8888", lockManager.getPin())
        assertTrue(lockManager.verifyPin("8888"))
        assertFalse(lockManager.verifyPin("7777"))

        // Reset to default
        lockManager.resetToDefault()
        assertEquals("7777", lockManager.getPin())
        assertTrue(lockManager.verifyPin("7777"))
    }

    @Test
    fun testLockEnabledToggle() {
        assertTrue(lockManager.isLockEnabled())
        lockManager.setLockEnabled(false)
        assertFalse(lockManager.isLockEnabled())
        lockManager.setLockEnabled(true)
        assertTrue(lockManager.isLockEnabled())
    }
}
