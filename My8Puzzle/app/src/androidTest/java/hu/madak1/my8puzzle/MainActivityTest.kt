package hu.madak1.my8puzzle

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setupTestClass() {
            System.setProperty("test", "true")
        }
    }

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testNavigateToStart() {
        onView(withId(R.id.main_start_btn)).perform(click())
        onView(withId(R.id.start_title_tv)).check(ViewAssertions.matches(withText("Start")))
    }

    @Test
    fun testNavigateToSettings() {
        onView(withId(R.id.main_settings_btn)).perform(click())
        onView(withId(R.id.settings_title_tv)).check(ViewAssertions.matches(withText("Settings")))
    }

    @Test
    fun testNavigateToLeaderboard() {
        onView(withId(R.id.main_leaderboard_btn)).perform(click())
        onView(withId(R.id.leaderboards_title_tv)).check(ViewAssertions.matches(withText("Leaderboard")))
    }
}