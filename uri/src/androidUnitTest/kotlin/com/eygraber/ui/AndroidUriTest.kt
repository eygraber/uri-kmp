package com.eygraber.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eygraber.uri.Uri
import com.eygraber.uri.toAndroidUri
import com.eygraber.uri.toUri
import com.eygraber.uri.toUrl
import com.eygraber.uri.toUrlOrNull
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import android.net.Uri as AndroidUri

@RunWith(AndroidJUnit4::class)
public class AndroidUriTest {
  @Test
  public fun `toAndroidUri does not change the uri`() {
    val uri = Uri.parse("https://google.com")
    assertEquals(uri.toString(), uri.toAndroidUri().toString())
  }

  @Test
  public fun `toUri does not change the uri`() {
    val uri = AndroidUri.parse("https://google.com")
    assertEquals(uri.toString(), uri.toUri().toString())
  }

  @Test
  public fun `androidUri without scheme fails when calling toUrl`() {
    val uri = AndroidUri.parse("/tmp/1.log")
    assertEquals(
      assertFailsWith<IllegalArgumentException> {
        uri.toUrl()
      }.message,
      "Url scheme must not be null"
    )
  }

  @Test
  public fun `androidUri without scheme returns null when calling toUrlOrNull`() {
    val uri = AndroidUri.parse("/tmp/1.log")
    assertNull(uri.toUrlOrNull())
  }

  @Test
  public fun `androidUri without host fails when calling toUrl`() {
    val uri = AndroidUri.parse("https:")
    assertEquals(
      assertFailsWith<IllegalArgumentException> {
        uri.toUrl()
      }.message,
      "Url host must not be null"
    )
  }

  @Test
  public fun `androidUri without host returns null when calling toUrlOrNull`() {
    val uri = AndroidUri.parse("https:")
    assertNull(uri.toUrlOrNull())
  }
}
