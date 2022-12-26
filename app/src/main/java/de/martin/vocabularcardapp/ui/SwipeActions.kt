package de.martin.vocabularcardapp.ui

abstract class SwipeActions {
    open fun onLeftSwiped(position: Int) {}
    open fun onRightSwiped(position: Int) {}
}
