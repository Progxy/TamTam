package com.theprogxy.tamtam

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import kotlinx.coroutines.flow.flowOf
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class RegisterActivityTest {
    private val dataStore = mock(DataStore::class.java) as DataStore<*>

    @JvmField
    @Rule
    var registerActivityRule: ActivityScenarioRule<RegisterActivity> = ActivityScenarioRule(RegisterActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    private fun setPermission(permissionType: String) {
        val device: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val allowPermissions: UiObject = device.findObject(UiSelector().text(permissionType))
        if (allowPermissions.exists()) {
            allowPermissions.click()
        }
        return
    }

    @Test
    fun testPermissionsRevoked() {
        this.setPermission("DENY")
        this.setPermission("DENY")

        onView(withTagValue(`is`("permission_revoked"))).check(matches(isDisplayed()))
        return
    }

    @Test
    fun testPermissionGranted() {
        this.setPermission("ALLOW")
        this.setPermission("ALLOW")

        val victimId = stringPreferencesKey("victimId")
        Mockito.`when`(dataStore.data).thenReturn(
            flowOf(
                preferencesOf(
                    victimId to "null"
                )
            )
        )

        onView(withId(R.id.idEditText)).check(matches(isDisplayed())).perform(replaceText(""))
        onView(withId(R.id.registerButton)).perform(click())

        onView(withId(R.id.idEditText)).check(matches(isDisplayed())).perform(replaceText("randomVictim"))
        onView(withId(R.id.registerButton)).perform(click())

        Mockito.`when`(dataStore.data).thenReturn(
            flowOf(
                preferencesOf(
                    victimId to "pippo"
                )
            )
        )

        onView(withId(R.id.confirmText)).check(matches(isDisplayed()))
        onView(withId(R.id.nextButton)).perform(click())
        onView(withTagValue(`is`("tracker"))).check(matches(isDisplayed()))
        return
    }

}