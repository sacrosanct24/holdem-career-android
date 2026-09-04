package com.example.holdemcareer

import com.example.holdemcareer.domain.career.SpecialEventsCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class EventSchedulerTest {

    @Test
    fun testTriggersDeepEndAt150Hands() {
        val event = SpecialEventsCatalog.checkActiveEvent(150)
        assertNotNull(event)
        assertEquals("The Deep End", event?.title)
    }

    @Test
    fun testTriggersShootoutAt300Hands() {
        val event = SpecialEventsCatalog.checkActiveEvent(300)
        assertNotNull(event)
        assertEquals("The Shootout Tournament", event?.title)
    }

    @Test
    fun testNoEventAtArbitraryHandCount() {
        val event = SpecialEventsCatalog.checkActiveEvent(47)
        assertNull(event)
    }
}
