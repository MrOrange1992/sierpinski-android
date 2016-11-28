/*
 * Copyright (C) 2009 The Android Open Source Project
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
package com.example.android.basicmultitouch

import scala.reflect.ClassTag


trait Pool[T] {

  def acquire: T

  def release(instance: T): Boolean
}


class SimplePool[T: ClassTag](val maxPoolSize: Int) extends Pool[T] {
  if (maxPoolSize <= 0) {
    throw new IllegalArgumentException("The max pool size must be > 0")
  }

  private val mPool: Array[T] = Array.fill[T](maxPoolSize)(null.asInstanceOf[T])
  private var mPoolSize: Int = 0

  @SuppressWarnings(Array("unchecked"))
  def acquire: T = {
    if (mPoolSize > 0) {
      val lastPooledIndex: Int = mPoolSize - 1
      val instance: T = mPool(lastPooledIndex)
      mPool(lastPooledIndex) = null.asInstanceOf[T]
      mPoolSize -= 1
      instance
    } else null.asInstanceOf[T]
  }

  def release(instance: T): Boolean = {
    if (isInPool(instance)) {
      throw new IllegalStateException("Already in the pool!")
    }
    if (mPoolSize < mPool.length) {
      mPool(mPoolSize) = instance
      mPoolSize += 1
      true
    } else false
  }

  private def isInPool(instance: T): Boolean = mPool.contains(instance)

}


