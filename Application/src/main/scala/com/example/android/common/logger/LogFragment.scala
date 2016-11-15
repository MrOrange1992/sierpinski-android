/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.example.android.common.logger

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView

class LogFragment extends Fragment {
  private var mLogView: LogView = null
  private var mScrollView: ScrollView = null

  def inflateViews: View = {
    mScrollView = new ScrollView(getActivity)
    val scrollParams: ViewGroup.LayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    mScrollView.setLayoutParams(scrollParams)
    mLogView = new LogView(getActivity)
    val logParams: ViewGroup.LayoutParams = new ViewGroup.LayoutParams(scrollParams)
    logParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
    mLogView.setLayoutParams(logParams)
    mLogView.setClickable(true)
    mLogView.setFocusable(true)
    mLogView.setTypeface(Typeface.MONOSPACE)
    val paddingDips: Int = 16
    val scale: Double = getResources.getDisplayMetrics.density
    val paddingPixels: Int = ((paddingDips * (scale)) + .5).toInt
    mLogView.setPadding(paddingPixels, paddingPixels, paddingPixels, paddingPixels)
    mLogView.setCompoundDrawablePadding(paddingPixels)
    mLogView.setGravity(Gravity.BOTTOM)
    mLogView.setTextAppearance(android.R.style.TextAppearance_Holo_Medium)
    mScrollView.addView(mLogView)
    mScrollView
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val result: View = inflateViews
    mLogView.addTextChangedListener(new TextWatcher() {
      def beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
      }

      def onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
      }

      def afterTextChanged(s: Editable) {
        mScrollView.fullScroll(View.FOCUS_DOWN)
      }
    })
    return result
  }

  def getLogView: LogView = {
    return mLogView
  }
}