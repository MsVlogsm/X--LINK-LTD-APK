package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PaymentMethod
import com.example.data.repository.XLinkRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("X-Link", appName)
  }

  @Test
  fun `test billing repository payment flow`() {
    val repo = XLinkRepository()
    val initialCust = repo.currentCustomer.value
    assertNotNull(initialCust)
    assertEquals("XLK-7842", initialCust.customerCode)

    // Pay bill
    val paymentResult = repo.payCurrentBill(PaymentMethod.BKASH, "01712345678")
    assertTrue(paymentResult)
    assertEquals(0.0, repo.currentCustomer.value.balanceDue, 0.01)
  }
}
