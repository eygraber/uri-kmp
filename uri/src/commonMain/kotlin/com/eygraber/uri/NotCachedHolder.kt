/*
 * Copyright (C) 2007 The Android Open Source Project
 * Copyright (C) 2022 Eliezer Graber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eygraber.uri

/**
 *
 * Holds a placeholder for strings which haven't been cached. This enables us
 * to cache null. We intentionally create a new String instance so we can
 * compare its identity and there is no chance we will confuse it with
 * user data.
 *
 * NOTE This value is held in its own Holder class is so that referring to
 * [NotCachedHolder.NotCached] does not trigger `Uri.<clinit>`.
 * For example, `PathPart.<init>` uses `NotCachedHolder.NOT_CACHED`
 * but must not trigger `Uri.<clinit>`: Otherwise, the initialization of
 * `Uri.EMPTY` would see a `null` value for `PathPart.EMPTY`!
 */
internal object NotCachedHolder {
  val NotCached = charArrayOf('N', 'O', 'T', ' ', 'C', 'A', 'C', 'H', 'E', 'D').concatToString()
}
