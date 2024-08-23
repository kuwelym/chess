package com.example.chess.ui

import android.view.View

interface ModelViewMapper<T, V: View> {
    fun getViewForModel(model: T): V?
    fun getModelForView(view: V): T?
    fun register(model: T, view: V)
    fun unregister(model: T)
}